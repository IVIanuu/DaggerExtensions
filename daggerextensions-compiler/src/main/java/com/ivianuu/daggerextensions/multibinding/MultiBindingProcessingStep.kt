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
import com.ivianuu.daggerextensions.AutoBindsIntoMap
import com.ivianuu.daggerextensions.AutoBindsIntoSet
import com.ivianuu.daggerextensions.util.intoMapName
import com.ivianuu.daggerextensions.util.intoSetName
import com.ivianuu.daggerextensions.util.writeFile
import com.squareup.javapoet.ClassName
import dagger.MapKey
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element


/**
 * @author Manuel Wrage (IVIanuu)
 */
class MultiBindingProcessingStep(private val processingEnv: ProcessingEnvironment) : BasicAnnotationProcessor.ProcessingStep {

    override fun process(elementsByAnnotation: SetMultimap<Class<out Annotation>, Element>): MutableSet<Element> {
        elementsByAnnotation[AutoBindsIntoMap::class.java]
            .map(this::createMapBindingDescriptor)
            .map(::AutoMapBindingGenerator)
            .map(AutoMapBindingGenerator::generate)
            .forEach { writeFile(processingEnv, it) }

        elementsByAnnotation[AutoBindsIntoSet::class.java]
            .map(this::createSetBindingDescriptor)
            .map(::AutoSetBindingGenerator)
            .map(AutoSetBindingGenerator::generate)
            .forEach { writeFile(processingEnv, it) }

        return mutableSetOf()
    }

    override fun annotations() =
        mutableSetOf(AutoBindsIntoMap::class.java, AutoBindsIntoSet::class.java)

    private fun createSetBindingDescriptor(element: Element): AutoSetBindingDescriptor {
        val annotation =
            MoreElements.getAnnotationMirror(element, AutoBindsIntoSet::class.java).get()

        val type =
            AnnotationMirrors.getAnnotationValue(annotation, "type")
                .value.toString()

        val moduleName = element.intoSetName()

        return AutoSetBindingDescriptor(
            ClassName.bestGuess(type), moduleName, ClassName.bestGuess(element.asType().toString())
        )
    }

    private fun createMapBindingDescriptor(element: Element): AutoMapBindingDescriptor {
        val annotation =
            MoreElements.getAnnotationMirror(element, AutoBindsIntoMap::class.java).get()

        val type =
            AnnotationMirrors.getAnnotationValue(annotation, "type")
                .value.toString()

        val mapKey =
            AnnotationMirrors.getAnnotatedAnnotations(element, MapKey::class.java)
                .first()

        val moduleName = element.intoMapName()

        return AutoMapBindingDescriptor(
            ClassName.bestGuess(type), moduleName, mapKey, ClassName.bestGuess(element.asType().toString())
        )
    }

}