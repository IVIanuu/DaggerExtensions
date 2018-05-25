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

package com.ivianuu.daggerextensions.processor.bindingmodule

import com.ivianuu.daggerextensions.processor.util.Module
import com.squareup.javapoet.ClassName
import javax.lang.model.element.AnnotationMirror

/**
 * @author Manuel Wrage (IVIanuu)
 */
data class BindingModuleDescriptor(
    val annotation: AnnotationMirror,
    val bindingModule: ClassName,
    val modules: Set<Module>
) {

    class Builder internal constructor(
        private val annotation: AnnotationMirror,
        private val bindingModule: ClassName
    ) {

        private val modules = mutableSetOf<Module>()

        fun addModule(module: Module): Builder {
            modules.add(module)
            return this
        }

        fun build(): BindingModuleDescriptor {
            return BindingModuleDescriptor(
                annotation,
                bindingModule,
                modules
            )
        }
    }

    companion object {

        fun builder(annotation: AnnotationMirror): Builder {
            val name =
                ClassName.bestGuess(annotation.annotationType.asElement().asType().toString() + "_")
            return Builder(
                annotation,
                name
            )
        }

    }

}