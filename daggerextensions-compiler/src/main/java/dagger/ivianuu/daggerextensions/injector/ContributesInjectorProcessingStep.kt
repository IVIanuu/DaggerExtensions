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

package com.ivianuu.daggerextensions.injector

import com.google.auto.common.BasicAnnotationProcessor
import com.google.common.collect.SetMultimap
import com.ivianuu.daggerextensions.ContributesInjector
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element

/**
 * @author Manuel Wrage (IVIanuu)
 */
class ContributesInjectorProcessingStep(private val processingEnv: ProcessingEnvironment) : BasicAnnotationProcessor.ProcessingStep {

    override fun process(elementsByAnnotation: SetMultimap<Class<out Annotation>, Element>): MutableSet<Element> {


        return mutableSetOf()
    }

    override fun annotations() = mutableSetOf(ContributesInjector::class.java)

}