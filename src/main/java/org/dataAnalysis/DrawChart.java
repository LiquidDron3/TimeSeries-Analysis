package org.dataAnalysis;

import org.teavm.jso.dom.html.HTMLCanvasElement;

public interface DrawChart {
    void drawGraphWithCalculation(HTMLCanvasElement canvas, double[] data1, double[] data2);

    void addResizeEventListener(HTMLCanvasElement canvas, double[] data1, double[] data2);

}