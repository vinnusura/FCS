package com.fulfilment.application.monolith.exceptions;

public class WarehouseException extends RuntimeException {

    private final ErrorRule errorRule;

    public WarehouseException(ErrorRule errorRule) {
        super(errorRule.name());
        this.errorRule = errorRule;
    }

    public WarehouseException(ErrorRule errorRule, String message) {
        super(message);
        this.errorRule = errorRule;
    }

    public ErrorRule getErrorRule() {
        return errorRule;
    }
}
