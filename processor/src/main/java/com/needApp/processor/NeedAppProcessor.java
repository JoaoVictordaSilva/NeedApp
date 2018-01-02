package com.needApp.processor;

import com.google.auto.service.AutoService;
import com.needApp.annotation.NeedApp;
import com.needApp.annotation.TargetClass;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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

    private CodeGenerator mCodeGenerator = new CodeGenerator();
    private static final String mNeedAppPackage = ClassNameHelper.needAppPackage;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> stringSet = new TreeSet<>();
        stringSet.add(TargetClass.class.getCanonicalName());
        stringSet.add(NeedApp.class.getCanonicalName());
        return stringSet;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> listTargetClass = roundEnvironment.getElementsAnnotatedWith(TargetClass.class);
        Set<? extends Element> listNeedApp = roundEnvironment.getElementsAnnotatedWith(NeedApp.class);
        writeTo(mNeedAppPackage, mCodeGenerator.typeUtil());
        Preconditions.checkTargetClass(getElement(listTargetClass), getElement(listNeedApp));

        for (Element targetClass : listTargetClass) {
            List<ExecutableElement> listMethods = listMethodsAnnotated(targetClass);
            generateCode(targetClass, listMethods);
        }

        return true;
    }

    private Element getElement(Set<? extends Element> elements) {
        return elements.iterator().hasNext() ? elements.iterator().next() : null;
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

    private void generateCode(Element element, List<ExecutableElement> executableElement) {
        TypeSpec generatedClass = mCodeGenerator.generateType(element, executableElement);
        writeTo(mNeedAppPackage, generatedClass);
    }

    private void writeTo(String packageName, TypeSpec typeSpec) {
        JavaFile javaFile = JavaFile.builder(packageName, typeSpec).build();
        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
