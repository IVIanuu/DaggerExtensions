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

package com.ivianuu.daggerextensions.sample.controller

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.ivianuu.daggerextensions.AutoContribute
import com.ivianuu.daggerextensions.conductor.ConductorInjection
import com.ivianuu.daggerextensions.sample.deps.ActivityDependency
import com.ivianuu.daggerextensions.sample.deps.AppDependency
import com.ivianuu.daggerextensions.sample.deps.ControllerDependency
import com.ivianuu.daggerextensions.sample.injection.ControllerBindingModule
import com.ivianuu.daggerextensions.sample.injection.PerController
import javax.inject.Inject

/**
 * @author Manuel Wrage (IVIanuu)
 */
@ControllerBindingModule
@PerController
@AutoContribute
class LoginController : Controller() {

    @Inject lateinit var appDependency: AppDependency
    @Inject lateinit var activityDependency: ActivityDependency
    @Inject lateinit var controllerDependency: ControllerDependency

    private var injected = false

    override fun onContextAvailable(context: Context) {
        super.onContextAvailable(context)
        if (!injected) {
            injected = true
            ConductorInjection.inject(this)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return View(activity)
    }

}