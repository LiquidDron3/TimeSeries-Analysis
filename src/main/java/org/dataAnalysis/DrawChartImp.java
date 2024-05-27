package org.dataAnalysis;

import org.teavm.jso.browser.Window;
import org.teavm.jso.canvas.CanvasRenderingContext2D;
import org.teavm.jso.dom.html.HTMLCanvasElement;

import java.util.Arrays;

public class DrawChartImp implements DrawChart {

    private double getMinOfDataset(double[] data) {
        if(data.length == 0) {
            throw new IllegalArgumentException(Messages.ERROR_EMPTY_DATASET);
        }
        return Arrays.stream(data).min().getAsDouble();
    }

    private double getMinOfDataset(double[] data1, double[] data2) {
        return Math.min(getMinOfDataset(data1), getMinOfDataset(data2));
    }

    private double getMaxOfDataset(double[] data) {
        if(data.length == 0) {
            throw new IllegalArgumentException(Messages.ERROR_EMPTY_DATASET);
        }
        return Arrays.stream(data).max().getAsDouble();
    }

    private double getMaxOfDataset(double[] data1, double[] data2) {
        return Math.max(getMaxOfDataset(data1), getMaxOfDataset(data2));
    }

    private int getMaxLengthOfDatasets(double[] data1, double[] data2) {
        return Math.max(data1.length, data2.length);
    }

    private void resizeCanvasSize(HTMLCanvasElement canvas) {
        //TODO: Find better ration between width and height
        int currentWindowWidth = Window.current().getInnerWidth();
        int calculatedHeight = (int) (currentWindowWidth * 0.6 * 9 / 16);
        canvas.setHeight(calculatedHeight);
        canvas.setWidth((int) (currentWindowWidth * 0.6));
    }

    @Override
    public void addResizeEventListener(HTMLCanvasElement canvas, double[] data1, double[] data2) {
        Window.current().addEventListener("resize", event -> {
            drawGraphWithCalculation(canvas, data1, data2);
        });
    }

    @Override
    public void drawGraphWithCalculation(HTMLCanvasElement canvas, double[] data1, double[] data2) {
        CanvasRenderingContext2D context = canvas.getContext("2d").cast();

        double min = getMinOfDataset(data1, data2);
        double max = getMaxOfDataset(data1, data2);
        int maxLength = getMaxLengthOfDatasets(data1, data2);

        // TODO: ADD AXIS AS canvasInit()
        context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        resizeCanvasSize(canvas);

        drawDataset(context, data1, "blue", min, max, maxLength);
        drawDataset(context, data2, "red", min, max, maxLength);
    }


    public void drawDataset(CanvasRenderingContext2D context, double[] data, String color, Double min, Double max, int maxLength) {
        context.beginPath();
        context.setStrokeStyle(color);

        int width = context.getCanvas().getWidth();
        int height = context.getCanvas().getHeight();

        for (int i = 0; i < data.length; i++) {
            double doubleValue = data[i];
            double x = i * (width / (double) (maxLength - 1)); // Calculate x-coordinate based on the maximum length
            double y = height - ((doubleValue - min) / (max - min) * height); // Calculate y-coordinate

            if (i == 0) {
                context.moveTo(x, y);
            } else {
                context.lineTo(x, y);
            }
        }
        context.stroke();
    }
}