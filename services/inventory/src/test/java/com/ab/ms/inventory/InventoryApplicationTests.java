package com.ab.ms.inventory;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@EmbeddedKafka(
        partitions = 1,
        topics = {"inventory.commands.reserve", "inventory.commands.release", "order.saga.events"})
@TestPropertySource(
        properties = {
            "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
            "spring.cloud.stream.kafka.binder.brokers=${spring.embedded.kafka.brokers}",
        })
@ActiveProfiles("test")
class InventoryApplicationTests {

    @Test
    void contextLoads() {}
}
