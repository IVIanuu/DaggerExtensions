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

package com.ivianuu.daggerextensions.injectorregistry

import com.google.auto.common.BasicAnnotationProcessor
import com.google.auto.common.MoreElements
import com.google.common.collect.SetMultimap
import com.ivianuu.daggerextensions.InjectorRegistry
import com.squareup.javapoet.ClassName
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

/**
 * @author Manuel Wrage (IVIanuu)
 */
class InjectorRegistryProcessingStep(private val processingEnv: ProcessingEnvironment) : BasicAnnotationProcessor.ProcessingStep {

    override fun process(elementsByAnnotation: SetMultimap<Class<out Annotation>, Element>): MutableSet<Element> {
        elementsByAnnotation[InjectorRegistry::class.java]
            .map(this::createDescriptors)
            .flatten()
            .map(::InjectorRegistryGenerator)
            .flatMap(InjectorRegistryGenerator::generate)
            .forEach { writeFile(processingEnv, it) }

        return mutableSetOf()
    }

    override fun annotations() = mutableSetOf(InjectorRegistry::class.java)

    private fun createDescriptors(element: Element): Set<InjectorRegistryDescriptor> {
        val annotation =
            MoreElements.getAnnotationMirror(element, InjectorRegistry::class.java)
                .get()

        return annotation.getClassArrayValues("types")
            .map {
                val target = ClassName.get(element as TypeElement)

                val type = ClassName.bestGuess(it)

                val simpleName = type.simpleName()

                val hasInjectorName =
                    ClassName.bestGuess("${target.packageName()}.Has${simpleName}Injector")

                val mapKeyName =
                    ClassName.bestGuess("${target.packageName()}.${simpleName}Key")

                val injectionModuleName =
                    ClassName.bestGuess("${target.packageName()}.${simpleName}InjectionModule")

                InjectorRegistryDescriptor(
                    element, target, type, hasInjectorName, mapKeyName, injectionModuleName)
            }
            .toSet()
    }
}