/*
 * Copyright 2018 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.daggerextensions.sample.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.ivianuu.daggerextensions.sample.deps.ActivityDependency
import com.ivianuu.daggerextensions.sample.deps.AppDependency
import com.ivianuu.daggerextensions.sample.deps.ChildFragmentDependency
import com.ivianuu.daggerextensions.sample.deps.FragmentDependency
import com.ivianuu.daggerextensions.sample.deps.ViewDependency
import com.ivianuu.daggerextensions.view.ViewInjection
import javax.inject.Inject

class MyView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    @Inject lateinit var appDependency: AppDependency
    @Inject lateinit var activityDependency: ActivityDependency
    @Inject lateinit var fragmentDependency: FragmentDependency
    @Inject lateinit var childFragmentDependency: ChildFragmentDependency
    @Inject lateinit var viewDependency: ViewDependency

    override fun onFinishInflate() {
        super.onFinishInflate()
        ViewInjection.inject(this)
    }

}