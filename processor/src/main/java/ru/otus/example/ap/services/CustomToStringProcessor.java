package ru.otus.example.ap.services;

import com.google.common.base.Joiner;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@SupportedAnnotationTypes("ru.otus.example.apro.annotations.CustomToString")
public class CustomToStringProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        for (TypeElement annotation : annotations) {
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);

            List<Element> classes = getClasses(annotatedElements);

            if (classes.isEmpty())
                continue;

            classes.forEach(clazz -> {
                Map<String, Field> fields = new HashMap<>();
                for (Field field : clazz.getClass().getDeclaredFields())
                    fields.put(field.getName(), field);

                Map<String, Method> methods = new HashMap<>();
                for (Method method : clazz.getClass().getDeclaredMethods())
                    methods.put(method.getName(), method);

                String toStringMethod = generateToStringMethod(clazz.getClass());

                try {
                    writeClass(clazz.getClass().getName(), toStringMethod, fields, methods);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

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

        return String.format("public void toString() {\nString result = %s;\nreturn result;}\n", toStringBody);
    }

    private void writeClass(String className,
                            String toStringMethodBody,
                            Map<String, Field> fields,
                            Map<String, Method> methods) throws IOException {
        int dot = className.lastIndexOf(".");

        String packageName = null;
        if (dot > 0)
            packageName = className.substring(0, dot);

        String simpleClassName = className.substring(dot - 1);
        String toStringClassName = className + "ToString";
        String toStringSimpleClassName = simpleClassName + "ToString";

        JavaFileObject toStringFile = processingEnv.getFiler().createSourceFile(toStringClassName);

        try (PrintWriter out = new PrintWriter(toStringFile.openWriter())) {
            if (Objects.nonNull(packageName))
                out.printf("package %s;\n", packageName);

            out.printf("public class %s {\n", toStringSimpleClassName);
            out.println();
            fields.entrySet().forEach(e -> {out.printf("%s;\n", e); });
            out.println();
            methods.entrySet().forEach(e -> {out.println(e); });
            out.println("}");

            // Здесь пока непонятно, как получить текст класса
        }
    }
}
