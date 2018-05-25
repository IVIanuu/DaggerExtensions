package com.ivianuu.daggerextensions.sample.deps

import com.ivianuu.daggerextensions.sample.main.MainActivity
import javax.inject.Inject

/**
 * @author Manuel Wrage (IVIanuu)
 */
class ActivityDependency @Inject constructor(mainActivity: MainActivity)