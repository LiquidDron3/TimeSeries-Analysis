package org.dataAnalysis;

import euclides.math.timeseries.*;
import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.html.*;

import java.util.Arrays;

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

    public static double[] getInputDataFromHtmlDocument(HTMLDocument document) {
        HTMLInputElement rawInputData = document.getElementById("fileContent").cast();
        return Arrays.stream(convertLinesTo2dArray(rawInputData.getValue()))
                .mapToDouble(arr -> Double.parseDouble(arr[1].replace(',', '.')))
                .toArray();
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

    private static HTMLCanvasElement getCanvasForRegression(HTMLDocument document, String regressionType) {
        try {
            return (HTMLCanvasElement) document.getElementById(regressionType + "Canvas");
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(Messages.ERROR_UNKNOWN_REGRESSION);
        }
    }

    private static void processAndVisualizeRegression(HTMLDocument document) {
        HTMLFormElement form = document.getElementById("regression_selector").cast();

        double[] inputData = getInputDataFromHtmlDocument(document);
        RegressionHandler regressionHandler = new RegressionHandler(document);
        String[] selectedRegressions = getSelectedRegressions(form);


        for (String regression : selectedRegressions) {
            DrawDataSet drawChart = new DrawDataSet();
            Model reg = regressionHandler.createAndInitRegression(regression, inputData);
            double[] calculatedData = regressionHandler.createDataSetForRegression(reg, inputData.length,
                    predictionPoint);
            HTMLCanvasElement canvas = getCanvasForRegression(document, regression);
            drawChart.drawGraphWithCalculation(canvas, calculatedData, inputData);
            drawChart.addResizeEventListener(canvas, calculatedData, inputData);
        }
    }
}
