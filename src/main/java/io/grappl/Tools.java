package io.grappl;

public class Tools {

    public void dumpStackTrace() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

        for(StackTraceElement stackTraceElement : stackTraceElements) {
            System.out.println(stackTraceElement);
        }
    }
}
