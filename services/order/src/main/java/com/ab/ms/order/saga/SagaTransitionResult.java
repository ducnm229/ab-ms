package com.ab.ms.order.saga;

/**
 * Result of resolving one incoming saga event against current order state
 */
public sealed interface SagaTransitionResult permits SagaTransitionResult.ApplyTransition, SagaTransitionResult.NoOpDuplicate {

    record ApplyTransition(SagaStep step) implements SagaTransitionResult {
    }

    record NoOpDuplicate() implements SagaTransitionResult {
    }

    static SagaTransitionResult apply(SagaStep step) {
        return new ApplyTransition(step);
    }

    static SagaTransitionResult noOpDuplicate() {
        return new NoOpDuplicate();
    }
}
