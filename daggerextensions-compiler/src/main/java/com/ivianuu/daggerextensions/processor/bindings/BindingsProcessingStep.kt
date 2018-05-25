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

package com.ivianuu.daggerextensions.processor.bindings

import com.google.auto.common.AnnotationMirrors
import com.google.auto.common.BasicAnnotationProcessor
import com.google.auto.common.MoreElements
import com.google.common.collect.SetMultimap
import com.ivianuu.daggerextensions.*
import com.ivianuu.daggerextensions.processor.util.bindsToName
import com.ivianuu.daggerextensions.processor.util.getClassArrayValues
import com.ivianuu.daggerextensions.processor.util.writeFile
import com.squareup.javapoet.ClassName
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

/**
 * @author Manuel Wrage (IVIanuu)
 */
class BindingsProcessingStep(private val processingEnv: ProcessingEnvironment): BasicAnnotationProcessor.ProcessingStep {

    override fun process(elementsByAnnotation: SetMultimap<Class<out Annotation>, Element>): MutableSet<Element> {
        val elements = mutableSetOf<Element>()
        elements.addAll(elementsByAnnotation[AutoComponent::class.java])
        elements.addAll(elementsByAnnotation[AutoSubcomponent::class.java])
        elements.addAll(elementsByAnnotation[BindsTo::class.java])
        elements.addAll(elementsByAnnotation[AutoContribute::class.java])
        elements.addAll(elementsByAnnotation[AutoBindsIntoMap::class.java])
        elements.addAll(elementsByAnnotation[AutoBindsIntoSet::class.java])

        elements
            .filter(this::isValid)
            .map(this::createBindsToDescriptor)
            .map(::BindingsGenerator)
            .map(BindingsGenerator::generate)
            .forEach { writeFile(processingEnv, it) }

        return mutableSetOf()
    }

    override fun annotations() = mutableSetOf(
        AutoComponent::class.java,
        AutoSubcomponent::class.java,
        BindsTo::class.java,
        AutoContribute::class.java,
        AutoBindsIntoMap::class.java,
        AutoBindsIntoSet::class.java
    )

    private fun isValid(element: Element): Boolean {
        return MoreElements.isAnnotationPresent(element, BindsTo::class.java)
                || AnnotationMirrors
            .getAnnotatedAnnotations(element, BindingSet::class.java).isNotEmpty()
    }

    private fun createBindsToDescriptor(element: Element): BindingsDescriptor {
        val types = mutableSetOf<ClassName>()

        // binds to
        MoreElements.getAnnotationMirror(element, BindsTo::class.java).orNull()
            ?.getClassArrayValues("types")
            ?.map(ClassName::bestGuess)
            ?.forEach { types.add(it) }

        // binding sets
        AnnotationMirrors.getAnnotatedAnnotations(element, BindingSet::class.java)
            .map { processingEnv.elementUtils.getTypeElement(it.annotationType.toString()) }
            .map { MoreElements.getAnnotationMirror(it, BindingSet::class.java).get() }
            .flatMap { it.getClassArrayValues("types") }
            .map(ClassName::bestGuess)
            .forEach { types.add(it) }

        val type = ClassName.get(element as TypeElement)

        val moduleName = element.bindsToName()

        return BindingsDescriptor(element, type, moduleName, types)
    }
}