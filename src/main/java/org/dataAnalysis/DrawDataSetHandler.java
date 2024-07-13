package org.dataAnalysis;

import org.teavm.jso.browser.Window;
import org.teavm.jso.canvas.CanvasRenderingContext2D;
import org.teavm.jso.dom.html.HTMLCanvasElement;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DrawDataSetHandler {
    private static boolean isPanning = false;
    private static double lastMouseX = 0;
    private static boolean redrawScheduled = false;
    private static final Map<String, double[][]> inputDataSetsMap = new HashMap<>();
    private static final Map<String, Integer> panningMap = new HashMap<>();
    private static final Map<String, Double> zoomMap = new HashMap<>();

    private static double getMinOfDataset(double[] columnOne, double[] columnTwo) {
        if(columnOne.length == 0 || columnTwo.length == 0) {
            throw new IllegalArgumentException(Messages.ERROR_EMPTY_DATASET);
        }

        double minOfFirstColumn = Arrays.stream(columnOne)
                .filter(d -> !Double.isNaN(d))
                .min()
                .orElse(Double.NaN);

        double minOfSecondColumn = Arrays.stream(columnTwo)
                .filter(d -> !Double.isNaN(d))
                .min()
                .orElse(Double.NaN);

        if (Double.isNaN(minOfFirstColumn) && Double.isNaN(minOfSecondColumn)) {
            return Double.NaN;
        } else if (Double.isNaN(minOfFirstColumn)) {
            return minOfSecondColumn;
        } else if (Double.isNaN(minOfSecondColumn)) {
            return minOfFirstColumn;
        } else {
            return Math.min(minOfFirstColumn, minOfSecondColumn);
        }
    }

    private static double getMaxOfDataset(double[] columnOne, double[] columnTwo) {
        if(columnOne.length == 0 || columnTwo.length == 0) {
            throw new IllegalArgumentException(Messages.ERROR_EMPTY_DATASET);
        }
        double maxOfFirstColumn = Arrays.stream(columnOne)
                .filter(d -> !Double.isNaN(d))
                .max()
                .orElse(Double.NaN);

        double maxOfSecondColumn = Arrays.stream(columnTwo)
                .filter(d -> !Double.isNaN(d))
                .max()
                .orElse(Double.NaN);

        if (Double.isNaN(maxOfFirstColumn) && Double.isNaN(maxOfSecondColumn)) {
            return Double.NaN;
        } else if (Double.isNaN(maxOfFirstColumn)) {
            return maxOfSecondColumn;
        } else if (Double.isNaN(maxOfSecondColumn)) {
            return maxOfFirstColumn;
        } else {
            return Math.max(maxOfFirstColumn, maxOfSecondColumn);
        }
    }

    public static void resetAndInitializeCanvasEventListeners(HTMLCanvasElement canvas) {
        panningMap.put(canvas.getId(), 0);
        zoomMap.put(canvas.getId(), 1.0);
        addPanningEventListeners(canvas);
        addZoomEventListeners(canvas);
        addResizeEventListener(canvas);
    }

    private static CanvasRenderingContext2D prepareAndReturnCanvasContext(HTMLCanvasElement canvas) {
        CanvasRenderingContext2D ctx = canvas.getContext("2d").cast();
        int currentWindowWidth = Window.current().getInnerWidth();
        int calculatedHeight = (int) (currentWindowWidth * 0.55 * 9 / 16);
        canvas.setHeight(calculatedHeight);
        canvas.setWidth((int) (currentWindowWidth * 0.55));
        ctx.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        return ctx;
    }

    public static void prepareAndDrawDataSetOnCanvas(HTMLCanvasElement canvas, double[][] dataSet) {
        inputDataSetsMap.put(canvas.getId(), dataSet);
        int standardDisplayedDataPoints = 200;
        CanvasRenderingContext2D ctx = prepareAndReturnCanvasContext(canvas);
        resetAndInitializeCanvasEventListeners(canvas);
        double zoomLevel = zoomMap.get(canvas.getId());
        int[] dataSetIndices =  DataPreparationHandler.getDataSetIndicesWithZoom(standardDisplayedDataPoints, dataSet, zoomLevel);
        double[][] extractDataFromDataSet = DataPreparationHandler.extractSubArray(dataSet, dataSetIndices[0], dataSetIndices[1]);
        drawTwoDataSetsOnCanvas(ctx, extractDataFromDataSet[0], extractDataFromDataSet[1], extractDataFromDataSet[2]);
    }

    private static void addPanningEventListeners(HTMLCanvasElement canvas) {
        canvas.addEventListener("mousedown", event -> {
            MouseEventProperty eventProperty = event.cast();
            lastMouseX = eventProperty.getClientX();
            isPanning = true;
        });

        canvas.addEventListener("mousemove", event -> {
            if (isPanning) {
                MouseEventProperty eventProperty = event.cast();
                double clientX = eventProperty.getClientX();
                double deltaX = clientX - lastMouseX;
                int panning = panningMap.get(canvas.getId());
                int threshold = 5;
                if (Math.abs(deltaX) >= threshold) {
                    panning -= (int)deltaX / threshold;
                    panningMap.put(canvas.getId(), panning);
                    if (!redrawScheduled) {
                        redrawScheduled = true;
                        Window.setTimeout(() -> {
                            updateCanvas(canvas);
                            redrawScheduled = false;
                        }, 10);
                    }
                    lastMouseX = clientX;
                }
            }
        });

        canvas.addEventListener("mouseup", event -> {
            isPanning = false;
            if (redrawScheduled) {
                updateCanvas(canvas);
                redrawScheduled = false;
            }
        });
        canvas.addEventListener("mouseup", event -> isPanning = false);
        canvas.addEventListener("mouseleave", event -> isPanning = false);
    }

    public static void addZoomEventListeners(HTMLCanvasElement canvas) {
        int maxDataPoints = inputDataSetsMap.get(canvas.getId()).length;
        int standardDisplayedDataPoints = 200;
        int minDataPoints = 10;

        canvas.addEventListener("wheel", event -> {
            event.preventDefault();
            MouseEventProperty eventProperty = event.cast();
            double deltaY = eventProperty.getDeltaY();
            double newZoomLevel;
            double oldZoomLevel = zoomMap.get(canvas.getId());

            if (deltaY < 0) {
                newZoomLevel = oldZoomLevel * 1.1;
            } else {
                newZoomLevel = oldZoomLevel / 1.1;
            }

            if((standardDisplayedDataPoints / newZoomLevel) >= minDataPoints &&
                    (standardDisplayedDataPoints / newZoomLevel) <= maxDataPoints) {
                zoomMap.put(canvas.getId(), newZoomLevel);
            }

            updateCanvas(canvas);
        });
    }

    public static void addResizeEventListener(HTMLCanvasElement canvas) {
        Window.current().addEventListener("resize", event -> updateCanvas(canvas));
    }

    public static void updateCanvas(HTMLCanvasElement canvas) {
        double[][] inputDataSet = inputDataSetsMap.get(canvas.getId());
        int standardDisplayedDataPoints = 200;
        CanvasRenderingContext2D ctx = prepareAndReturnCanvasContext(canvas);
        double zoomLevel = zoomMap.get(canvas.getId());
        int[] dataSetIndices =  DataPreparationHandler.getDataSetIndicesWithZoom(standardDisplayedDataPoints, inputDataSet, zoomLevel);

        int startIndex = dataSetIndices[0];
        int endOfIndex = dataSetIndices[1];
        int panning = panningMap.get(canvas.getId());

        if(startIndex + panning < 0) {
            panning = -startIndex;
            panningMap.put(canvas.getId(), -startIndex);
        } else if(endOfIndex + panning >= inputDataSet.length) {
            panning = inputDataSet.length - endOfIndex;
            panningMap.put(canvas.getId(), inputDataSet.length - endOfIndex);
        }

        startIndex += panning;
        endOfIndex += panning;

        double[][] extractDataFromDataSet = DataPreparationHandler.extractSubArray(inputDataSet, startIndex, endOfIndex);
        drawTwoDataSetsOnCanvas(ctx, extractDataFromDataSet[0], extractDataFromDataSet[1], extractDataFromDataSet[2]);
    }

    private static void drawTwoDataSetsOnCanvas(CanvasRenderingContext2D ctx, double[] firstArray, double[] secondArray, double[] thirdArray) {
        double min = getMinOfDataset(firstArray, secondArray);
        double max = getMaxOfDataset(firstArray, secondArray);
        drawAxisAndReferencePoints(ctx, thirdArray, 10, max, min);
        drawSingleDataSetOnCanvas(ctx, firstArray, max, min, "blue");
        drawSingleDataSetOnCanvas(ctx, secondArray, max, min, "red");
    }

    private static void drawSingleDataSetOnCanvas(CanvasRenderingContext2D ctx, double[] inputData, double max, double min, String colour) {
        ctx.beginPath();
        ctx.setStrokeStyle(colour);

        int padding = 45;
        int canvasWidth = ctx.getCanvas().getWidth();
        int canvasHeight = ctx.getCanvas().getHeight();

        int width = canvasWidth - 2 * padding;
        int height = canvasHeight - 2 * padding;

        for (int i = 0; i < inputData.length; i++) {
            if(Double.isNaN(inputData[i])) continue;

            double dataPointXCoordinate = padding + i * (width / (double) (inputData.length - 1));
            double midCanvasHeight = height / 2.0;
            double scaledDataPointHeight = (height - ((inputData[i] - min) / (max - min) * height));
            double dataPointYCoordinate = padding + ((max == min) ? midCanvasHeight : scaledDataPointHeight);

            if (i == 0) {
                ctx.moveTo(dataPointXCoordinate, dataPointYCoordinate);
            } else {
                ctx.lineTo(dataPointXCoordinate, dataPointYCoordinate);
            }
        }
        ctx.stroke();
    }

    public static void drawAxisAndReferencePoints(CanvasRenderingContext2D ctx, double[] indexData, int numReferencePoints, double max, double min) {
        if(indexData == null) {
            throw new IllegalArgumentException(Messages.ERROR_EMPTY_DATASET);
        }
        int padding = 45;
        int canvasWidth = ctx.getCanvas().getWidth();
        int canvasHeight = ctx.getCanvas().getHeight();

        int width = canvasWidth - 2 * padding;
        int height = canvasHeight - 2 * padding;

        ctx.setLineWidth(1);
        ctx.beginPath();
        ctx.setStrokeStyle("black");
        ctx.moveTo(padding, height + padding);
        ctx.lineTo(width + padding, height + padding);
        ctx.moveTo(padding, padding);
        ctx.lineTo(padding, height + padding);
        ctx.stroke();
        ctx.setLineWidth(1);

        ctx.setFont("14px Arial");

        double yInterval = (double) height / (numReferencePoints - 1);
        double range = max - min;
        for (int i = 0; i < numReferencePoints; i++) {
            int y = height + padding - (int) (i * yInterval);
            double value;
            if (i == 0) {
                value = min;
            } else if (i == numReferencePoints - 1) {
                value = max;
            } else {
                value = i * (range / (numReferencePoints - 1)) + min;
            }
            double roundedValue = Math.round(value * 100.0) / 100.0;
            String label = String.valueOf(roundedValue);
            ctx.fillText(label, 0, y + 5);

            ctx.beginPath();
            ctx.moveTo(padding - 5, y);
            ctx.lineTo(padding + 5, y);
            ctx.stroke();
        }

        double xInterval = (double) width / (numReferencePoints - 1);
        for (int i = 0; i < numReferencePoints; i++) {
            int x = padding + (int) (i * xInterval);
            int y = height + padding;
            double value;
            if (i == 0) {
                value = indexData[0];
            } else if (i == numReferencePoints - 1) {
                value = indexData[indexData.length - 1];
            } else {
                double exactPosition = (double) i / (numReferencePoints - 1) * (indexData.length - 1);
                if (exactPosition % 1 == 0) {
                    value = indexData[(int) exactPosition];
                } else {
                    int index1 = (int) Math.floor(exactPosition);
                    int index2 = (int) Math.ceil(exactPosition);
                    double diff = exactPosition - index1;
                    value = indexData[index1] * (1 - diff) + indexData[index2] * diff;
                    value = Math.round(value * 100.0) / 100.0;
                }
            }
            String label = String.valueOf(value);
            double textWidth = ctx.measureText(label).getWidth();
            ctx.fillText(label, x - textWidth / 2, y + 30);

            ctx.beginPath();
            ctx.moveTo(x, y - 5);
            ctx.lineTo(x, y + 5);
            ctx.stroke();
        }
    }
}