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

package com.ivianuu.daggerextensions.autocomponent

import com.google.auto.common.AnnotationMirrors
import com.google.auto.common.BasicAnnotationProcessor
import com.google.auto.common.MoreElements
import com.google.common.collect.SetMultimap
import com.ivianuu.daggerextensions.AutoComponent
import com.ivianuu.daggerextensions.AutoSubcomponent
import com.ivianuu.daggerextensions.BindingSet
import com.ivianuu.daggerextensions.BindsTo
import com.ivianuu.daggerextensions.util.Module
import com.ivianuu.daggerextensions.util.bindsToName
import com.ivianuu.daggerextensions.util.getClassArrayValues
import com.ivianuu.daggerextensions.util.writeFile
import com.squareup.javapoet.ClassName
import javax.annotation.processing.ProcessingEnvironment
import javax.inject.Scope
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement

class AutoComponentProcessingStep(private val processingEnv: ProcessingEnvironment) : BasicAnnotationProcessor.ProcessingStep {

    override fun process(elementsByAnnotation: SetMultimap<Class<out Annotation>, Element>): MutableSet<Element> {
        elementsByAnnotation[AutoSubcomponent::class.java]
            .filterIsInstance<TypeElement>()
            .map { createDescriptor(it, ComponentType.SUBCOMPONENT) }
            .map(AutoComponentDescriptor.Builder::build)
            .map(::AutoComponentGenerator)
            .map(AutoComponentGenerator::generate)
            .forEach { writeFile(processingEnv, it) }

        elementsByAnnotation[AutoComponent::class.java]
            .filterIsInstance<TypeElement>()
            .map { createDescriptor(it, ComponentType.COMPONENT) }
            .map(AutoComponentDescriptor.Builder::build)
            .map(::AutoComponentGenerator)
            .map(AutoComponentGenerator::generate)
            .forEach { writeFile(processingEnv, it) }

        return mutableSetOf()
    }

    override fun annotations() =
        mutableSetOf(AutoComponent::class.java, AutoSubcomponent::class.java)

    private fun createDescriptor(
        element: TypeElement,
        type: ComponentType
    ): AutoComponentDescriptor.Builder {
        val builder =
            AutoComponentDescriptor.builder(element, type)

        AnnotationMirrors.getAnnotatedAnnotations(element, Scope::class.java)
            .forEach { builder.addScope(it) }

        val annotation =
            if (type == ComponentType.COMPONENT) {
                MoreElements.getAnnotationMirror(element, AutoComponent::class.java).get()
            } else {
                MoreElements.getAnnotationMirror(element, AutoSubcomponent::class.java).get()
            }

        if (type == ComponentType.COMPONENT) {
            // dependencies
            annotation.getClassArrayValues("dependencies")
                .forEach { builder.addDependency(it) }
        }

        // modules
        annotation.getClassArrayValues(
            "modules")
            .map {
                val moduleType =
                    processingEnv.elementUtils.getTypeElement(it)
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

        if (type == ComponentType.COMPONENT) {
            // subcomponents
            annotation.getClassArrayValues(
                "subcomponents")
                .forEach { builder.addSubcomponent(it) }
        }

        // injects
        annotation.getClassArrayValues(
            "injects")
            .forEach { builder.addInjectClass(it) }

        // superinterfaces
        annotation.getClassArrayValues(
            "superInterfaces")
            .forEach { builder.addSuperInterface(it) }

        return builder
    }

}