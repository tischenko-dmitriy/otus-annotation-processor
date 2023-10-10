package ru.example.otus.ap.processor;

import com.google.auto.service.AutoService;
import ru.example.otus.ap.annotations.ShowValues;
import ru.example.otus.ap.configuration.ThreadLocalContext;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedAnnotationTypes("org.example.otus.ap.annotations.ShowValues")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(AnnotationProcessor.class)
public class AnnotationProcessor extends AbstractProcessor {

    private Boolean done;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if (done) {
            return true;
        }

        ThreadLocalContext.setRoundEnvironment(roundEnv);

        try {
            processClasses();
        } finally {
            ThreadLocalContext.remove();
            done = true;
        }

        return true;
    }

    private void processClasses() {
        for (Element classElement : ThreadLocalContext.getRoundEnvironment().getElementsAnnotatedWith(ShowValues.class)) {
            processClass(classElement);
        }

    }

    private void processClass(Element classElement) {
        System.out.println("Processing class element: " + classElement.toString());
    }

}
