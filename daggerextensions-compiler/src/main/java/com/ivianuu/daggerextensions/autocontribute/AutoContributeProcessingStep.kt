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

package com.ivianuu.daggerextensions.autocontribute

import com.google.auto.common.AnnotationMirrors
import com.google.auto.common.BasicAnnotationProcessor
import com.google.auto.common.MoreElements
import com.google.common.collect.SetMultimap
import com.ivianuu.daggerextensions.AutoContribute
import com.ivianuu.daggerextensions.BindsTypes
import com.ivianuu.daggerextensions.util.Module
import com.ivianuu.daggerextensions.util.getClassArrayValues
import com.ivianuu.daggerextensions.util.n
import com.ivianuu.daggerextensions.util.writeFile
import com.squareup.javapoet.ClassName
import javax.annotation.processing.ProcessingEnvironment
import javax.inject.Scope
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

/**
 * @author Manuel Wrage (IVIanuu)
 */
class AutoContributeProcessingStep(
    private val processingEnv: ProcessingEnvironment
) : BasicAnnotationProcessor.ProcessingStep {

    override fun process(elementsByAnnotation: SetMultimap<Class<out Annotation>, Element>): MutableSet<Element> {
        val autoContributions = elementsByAnnotation[AutoContribute::class.java]
            .filterIsInstance<TypeElement>()
            .map(this::createAutoContributer)
            .toSet()

        autoContributions
            .map(AutoContributeDescriptor.Builder::build)
            .map(::AutoContributeGenerator)
            .map(AutoContributeGenerator::generate)
            .forEach { writeFile(processingEnv, it) }

        return mutableSetOf()
    }

    override fun annotations() =
        mutableSetOf(AutoContribute::class.java)

    private fun createAutoContributer(element: TypeElement): AutoContributeDescriptor.Builder {
        val builder = AutoContributeDescriptor.builder(element)

        processingEnv.n { "create auto contribute for ${element.simpleName}" }

        MoreElements.getAnnotationMirror(element, BindsTypes::class.java).orNull()
            ?.getClassArrayValues("types")
            ?.forEach { builder.addBinding(it) }

        // scopes
        AnnotationMirrors.getAnnotatedAnnotations(element, Scope::class.java)
            .forEach { builder.addScope(it) }

        val annotation = MoreElements.getAnnotationMirror(element, AutoContribute::class.java).get()

        // modules
        annotation.getClassArrayValues(
            "modules")
            .map {
                val moduleType = processingEnv.elementUtils.getTypeElement(it)
                Module(ClassName.get(moduleType), moduleType.modifiers)
            }
            .forEach { builder.addModule(it) }

        return builder
    }
}