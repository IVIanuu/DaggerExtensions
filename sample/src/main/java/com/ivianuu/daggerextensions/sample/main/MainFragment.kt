package com.ivianuu.daggerextensions.sample.main

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import com.ivianuu.daggerextensions.AutoContribute
import com.ivianuu.daggerextensions.sample.child.ChildFragment
import com.ivianuu.daggerextensions.sample.child.ChildFragmentBuilder
import com.ivianuu.daggerextensions.sample.deps.ActivityDependency
import com.ivianuu.daggerextensions.sample.deps.FragmentDependency
import com.ivianuu.daggerextensions.sample.injection.FragmentBindingModule
import com.ivianuu.daggerextensions.sample.injection.PerFragment
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

/**
 * @author Manuel Wrage (IVIanuu)
 */
@PerFragment
@FragmentBindingModule
@AutoContribute(modules = [ChildFragmentBuilder::class])
class MainFragment : Fragment(), HasSupportFragmentInjector {

    @Inject lateinit var activityDependency: ActivityDependency
    @Inject lateinit var fragmentDependency: FragmentDependency
    @Inject lateinit var fragAct: FragmentActivity
    @Inject lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)

        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        childFragmentManager.beginTransaction()
            .add(ChildFragment(), "child")
            .commitNow()
    }

    override fun supportFragmentInjector() = supportFragmentInjector

}