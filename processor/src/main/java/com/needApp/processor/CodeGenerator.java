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

    public static TypeSpec createTypeUtil() {
        return TypeSpec.classBuilder("Util")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(generateAppVerification())
                .build();
    }


    public static MethodSpec generateAppVerification() {
        return MethodSpec.methodBuilder("isAppInstalled")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(Context, "context")
                .addParameter(String[].class, "needApps")
                .addStatement("String[] apps = new String[$N]", "needApps.length")
                .beginControlFlow("for(int index = 0; index < needApps.length; index++)")
                .addCode("try{")
                .addStatement("context.getPackageManager().getPackageInfo(needApps[index], 0)")
                .addCode("}  catch ($T e){", AndroidException)
                .addStatement("apps[index] = needApps[index]")
                .addCode("}")
                .endControlFlow()
                .addStatement("return apps")
                .returns(String[].class)
                .build();

    }

    private static MethodSpec cloneMethod(ExecutableElement method, String[] apps, Element targetClass) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(method.getSimpleName() + "NeedApp");
        builder.addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC);
        builder.addParameter(Context, "context");
        transformArray("values", builder, apps);
        addBodyMethod(method, apps, builder, targetClass);
        builder.returns(TypeName.VOID);

        return builder.build();
    }

    private static void transformArray(String nameMethod, MethodSpec.Builder builder, String[] apps) {
        builder.addStatement("String[] $L = new String[$L]", nameMethod, apps.length);
        for (int index = 0; index < apps.length; index++)
            builder.addStatement("$L[$L] = $S", nameMethod, index, apps[index]);
    }

    private static void addMethodsToClass(List<ExecutableElement> list, TypeSpec.Builder builder, Element targetClass) {
        String[] apps;
        for (ExecutableElement executableElement : list) {
            apps = executableElement.getAnnotation(NeedApp.class).apps();
            builder.addMethod(cloneMethod(executableElement, apps, targetClass));
        }
    }

    private static void addBodyMethod(ExecutableElement method, String[] apps, MethodSpec.Builder builder, Element targetClass) {
        List<? extends VariableElement> parameters = method.getParameters();
        builder.addCode(CodeBlock.builder()
                .addStatement("$T listApps = $T.$N(context, values)", String[].class, Util, "isAppInstalled")
                .add("if($N[0] == null)\n", "listApps")
                .add("(($L)context).$L(", targetClass, method.getSimpleName())
                .add(addParameterToMethodAnnotated(parameters, builder))
                .addStatement(")")
                .add(outputAnnotation(method, targetClass, apps))
                .build());
    }


    private static CodeBlock outputAnnotation(ExecutableElement method, Element targetClass, String[] apps) {
        CodeBlock.Builder builder = CodeBlock.builder();
        if (Preconditions.hasOutputAnnotation(targetClass)) {
            builder.add("else");
            Element outputAnnotation = getOutputAnnotation(targetClass, apps);
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

    private static Element getOutputAnnotationValue(Element targetClass, String app) {
        List<? extends Element> targetClassMethods = targetClass.getEnclosedElements();
        for (Element method : targetClassMethods) {
            OnAppUninstalled annotation = method.getAnnotation(OnAppUninstalled.class);
            if (annotation != null && annotation.value().equals(app))
                return method;
        }

        return null;
    }

    private static Element getOutputAnnotation(Element targetClass, String[] apps) {
        for (String app : apps) {
            Element outputAnnotationValue = getOutputAnnotationValue(targetClass, app);
            if (outputAnnotationValue != null)
                return outputAnnotationValue;
        }
        return null;
    }


}




