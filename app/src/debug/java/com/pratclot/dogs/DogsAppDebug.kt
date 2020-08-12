package com.pratclot.dogs

import com.facebook.stetho.Stetho

open class DogsAppDebug : DogsApp() {
    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(applicationContext)
    }
}