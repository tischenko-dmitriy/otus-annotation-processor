package org.example.otus.ap.configuration;

import javax.annotation.processing.RoundEnvironment;

public final class ThreadLocalContext {

    private static final ThreadLocal<RoundEnvironment> ROUND_ENVIRONMENT = new ThreadLocal<>();
    private static final ThreadLocal<String> PROCESSING_CLASS_NAME = new ThreadLocal<>();
    private static final ThreadLocal<String> SHOW_VALUES_CONTENT = new ThreadLocal<>();

    public static RoundEnvironment getRoundEnvironment() {
        return ROUND_ENVIRONMENT.get();
    }

    public static void setRoundEnvironment(RoundEnvironment roundEnvironment) {
        ROUND_ENVIRONMENT.set(roundEnvironment);
    }

    public static String getProcessingClassName() {
        return PROCESSING_CLASS_NAME.get();
    }

    public static void setProcessingClassName(String className) {
        PROCESSING_CLASS_NAME.set(className);
    }

    public static String getShowValuesContent() {
        return SHOW_VALUES_CONTENT.get();
    }

    public static void setShowValuesContent(String toStringContent) {
        SHOW_VALUES_CONTENT.set(toStringContent);
    }

}
