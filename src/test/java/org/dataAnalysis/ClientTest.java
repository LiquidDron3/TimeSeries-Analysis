package org.dataAnalysis;

import euclides.math.timeseries.*;
import junit.framework.TestCase;
import org.junit.Test;

public class ClientTest extends TestCase {

    public void testInstanceOfAutoRegressionFamily() {
            AutoRegression autoRegression = new AutoRegression(1);
            AutoRegressiveMovingAverage autoRegressiveMovingAverage = new AutoRegressiveMovingAverage(1, 1);
            AutoRegressiveIntegratedMovingAverage autoRegressiveIntegratedMovingAverage = new AutoRegressiveIntegratedMovingAverage(1, 1, 1);
            SimpleLinearRegression simpleLinearRegression = new SimpleLinearRegression();

            assertTrue(Client.isInstanceOfAutoRegressionFamily(autoRegression));
            assertTrue(Client.isInstanceOfAutoRegressionFamily(autoRegressiveMovingAverage));
            assertTrue(Client.isInstanceOfAutoRegressionFamily(autoRegressiveIntegratedMovingAverage));
            assertFalse(Client.isInstanceOfAutoRegressionFamily(simpleLinearRegression));
        }

    public void testIsInstanceOfSupportedRegressions() {
        AutoRegression autoRegression = new AutoRegression(1);
        AutoRegressiveMovingAverage autoRegressiveMovingAverage = new AutoRegressiveMovingAverage(1, 1);
        AutoRegressiveIntegratedMovingAverage autoRegressiveIntegratedMovingAverage = new AutoRegressiveIntegratedMovingAverage(1, 1, 1);
        SimpleLinearRegression simpleLinearRegression = new SimpleLinearRegression();
        ConstantRegression constantRegression = new ConstantRegression();
        PolynomialRegression polynomialRegression = new PolynomialRegression(2);


        assertTrue(Client.isInstanceOfSupportedRegressions(autoRegression));
        assertTrue(Client.isInstanceOfSupportedRegressions(autoRegressiveMovingAverage));
        assertTrue(Client.isInstanceOfSupportedRegressions(autoRegressiveIntegratedMovingAverage));
        assertTrue(Client.isInstanceOfSupportedRegressions(simpleLinearRegression));
        assertTrue(Client.isInstanceOfSupportedRegressions(constantRegression));
        assertTrue(Client.isInstanceOfSupportedRegressions(polynomialRegression));
    }

}