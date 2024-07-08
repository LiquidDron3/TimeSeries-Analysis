package org.dataAnalysis;

import euclides.math.timeseries.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class RegressionHandlerTest {
    int predictionPoint = 5;
    double[] inputDataSet = {245, 247, 256, 257, 257, 258, 255, 253, 255, 251, 251, 256, 256, 253,
            249, 247, 246, 244, 242, 244};
    ConstantRegression constantRegression;
    SimpleLinearRegression simpleLinearRegression;
    PolynomialRegression polynomialRegression;
    AutoRegression autoRegression;
    AutoRegressiveMovingAverage autoRegressiveMovingAverage;
    AutoRegressiveIntegratedMovingAverage autoRegressiveIntegratedMovingAverage;
    RegressionHandler regressionHandler;

    @Before
    public void setUp() {
        constantRegression = new ConstantRegression();
        constantRegression.init(inputDataSet);

        simpleLinearRegression = new SimpleLinearRegression();
        simpleLinearRegression.init(inputDataSet);

        polynomialRegression = new PolynomialRegression(3);
        polynomialRegression.init(inputDataSet);

        autoRegression = new AutoRegression(3);
        autoRegression.init(inputDataSet);

        autoRegressiveMovingAverage = new AutoRegressiveMovingAverage(3, 1);
        autoRegressiveMovingAverage.init(inputDataSet);

        autoRegressiveIntegratedMovingAverage = new AutoRegressiveIntegratedMovingAverage(3, 1, 1);
        autoRegressiveIntegratedMovingAverage.init(inputDataSet);

        HtmlHandler htmlHandler = Mockito.mock(HtmlHandler.class);
        regressionHandler = new RegressionHandler(htmlHandler);
    }

    @Test
    public void checkIfInstanceIsAutoRegressionType() {

        assertTrue(regressionHandler.isInstanceAutoRegressionType(autoRegression));
        assertTrue(regressionHandler.isInstanceAutoRegressionType(autoRegressiveMovingAverage));
        assertTrue(regressionHandler.isInstanceAutoRegressionType(autoRegressiveIntegratedMovingAverage));

        assertFalse(regressionHandler.isInstanceAutoRegressionType(constantRegression));
        assertFalse(regressionHandler.isInstanceAutoRegressionType(simpleLinearRegression));
        assertFalse(regressionHandler.isInstanceAutoRegressionType(polynomialRegression));
    }

    @Test
    public void checkIfInstanceIsSupported() {

        assertTrue(regressionHandler.isInstanceSupportedRegression(autoRegression));
        assertTrue(regressionHandler.isInstanceSupportedRegression(autoRegressiveMovingAverage));
        assertTrue(regressionHandler.isInstanceSupportedRegression(autoRegressiveIntegratedMovingAverage));
        assertTrue(regressionHandler.isInstanceSupportedRegression(constantRegression));
        assertTrue(regressionHandler.isInstanceSupportedRegression(simpleLinearRegression));
        assertTrue(regressionHandler.isInstanceSupportedRegression(polynomialRegression));
    }

    @Test
    public void checkEvaluationForConstantRegression() {
        double epsilon = 0.01;
        assertEquals(251.1, constantRegression.eval(1), epsilon);
    }

    @Test
    public void checkEvaluationForSimpleLinearRegression() {
        double epsilon = 0.01;
        assertEquals(246.25, simpleLinearRegression.eval(1), epsilon);
        assertEquals(245.79, simpleLinearRegression.eval(2), epsilon);
        assertEquals(245.32, simpleLinearRegression.eval(3), epsilon);
    }

    @Test
    public void checkEvaluationForPolynomialRegression() {
        double epsilon = 0.001;
        assertEquals(242.253, polynomialRegression.eval(1), epsilon);
        assertEquals(241.950, polynomialRegression.eval(2), epsilon);
        assertEquals(242.016, polynomialRegression.eval(3), epsilon);
    }

    @Test
    public void checkEvaluationForAutoRegression() {
        double epsilon = 0.01;
        assertEquals(245.67, autoRegression.eval(1), epsilon);
        assertEquals(246.76, autoRegression.eval(2), epsilon);
        assertEquals(247.58, autoRegression.eval(3), epsilon);
    }

    @Test
    public void checkEvaluationForAutoRegressiveMovingAverage() {
        double epsilon = 0.01;
        assertEquals(247.03, autoRegressiveMovingAverage.eval(1), epsilon);
        assertEquals(232.24, autoRegressiveMovingAverage.eval(2), epsilon);
        assertEquals(430.23, autoRegressiveMovingAverage.eval(3), epsilon);
    }

    @Test
    public void checkEvaluationForAutoRegressiveIntegratedMovingAverage() {
        double epsilon = 0.01;
        assertEquals(244.0, autoRegressiveIntegratedMovingAverage.eval(1), epsilon);
        assertEquals(246.25, autoRegressiveIntegratedMovingAverage.eval(2), epsilon);
        assertEquals(240.28, autoRegressiveIntegratedMovingAverage.eval(3), epsilon);
    }

    @Test
    public void checkHistoryDataSetCalculation() {
        int inputDataLength = inputDataSet.length;
        double[] expectedDataSet = {
                255.49, 255.02, 254.56, 254.10, 253.64, 253.18, 252.72, 252.25, 251.79,
                251.33, 250.87, 250.41, 249.95, 249.48, 249.02, 248.56, 248.10, 247.64,
                247.18, 246.71};

        double[] calculatedDataSet = regressionHandler.calculateHistoryDataSetOfRegression(simpleLinearRegression,
                inputDataLength);

        assertArrayEquals(expectedDataSet, calculatedDataSet, 0.01);
    }

    @Test
    public void checkFutureDataSetCalculation() {
        double[] expectedDataSet = {
                246.25, 245.79, 245.32, 244.86, 244.40};

        double[] calculatedDataSet = regressionHandler.calculateFutureDataSetOfRegression(simpleLinearRegression,
                predictionPoint);

        assertArrayEquals(expectedDataSet, calculatedDataSet, 0.01);
    }

    @Test
    public void checkDatasetCalculationForSimpleLinearReg() {
        int inputDataLength = inputDataSet.length;
        double[] expectedDataSet = {
                255.49, 255.02, 254.56, 254.10, 253.64, 253.18, 252.72, 252.25, 251.79,
                251.33, 250.87, 250.41, 249.95, 249.48, 249.02, 248.56, 248.10, 247.64,
                247.18, 246.71, 246.25, 245.79, 245.32, 244.86, 244.40};

        double[] calculatedDataSet = regressionHandler.calculateDataSetForRegression(simpleLinearRegression, inputDataLength,
                predictionPoint);

        assertArrayEquals(expectedDataSet, calculatedDataSet, 0.01);
    }

    @Test
    public void checkDatasetCalculationForAutoRegressionFamily() {
        int inputDataLength = inputDataSet.length;
        double[] expectedDataSet = {245.66, 246.76, 247.58, 248.23, 248.77};

        double[] calculatedDataSet = regressionHandler.calculateDataSetForRegression(autoRegression, inputDataLength,
                predictionPoint);

        assertArrayEquals(expectedDataSet, calculatedDataSet, 0.01);
    }
}