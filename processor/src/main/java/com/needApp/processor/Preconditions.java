package com.needApp.processor;

import com.needApp.annotation.NeedApp;
import com.needApp.annotation.OnAppUninstalled;

import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;

/**
 * Created by joao.victor on 15/12/2017.
 */

class Preconditions {

    static void checkConditions(Element targetClass) {
        checkNeedAppExists(targetClass);
        checkNeedAppValues(targetClass);
    }

    static <T extends Element> void checkTargetClass(T targetClass, T children) {
        if (targetClass == null && children != null)
            throw new IllegalStateException("@NeedApp must have @TargetClass annotated in current class");
    }

    private static void checkNeedAppExists(Element targetClass) {
        boolean hasAnnotation = false;
        List<? extends Element> enclosedElements = targetClass.getEnclosedElements();
        for (Element method : enclosedElements) {
            if (method.getAnnotation(NeedApp.class) != null) {
                hasAnnotation = true;
                break;
            }
        }
        if (!hasAnnotation)
            throw new IllegalStateException(String.format("Class %s must have at least one method annotated with @NeedApp",
                    targetClass.getSimpleName()));
    }

    private static void checkNeedAppValues(Element targetClass) {
        List<? extends Element> listMethods = targetClass.getEnclosedElements();
        String app;
        for (Element method : listMethods) {
            NeedApp annotation = method.getAnnotation(NeedApp.class);
            if (annotation != null) {
                app = annotation.app();
                checkModifier(targetClass, method);
                checkValue(app, targetClass, method);
            }
        }
    }

    private static void checkModifier(Element targetClass, Element element) {
        ExecutableElement executableElement = (ExecutableElement) element;
        Set<Modifier> modifiers = executableElement.getModifiers();
        for (Modifier modifier : modifiers) {
            if (!modifier.equals(Modifier.PUBLIC))
                throw new IllegalStateException(String.format("Method %s() in %s must be public",
                        executableElement.getSimpleName(), targetClass.getSimpleName()));

        }
    }

    private static void checkValue(String value, Element targetClass, Element method) {
        if (value.equals(""))
            throw new IllegalStateException(String.format("Method %s() in class %s must have a valid annotation value",
                    method.getSimpleName(), targetClass.getSimpleName()));

    }

    static boolean hasOutputAnnotation(Element targetClass) {
        List<? extends Element> enclosedElements = targetClass.getEnclosedElements();
        String value;
        for (Element method : enclosedElements) {
            OnAppUninstalled annotation = method.getAnnotation(OnAppUninstalled.class);
            if (annotation != null) {
                value = annotation.value();
                checkOutputAnnotationParameters(targetClass);
                checkValue(value, targetClass, method);
                checkModifier(targetClass, method);
                return true;
            }
        }
        return false;
    }

    private static void checkOutputAnnotationParameters(Element targetClass) {
        Element outputAnnotation = CodeGenerator.getOutputAnnotationValue(targetClass);
        ExecutableElement method = (ExecutableElement) outputAnnotation;
        if (method.getParameters().size() != 0)
            throw new IllegalArgumentException(String.format("Method %s() in class %s can't have any parameter ",
                    method.getSimpleName(), targetClass.getSimpleName()));
        else if (!method.getReturnType().getKind().equals(TypeKind.VOID))
            throw new IllegalArgumentException(String.format("Method %s() in class %s must be void",
                    method.getSimpleName(), targetClass.getSimpleName()));
    }

    static void checkOutputAnnotationValue(Element targetClass, ExecutableElement method, Element outputAnnotation) {
        if (outputAnnotation == null)
            throw new IllegalArgumentException(String.format("Method %s() in %s must have same annotation value as @OnAppUninstalled value",
                    method.getSimpleName(), targetClass.getSimpleName()));
    }

}
