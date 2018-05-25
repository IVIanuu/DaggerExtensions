package com.ivianuu.daggerextensions.sample.child

import android.content.Context
import android.support.v4.app.Fragment
import com.ivianuu.daggerextensions.AutoContribute
import com.ivianuu.daggerextensions.sample.deps.ActivityDependency
import com.ivianuu.daggerextensions.sample.deps.ChildFragmentDependency
import com.ivianuu.daggerextensions.sample.deps.FragmentDependency
import com.ivianuu.daggerextensions.sample.injection.PerChildFragment
import com.ivianuu.daggerextensions.sample.main.MainFragmentBuilder
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

/**
 * @author Manuel Wrage (IVIanuu)
 */

@AutoContribute(modules = [MainFragmentBuilder::class])
class OtherChildFragment : Fragment()

@PerChildFragment
@AutoContribute
class ChildFragment : Fragment() {

    @Inject lateinit var activityDependency: ActivityDependency
    @Inject lateinit var fragmentDependency: FragmentDependency
    @Inject lateinit var childFragmentDependency: ChildFragmentDependency

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

}