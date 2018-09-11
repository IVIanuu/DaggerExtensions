package com.ivianuu.daggerextensions.sample.deps

import com.ivianuu.daggerextensions.sample.app.App
import com.ivianuu.daggerextensions.sample.child.ChildFragment
import com.ivianuu.daggerextensions.sample.main.MainActivity
import com.ivianuu.daggerextensions.sample.main.MainFragment
import com.ivianuu.daggerextensions.sample.view.MyView
import javax.inject.Inject

/**
 * @author Manuel Wrage (IVIanuu)
 */
class AppDependency @Inject constructor(app: App)

/**
 * @author Manuel Wrage (IVIanuu)
 */
class ActivityDependency @Inject constructor(mainActivity: MainActivity)

/**
 * @author Manuel Wrage (IVIanuu)
 */
class FragmentDependency @Inject constructor(mainFragment: MainFragment)

/**
 * @author Manuel Wrage (IVIanuu)
 */
class ChildFragmentDependency @Inject constructor(childFragment: ChildFragment)

class ViewDependency @Inject constructor(myView: MyView)