package org.dataAnalysis;

import euclides.math.timeseries.*;
import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.html.*;

public final class Client {
    private static DataPreparationHandler dataPreparationHandler;
    private static HtmlHandler htmlHandler;
    private static RegressionHandler regressionHandler;
    private static DrawDataSetHandler drawDataSethandler;


    public static void main(String[] args) {
        startProcess();
    }

    private static void startProcess() {
        HTMLDocument doc = Window.current().getDocument();
        initHandlers(doc);
        initStartButton();
    }

    private static void initHandlers(HTMLDocument document) {
        dataPreparationHandler = new DataPreparationHandler();
        htmlHandler = new HtmlHandler(document);
        regressionHandler = new RegressionHandler(htmlHandler);
        drawDataSethandler = new DrawDataSetHandler(dataPreparationHandler);
    }

    private static void initStartButton() {
        HTMLInputElement startButton = htmlHandler.getStartButtonElement();
        startButton.addEventListener("click", evt -> processAndVisualizeRegression());
    }

    private static double[] getRawInputData() {
        HTMLInputElement getInputDataElement = htmlHandler.getInputDataElement();
        String inputData = getInputDataElement.getValue();
        return dataPreparationHandler.prepareRawInputData(inputData);
    }

    private static void processAndVisualizeRegression() {
        int predictionPoint = htmlHandler.getPredictionPointFromHTML();
        double[] rawInputData = getRawInputData();
        String[] selectedRegressions = htmlHandler.getSelectedRegressionsFromHTML();

        for (String regression : selectedRegressions) {
            Model reg = regressionHandler.createAndInitRegression(regression, rawInputData);
            HTMLCanvasElement canvas = htmlHandler.getCanvasElement(regression);
            double[] calculatedData = regressionHandler.calculateDataSetForRegression(reg, rawInputData.length,
                    predictionPoint);
            double[][] preparedDataSet = dataPreparationHandler.prepareDataSet(rawInputData, calculatedData, predictionPoint);
            drawDataSethandler.prepareAndDrawDataSetOnCanvas(canvas, preparedDataSet);
            double roundedOutputValue = Math.round(calculatedData[predictionPoint-1] * 1000.0) / 1000.0;
            htmlHandler.setEvaluationOutputElement(regression, roundedOutputValue);
        }
        htmlHandler.scrollToCanvasContainerWithTopOffset();
    }
}