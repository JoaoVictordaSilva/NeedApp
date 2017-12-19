package com.example.joaovictor.annotationprocessing;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


import com.example.annotation.NeedApp;
import com.example.annotation.OnAppUninstalled;
import com.example.annotation.TargetClass;

import java.util.List;


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

    @OnAppUninstalled
    public void outputAnnotation(List<String> string) {
        Log.e(this.getClass().getSimpleName(), " App unninstalled " + string.get(0));
    }

}
