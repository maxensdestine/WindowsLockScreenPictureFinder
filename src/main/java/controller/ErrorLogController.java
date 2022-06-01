package controller;

import view.MainFrame;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class ErrorLogController {

    private static MainFrame view;
    private static boolean hasError = false;

    public static void addError(String message){
        hasError = true;
        LocalTime localTime = LocalTime.now();
        message = localTime.truncatedTo(ChronoUnit.SECONDS).toString() + " : " + message;
        view.addError(message);

    }

    public static MainFrame getView() {
        return view;
    }

    public static void setView(MainFrame view) {
        ErrorLogController.view = view;
    }

    public static boolean isHasError() {
        return hasError;
    }

    public static void setHasError(boolean hasError) {
        ErrorLogController.hasError = hasError;
    }
}
