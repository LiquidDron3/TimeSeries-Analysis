package org.dataAnalysis;

import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.html.HTMLButtonElement;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;

public class NotificationHandler {
    private static HTMLDocument doc;

    public static void initDocument(HTMLDocument doc) {
        NotificationHandler.doc = doc;
    }

    public static void displaySuccessMessage() {
        HTMLElement toastElement = doc.querySelector(".toast.success").cast();
        HTMLButtonElement closeBtn = doc.getElementById("toastSuccessClose").cast();
        toastElement.setAttribute("style", "display: block;");

        closeBtn.addEventListener("click", evt -> toastElement.setAttribute("style", "display: none;"));
        Window.setTimeout(() -> toastElement.setAttribute("style", "display: none;"), 3000);
    }

    public static void displayWarningMessage(String message) {
        HTMLElement toastElement = doc.querySelector(".toast.warning").cast();
        HTMLButtonElement closeBtn = doc.getElementById("toastWarningClose").cast();
        HTMLElement outputMessage = doc.getElementById("toastWarningMessage");

        outputMessage.setInnerHTML(message);
        toastElement.setAttribute("style", "display: block;");
        closeBtn.addEventListener("click", evt -> toastElement.setAttribute("style", "display: none;"));
    }

    public static void displayErrorMessage(String message) {
        HTMLElement toastElement = doc.querySelector(".toast.error").cast();
        HTMLButtonElement closeBtn = doc.getElementById("toastErrorClose").cast();
        HTMLElement outputMessage = doc.getElementById("toastErrorMessage");

        outputMessage.setInnerHTML(message);
        toastElement.setAttribute("style", "display: block;");
        closeBtn.addEventListener("click", evt -> toastElement.setAttribute("style", "display: none;"));
    }
}
