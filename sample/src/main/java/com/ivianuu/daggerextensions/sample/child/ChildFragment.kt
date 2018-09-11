package com.ivianuu.daggerextensions.sample.child

import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ivianuu.daggerextensions.sample.deps.ActivityDependency
import com.ivianuu.daggerextensions.sample.deps.AppDependency
import com.ivianuu.daggerextensions.sample.deps.ChildFragmentDependency
import com.ivianuu.daggerextensions.sample.deps.FragmentDependency
import com.ivianuu.daggerextensions.sample.view.MyView
import com.ivianuu.daggerextensions.view.HasViewInjector
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class ChildFragment : Fragment(), HasViewInjector {

    @Inject lateinit var appDependency: AppDependency
    @Inject lateinit var activityDependency: ActivityDependency
    @Inject lateinit var fragmentDependency: FragmentDependency
    @Inject lateinit var childFragmentDependency: ChildFragmentDependency

    @Inject lateinit var viewInjector: DispatchingAndroidInjector<View>

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val injectorContext = object : ContextWrapper(requireContext()), HasViewInjector {
            override fun viewInjector() = this@ChildFragment.viewInjector
        }

        return MyView(injectorContext)
    }

    override fun viewInjector(): AndroidInjector<View> = viewInjector
}