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

package com.ivianuu.daggerextensions.sample.injector

import android.view.View

object ViewInjection {

    fun inject(view: View) {
        val hasViewInjector = findHasViewInjector(view)
        val viewInjector = hasViewInjector.viewInjector()
        viewInjector.inject(view)
    }

    private fun findHasViewInjector(view: View): HasViewInjector {
        if (view.context is HasViewInjector) {
            return view.context as HasViewInjector
        } else if (view.context.applicationContext is HasViewInjector) {
            return view.context.applicationContext as HasViewInjector
        }

        throw IllegalArgumentException(
            String.format(
                "No injector was found for %s", view.javaClass.canonicalName
            )
        )
    }

}