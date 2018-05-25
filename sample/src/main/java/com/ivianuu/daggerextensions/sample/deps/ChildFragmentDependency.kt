package com.ivianuu.daggerextensions.sample.deps

import com.ivianuu.daggerextensions.sample.child.ChildFragment
import javax.inject.Inject

/**
 * @author Manuel Wrage (IVIanuu)
 */
class ChildFragmentDependency @Inject constructor(childFragment: ChildFragment)