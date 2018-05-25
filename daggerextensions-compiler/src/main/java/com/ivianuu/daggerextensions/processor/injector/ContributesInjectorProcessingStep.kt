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

package com.ivianuu.daggerextensions.processor.injector

import com.google.auto.common.AnnotationMirrors
import com.google.auto.common.BasicAnnotationProcessor
import com.google.auto.common.MoreElements
import com.google.common.base.CaseFormat
import com.google.common.base.Joiner
import com.google.common.collect.SetMultimap
import com.ivianuu.daggerextensions.ContributesInjector
import com.ivianuu.daggerextensions.processor.util.getClassArrayValues
import com.ivianuu.daggerextensions.processor.util.writeFile
import com.squareup.javapoet.ClassName
import javax.annotation.processing.ProcessingEnvironment
import javax.inject.Scope
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement

/**
 * @author Manuel Wrage (IVIanuu)
 */
class ContributesInjectorProcessingStep(
    private val processingEnv: ProcessingEnvironment,
    private val keyFinder: InjectorKeyFinderProcessingStep
) : BasicAnnotationProcessor.ProcessingStep {

    override fun process(elementsByAnnotation: SetMultimap<Class<out Annotation>, Element>): MutableSet<Element> {
        val deferred = mutableSetOf<Element>()

        elementsByAnnotation[ContributesInjector::class.java]
            .filterIsInstance<ExecutableElement>()
            .mapNotNull {
                val descriptor = createDescriptor(it)
                if (descriptor == null) {
                    deferred.add(it)
                }

                descriptor
            }
            .map(::ContributesInjectorGenerator)
            .map(ContributesInjectorGenerator::generate)
            .forEach { writeFile(processingEnv, it) }

        return deferred
    }

    override fun annotations() = mutableSetOf(ContributesInjector::class.java)

    private fun createDescriptor(element: ExecutableElement): ContributesInjectorDescriptor? {
        val injectedType = element.returnType

        val key = keyFinder.keys.firstOrNull {
            processingEnv.typeUtils.isAssignable(
                injectedType,
                processingEnv.elementUtils.getTypeElement(it.baseType.toString()).asType()
            )
        } ?: return null

        val enclosingModule = ClassName.get(element.enclosingElement as TypeElement)

        val scopes =
            AnnotationMirrors.getAnnotatedAnnotations(element, Scope::class.java)

        val contributesAnnotation =
            MoreElements.getAnnotationMirror(element, ContributesInjector::class.java).get()

        val modules = contributesAnnotation.getClassArrayValues("modules")
            .map(ClassName::bestGuess)
            .toSet()

        val moduleName = enclosingModule
            .topLevelClassName()
            .peerClass(
                Joiner.on('_').join(enclosingModule.simpleNames())
                        + "_"
                        + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, element.simpleName.toString())
            )

        val subcomponentName =
            moduleName.nestedClass(ClassName.bestGuess(element.returnType.toString()).simpleName() + "Subcomponent")
        return ContributesInjectorDescriptor(element, enclosingModule,
            ClassName.bestGuess(injectedType.toString()), scopes,
            modules, key.baseType, key.mapKey, moduleName, subcomponentName)
    }
}