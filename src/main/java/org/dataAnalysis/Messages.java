package org.dataAnalysis;

public class Messages {
    public static final String ERROR_2D_ARRAY;
    public static final String ERROR_EMPTY_ARRAY;
    public static final String ERROR_NO_EARLIEST_DATE;
    public static final String ERROR_UNKNOWN_REGRESSION;

    static {
        ERROR_2D_ARRAY = "The input has to be a 2d array.";
        ERROR_EMPTY_ARRAY = "The input is empty.";
        ERROR_NO_EARLIEST_DATE = "No earliest date set.";
        ERROR_UNKNOWN_REGRESSION = "Unknown regression type.";
    }
}
