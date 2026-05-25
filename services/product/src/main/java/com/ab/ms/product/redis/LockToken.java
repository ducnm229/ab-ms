package com.ab.ms.product.redis;

public record LockToken(String lockKey, String lockId) {
}
