package org.dataAnalysis;


import org.junit.Test;

import static org.junit.Assert.*;

public class DataPreparationHandlerTest {
    DataPreparationHandler handler = new DataPreparationHandler();

    @Test
    public void prepareDataSetWithPredictionPointIsZero() {
        double[] inputData = {
                3.37, 51.16, 33.28, 80.67, 36.16, 13.8, 71.54, 26.58, 55.98, 66.7,
                40.18, 10.98, 40.1, 83.68, 11.55};
        double[] calculatedData = {11.55};
        double[][] expectedOutput = {
                {3.37, Double.NaN, -14.0}, {51.16, Double.NaN, -13.0}, {33.28, Double.NaN, -12.0}, {80.67, Double.NaN, -11.0},
                {36.16, Double.NaN, -10.0}, {13.8, Double.NaN, -9.0}, {71.54, Double.NaN, -8.0}, {26.58, Double.NaN, -7.0},
                {55.98, Double.NaN, -6.0}, {66.7, Double.NaN, -5.0}, {40.18, Double.NaN, -4.0}, {10.98, Double.NaN, -3.0},
                {40.1, Double.NaN, -2.0}, {83.68, Double.NaN, -1.0}, {11.55, 11.55, 0.0}
        };
        int predictionPoint = 0;

        double[][] result = handler.prepareDataSet(inputData, calculatedData, predictionPoint);

        assertArrayEquals(expectedOutput, result);
    }

    @Test
    public void prepareDataSetWithCalculationPointsInFutureOnly() {
        double[] inputData = {
                3.37, 51.16, 33.28, 80.67, 36.16, 13.8, 71.54, 26.58, 55.98, 66.7,
                40.18, 10.98, 40.1, 83.68, 11.55};
        double[] calculatedData = {83.68, 33.28, 80.67, 36.16, 13.8};
        double[][] expectedOutput = {
                {3.37, Double.NaN, -14.0}, {51.16, Double.NaN, -13.0}, {33.28, Double.NaN, -12.0}, {80.67, Double.NaN, -11.0},
                {36.16, Double.NaN, -10.0}, {13.8, Double.NaN, -9.0}, {71.54, Double.NaN, -8.0}, {26.58, Double.NaN, -7.0},
                {55.98, Double.NaN, -6.0}, {66.7, Double.NaN, -5.0}, {40.18, Double.NaN, -4.0}, {10.98, Double.NaN, -3.0},
                {40.1, Double.NaN, -2.0}, {83.68, Double.NaN, -1.0}, {11.55, 11.55, 0.0}, {Double.NaN, 83.68, 1.0},
                {Double.NaN, 33.28, 2.0}, {Double.NaN, 80.67, 3.0}, {Double.NaN, 36.16, 4.0}, {Double.NaN, 13.8, 5.0}
        };
        int predictionPoint = 5;

        double[][] result = handler.prepareDataSet(inputData, calculatedData, predictionPoint);

        assertArrayEquals(expectedOutput, result);
    }

    @Test
    public void prepareDataSetWithCalculationPointsInHistory() {
        double[] inputData = {
                3.37, 51.16, 33.28, 80.67, 36.16, 13.8, 71.54, 26.58, 55.98, 66.7,
                40.18, 10.98, 40.1, 83.68, 11.55};
        double[] calculatedData = {
                3.37, 51.16, 33.28, 80.67, 36.16, 13.8, 71.54, 26.58, 55.98, 66.7,
                40.18, 10.98, 40.1, 83.68, 11.55, 83.68, 33.28, 80.67, 36.16, 13.8};
        double[][] expectedOutput = {
                {3.37, 3.37, -14.0}, {51.16, 51.16, -13.0}, {33.28, 33.28, -12.0}, {80.67, 80.67, -11.0},
                {36.16, 36.16, -10.0}, {13.8, 13.8, -9.0}, {71.54, 71.54, -8.0}, {26.58, 26.58, -7.0},
                {55.98, 55.98, -6.0}, {66.7, 66.7, -5.0}, {40.18, 40.18, -4.0}, {10.98, 10.98, -3.0},
                {40.1, 40.1, -2.0}, {83.68, 83.68, -1.0}, {11.55, 11.55, 0.0}, {Double.NaN, 83.68, 1.0},
                {Double.NaN, 33.28, 2.0}, {Double.NaN, 80.67, 3.0}, {Double.NaN, 36.16, 4.0}, {Double.NaN, 13.8, 5.0}
        };
        int predictionPoint = 5;

        double[][] result = handler.prepareDataSet(inputData, calculatedData, predictionPoint);

        assertArrayEquals(expectedOutput, result);
    }

    @Test
    public void prepareDataSetWithoutCalculationData() {
        double[] inputData = {
                3.37, 51.16, 33.28, 80.67, 36.16, 13.8, 71.54, 26.58, 55.98, 66.7,
                40.18, 10.98, 40.1, 83.68, 11.55};
        double[] calculatedData = {};
        int predictionPoint = 1;

        try {
            handler.prepareDataSet(inputData, calculatedData, predictionPoint);
            fail("IllegalArgumentException was expected here.");
        } catch (IllegalArgumentException e) {
            assertEquals(Messages.ERROR_EMPTY_DATASET, e.getMessage());
        }
    }

    @Test
    public void prepareDataSetWithoutInputAndData() {
        double[] inputData = {};
        double[] calculatedData = {
                3.37, 51.16, 33.28, 80.67, 36.16, 13.8, 71.54, 26.58, 55.98, 66.7,
                40.18, 10.98, 40.1, 83.68, 11.55};
        int predictionPoint = 1;

        try {
            handler.prepareDataSet(inputData, calculatedData, predictionPoint);
            fail("IllegalArgumentException was expected here.");
        } catch (IllegalArgumentException e) {
            assertEquals(Messages.ERROR_EMPTY_DATASET, e.getMessage());
        }
    }

    @Test
    public void prepareDataSetWithNegativePredictionPoint() {
        double[] inputData = {
                3.37, 51.16, 33.28, 80.67, 36.16, 13.8, 71.54, 26.58, 55.98, 66.7,
                40.18, 10.98, 40.1, 83.68, 11.55};
        double[] calculatedData = {
                3.37, 51.16, 33.28, 80.67, 36.16, 13.8, 71.54, 26.58, 55.98, 66.7,
                40.18, 10.98, 40.1, 83.68, 11.55};
        int predictionPoint = -1;

        try {
            handler.prepareDataSet(inputData, calculatedData, predictionPoint);
            fail("IllegalArgumentException was expected here.");
        } catch (IllegalArgumentException e) {
            assertEquals(Messages.ERROR_NEGATIVE_PREDICTION_POINT, e.getMessage());
        }
    }

    @Test
    public void fillInputDataSetWithoutEqualInputAndPredPoint() {
        double[] inputData = {
                3.37, 51.16, 33.28, 80.67, 36.16, 13.8, 71.54, 26.58, 55.98, 66.7,
                40.18, 10.98, 40.1, 83.68, 11.55};
        double[] expectedDataSet = {
                3.37, 51.16, 33.28, 80.67, 36.16, 13.8, 71.54, 26.58, 55.98, 66.7,
                40.18, 10.98, 40.1, 83.68, 11.55, Double.NaN, Double.NaN, Double.NaN,
                Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN};
        int predictionPoint = 9;

        double[] result = handler.fillInputDataSetWithNan(inputData, predictionPoint);

        assertArrayEquals(expectedDataSet, result, 0.01);
    }

    @Test
    public void fillInputDataSetWithPredictionPointIsZero() {
        double[] inputData = {
                3.37, 51.16, 33.28, 80.67, 36.16, 13.8, 71.54, 26.58, 55.98, 66.7,
                40.18, 10.98, 40.1, 83.68, 11.55};
        double[] expectedDataSet = {
                3.37, 51.16, 33.28, 80.67, 36.16, 13.8, 71.54, 26.58, 55.98, 66.7,
                40.18, 10.98, 40.1, 83.68, 11.55};
        int predictionPoint = 0;

        double[] result = handler.fillInputDataSetWithNan(inputData, predictionPoint);

        assertArrayEquals(expectedDataSet, result, 0.01);
    }

    @Test
    public void fillCalculatedDataSetWithCalculationsInHistory() {
        double[] inputData = {3.37, 51.16, 33.28, 80.67, 36.16};
        double[] calculatedData = {
                3.37, 51.16, 33.28, 80.67, 36.16, 13.8, 71.54, 26.58, 55.98, 66.7,
                40.18, 10.98, 40.1, 83.68, 11.55};
        double[] expectedDataSet = {
                3.37, 51.16, 33.28, 80.67, 36.16, 13.8, 71.54, 26.58, 55.98, 66.7,
                40.18, 10.98, 40.1, 83.68, 11.55};
        int predictionPoint = 10;

        double[] result = handler.fillCalculatedDataSetWithNan(inputData, calculatedData, predictionPoint);

        assertArrayEquals(expectedDataSet, result, 0.01);
    }

    @Test
    public void fillCalculationDataSetWitCalculationsInFutureOnly() {
        double[] inputData = {3.37, 51.16, 33.28, 80.67, 36.16, 80.67};
        double[] calculatedData = {
                3.37, 51.16, 33.28, 80.67, 36.16, 13.8, 71.54, 26.58, 55.98, 66.7,
                40.18, 10.98, 40.1, 83.68, 11.55};
        double[] expectedDataSet = {
                Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN,
                80.67, 3.37, 51.16, 33.28, 80.67, 36.16, 13.8, 71.54,
                26.58, 55.98, 66.7, 40.18, 10.98, 40.1, 83.68, 11.55, };
        int predictionPoint = 15;

        double[] result = handler.fillCalculatedDataSetWithNan(inputData, calculatedData, predictionPoint);

        assertArrayEquals(expectedDataSet, result, 0.01);
    }

    @Test
    public void fillCalculatedDataSetWithPredictionPointIsZero() {
        double[] inputData = {3.37, 51.16, 33.28, 80.67, 36.16, 80.67};
        double[] calculatedData = {80.67};
        double[] expectedDataSet = {Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, 80.67 };
        int predictionPoint = 0;

        double[] result = handler.fillCalculatedDataSetWithNan(inputData, calculatedData, predictionPoint);

        assertArrayEquals(expectedDataSet, result, 0.01);
    }

}