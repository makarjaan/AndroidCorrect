package ru.itis.base

import android.app.Application
import ru.itis.di.ServiceLocator

class App : Application() {

    private var serviceLocator = ServiceLocator

    override fun onCreate() {
        super.onCreate()
        serviceLocator.initDataLayerDependencies(ctx = this)
    }
}