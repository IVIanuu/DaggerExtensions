package com.ivianuu.daggerextensions.sample.deps

import com.ivianuu.daggerextensions.sample.main.MainFragment
import javax.inject.Inject

/**
 * @author Manuel Wrage (IVIanuu)
 */
class FragmentDependency @Inject constructor(mainFragment: MainFragment)