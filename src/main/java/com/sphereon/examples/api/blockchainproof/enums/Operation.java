package com.sphereon.examples.api.blockchainproof.enums;

import org.apache.commons.lang3.StringUtils;

public enum Operation {
    REGISTER, VERIFY;


    public static Operation fromString(final String value) {
        for (var operation : values()) {
            if (StringUtils.equalsIgnoreCase(operation.name(), value)) {
                return operation;
            }
        }
        throw new IllegalArgumentException(value + " is not a known operation!");
    }
}
