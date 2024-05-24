package org.dataAnalysis;

import euclides.math.timeseries.*;
import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.html.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.dataAnalysis.Messages.*;

public final class Client {

    private static int predictionPoint;
    public static void main(String[] args) {
        init();
    }

    private static void init() {
        HTMLDocument document = Window.current().getDocument();
        HTMLInputElement startButton = document.getElementById("startButton").cast();
        HTMLInputElement predictionPointInput = document.getElementById("PredictionPoint").cast();


        startButton.addEventListener("click", evt -> {

            predictionPoint = Integer.parseInt(predictionPointInput.getValue()); //need null error catch
            processAndVisualizeRegression(document);

        });
    }

    public static String[][] convertLinesTo2dArray(String content) {

        if(content.isEmpty()) {
            throw new IllegalArgumentException(Messages.ERROR_EMPTY_ARRAY);
        }

        String[] lines = content.split("\\r?\\n");
        String[][] array = new String[lines.length][2];

        for (int i = 0; i < lines.length; i++) {
            String[] parts = lines[i].split(";");

            if(parts.length != 2) {
                throw new IllegalArgumentException(Messages.ERROR_2D_ARRAY);
            }
            array[i][0] = parts[0].trim();
            array[i][1] = parts[1].trim();
        }
        return array;
    }

    private static String[] getSelectedRegressions(HTMLFormElement form) {
        HTMLCollection inputs = form.getElementsByTagName("input").cast();
        int count = 0;
        for (int i = 0; i < inputs.getLength(); i++) {
            HTMLInputElement input = (HTMLInputElement) inputs.item(i);
            if (input.getType().equals("checkbox") && input.isChecked()) {
                count++;
            }
        }
        String[] selectedCheckboxes = new String[count];
        int index = 0;
        for (int i = 0; i < inputs.getLength(); i++) {
            HTMLInputElement input = (HTMLInputElement) inputs.item(i);
            if (input.getType().equals("checkbox") && input.isChecked()) {
                selectedCheckboxes[index++] = input.getId();
            }
        }
        return selectedCheckboxes;
    }

    private static void processAndVisualizeRegression(HTMLDocument document) {
        HTMLFormElement form = document.getElementById("regression_selector").cast();
        HTMLInputElement userContent = document.getElementById("fileContent").cast();

        String[] selectedRegressions = getSelectedRegressions(form);
        double[] inputData = Arrays.stream(convertLinesTo2dArray(userContent.getValue()))
                .mapToDouble(arr -> Double.parseDouble(arr[1].replace(',', '.')))
                .toArray();


        for (String regression : selectedRegressions) {
            Model reg = createAndInitRegression(document, regression, inputData);
            int dataSetLength = predictionPoint + inputData.length;
            if (isInstanceOfAutoRegressionFamily(reg)) {
                dataSetLength = predictionPoint;
            }
            double[] calculatedData = calculateRegressionDataSet(reg, dataSetLength);
            HTMLCanvasElement canvas = getCanvasForRegression(document, regression);
            //drawGraph(canvas, calculatedData, inputData); TODO: implement it
        }

    }

    private static HTMLCanvasElement getCanvasForRegression(HTMLDocument document, String regressionType) {
        try {
            return (HTMLCanvasElement) document.getElementById(regressionType + "Canvas");
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(ERROR_UNKNOWN_REGRESSION);
        }
    }

    private static Model createAndInitRegression(HTMLDocument document, String selectedRegression, double[] inputDataSet) {
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
                int[] argsPolynomial = getRegressionArgs(document, selectedRegression);
                PolynomialRegression polynomialRegression = new PolynomialRegression(argsPolynomial[0]);
                polynomialRegression.init(inputDataSet);
                return polynomialRegression;
            case "autoRegression":
                int[] argsAuto = getRegressionArgs(document, selectedRegression);
                AutoRegression regressionAR = new AutoRegression(argsAuto[0]);
                regressionAR.init(inputDataSet);
                return regressionAR;
            case "autoRegressionMovingAverage":
                int[] argsARMA = getRegressionArgs(document, selectedRegression);
                AutoRegressiveMovingAverage regressionARMA = new AutoRegressiveMovingAverage(argsARMA[0], argsARMA[1]);
                regressionARMA.init(inputDataSet);
                return regressionARMA;
            case "autoRegressionIntegratedAverage":
                int[] argsARIMA = getRegressionArgs(document, selectedRegression);
                AutoRegressiveIntegratedMovingAverage regressionARIMA = new AutoRegressiveIntegratedMovingAverage(argsARIMA[0], argsARIMA[1], argsARIMA[2]);
                regressionARIMA.init(inputDataSet);
                return regressionARIMA;
            default:
                throw new IllegalArgumentException(ERROR_UNKNOWN_REGRESSION);
        }
    }

    private static int[] getRegressionArgs(HTMLDocument form, String selectedRegression) {
        List<String> inputValues = getRegressionArgsBasedOnType(selectedRegression);
        int[] values = new int[inputValues.size()];
        for (int i = 0; i < inputValues.size(); i++) {
            HTMLInputElement input = form.getElementById(selectedRegression + inputValues.get(i)).cast();
            if (input != null && !input.getValue().isEmpty()) {
                values[i] = Integer.parseInt(input.getValue());
            }
        }
        return values;
    }

    private static List<String> getRegressionArgsBasedOnType(String selectedRegression) {
        switch (selectedRegression) {
            case "simpleLinearRegression":
            case "constRegression":
                return Collections.emptyList();
            case "polynomialRegression":
                return Collections.singletonList("Degree");
            case "autoRegression":
                return Collections.singletonList("Degree");
            case "autoRegressionMovingAverage":
                return Arrays.asList("Degree", "MovingAverage");
            case "autoRegressionIntegratedAverage":
                return Arrays.asList("Degree", "MovingAverage", "Differencing");
            default:
                throw new IllegalArgumentException(ERROR_UNKNOWN_REGRESSION);
        }
    }

    /**
     * This method calculates the regression data set based on the provided regression model and data set length.
     *
     * @param regression The regression model to be used for the calculation.
     * @param dataSetLength The length of the data set to be calculated.
     * @return A double array containing the calculated data set.
     * @throws IllegalArgumentException If the provided regression model is not an instance of a supported regression model.
     */
    private static double[] calculateRegressionDataSet(Model regression, int dataSetLength) {

        double[] calculatedData = new double[dataSetLength];

        if(isInstanceOfSupportedRegressions(regression)) {
            if (isInstanceOfAutoRegressionFamily(regression)) {

                for (int i = 0; i <= dataSetLength; i++) {
                    calculatedData[i] = regression.eval(i);
                }
            } else {

                for (int i = 0; i < dataSetLength; i++) {
                    int evalIndex = -(dataSetLength - i);
                    calculatedData[i] = regression.eval(evalIndex);
                }
            }

            return calculatedData;
        } else {
            throw new IllegalArgumentException(ERROR_UNKNOWN_REGRESSION);
        }
    }

    /**
     * Checks if the regression model is an instance of AutoRegression, AutoRegressiveMovingAverage
     * or AutoRegressiveIntegratedMovingAverage.
     * @param regression the regression model
     * @return true if the regression model is an instance of AutoRegression, AutoRegressiveMovingAverage
     * or AutoRegressiveIntegratedMovingAverage, false otherwise
     */
    static boolean isInstanceOfAutoRegressionFamily(Model regression) {
        return regression instanceof AutoRegression ||
                regression instanceof AutoRegressiveMovingAverage ||
                regression instanceof AutoRegressiveIntegratedMovingAverage;
    }

    /**
     * Checks if the regression model is an instance of a known regression.
     * @param regression the regression model
     * @return true if the regression model is an instance of a known regression, false otherwise
     */
    static boolean isInstanceOfSupportedRegressions(Model regression) {
        return regression instanceof AutoRegression ||
                regression instanceof AutoRegressiveMovingAverage ||
                regression instanceof AutoRegressiveIntegratedMovingAverage ||
                regression instanceof SimpleLinearRegression ||
                regression instanceof ConstantRegression ||
                regression instanceof PolynomialRegression;
    }

}
