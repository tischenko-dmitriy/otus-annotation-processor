package ru.example.otus.ap.configuration;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;

public final class ThreadLocalContext {

    private static final ThreadLocal<RoundEnvironment> ROUND_ENVIRONMENT = new ThreadLocal<>();
    private static final ThreadLocal<ProcessingEnvironment> PROCESS_ENV = new ThreadLocal<>();

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

    public static void remove() {
        ROUND_ENVIRONMENT.remove();
        PROCESS_ENV.remove();
    }

}
