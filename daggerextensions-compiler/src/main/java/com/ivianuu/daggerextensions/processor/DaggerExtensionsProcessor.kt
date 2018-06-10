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

package com.ivianuu.daggerextensions.processor

import com.google.auto.common.BasicAnnotationProcessor
import com.google.auto.service.AutoService
import com.ivianuu.daggerextensions.processor.autocomponent.AutoComponentProcessingStep
import com.ivianuu.daggerextensions.processor.autocontribute.AutoContributeProcessingStep
import com.ivianuu.daggerextensions.processor.bindingmodule.BindingModuleProcessor
import com.ivianuu.daggerextensions.processor.bindings.BindingsProcessingStep
import com.ivianuu.daggerextensions.processor.injector.InjectorCreatorProcessingStep
import com.ivianuu.daggerextensions.processor.injector.InjectorKeyFinderProcessingStep
import com.ivianuu.daggerextensions.processor.multibinding.MultiBindingProcessingStep
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion

/**
 * @author Manuel Wrage (IVIanuu)
 */
@AutoService(Processor::class)
class DaggerExtensionsProcessor : BasicAnnotationProcessor() {

    private val injectorKeyFinder by lazy { InjectorKeyFinderProcessingStep(processingEnv) }
    private val bindingModuleProcessor by lazy { BindingModuleProcessor(processingEnv) }

    override fun initSteps(): MutableIterable<ProcessingStep> {
        return mutableSetOf(
            InjectorCreatorProcessingStep(
                processingEnv
            ),
            injectorKeyFinder,
            MultiBindingProcessingStep(processingEnv),
            AutoContributeProcessingStep(processingEnv, injectorKeyFinder),
            BindingsProcessingStep(processingEnv),
            AutoComponentProcessingStep(processingEnv)
        )
    }

    override fun postRound(roundEnv: RoundEnvironment) {
        super.postRound(roundEnv)
        bindingModuleProcessor.postRound(roundEnv)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latest()
    }
}