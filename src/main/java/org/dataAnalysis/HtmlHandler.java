package org.dataAnalysis;

import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.html.*;

public class HtmlHandler {
    public static HTMLDocument doc;

    HtmlHandler(HTMLDocument doc) {
        HtmlHandler.doc = doc;
    }

    public HTMLCanvasElement getCanvasElement(String regressionType) {
        try {
            return (HTMLCanvasElement) doc.getElementById(regressionType + "Canvas");
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(Messages.ERROR_UNKNOWN_REGRESSION);
        }
    }

    public void setEvaluationOutputElement(String regression, double value) {
        HTMLElement outputElement;
        try {
            outputElement = doc.getElementById(regression + "EvaluationForPrediction");
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(Messages.ERROR_UNKNOWN_REGRESSION);
        }
        outputElement.setTextContent(String.valueOf(value));
    }

    public void setCoefficientOutputElement(String regression, double value) {
        HTMLElement outputElement;
        try {
            outputElement = doc.getElementById(regression + "Coefficient");
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(Messages.ERROR_UNKNOWN_REGRESSION);
        }
        value = Math.round(value * 1000.0) / 1000.0;
        outputElement.setTextContent(String.valueOf(value));
    }

    public HTMLInputElement getStartButtonElement() {
        return doc.getElementById("startButton").cast();
    }

    public HTMLInputElement getRegressionParameterElement(String selectedRegression, String parameterName) {
        return doc.getElementById(selectedRegression + parameterName).cast();
    }

    public void scrollToCanvasContainerWithTopOffset() {
        int topOffset = 50;
        HTMLElement canvasContainer = doc.getElementById("canvas-container");
        int top = canvasContainer.getOffsetTop();
        int left = canvasContainer.getOffsetLeft();
        Window.current().scrollTo(left, top - topOffset);
    }

    public HTMLInputElement getInputDataElement() {
        return doc.getElementById("temporary-data-storage").cast();
    }

    public String[] getSelectedRegressionsFromHTML() {
        HTMLFormElement form = doc.getElementById("regression_selector").cast();
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

    public int getPredictionPointFromHTML() {
        HTMLInputElement predictionPointInput = doc.getElementById("PredictionPoint").cast();
        int predictionPoint = Integer.parseInt(predictionPointInput.getValue());
        if(predictionPoint < 0) {
            throw new IllegalArgumentException(Messages.ERROR_NEGATIVE_PREDICTION_POINT);
        }
        return predictionPoint;
    }
}
