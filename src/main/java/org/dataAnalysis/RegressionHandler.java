package org.dataAnalysis;

import euclides.math.timeseries.*;
import org.teavm.jso.dom.html.HTMLInputElement;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RegressionHandler {
    HtmlHandler htmlHandler;

    public RegressionHandler(HtmlHandler htmlHandler) {
        this.htmlHandler = htmlHandler;
    }

    public Model createAndInitRegression(String selectedRegression, double[] inputDataSet) {
        double regression;
        switch (selectedRegression) {
            case "simpleLinearRegression" -> {
                SimpleLinearRegression simpleLinearRegression = new SimpleLinearRegression();
                regression = simpleLinearRegression.init(inputDataSet);
                htmlHandler.setCoefficientOutputElement(selectedRegression, regression);
                return simpleLinearRegression;
            }
            case "constRegression" -> {
                ConstantRegression constantRegression = new ConstantRegression();
                regression = constantRegression.init(inputDataSet);
                htmlHandler.setCoefficientOutputElement(selectedRegression, regression);
                return constantRegression;
            }
            case "polynomialRegression" -> {
                int[] argsPolynomial = getRegressionParameters(selectedRegression);
                PolynomialRegression polynomialRegression = new PolynomialRegression(argsPolynomial[0]);
                regression = polynomialRegression.init(inputDataSet);
                htmlHandler.setCoefficientOutputElement(selectedRegression, regression);
                return polynomialRegression;
            }
            case "autoRegression" -> {
                int[] argsAuto = getRegressionParameters(selectedRegression);
                AutoRegression regressionAR = new AutoRegression(argsAuto[0]);
                regression = regressionAR.init(inputDataSet);
                htmlHandler.setCoefficientOutputElement(selectedRegression, regression);
                return regressionAR;
            }
            case "autoRegressionMovingAverage" -> {
                int[] argsARMA = getRegressionParameters(selectedRegression);
                AutoRegressiveMovingAverage regressionARMA = new AutoRegressiveMovingAverage(argsARMA[0], argsARMA[1]);
                regression = regressionARMA.init(inputDataSet);
                htmlHandler.setCoefficientOutputElement(selectedRegression, regression);
                return regressionARMA;
            }
            case "autoRegressionIntegratedAverage" -> {
                int[] argsARIMA = getRegressionParameters(selectedRegression);
                AutoRegressiveIntegratedMovingAverage regressionARIMA = new AutoRegressiveIntegratedMovingAverage(argsARIMA[0], argsARIMA[1], argsARIMA[2]);
                regression = regressionARIMA.init(inputDataSet);
                htmlHandler.setCoefficientOutputElement(selectedRegression, regression);
                return regressionARIMA;
            }
            default -> throw new IllegalArgumentException(Messages.ERROR_UNKNOWN_REGRESSION);
        }
    }

    private int[] getRegressionParameters(String selectedRegression) {
        List<String> inputNames = switch (selectedRegression) {
            case "polynomialRegression" -> Collections.singletonList("Degree");
            case "autoRegression" -> Collections.singletonList("PValue");
            case "autoRegressionMovingAverage" -> Arrays.asList("PValue", "MA");
            case "autoRegressionIntegratedAverage" -> Arrays.asList("PValue", "DifferencingOrder", "MA");
            default -> throw new IllegalArgumentException(Messages.ERROR_UNKNOWN_REGRESSION);
        };

        int[] values = new int[inputNames.size()];
        for (int i = 0; i < inputNames.size(); i++) {
            HTMLInputElement input = htmlHandler.getRegressionParameterElement(selectedRegression, inputNames.get(i));
            if (input == null || input.getValue().isEmpty()) {
                throw new IllegalArgumentException(Messages.ERROR_EMPTY_PARAMETER);
            }
            values[i] = Integer.parseInt(input.getValue());
        }
        return values;
    }

    public boolean isInstanceAutoRegressionType(Model regression) {
        return regression instanceof AutoRegression ||
                regression instanceof AutoRegressiveMovingAverage ||
                regression instanceof AutoRegressiveIntegratedMovingAverage;
    }

    public boolean isInstanceSupportedRegression(Model regression) {
        return regression instanceof AutoRegression ||
                regression instanceof AutoRegressiveMovingAverage ||
                regression instanceof AutoRegressiveIntegratedMovingAverage ||
                regression instanceof SimpleLinearRegression ||
                regression instanceof ConstantRegression ||
                regression instanceof PolynomialRegression;
    }

    public double[] calculateDataSetForRegression(Model regression, int sizeOfInputData, int predictionPoint) {
        if(!isInstanceSupportedRegression(regression)) {
            throw new IllegalArgumentException(Messages.ERROR_UNKNOWN_REGRESSION);
        }

        if (isInstanceAutoRegressionType(regression)) {
            return calculateFutureDataSetOfRegression(regression, predictionPoint);
        } else {
            double[] calculatedDataSet = new double[sizeOfInputData + predictionPoint];
            double[] historyDataSet = calculateHistoryDataSetOfRegression(regression, sizeOfInputData);
            double[] futureDataSet = calculateFutureDataSetOfRegression(regression, predictionPoint);


            System.arraycopy(historyDataSet, 0, calculatedDataSet, 0, historyDataSet.length);
            System.arraycopy(futureDataSet, 0, calculatedDataSet, historyDataSet.length, futureDataSet.length);

            return calculatedDataSet;
        }
    }

    protected double[] calculateHistoryDataSetOfRegression(Model regression, int lengthOfInputDataSet) {
        if(isInstanceAutoRegressionType(regression)) {
            throw new IllegalArgumentException(Messages.ERROR_UNKNOWN_REGRESSION);
        }
        if(lengthOfInputDataSet < 1) {
            throw new IllegalArgumentException(Messages.ERROR_NEGATIVE_NO_INPUT_DATA);
        }

        double[] dataSet = new double[lengthOfInputDataSet];
        for(int i = 0; i < lengthOfInputDataSet; i++) {
            int index = -(lengthOfInputDataSet - i - 1);
            dataSet[i] = regression.eval(index);
        }
        return dataSet;
    }

    protected double[] calculateFutureDataSetOfRegression(Model regression, int predictionPoint) {
        double[] dataSet = new double[predictionPoint];
        for(int i = 1; i <= predictionPoint; i++) {
            dataSet[i - 1] = regression.eval(i);
        }
        return dataSet;
    }
}
