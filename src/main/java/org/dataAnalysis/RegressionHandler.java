package org.dataAnalysis;

import euclides.math.timeseries.*;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLInputElement;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RegressionHandler {
    private static HTMLDocument document;


    public RegressionHandler(HTMLDocument document) {
        RegressionHandler.document = document;
    }

    public Model createAndInitRegression(String selectedRegression, double[] inputDataSet) {
        switch (selectedRegression) {
            case "simpleLinearRegression":
                SimpleLinearRegression simpleLinearRegression = new SimpleLinearRegression();
                simpleLinearRegression.init(inputDataSet);
                return simpleLinearRegression;
            case "constRegression":
                ConstantRegression constantRegression = new ConstantRegression();
                constantRegression.init(inputDataSet);
                return constantRegression;
            case "polynomialRegression":
                int[] argsPolynomial = getArgsOfRegression(document, selectedRegression);
                PolynomialRegression polynomialRegression = new PolynomialRegression(argsPolynomial[0]);
                polynomialRegression.init(inputDataSet);
                return polynomialRegression;
            case "autoRegression":
                int[] argsAuto = getArgsOfRegression(document, selectedRegression);
                AutoRegression regressionAR = new AutoRegression(argsAuto[0]);
                regressionAR.init(inputDataSet);
                return regressionAR;
            case "autoRegressionMovingAverage":
                int[] argsARMA = getArgsOfRegression(document, selectedRegression);
                AutoRegressiveMovingAverage regressionARMA = new AutoRegressiveMovingAverage(argsARMA[0], argsARMA[1]);
                regressionARMA.init(inputDataSet);
                return regressionARMA;
            case "autoRegressionIntegratedAverage":
                int[] argsARIMA = getArgsOfRegression(document, selectedRegression);
                AutoRegressiveIntegratedMovingAverage regressionARIMA = new AutoRegressiveIntegratedMovingAverage(argsARIMA[0], argsARIMA[1], argsARIMA[2]);
                regressionARIMA.init(inputDataSet);
                return regressionARIMA;
            default:
                throw new IllegalArgumentException(Messages.ERROR_UNKNOWN_REGRESSION);
        }
    }

    private int[] getArgsOfRegression(HTMLDocument form, String selectedRegression) {
        List<String> inputValues = getArgsNameOfRegression(selectedRegression);
        int[] values = new int[inputValues.size()];
        for (int i = 0; i < inputValues.size(); i++) {
            HTMLInputElement input = form.getElementById(selectedRegression + inputValues.get(i)).cast();
            if (input != null && !input.getValue().isEmpty()) {
                values[i] = Integer.parseInt(input.getValue());
            }
        }
        return values;
    }

    public List<String> getArgsNameOfRegression(String selectedRegression) {
        switch (selectedRegression) {
            case "simpleLinearRegression":
            case "constRegression":
                return Collections.emptyList();
            case "polynomialRegression":
                return Collections.singletonList("Degree");
            case "autoRegression":
                return Collections.singletonList("PValue");
            case "autoRegressionMovingAverage":
                return Arrays.asList("PValue", "MA");
            case "autoRegressionIntegratedAverage":
                return Arrays.asList("PValue", "DifferencingOrder", "MA");
            default:
                throw new IllegalArgumentException(Messages.ERROR_UNKNOWN_REGRESSION);
        }
    }

    public double[] createDataSetForRegression(Model regression, int sizeOfInputData, int predictionPoint) {
        if(!isInstanceSupportedRegression(regression)) {
            throw new IllegalArgumentException(Messages.ERROR_UNKNOWN_REGRESSION);
        }

        if (isInstanceAutoRegressionType(regression)) {
            return calculateFutureDataSetOfRegression(regression, predictionPoint);
        } else {
            double[] historyDataSet;
            double[] futureDataSet;
            double[] calculatedDataSet = new double[sizeOfInputData + predictionPoint];

            historyDataSet = calculateHistoryDataSetOfRegression(regression, sizeOfInputData);
            futureDataSet = calculateFutureDataSetOfRegression(regression, predictionPoint);

            System.arraycopy(historyDataSet, 0, calculatedDataSet, 0, historyDataSet.length);
            System.arraycopy(futureDataSet, 0, calculatedDataSet, historyDataSet.length, futureDataSet.length);

            return calculatedDataSet;
        }
    }

    protected double[] calculateHistoryDataSetOfRegression(Model regression, int lengthOfInputDataSet) {
        if(isInstanceAutoRegressionType(regression)) {
            throw new IllegalArgumentException(Messages.ERROR_UNKNOWN_REGRESSION); //add new msg
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
}
