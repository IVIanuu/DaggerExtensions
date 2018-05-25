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

import com.google.auto.common.AnnotationMirrors
import com.google.auto.common.BasicAnnotationProcessor
import com.google.auto.common.MoreElements
import com.google.common.collect.SetMultimap
import com.ivianuu.daggerextensions.AutoContribute
import com.ivianuu.daggerextensions.BindingSet
import com.ivianuu.daggerextensions.BindsTo
import com.ivianuu.daggerextensions.processor.injector.InjectorKey
import com.ivianuu.daggerextensions.processor.util.*
import com.squareup.javapoet.ClassName
import javax.annotation.processing.ProcessingEnvironment
import javax.inject.Scope
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement

/**
 * @author Manuel Wrage (IVIanuu)
 */
class AutoContributeProcessingStep(
    private val processingEnv: ProcessingEnvironment) : BasicAnnotationProcessor.ProcessingStep {

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
        val isDaggerSupported = InjectorKey.DAGGER_SUPPORTED_TYPES.any {
            processingEnv.typeUtils.isAssignable(
                element.asType(),
                processingEnv.elementUtils.getTypeElement(it.baseType.toString()).asType()
            )

        }

        val type = if (isDaggerSupported) {
            ContributionType.ANDROID_INJECTOR
        } else {
            ContributionType.INJECTOR
        }

        val builder = AutoContributeDescriptor.builder(element, type)

        processingEnv.n { "create auto contribute for ${element.simpleName}" }

        AnnotationMirrors.getAnnotatedAnnotations(element, Scope::class.java)
            .forEach { builder.addScope(it) }

        val annotation = MoreElements.getAnnotationMirror(element, AutoContribute::class.java).get()

        annotation.getClassArrayValues(
            "modules")
            .map {
                val moduleType = processingEnv.elementUtils.getTypeElement(it)
                Module(ClassName.get(moduleType), moduleType.modifiers)
            }
            .forEach { builder.addModule(it) }

        // auto include binding modules
        if (MoreElements.isAnnotationPresent(element, BindsTo::class.java)
            || AnnotationMirrors.getAnnotatedAnnotations(element, BindingSet::class.java).isNotEmpty()) {
            builder.addModule(
                Module(
                    element.bindsToName(), setOf(Modifier.PUBLIC, Modifier.ABSTRACT)
                )
            )
        }

        return builder
    }
}