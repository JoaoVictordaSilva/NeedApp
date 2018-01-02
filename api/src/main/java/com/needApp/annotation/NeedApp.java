package com.needApp.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Created by joao.victor on 12/12/2017.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NeedApp {
    String app();

    String YOUTUBE = "com.google.android.youtube";

    String FACEBOOK = "com.facebook.katana";

    String TWITTER = "com.twitter.android";

    String INSTAGRAM = "com.instagram.android";

    String SNAPCHAT = "com.snapchat.android";

    String WHATSAPP = "com.whatsapp.android";

    String LINKEDIN = "com.linkedin.android";
}

