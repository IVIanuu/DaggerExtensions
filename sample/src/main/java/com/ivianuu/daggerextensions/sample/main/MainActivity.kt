package com.ivianuu.daggerextensions.sample.main

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.bluelinelabs.conductor.Controller
import com.ivianuu.daggerextensions.AutoContribute
import com.ivianuu.daggerextensions.BindsTo
import com.ivianuu.daggerextensions.conductor.HasControllerInjector
import com.ivianuu.daggerextensions.sample.R
import com.ivianuu.daggerextensions.sample.injection.*
import com.ivianuu.daggerextensions.sample.multibinding.Logger
import com.ivianuu.daggerextensions.sample.multibinding.MediaPlayer
import com.ivianuu.daggerextensions.sample.multibinding.Pizza
import com.ivianuu.daggerextensions.sample.multibinding.Translator
import com.ivianuu.daggerextensions.view.HasViewInjector
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

@ActivityBindingModule
@PerActivity
@ActivityBindingSet
@AutoContribute(modules = [ControllerBindingModule_::class, FragmentBindingModule_::class, ViewBindingModule_::class])
class MainActivity : AppCompatActivity(), HasSupportFragmentInjector, HasControllerInjector, HasViewInjector {

    @Inject lateinit var app: Application
    @Inject lateinit var loggers: Set<@JvmSuppressWildcards Logger>
    @Inject lateinit var mediaPlayers: Set<@JvmSuppressWildcards MediaPlayer>
    @Inject lateinit var pizzas: Map<String, @JvmSuppressWildcards Pizza>
    @Inject lateinit var translators: Map<String, @JvmSuppressWildcards Translator>

    @Inject lateinit var controllerInjector: DispatchingAndroidInjector<Controller>
    @Inject lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>
    @Inject lateinit var viewInjector: DispatchingAndroidInjector<View>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(MainFragment(), "main")
                .commitNow()
        }
    }

    override fun controllerInjector() = controllerInjector

    override fun supportFragmentInjector() = supportFragmentInjector

    override fun viewInjector() = viewInjector
}


@ActivityBindingModule
@PerActivity
@BindsTo(types = [Activity::class, AppCompatActivity::class, FragmentActivity::class])
@AutoContribute
class OtherActivity : AppCompatActivity()