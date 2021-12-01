package org.kuga.kip.datastore.field;

public enum Type {

    STRING(Values.STRING),
    ENUM(Values.ENUM),
    BOOLEAN(Values.BOOLEAN),
    NUMERIC(Values.NUMERIC),
    DATE(Values.DATE),
    LIST(Values.LIST),
    RANGE(Values.RANGE),
    URL(Values.URL);

    Type(String value) {
        // force equality between name of enum instance, and value of constant
        if (!this.name().equals(value))
            throw new IllegalArgumentException("enum type accessor must match value");
    }

    public static class Values {
        public static final String STRING = "STRING";
        public static final String ENUM = "ENUM";
        public static final String BOOLEAN = "BOOLEAN";
        public static final String NUMERIC = "NUMERIC";
        public static final String DATE = "DATE";
        public static final String LIST = "LIST";
        public static final String RANGE = "RANGE";
        public static final String URL = "URL";
    }

}
