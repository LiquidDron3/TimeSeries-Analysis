package org.dataAnalysis;

public class Messages {
    public static final String ERROR_2D_ARRAY;
    public static final String ERROR_EMPTY_ARRAY;
    public static final String ERROR_NO_EARLIEST_DATE;
    public static final String ERROR_UNKNOWN_REGRESSION;
    public static final String ERROR_EMPTY_DATASET;
    public static final String ERROR_NEGATIVE_PREDICTION_POINT;
    public static final String ERROR_NEGATIVE_NO_INPUT_DATA;
    public static final String ERROR_DATASET_LENGTH;
    public static final String ERROR_CANVAS_NOT_FOUND;
    public static final String ERROR_EMPTY_PARAMETER;
    static {
        ERROR_2D_ARRAY = "The input has to be a 2d array.";
        ERROR_EMPTY_ARRAY = "The input is empty.";
        ERROR_NO_EARLIEST_DATE = "No earliest date set.";
        ERROR_UNKNOWN_REGRESSION = "Unknown regression type.";
        ERROR_EMPTY_DATASET = "The dataset is empty";
        ERROR_NEGATIVE_PREDICTION_POINT = "The prediction point cannot be negative.";
        ERROR_NEGATIVE_NO_INPUT_DATA = "Input data is empty.";
        ERROR_DATASET_LENGTH = "The length of the prepared input and prepared calculated data is not equal.";
        ERROR_EMPTY_PARAMETER = "At least one regression parameter is empty.";
        ERROR_CANVAS_NOT_FOUND = "Canvas not found.";
    }
}