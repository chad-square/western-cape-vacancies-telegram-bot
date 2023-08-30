package org.chadsquare.telegram_bot;

public enum QueryPrefix {

    DYNAMIC_QUERY("dynamic-query::");

    public final String value;

    private QueryPrefix(String value) {
        this.value = value;
    }
}
