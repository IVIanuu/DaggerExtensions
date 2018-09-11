package com.ivianuu.daggerextensions.sample.injection

import com.ivianuu.daggerextensions.ContributeInjector
import com.ivianuu.daggerextensions.sample.child.ChildFragment
import com.ivianuu.daggerextensions.sample.main.MainActivity
import com.ivianuu.daggerextensions.sample.main.MainFragment
import com.ivianuu.daggerextensions.sample.view.MyView
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * @author Manuel Wrage (IVIanuu)
 */
@Module
abstract class ActivityBindingModule {

    @PerActivity
    @ContributesAndroidInjector(modules = [FragmentBindingModule::class])
    abstract fun bindMainActivity(): MainActivity
}

/**
 * @author Manuel Wrage (IVIanuu)
 */
@Module
abstract class FragmentBindingModule {

    @PerFragment
    @ContributesAndroidInjector(modules = [ChildFragmentBindingModule::class])
    abstract fun bindMainFragment(): MainFragment
}

@Module
abstract class ChildFragmentBindingModule {

    @PerChildFragment
    @ContributesAndroidInjector(modules = [ViewModule::class])
    abstract fun bindChildFragment(): ChildFragment
}

@Module(includes = [ViewBindingModule_BindMyView::class])
abstract class ViewModule

/**
 * @author Manuel Wrage (IVIanuu)
 */
@Module
abstract class ViewBindingModule {

    @PerView
    @ContributeInjector
    abstract fun bindMyView(): MyView
}