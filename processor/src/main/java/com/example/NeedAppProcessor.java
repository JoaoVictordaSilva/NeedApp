package com.example;

import com.example.annotation.NeedApp;
import com.example.annotation.TargetClass;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

@AutoService(Processor.class)
public class NeedAppProcessor extends AbstractProcessor {

    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> listTargetClass = roundEnvironment.getElementsAnnotatedWith(TargetClass.class);
        for (Element targetClass : listTargetClass) {
            List<ExecutableElement> listMethods = listMethodsAnnotated(targetClass);
            generateCode(targetClass, listMethods);
        }

        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(TargetClass.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
    }

    private void generateCode(Element element, List<ExecutableElement> executableElement) {
        TypeSpec generatedClass = CodeGenerator.generateType(element, executableElement);
        String packageName = processingEnv.getElementUtils().getPackageOf(element).getQualifiedName().toString();
        JavaFile javaFile = JavaFile.builder(packageName, generatedClass).build();
        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<ExecutableElement> listMethodsAnnotated(Element targetClass) {
        Preconditions.checkConditions(targetClass);
        List<ExecutableElement> listMethods = new ArrayList<>();
        List<? extends Element> targetClassMethods = targetClass.getEnclosedElements();
        for (Element method : targetClassMethods) {
            if (method.getAnnotation(NeedApp.class) != null) {
                ExecutableElement executableElement = (ExecutableElement) method;
                listMethods.add(executableElement);
            }
        }
        return listMethods;
    }


}
