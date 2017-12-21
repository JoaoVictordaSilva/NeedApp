package com.needApp.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.needApp.annotation.NeedApp;
import com.needApp.annotation.OnAppUninstalled;
import com.needApp.annotation.TargetClass;

@TargetClass
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @NeedApp(apps = NeedApp.LINKEDIN)
    public void test(String testing) {
        Log.e(this.getClass().toString(), testing);
    }

    @OnAppUninstalled(NeedApp.TWITTER)
    public void outputAnnotation_1() {
        Log.e(this.getClass().getSimpleName(), " App unninstalled ");
    }

    @OnAppUninstalled(NeedApp.LINKEDIN)
    public void outputAnnotation() {
        Log.e(this.getClass().getSimpleName(), " App unninstalled linkedinho");
    }

    @OnAppUninstalled(NeedApp.SNAPCHAT)
    public void outputAnnotation1() {
        Log.e(this.getClass().getSimpleName(), " App unninstalled snapxota");
    }

}
