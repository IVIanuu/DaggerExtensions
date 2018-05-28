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

package com.ivianuu.daggerextensions.glide

import android.content.Context
import com.bumptech.glide.module.AppGlideModule
import com.ivianuu.daggerextensions.InjectorCreator

@InjectorCreator([AppGlideModule::class])
interface AppGlideModuleInjectorCreator

object GlideInjection {

    @JvmStatic
    fun inject(module: AppGlideModule, context: Context) {
        val hasAppGlideModuleInjector =
            findHasAppGlideModuleInjector(module, context)
        val controllerInjector = hasAppGlideModuleInjector.appGlideModuleInjector()
        controllerInjector.inject(module)
    }

    private fun findHasAppGlideModuleInjector(module: AppGlideModule, context: Context): HasAppGlideModuleInjector {
        if (context is HasAppGlideModuleInjector) {
            return context
        }

        val applicationContext = context.applicationContext
        if (applicationContext is HasAppGlideModuleInjector) {
            return applicationContext
        }

        throw IllegalArgumentException("no injector found for ${module.javaClass.name}")
    }

}