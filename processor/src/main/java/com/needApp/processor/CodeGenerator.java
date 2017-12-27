package com.needApp.processor;

import com.needApp.annotation.NeedApp;
import com.needApp.annotation.OnAppUninstalled;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;

import static com.needApp.processor.ClassNameHelper.AndroidException;
import static com.needApp.processor.ClassNameHelper.Context;
import static com.needApp.processor.ClassNameHelper.Util;

/**
 * Created by joao.victor on 12/12/2017.
 */

public class CodeGenerator {

    public static TypeSpec generateType(Element targetClass, List<ExecutableElement> listMethods) {
        TypeSpec.Builder builder = TypeSpec.classBuilder(targetClass.getSimpleName() + "_Delegate");
        builder.addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        addMethodsToClass(listMethods, builder, targetClass);

        return builder.build();
    }

    public static TypeSpec typeUtil() {
        return TypeSpec.classBuilder("Util")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(appVerification())
                .build();
    }


    public static MethodSpec appVerification() {
        return MethodSpec.methodBuilder("isAppInstalled")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(Context, "context")
                .addParameter(String.class, "needApp")
                .addStatement("String app = null")
                .addCode("  try{ \n")
                .addStatement("context.getPackageManager().getPackageInfo(needApp, 0)")
                .addCode("} catch ($T e){ \n", AndroidException)
                .addStatement("app = needApp")
                .addCode("} \n")
                .addStatement("return app")
                .returns(String.class)
                .build();

    }

    private static MethodSpec cloneMethod(ExecutableElement method, String app, Element targetClass) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(method.getSimpleName() + "NeedApp");
        builder.addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC);
        builder.addParameter(Context, "context");
        builder.addStatement("String value = $S", app);
        addBodyMethod(method, app, builder, targetClass);
        builder.returns(TypeName.VOID);

        return builder.build();
    }


    private static void addMethodsToClass(List<ExecutableElement> executableElements, TypeSpec.Builder builder, Element targetClass) {
        String app;
        for (ExecutableElement executableElement : executableElements) {
            app = executableElement.getAnnotation(NeedApp.class).app();
            builder.addMethod(cloneMethod(executableElement, app, targetClass));
        }
    }

    private static void addBodyMethod(ExecutableElement method, String app, MethodSpec.Builder builder, Element targetClass) {
        List<? extends VariableElement> parameters = method.getParameters();
        builder.addCode(CodeBlock.builder()
                .addStatement("String app = $T.$N(context, value)", Util, "isAppInstalled")
                .add("if(app == null)\n")
                .add("(($L)context).$L(", targetClass, method.getSimpleName())
                .add(addParameterToMethodAnnotated(parameters, builder))
                .addStatement(")")
                .add(outputAnnotation(targetClass, method, app))
                .build());
    }


    private static CodeBlock outputAnnotation(Element targetClass, ExecutableElement method, String app) {
        CodeBlock.Builder builder = CodeBlock.builder();
        if (Preconditions.hasOutputAnnotation(targetClass)) {
            builder.add("else \n");
            Element outputAnnotation = getRealOutputAnnotation(targetClass, app);
            Preconditions.checkOutputAnnotationValue(targetClass, method, outputAnnotation);
            builder.addStatement("(($L)context).$L()", targetClass, outputAnnotation.getSimpleName());
        }

        return builder.build();
    }

    private static CodeBlock addParameterToMethodAnnotated(List<? extends VariableElement> parameters, MethodSpec.Builder methodSpecBuilder) {
        CodeBlock.Builder builder = CodeBlock.builder();
        int size = parameters.size();
        for (VariableElement param : parameters) {
            methodSpecBuilder.addParameter(TypeName.get(param.asType()), param.getSimpleName().toString());
            builder.add(param.getSimpleName().toString());
            if (size > 1) {
                builder.add(",");
                size--;
            }
        }

        return builder.build();
    }

    static Element getOutputAnnotationValue(Element targetClass) {
        List<? extends Element> targetClassMethods = targetClass.getEnclosedElements();
        for (Element method : targetClassMethods) {
            if (method.getAnnotation(OnAppUninstalled.class) != null)
                return method;
        }
        return null;
    }

    private static Element getRealOutputAnnotation(Element targetClass, String app) {
        Element outputAnnotationValue = getOutputAnnotationValue(targetClass, app);
        if (outputAnnotationValue != null)
            return outputAnnotationValue;

        return null;
    }

    private static Element getOutputAnnotationValue(Element targetClass, String app) {
        List<? extends Element> targetClassMethods = targetClass.getEnclosedElements();
        for (Element method : targetClassMethods) {
            OnAppUninstalled annotation = method.getAnnotation(OnAppUninstalled.class);
            if (annotation != null && annotation.value().equals(app))
                return method;
        }

        return null;
    }


}




