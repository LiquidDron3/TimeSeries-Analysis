package org.dataAnalysis;

import euclides.math.timeseries.Model;
import java.util.Arrays;

public class DataPreparationHandler {

    public static double[] prepareRawInputData(String rawInputData) {
        if (rawInputData.isEmpty()) {
            throw new IllegalArgumentException(Messages.ERROR_EMPTY_ARRAY);
        }
        String[][] convertedStringArray = convertLinesTo2DArray(rawInputData);

        return Arrays.stream(convertedStringArray)
                .mapToDouble(arr -> Double.parseDouble(arr[1].replace(',', '.')))
                .toArray();
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

    public static double[][] prepareCalcDataForD3(double[] calculatedData, int predPoint, Model reg) {
        double[][] dataSetWithIndex = new double[calculatedData.length][2];
        for (int i = 0; i < dataSetWithIndex.length; i++) {
            dataSetWithIndex[i][0] = calculatedData[i];

            if (RegressionHandler.isInstanceAutoRegressionType(reg)) {
                dataSetWithIndex[i][1] = i + 1;
            } else {
                // Index set from -calculatedData + 1 to predPoint
                dataSetWithIndex[i][1] = i - (calculatedData.length - predPoint) + 1;
            }
        }
        return dataSetWithIndex;
    }

    public static double[][] prepareInputDataForD3(double[] inputData) {
        double[][] dataSetWithIndex = new double[inputData.length][2];
        for (int i = 0; i < dataSetWithIndex.length; i++) {
            dataSetWithIndex[i][0] = inputData[i];
            dataSetWithIndex[i][1] = i - (inputData.length) + 1;
        }
        return dataSetWithIndex;
    }
}
