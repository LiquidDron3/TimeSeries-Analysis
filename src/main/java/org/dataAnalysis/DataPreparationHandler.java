package org.dataAnalysis;

import java.util.Arrays;

public class DataPreparationHandler {
    public static int[] getDataSetIndicesWithZoom(int standardDisplayedDataPoints, double[][] dataSet, double zoomLevel) {
        int visibleDataPoints = (int) (standardDisplayedDataPoints / zoomLevel);
        int totalDataPoints = Math.min(visibleDataPoints, dataSet.length);
        int startIndex = Math.max(0, dataSet.length - visibleDataPoints);
        int endIndex = Math.min(dataSet.length, startIndex + totalDataPoints);
        return new int[] {startIndex, endIndex};
    }

    public static double[][] prepareDataSet(double[] inputData, double[] calculatedData, int predictionPoint) {
        if (predictionPoint < 0) {
            throw new IllegalArgumentException(Messages.ERROR_NEGATIVE_PREDICTION_POINT);
        }
        if (inputData.length == 0 || calculatedData.length == 0) {
            throw new IllegalArgumentException(Messages.ERROR_EMPTY_DATASET);
        }

        double[][] preparedDataSet = new double[inputData.length + predictionPoint][3];
        double[] preparedInputData = fillInputDataSetWithNan(inputData, predictionPoint);
        double[] preparedCalculatedData = fillCalculatedDataSetWithNan(inputData, calculatedData, predictionPoint);

        if (preparedCalculatedData.length != preparedInputData.length) {
            throw new IllegalArgumentException(Messages.ERROR_DATASET_LENGTH);
        }

        for (int i = 0; i < preparedDataSet.length; i++) {
            preparedDataSet[i][0] = preparedInputData[i];
            preparedDataSet[i][1] = preparedCalculatedData[i];

            if(i < preparedInputData.length - predictionPoint) {
                preparedDataSet[i][2] = -(inputData.length) + i + 1;
            } else {
                preparedDataSet[i][2] = preparedDataSet[i][2] = i - (inputData.length) + 1;
            }
        }

        return preparedDataSet;
    }

    public static double[] fillInputDataSetWithNan(double[] inputData, int predictionPoint) {
        double[] preparedData = new double[inputData.length + predictionPoint];

        System.arraycopy(inputData, 0, preparedData, 0, inputData.length);
        for (int i = inputData.length; i < inputData.length + predictionPoint; i++) {
            preparedData[i] = Double.NaN;
        }
        return preparedData;
    }

    public static double[] fillCalculatedDataSetWithNan(double[] inputData, double[] calculatedData, int predictionPoint) {
        int inputLength = inputData.length;
        int calculatedLength = calculatedData.length;
        if (inputLength + predictionPoint == calculatedLength) {
            return calculatedData;
        } else {
            double[] preparedData = new double[inputLength + predictionPoint];
            for (int i = 0; i < inputLength; i++) {
                preparedData[i] = Double.NaN;
            }
            preparedData[inputLength - 1] = inputData[inputLength - 1];
            System.arraycopy(calculatedData, 0, preparedData, inputLength, predictionPoint);
            return preparedData;
        }
    }

    public static double[] prepareRawInputData(String rawInputData) {
        if (rawInputData.isEmpty()) {
            throw new IllegalArgumentException(Messages.ERROR_EMPTY_ARRAY);
        }
        String[][] convertedStringArray = convertLinesTo2DArray(rawInputData);

        return Arrays.stream(convertedStringArray)
                .mapToDouble(arr -> Double.parseDouble(arr[1].replace(',', '.')))
                .toArray();
    }

    public static double[][] extractSubArray(double[][] dataSet, int startIndex, int endIndex) {
        double[] firstArray = new double[endIndex - startIndex];
        double[] secondArray = new double[endIndex - startIndex];
        double[] thirdArray = new double[endIndex - startIndex];
        for(int i = startIndex; i < endIndex; i++) {
            firstArray[i - startIndex] = dataSet[i][0];
            secondArray[i - startIndex] = dataSet[i][1];
            thirdArray[i - startIndex] = dataSet[i][2];
        }
        return new double[][] {firstArray, secondArray, thirdArray};
    }

    public static String[][] convertLinesTo2DArray(String content) {
        String[] lines = content.split("\\r?\\n");
        String[][] array = new String[lines.length][2];

        for (int i = 0; i < lines.length; i++) {
            String[] parts = lines[i].split(";");

            if (parts.length != 2) {
                throw new IllegalArgumentException(Messages.ERROR_2D_ARRAY);
            }
            array[i][0] = parts[0].trim();
            array[i][1] = parts[1].trim();
        }
        return array;
    }
}
