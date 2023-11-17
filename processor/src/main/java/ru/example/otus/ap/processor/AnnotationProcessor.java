package ru.example.otus.ap.processor;

import com.google.auto.service.AutoService;
import com.google.common.base.Joiner;
import ru.example.otus.ap.annotations.ShowValues;
import ru.example.otus.ap.configuration.ThreadLocalContext;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@SupportedAnnotationTypes("ru.example.otus.ap.annotations.ShowValues")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(AnnotationProcessor.class)
public class AnnotationProcessor extends AbstractProcessor {

    private Elements elementUtils;

    private final String A_SHOW_VALUES = "@ShowValues";
    private final String PROJECT_DIR = System.getProperty("user.dir") + File.separator;
    private final String SOURCE_ROOT = "src" + File.separator + "main" + File.separator + "java" + File.separator;

    private Boolean done = false;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        elementUtils = processingEnv.getElementUtils();

        System.out.println("processing annotations...");
        if (done) {
            return true;
        }

        ThreadLocalContext.setRoundEnvironment(roundEnv);
        ThreadLocalContext.setProcessEnv(processingEnv);

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

        TypeElement typeElement = elementUtils.getTypeElement(classElement.asType().toString());
        String module = elementUtils.getModuleOf(typeElement).toString();
        System.out.println("Module: " + module);

        String sourcePath = classElement.toString().replace(".", File.separator);
        String sourceFileName = PROJECT_DIR + SOURCE_ROOT + sourcePath + ".java";
        System.out.println("Source file: " + sourceFileName);
        List<String> source = Files.readAllLines(Paths.get(sourceFileName));

        String targetFileName = classElement.getSimpleName().toString() + "_show-values" + ".java";
        System.out.println("Processing class element: " + classElement.toString());

        FileObject file = ThreadLocalContext.getProcessEnv().getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", targetFileName);

        String showValuesMethod = generateShowValuesMethod(classElement);
        System.out.println("SHOW VALUES:\n" + showValuesMethod);

        try (OutputStreamWriter out = new OutputStreamWriter(file.openOutputStream(), StandardCharsets.UTF_8)) {
            for (int i = 0; i < source.size(); i++) {
                if (source.get(i).startsWith(A_SHOW_VALUES)) {
                    continue;
                }
                out.append(source.get(i)).append("\n");
                if (i == source.size() - 2) {
                    out.append(showValuesMethod);
                }
            }
            out.append("\n");
        }

    }

    private String generateShowValuesMethod(Element classElement) {

        TypeElement typeElement = elementUtils.getTypeElement(classElement.asType().toString());
        Set<? extends Element> fields = typeElement.getEnclosedElements()
                .stream()
                .filter(o -> o.getKind().isField())
                .collect(Collectors.toSet());

        String className = classElement.getSimpleName().toString();

        List<String> showValuesContent = new ArrayList<>();
        fields.forEach(item -> {
            showValuesContent.add(String.format("\t\t%s = this.%s", item, item));
        });

        String showValuesBody = Joiner.on(";\n").join(showValuesContent);

        return String.format("public String showValues() {\n\tString result = \"" + className + ":\n%s\";\n\treturn result;\n}\n", showValuesBody);
    }

}
