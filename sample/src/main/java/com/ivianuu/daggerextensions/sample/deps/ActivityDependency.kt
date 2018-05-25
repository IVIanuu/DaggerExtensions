package com.ivianuu.daggerextensions.sample.deps

import com.ivianuu.daggerextensions.sample.app.AppComponent
import com.ivianuu.daggerextensions.sample.main.MainActivity
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjector
import javax.inject.Inject

/**
 * @author Manuel Wrage (IVIanuu)
 */
class ActivityDependency @Inject constructor(mainActivity: MainActivity)