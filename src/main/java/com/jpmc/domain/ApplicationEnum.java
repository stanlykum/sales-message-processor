package com.jpmc.domain;

/**
 * This is used to hold application level constants
 *
 * @author Stanly
 */
public enum ApplicationEnum {

    ADDITION("Add"),
    SUBTRACTION("Subtract"),
    MULTIPLICATION("Multiply"),
    LOG("Log"),
    PROCESS("Process"),
    RECORD("Record"),
    LOG_REPORT_PER_MESSAGE_RECEIVED("10"),
    MAX_MESSAGES_TO_PAUSE_PROCESSING("50"),
    TCP_PORT("9898"),
    MSG_VALID_TYPE("mango,apple,mangos,apples,orange,oranges"),
    MIN_MSG_LENGTH("10"),
    TCP_HOST("localhost");
    private final String type;

    ApplicationEnum(String type) {
        this.type = type;
    }

    public String getEnumType() {
        return this.type;
    }
}