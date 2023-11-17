package ru.example.otus.ap.configuration;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;

public final class ThreadLocalContext {

    private static final ThreadLocal<RoundEnvironment> ROUND_ENVIRONMENT = new ThreadLocal<>();
    private static final ThreadLocal<ProcessingEnvironment> PROCESS_ENV = new ThreadLocal<>();
    private static final ThreadLocal<String> PROCESSING_CLASS_NAME = new ThreadLocal<>();
    private static final ThreadLocal<String> SHOW_VALUES_CONTENT = new ThreadLocal<>();

    public static RoundEnvironment getRoundEnvironment() {
        return ROUND_ENVIRONMENT.get();
    }

    public static void setRoundEnvironment(RoundEnvironment roundEnvironment) {
        ROUND_ENVIRONMENT.set(roundEnvironment);
    }

    public static ProcessingEnvironment getProcessEnv() {
        return PROCESS_ENV.get();
    }

    public static void setProcessEnv(ProcessingEnvironment processEnv) {
        PROCESS_ENV.set(processEnv);
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

    public static void remove() {
        ROUND_ENVIRONMENT.remove();
        PROCESSING_CLASS_NAME.remove();
        SHOW_VALUES_CONTENT.remove();
    }

}
