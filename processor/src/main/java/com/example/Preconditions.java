package com.example;

import com.example.annotation.NeedApp;
import com.example.annotation.OnAppUninstalled;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

/**
 * Created by joao.victor on 15/12/2017.
 */

class Preconditions {

    static void checkConditions(Element targetClass) {
        checkNeedAppExists(targetClass);
        checkNeedAppValues(targetClass);
    }

    static <T> void checkTargetClass(T targetClass, T children){
        if(targetClass == null && children != null)
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
            throw new IllegalStateException(String.format("Class %s must have at least one method annotated with @NeedApp ", targetClass.getSimpleName()));
    }

    private static void checkNeedAppValues(Element targetClass) {
        List<? extends Element> listMethods = targetClass.getEnclosedElements();
        String[] apps;
        for (Element method : listMethods) {
            NeedApp annotation = method.getAnnotation(NeedApp.class);
            if (annotation != null) {
                apps = annotation.apps();
                checkValues(apps, targetClass, method);
            }
        }
    }

    private static void checkValues(String[] apps, Element element, Element method) {
        for (String app : apps) {
            if (app.equals(""))
                throw new IllegalStateException(String.format("Method %s() in class %s must have a valid annotation value ",
                        element.getSimpleName(), method.getSimpleName()));
        }
    }

    static boolean hasOutputAnnotation(Element targetClass) {
        List<? extends Element> enclosedElements = targetClass.getEnclosedElements();
        for (Element method : enclosedElements) {
            if (method.getAnnotation(OnAppUninstalled.class) != null) {
                checkOutputAnnotationParameters(targetClass);
                return true;
            }
        }
        return false;
    }

    private static void checkOutputAnnotationParameters(Element targetClass) {
        Element outputAnnotation = CodeGenerator.getOutputAnnotation(targetClass);
        ExecutableElement method = (ExecutableElement) outputAnnotation;
        List<? extends VariableElement> parameters = method.getParameters();
        if (method.getParameters().size() > 1 || method.getParameters().size() == 0)
            throw new IllegalArgumentException(String.format("Method %s() in class %s must have only one parameter",
                    method.getSimpleName(), targetClass.getSimpleName()));
//        else if (!parameters.get(0).asType().getKind().equals(TypeKind.ARRAY))
//            throw new IllegalArgumentException(String.format("Method %s() in class %s must have a list of string as parameter",
//                    method.getSimpleName(), targetClass.getSimpleName()));
    }

    private <T> void checkNonNull(T value) {
        if (value == null)
            throw new IllegalStateException(String.format("%s must not be null", value));
    }


}
