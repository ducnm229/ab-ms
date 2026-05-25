package com.ab.ms.product.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisLockService {
    private final StringRedisTemplate stringRedisTemplate;
    private static final String UNLOCK_SCRIPT = """
		if redis.call("get", KEYS[1]) == ARGV[1] then
			return redis.call("del", KEYS[1])
		else
			return 0
		 end
	""";
    private static final RedisScript<Long> unlockScript =
            new DefaultRedisScript<>(UNLOCK_SCRIPT, Long.class);


    public Optional<LockToken> tryLock(String productId) {
        String lockKey = productLockKey(productId);
        String lockId = UUID.randomUUID().toString();
        if (Boolean.TRUE.equals(stringRedisTemplate.opsForValue()
                .setIfAbsent(lockKey, lockId, Duration.ofSeconds(5))))
            return Optional.of(new LockToken(lockKey, lockId));
        return Optional.empty();
    }

    public void unlock(LockToken lockToken) {
        Long result = stringRedisTemplate.execute(
                unlockScript,
                Collections.singletonList(lockToken.lockKey()),
                lockToken.lockId()
        );
        if (Long.valueOf(1L).equals(result)) {
            log.info("Lock released: {}", lockToken.lockKey());
        }
    }

    private static String productLockKey(String id) {
        return "lock:product:" + id;
    }
}
