package com.ivianuu.daggerextensions.sample.app

import android.app.Application
import com.ivianuu.daggerextensions.AutoComponent
import com.ivianuu.daggerextensions.BindsTypes
import com.ivianuu.daggerextensions.sample.injection.ActivityBindingModule_
import com.ivianuu.daggerextensions.sample.injection.ServiceBindingModule_
import com.ivianuu.daggerextensions.sample.multibinding.*
import com.ivianuu.daggerextensions.sample.sub.SomethingComponent
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import dagger.android.support.DaggerApplication
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author Manuel Wrage (IVIanuu)
 */
@BindsTypes(types = [Application::class])
@Singleton
@AutoComponent(
    modules = [
        AndroidInjectionModule::class,
        AndroidSupportInjectionModule::class,
        ActivityBindingModule_::class,
        FunghiPizzaModule_::class,
        SalamiPizzaModule_::class,
        SpotifyMediaPlayerModule_::class,
        SoundCloudMediaPlayerModule_::class,
        LoggerModule::class,
        TranslatorModule::class,
        ServiceBindingModule_::class
    ],
    injects = [GlideConfig::class],
    superInterfaces = [MySuperInterface::class],
    subcomponents = [SomethingComponent::class]
)
class App : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder()
            .app(this)
            .build()
    }
}

interface MySuperInterface {
    fun inject(controller: Controller)
}

class Controller {
    @Inject lateinit var app: App
}

class GlideConfig {

    @Inject lateinit var app: App

}