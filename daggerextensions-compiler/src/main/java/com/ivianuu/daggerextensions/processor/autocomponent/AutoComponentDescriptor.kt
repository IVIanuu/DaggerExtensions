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

package com.ivianuu.daggerextensions.processor.autocomponent

import com.ivianuu.daggerextensions.processor.util.Module
import com.squareup.javapoet.ClassName
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.TypeElement

data class AutoComponentDescriptor(
    val type: ComponentType,
    val element: TypeElement,
    val target: ClassName,
    val component: ClassName,
    val componentBuilder: ClassName,
    val injectClasses: Set<ClassName>,
    val modules: Set<Module>,
    val dependencies: Set<ClassName>,
    val scopes: Set<AnnotationMirror>,
    val subcomponents: Set<ClassName>,
    val superInterfaces: Set<ClassName>
) {

    class Builder internal constructor(
        val type: ComponentType,
        val element: TypeElement,
        val target: ClassName,
        val componentName: ClassName,
        val componentBuilderName: ClassName
    ) {

        private val injectClasses = mutableSetOf<ClassName>()
        private val modules = mutableSetOf<Module>()
        private val dependencies = mutableSetOf<ClassName>()
        private val scopes = mutableSetOf<AnnotationMirror>()
        private val subcomponents = mutableSetOf<ClassName>()
        private val superInterfaces = mutableSetOf<ClassName>()

        fun addInjectClass(injectClass: String): Builder {
            injectClasses.add(ClassName.bestGuess(injectClass))
            return this
        }

        fun addModule(module: Module): Builder {
            modules.add(module)
            return this
        }

        fun addDependency(dependency: String): Builder {
            dependencies.add(ClassName.bestGuess(dependency))
            return this
        }

        fun addScope(scope: AnnotationMirror): Builder {
            scopes.add(scope)
            return this
        }

        fun addSubcomponent(subcomponent: String): Builder {
            subcomponents.add(ClassName.bestGuess(subcomponent))
            return this
        }

        fun addSuperInterface(superInterface: String): Builder {
            superInterfaces.add(ClassName.bestGuess(superInterface))
            return this
        }

        fun build(): AutoComponentDescriptor {
            return AutoComponentDescriptor(
                type,
                element,
                target,
                componentName,
                componentBuilderName,
                injectClasses,
                modules,
                dependencies,
                scopes,
                subcomponents,
                superInterfaces
            )
        }

    }

    companion object {

        fun builder(element: TypeElement, target: ClassName, type: ComponentType): Builder {
            val component = if (target.toString().endsWith("Component")) {
                ClassName.bestGuess(target.toString() + "_")
            } else {
                ClassName.bestGuess(target.toString() + "Component")
            }
            val componentBuilder = component.nestedClass("Builder")
            return Builder(type, element, target, component, componentBuilder)
        }

    }
}