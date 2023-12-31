package ru.otus.example.ap.services;

import com.google.auto.service.AutoService;
import com.google.common.base.Joiner;
import org.apache.commons.io.FileUtils;
import ru.otus.example.ap.annotations.CustomToString;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ExecutableType;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@SupportedAnnotationTypes("ru.otus.example.ap.annotations.CustomToString")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class CustomToStringProcessor extends AbstractProcessor {
    private final String A_CUSTOM_TO_STRING = "@CustomToString";
    private final String PROJECT_DIR = System.getProperty("user.dir") + File.separator;
    private final String SOURCE_ROOT = "src" + File.separator + "main" + File.separator + "java" + File.separator;
    private final String TARGET_ROOT = "target" + File.separator;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if (annotations.isEmpty()) {
            return false;
        }

        final Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(CustomToString.class);

        List<? extends Element> classes = getClasses(annotatedElements);

        if (classes.isEmpty()) {
            return false;
        }

        classes.forEach(c -> {
            String className = c.toString();
            Element element = c;
            Class<?> clazz = null;//element.getClass();
            try {
                clazz = ClassLoader.getPlatformClassLoader().loadClass(className);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            String toStringMethod = generateToStringMethod(clazz);

            System.out.printf("customToString: %s\n", toStringMethod);
            try {
                writeClass(clazz.getName(), toStringMethod);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        return true;
    }

    private List<Element> getClasses(Set<? extends Element> annotatedElements) {
        return annotatedElements.stream()
                .filter(element -> element.getKind().isClass() )
                .collect(Collectors.toList());
    }

    private String generateToStringMethod(Class<?> clazz) {
        List<Field> fields = Arrays.asList(clazz.getDeclaredFields());

        List<String> toStringContent = new ArrayList<>();
        fields.forEach(item -> {
            toStringContent.add(String.format("%s = %%s", item));
        });

        String toStringBody = Joiner.on("; ").join(toStringContent);

        return String.format("public void customToString() {\nString result = %s;\nreturn result;}\n", toStringBody);
    }

    private void writeClass(String className, String toStringMethodBody) throws IOException {
        int dot = className.lastIndexOf(".");

        String packageName = null;
        if (dot > 0) {
            packageName = className.substring(0, dot);
        }

        String simpleClassName = className.substring(dot + 1);
        String toStringClassName = className + "ToString";
        String toStringSimpleClassName = simpleClassName + "ToString";

        String targetFileName = getTargetFileName(toStringClassName);

        String sourceFileName = getSourceFileName(className);
        System.out.printf("className: %s\n", className);
        System.out.printf("sourceFileName: %s\n", sourceFileName);

        List<String> content = Files.readAllLines(Paths.get(sourceFileName));

        System.out.printf("TARGET FILE: %s\n", targetFileName);
        FileUtils.touch(new File(targetFileName));
        FileWriter fileWriter = new FileWriter(targetFileName);

        try (PrintWriter printWriter = new PrintWriter(fileWriter)) {
            for (int i = 0; i < content.size() - 1; i++) {
                if (content.get(i).startsWith(A_CUSTOM_TO_STRING)) {
                    continue;
                }

                printWriter.println(content.get(i));
            }

            printWriter.println();
            printWriter.println(toStringMethodBody);
            printWriter.println();
            printWriter.println(content.get(content.size() - 1));
        }

    }

    private String convertQualifiedNameToFileName(String className) {
        return className.replace(".", File.separator) + ".java";
    }

    private String getSourceFileName(String className) {
        return PROJECT_DIR +
                SOURCE_ROOT +
                convertQualifiedNameToFileName(className);
    }

    private String getTargetFileName(String className) {
        return PROJECT_DIR +
                TARGET_ROOT +
                "generated-sources" + File.separator +
                convertQualifiedNameToFileName(className);
    }

}
