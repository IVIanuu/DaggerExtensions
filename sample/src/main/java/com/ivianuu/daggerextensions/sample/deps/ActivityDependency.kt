package com.ivianuu.daggerextensions.sample.deps

import com.ivianuu.daggerextensions.BindsTo
import com.ivianuu.daggerextensions.sample.main.MainActivity
import javax.inject.Inject

/**
 * @author Manuel Wrage (IVIanuu)
 */
@BindsTo(types = [Any::class])
class ActivityDependency @Inject constructor(mainActivity: MainActivity)