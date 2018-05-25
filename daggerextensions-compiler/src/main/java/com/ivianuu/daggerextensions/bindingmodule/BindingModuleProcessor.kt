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

package com.ivianuu.daggerextensions.bindingmodule

import com.google.auto.common.AnnotationMirrors
import com.google.auto.common.MoreElements
import com.ivianuu.daggerextensions.AutoBindsIntoMap
import com.ivianuu.daggerextensions.AutoBindsIntoSet
import com.ivianuu.daggerextensions.AutoContribute
import com.ivianuu.daggerextensions.BindingModule
import com.ivianuu.daggerextensions.util.*
import com.squareup.javapoet.ClassName
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier

/**
 * @author Manuel Wrage (IVIanuu)
 */
class BindingModuleProcessor(private val processingEnv: ProcessingEnvironment) {

    private var processed = false

    fun postRound(roundEnv: RoundEnvironment) {
        if (processed) return
        processed = true

        val elements = mutableSetOf<Element>()
        elements.addAll(roundEnv.getElementsAnnotatedWith(AutoContribute::class.java))
        elements.addAll(roundEnv.getElementsAnnotatedWith(AutoBindsIntoSet::class.java))
        elements.addAll(roundEnv.getElementsAnnotatedWith(AutoBindsIntoMap::class.java))

        createBindingModuleDescriptors(elements)
            .map(BindingModuleDescriptor.Builder::build)
            .map(::BindingModuleGenerator)
            .map(BindingModuleGenerator::generate)
            .forEach { writeFile(processingEnv, it) }
    }

    private fun createBindingModuleDescriptors(elements: Set<Element>): Set<BindingModuleDescriptor.Builder> {
        val builders =
            mutableMapOf<ClassName, BindingModuleDescriptor.Builder>()

        for (element in elements) {
            val bindingModuleAnnotation =
                AnnotationMirrors.getAnnotatedAnnotations(element, BindingModule::class.java)
                    .firstOrNull() ?: continue

            val builder = builders.getOrPut(
                ClassName.bestGuess(bindingModuleAnnotation.annotationType.toString())) {
                BindingModuleDescriptor.builder(bindingModuleAnnotation)
            }

            val module = when {
                MoreElements.isAnnotationPresent(element, AutoContribute::class.java) -> {
                    Module(
                        element.autoContributeName(), setOf(Modifier.PUBLIC, Modifier.ABSTRACT)
                    )
                }
                MoreElements.isAnnotationPresent(element, AutoBindsIntoMap::class.java) -> {
                    Module(
                        element.intoMapName(), setOf(Modifier.PUBLIC, Modifier.ABSTRACT)
                    )
                }
                MoreElements.isAnnotationPresent(element, AutoBindsIntoSet::class.java) -> {
                    Module(
                        element.intoSetName(), setOf(Modifier.PUBLIC, Modifier.ABSTRACT)
                    )
                }
                else -> {
                    // todo print
                    throw IllegalArgumentException()
                }
            }

            builder.addModule(module)
        }

        return builders
            .values
            .toSet()
    }
}