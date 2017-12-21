package com.example.joaovictor.annotationprocessing.Teste

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.needApp.annotation.NeedApp
import com.needApp.annotation.TargetClass

import com.example.joaovictor.annotationprocessing.R
import com.needApp.Main2Activity_Delegate

@TargetClass
class Main2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        Main2Activity_Delegate.getNoiseNeedApp(this);
    }

    @NeedApp(apps = [com.needApp.annotation.NeedApp.FACEBOOK])
    fun getNoise(){
        Log.e(this::class.java.simpleName, "Make some noisee")
    }
}
