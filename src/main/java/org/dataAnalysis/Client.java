package org.dataAnalysis;

import euclides.math.timeseries.*;
import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.html.*;

public final class Client {
    public static void main(String[] args) {
        startProcess();
    }

    private static void startProcess() {
        HTMLDocument doc = Window.current().getDocument();
        initHandlers(doc);
        initStartButton();
    }

    private static void initHandlers(HTMLDocument document) {
        ErrorNotificationHandler.initDocument(document);
        HtmlHandler.initDocument(document);
    }

    private static void initStartButton() {
        HTMLInputElement startButton = HtmlHandler.getStartButtonElement();
        startButton.addEventListener("click", evt -> processAndVisualizeRegression());
    }

    private static double[] getRawInputData() {
        HTMLInputElement getInputDataElement = HtmlHandler.getInputDataElement();
        String inputData = getInputDataElement.getValue();
        return DataPreparationHandler.prepareRawInputData(inputData);
    }

    private static void processAndVisualizeRegression() {
        int predictionPoint = HtmlHandler.getPredictionPointFromHTML();
        double[] rawInputData = getRawInputData();
        String[] selectedRegressions = HtmlHandler.getSelectedRegressionsFromHTML();

        for (String regression : selectedRegressions) {
            Model reg = RegressionHandler.createAndInitRegression(regression, rawInputData);
            HTMLCanvasElement canvas = HtmlHandler.getCanvasElement(regression);
            double[] calculatedData = RegressionHandler.calculateDataSetForRegression(reg, rawInputData.length,
                    predictionPoint);
            double[][] preparedDataSet = DataPreparationHandler.prepareDataSet(rawInputData, calculatedData, predictionPoint);
            DrawDataSetHandler.prepareAndDrawDataSetOnCanvas(canvas, preparedDataSet);
            double roundedOutputValue = Math.round(calculatedData[predictionPoint-1] * 1000.0) / 1000.0;
            HtmlHandler.setEvaluationOutputElement(regression, roundedOutputValue);
        }
        HtmlHandler.scrollToCanvasContainerWithTopOffset();
        ErrorNotificationHandler.displaySuccessMessage();
    }
}