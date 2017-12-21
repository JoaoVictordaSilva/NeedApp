package com.example;

import com.squareup.javapoet.ClassName;

/**
 * Created by joao.victor on 12/12/2017.
 */

class ClassNameHelper {

    private static final String contextPackage = "android.content";
    public static final String needAppPackage = "com.needApp";
    private static final String androidUtilPackage = "android.util";

    static final ClassName Context = ClassName.get(contextPackage, "Context");
    static final ClassName AndroidException = ClassName.get(androidUtilPackage, "AndroidException");
    static final ClassName Util = ClassName.get(needAppPackage, "Util");



}
