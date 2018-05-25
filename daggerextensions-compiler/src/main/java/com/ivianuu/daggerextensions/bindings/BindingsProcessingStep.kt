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

package com.ivianuu.daggerextensions.bindings

import com.google.auto.common.BasicAnnotationProcessor
import com.google.auto.common.MoreElements
import com.google.common.collect.SetMultimap
import com.ivianuu.daggerextensions.BindsTo
import com.ivianuu.daggerextensions.util.bindsToName
import com.ivianuu.daggerextensions.util.getClassArrayValues
import com.ivianuu.daggerextensions.util.writeFile
import com.squareup.javapoet.ClassName
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

/**
 * @author Manuel Wrage (IVIanuu)
 */
class BindingsProcessingStep(private val processingEnv: ProcessingEnvironment): BasicAnnotationProcessor.ProcessingStep {

    override fun process(elementsByAnnotation: SetMultimap<Class<out Annotation>, Element>): MutableSet<Element> {
        elementsByAnnotation[BindsTo::class.java]
            .map(this::createBindsToDescriptor)
            .map(::BindingsGenerator)
            .map(BindingsGenerator::generate)
            .forEach { writeFile(processingEnv, it) }

        return mutableSetOf()
    }

    override fun annotations() = mutableSetOf(BindsTo::class.java)

    private fun createBindsToDescriptor(element: Element): BindingsDescriptor {
        val annotation =
            MoreElements.getAnnotationMirror(element, BindsTo::class.java).get()

        val type = ClassName.get(element as TypeElement)

        val to = annotation.getClassArrayValues("types")
            .map(ClassName::bestGuess)
            .toSet()

        val moduleName = element.bindsToName()

        return BindingsDescriptor(element, type, moduleName, to)
    }
}