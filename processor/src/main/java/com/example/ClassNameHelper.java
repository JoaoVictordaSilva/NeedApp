package com.example;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

/**
 * Created by joao.victor on 12/12/2017.
 */

class ClassNameHelper {

    private static  final String contextPackage = "android.content";
    private static  final String packageManagerPackage = "android.content.pm";
    private static  final String androidUtilPackage = "android.util";
    private static  final String javaUtilPackage = "java.util";
    private static  final String thisPackage = "com.example";
    private static  final String activityPackage = "android.app";
    private static  final String stringPackage = "java.lang";


    static final ClassName Context = ClassName.get(contextPackage, "Context");
    static final ClassName PackageManageNameNotFoundException = ClassName.get(packageManagerPackage, "PackageManager");
    static final ClassName AndroidException = ClassName.get(androidUtilPackage, "AndroidException");
    static final ClassName ArrayList = ClassName.get(javaUtilPackage, "ArrayList");
    private static final ClassName List = ClassName.get(javaUtilPackage, "List");
    static final ClassName String = ClassName.get(stringPackage, "String");
    static final TypeName ListString = ParameterizedTypeName.get(List, String);


}
