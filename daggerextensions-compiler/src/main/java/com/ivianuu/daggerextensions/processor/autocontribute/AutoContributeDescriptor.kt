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

package com.ivianuu.daggerextensions.processor.autocontribute

import com.ivianuu.daggerextensions.processor.util.Module
import com.squareup.javapoet.ClassName
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

/**
 * @author Manuel Wrage (IVIanuu)
 */
data class AutoContributeDescriptor(
    val element: Element,
    val target: ClassName,
    val builder: ClassName,
    val modules: Set<Module>,
    val scopes: Set<AnnotationMirror>
) {

    class Builder internal constructor(
        val element: TypeElement,
        val target: ClassName,
        val builder: ClassName
    ) {

        private val modules = mutableSetOf<Module>()
        private val scopes = mutableSetOf<AnnotationMirror>()

        fun addModule(module: Module): Builder {
            modules.add(module)
            return this
        }

        fun addScope(scope: AnnotationMirror): Builder {
            scopes.add(scope)
            return this
        }

        fun build(): AutoContributeDescriptor {
            return AutoContributeDescriptor(
                element,
                target,
                builder,
                modules,
                scopes
            )
        }

    }

    companion object {

        fun builder(element: TypeElement): Builder {
            val target = ClassName.get(element)
            val builder = ClassName.bestGuess(target.toString() + "Builder")
            return Builder(element, target, builder)
        }

    }

}