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

package com.ivianuu.daggerextensions.processor.contributeinjector

import com.google.auto.common.AnnotationMirrors
import com.google.auto.common.BasicAnnotationProcessor
import com.google.auto.common.MoreElements
import com.google.common.collect.SetMultimap
import com.ivianuu.daggerextensions.ContributeInjector
import com.ivianuu.daggerextensions.processor.injector.InjectorKey
import com.ivianuu.daggerextensions.processor.injector.InjectorKeyFinderProcessingStep
import com.ivianuu.daggerextensions.processor.util.Module
import com.ivianuu.daggerextensions.processor.util.getClassArrayValues
import com.ivianuu.daggerextensions.processor.util.n
import com.ivianuu.daggerextensions.processor.util.writeFile
import com.squareup.javapoet.ClassName
import javax.annotation.processing.ProcessingEnvironment
import javax.inject.Scope
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.tools.Diagnostic

/**
 * @author Manuel Wrage (IVIanuu)
 */
class ContributeInjectorProcessingStep(
    private val processingEnv: ProcessingEnvironment,
    private val keyFinder: InjectorKeyFinderProcessingStep
) : BasicAnnotationProcessor.ProcessingStep {

    override fun process(elementsByAnnotation: SetMultimap<Class<out Annotation>, Element>): MutableSet<Element> {
        val deferred = mutableSetOf<Element>()

        elementsByAnnotation[ContributeInjector::class.java]
            .asSequence()
            .filterIsInstance<ExecutableElement>()
            .map { it to createAutoContributer(it) }
            .mapNotNull { (element, builder) ->
                if (builder == null) {
                    deferred.add(element)
                }

                builder
            }
            .map { it.build() }
            .map { ContributeInjectorGenerator(it) }
            .map { it.generate() }
            .toList()
            .forEach { writeFile(processingEnv, it) }

        return deferred
    }

    override fun annotations() =
        mutableSetOf(ContributeInjector::class.java)

    private fun createAutoContributer(element: ExecutableElement): ContributeInjectorDescriptor.Builder? {
        val isDaggerSupported = InjectorKey.DAGGER_SUPPORTED_TYPES.any {
            processingEnv.typeUtils.isAssignable(
                element.returnType,
                processingEnv.elementUtils.getTypeElement(it.baseType.toString()).asType()
            )

        }

        val injectedType = element.returnType

        val key = keyFinder.keys.firstOrNull {
            processingEnv.typeUtils.isAssignable(
                injectedType,
                processingEnv.elementUtils.getTypeElement(it.baseType.toString()).asType()
            )
        }

        if (key == null) {
            processingEnv.messager.printMessage(
                Diagnostic.Kind.ERROR,
                "no matching binding key found for $element"
            )
        }

        val builder =
            ContributeInjectorDescriptor.builder(
                element,
                key?.baseType,
                key?.mapKey
            )

        processingEnv.n { "create auto contribute for ${element.simpleName}" }

        AnnotationMirrors.getAnnotatedAnnotations(element, Scope::class.java)
            .forEach { builder.addScope(it) }

        val annotation =
            MoreElements.getAnnotationMirror(element, ContributeInjector::class.java).get()

        annotation.getClassArrayValues(
            "modules")
            .map { processingEnv.elementUtils.getTypeElement(it) }
            .map { Module(ClassName.get(it), it.modifiers) }
            .forEach { builder.addModule(it) }

        return builder
    }
}