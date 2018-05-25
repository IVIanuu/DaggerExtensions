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

package com.ivianuu.daggerextensions.multibinding

import com.google.auto.common.AnnotationMirrors
import com.google.auto.common.BasicAnnotationProcessor
import com.google.auto.common.MoreElements
import com.google.common.collect.SetMultimap
import com.ivianuu.daggerextensions.AutoBindsIntoSet
import com.ivianuu.daggerextensions.util.n
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.Element
import javax.lang.model.type.TypeMirror
import com.google.auto.common.MoreTypes
import com.google.common.collect.Iterables.getOnlyElement
import com.ivianuu.daggerextensions.AutoBindsIntoMap
import com.ivianuu.daggerextensions.BindingModule
import com.ivianuu.daggerextensions.util.writeFile
import com.squareup.javapoet.ClassName
import javax.lang.model.element.ExecutableElement
import javax.lang.model.type.MirroredTypesException
import javax.lang.model.type.WildcardType
import javax.lang.model.util.ElementFilter
import javax.lang.model.util.ElementFilter.methodsIn
import com.sun.tools.javac.file.BaseFileObject.getSimpleName
import dagger.MapKey
import javax.lang.model.element.AnnotationValue


/**
 * @author Manuel Wrage (IVIanuu)
 */
class MultiBindingProcessingStep(private val processingEnv: ProcessingEnvironment) : BasicAnnotationProcessor.ProcessingStep {

    override fun process(elementsByAnnotation: SetMultimap<Class<out Annotation>, Element>): MutableSet<Element> {
        findMapBindings(elementsByAnnotation[AutoBindsIntoMap::class.java])
            .map(AutoMapBindingDescriptor.Builder::build)
            .map(::AutoMapBindingGenerator)
            .map(AutoMapBindingGenerator::generate)
            .forEach { writeFile(processingEnv, it) }

        findSetBindings(elementsByAnnotation[AutoBindsIntoSet::class.java])
            .map(AutoSetBindingDescriptor.Builder::build)
            .map(::AutoSetBindingGenerator)
            .map(AutoSetBindingGenerator::generate)
            .forEach { writeFile(processingEnv, it) }

        return mutableSetOf()
    }

    override fun annotations() =
        mutableSetOf(AutoBindsIntoMap::class.java, AutoBindsIntoSet::class.java)

    private fun findSetBindings(elements: Set<Element>): Set<AutoSetBindingDescriptor.Builder> {
        val typeMap =
            mutableMapOf<String, MutableMap<String, AutoSetBindingDescriptor.Builder>>()

        for (element in elements) {
            val annotation =
                MoreElements.getAnnotationMirror(element, AutoBindsIntoSet::class.java).get()

            val type =
                AnnotationMirrors.getAnnotationValue(annotation, "type")
                    .value.toString()

            val bindingModule =
                    AnnotationMirrors.getAnnotatedAnnotations(element, BindingModule::class.java)
                        .firstOrNull()
                        ?.annotationType
                        ?.toString()

            val moduleName = if (bindingModule != null) {
                ClassName.bestGuess(bindingModule + "_")
            } else {
                ClassName.bestGuess(type + "Module")
            }

            val moduleMap = typeMap.getOrPut(type) { mutableMapOf() }

            val builder = moduleMap.getOrPut(moduleName.toString()) {
                AutoSetBindingDescriptor.builder(type, moduleName)
            }

            builder.addItem(element.asType().toString())
        }

        return typeMap.values
            .toList()
            .flatMap { it.values }
            .toSet()
    }

    private fun findMapBindings(elements: Set<Element>): Set<AutoMapBindingDescriptor.Builder> {
        val typeMap =
            mutableMapOf<String, MutableMap<String, AutoMapBindingDescriptor.Builder>>()

        for (element in elements) {
            val annotation =
                MoreElements.getAnnotationMirror(element, AutoBindsIntoMap::class.java).get()

            val type =
                AnnotationMirrors.getAnnotationValue(annotation, "type")
                    .value.toString()

            val mapKey =
                AnnotationMirrors.getAnnotatedAnnotations(element, MapKey::class.java)
                    .first()

            val bindingModule =
                AnnotationMirrors.getAnnotatedAnnotations(element, BindingModule::class.java)
                    .firstOrNull()
                    ?.annotationType
                    ?.toString()

            val moduleName = if (bindingModule != null) {
                ClassName.bestGuess(bindingModule + "_")
            } else {
                ClassName.bestGuess(type + "Module")
            }

            val moduleMap = typeMap.getOrPut(type) { mutableMapOf() }

            val builder = moduleMap.getOrPut(moduleName.toString()) {
                AutoMapBindingDescriptor.builder(type, moduleName)
            }

            builder.putItem(mapKey, element.asType().toString())
        }

        return typeMap
            .values
            .flatMap { it.values }
            .toSet()
    }
}