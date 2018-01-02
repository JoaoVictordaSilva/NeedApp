package com.needApp.processor;

import com.needApp.annotation.NeedApp;
import com.needApp.annotation.TargetClass;
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

class CodeGenerator {

    public TypeSpec typeUtil() {
        return TypeSpec.classBuilder("Util")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(appVerification())
                .build();
    }

    public TypeSpec generateType(Element targetClass, List<ExecutableElement> listMethods) {
        TypeSpec.Builder builder = TypeSpec.classBuilder(targetClass.getSimpleName() + "_Delegate");
        builder.addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        addMethodsToClass(listMethods, builder, targetClass);

        return builder.build();
    }


    private MethodSpec appVerification() {
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

    private void addMethodsToClass(List<ExecutableElement> executableElements, TypeSpec.Builder builder, Element targetClass) {
        String app;
        for (ExecutableElement executableElement : executableElements) {
            app = executableElement.getAnnotation(NeedApp.class).app();
            builder.addMethod(cloneMethod(executableElement, app, targetClass));
        }
    }


    private MethodSpec cloneMethod(ExecutableElement method, String app, Element targetClass) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(method.getSimpleName() + "NeedApp");
        builder.addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC);
        builder.addParameter(Context, "context");
        addBodyMethod(method, app, builder, targetClass);
        builder.returns(TypeName.VOID);

        return builder.build();
    }

    private void addBodyMethod(ExecutableElement method, String app, MethodSpec.Builder builder, Element targetClass) {
        List<? extends VariableElement> parameters = method.getParameters();
        builder.addCode(CodeBlock.builder()
                .addStatement("$1L $2L = new $1L()", targetClass, formatNameVariable(targetClass.getSimpleName().toString()))
                .addStatement("String app = $T.$N(context, $S)", Util, "isAppInstalled", app)
                .add("if(app == null)\n")
                .add("$L.$L(", formatNameVariable(targetClass.getSimpleName().toString()), method.getSimpleName())
                .add(addParameter(parameters, builder))
                .addStatement(")")
                .add(outputAnnotation(targetClass, method, app))
                .build());

    }

    private String formatNameVariable(String var) {
        String var1 = var.substring(0, 1).toLowerCase();
        return var.replace(var.substring(0, 1), var1);
    }

    private CodeBlock addParameter(List<? extends VariableElement> parameters, MethodSpec.Builder methodSpecBuilder) {
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

    private Element getOutputAnnotationValue(Element targetClass, String app) {
        List<? extends Element> targetClassMethods = targetClass.getEnclosedElements();
        for (Element method : targetClassMethods) {
            OnAppUninstalled annotation = method.getAnnotation(OnAppUninstalled.class);
            if (annotation != null && annotation.value().equals(app))
                return method;
        }

        return null;
    }

    private Element getOutputAnnotation(Element targetClass, String app) {
        Element outputAnnotationValue = getOutputAnnotationValue(targetClass, app);
        if (outputAnnotationValue != null)
            return outputAnnotationValue;

        return null;
    }

    private CodeBlock outputAnnotation(Element targetClass, ExecutableElement method, String app) {
        CodeBlock.Builder builder = CodeBlock.builder();
        if (Preconditions.hasOutputAnnotation(targetClass)) {
            builder.add("else \n");
            Element outputAnnotation = getOutputAnnotation(targetClass, app);
            Preconditions.checkOutputAnnotationValue(targetClass, method, outputAnnotation);
            builder.addStatement("$L.$L()", formatNameVariable(targetClass.getSimpleName().toString()), outputAnnotation.getSimpleName());
        }
        return builder.build();
    }

}




