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
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@SupportedAnnotationTypes("ru.example.otus.ap.annotations.ShowValues")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(AnnotationProcessor.class)
public class AnnotationProcessor extends AbstractProcessor {

    private Boolean done = false;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        System.out.println("processing annotations...");
        if (done) {
            return true;
        }

        ThreadLocalContext.setRoundEnvironment(roundEnv);

        try {
            try {
                processClasses();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } finally {
            ThreadLocalContext.remove();
            done = true;
        }

        return true;
    }

    private void processClasses() throws IOException {
        for (Element classElement : ThreadLocalContext.getRoundEnvironment().getElementsAnnotatedWith(ShowValues.class)) {
            processClass(classElement);
        }

    }

    private void processClass(Element classElement) throws IOException {
        List<Field> fields = Stream.of(classElement.getClass().getDeclaredFields())
                .toList();

        String fileName = classElement.getSimpleName().toString();
        System.out.println("Processing class element: " + classElement.toString());
        FileObject file = ThreadLocalContext.getProcessEnv().getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", fileName);

        try (OutputStreamWriter out = new OutputStreamWriter(file.openOutputStream(), StandardCharsets.UTF_8)) {
            out.append("Hello, world!\n");
        }

    }

}
