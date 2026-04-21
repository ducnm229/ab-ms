package com.ab.ms.payment;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
public class PaymentApplication {
	private static final String DEBUG_LOG_PATH = "debug-772919.log";
	private static final String DEBUG_SESSION_ID = "772919";
	private static final String DEBUG_RUN_ID = "startup-schema-init";

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(PaymentApplication.class);
		// #region agent log
		app.addListeners((ApplicationEnvironmentPreparedEvent event) -> writeDebugLog(
			"H1",
			"PaymentApplication:main",
			"Environment prepared before SQL initialization",
			"\"datasourceUrl\":\"" + safe(event.getEnvironment().getProperty("spring.datasource.url")) + "\","
				+ "\"driver\":\"" + safe(event.getEnvironment().getProperty("spring.datasource.driverClassName")) + "\","
				+ "\"jpaDialect\":\"" + safe(event.getEnvironment().getProperty("spring.jpa.database-platform")) + "\","
				+ "\"activeProfiles\":\"" + safe(String.join(",", event.getEnvironment().getActiveProfiles())) + "\""
		));
		// #endregion
		// #region agent log
		app.addListeners((ApplicationEnvironmentPreparedEvent event) -> {
			String datasourceUrl = safe(event.getEnvironment().getProperty("spring.datasource.url"));
			String schema = loadSchemaSql();
			writeDebugLog(
				"H3",
				"PaymentApplication:main",
				"Schema compatibility indicators",
				"\"datasourceIsH2\":" + datasourceUrl.toLowerCase(Locale.ROOT).contains("jdbc:h2") + ","
					+ "\"schemaContainsPartialUniqueIndex\":" + schema.toLowerCase(Locale.ROOT).contains("create unique index")
					+ ",\"schemaContainsWherePredicate\":" + schema.toLowerCase(Locale.ROOT).contains("where status = 'succeeded'")
			);
		});
		// #endregion
		// #region agent log
		app.addListeners((ApplicationFailedEvent event) -> {
			Throwable root = event.getException();
			while (root != null && root.getCause() != null && root.getCause() != root) {
				root = root.getCause();
			}
			writeDebugLog(
				"H2",
				"PaymentApplication:main",
				"Application startup failed",
				"\"exceptionType\":\"" + safe(root == null ? "null" : root.getClass().getName()) + "\","
					+ "\"exceptionMessage\":\"" + safe(root == null ? "null" : root.getMessage()) + "\""
			);
		});
		// #endregion
		app.run(args);
	}

	private static void writeDebugLog(String hypothesisId, String location, String message, String dataJson) {
		String payload = "{\"sessionId\":\"" + DEBUG_SESSION_ID + "\",\"runId\":\"" + DEBUG_RUN_ID
			+ "\",\"hypothesisId\":\"" + hypothesisId + "\",\"location\":\"" + safe(location)
			+ "\",\"message\":\"" + safe(message) + "\",\"data\":{" + dataJson + "},\"timestamp\":"
			+ System.currentTimeMillis() + "}";
		try {
			Files.writeString(
				Path.of(DEBUG_LOG_PATH),
				payload + System.lineSeparator(),
				StandardCharsets.UTF_8,
				StandardOpenOption.CREATE,
				StandardOpenOption.APPEND
			);
		} catch (IOException ignored) {
			// Debug logging must not break app startup.
		}
	}

	private static String safe(String value) {
		if (value == null) {
			return "null";
		}
		return value.replace("\\", "\\\\").replace("\"", "\\\"");
	}

	private static String loadSchemaSql() {
		try {
			return Files.readString(Path.of("payment/src/main/resources/schema.sql"), StandardCharsets.UTF_8);
		} catch (IOException ignored) {
			return "";
		}
	}
}
