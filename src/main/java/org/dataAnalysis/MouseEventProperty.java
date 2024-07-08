package org.dataAnalysis;

import org.teavm.jso.JSProperty;
import org.teavm.jso.JSObject;

public interface MouseEventProperty extends JSObject {
    @JSProperty
    double getClientX();

    @JSProperty
    double getDeltaY();
}