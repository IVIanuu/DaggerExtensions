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

package com.ivianuu.daggerextensions

import com.google.auto.common.BasicAnnotationProcessor
import com.google.auto.service.AutoService
import com.ivianuu.daggerextensions.autocomponent.AutoComponentProcessingStep
import com.ivianuu.daggerextensions.autocontribute.AutoContributeProcessingStep
import com.ivianuu.daggerextensions.bindingmodule.BindingModuleProcessor
import com.ivianuu.daggerextensions.bindings.BindingsProcessingStep
import com.ivianuu.daggerextensions.multibinding.MultiBindingProcessingStep
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion

/**
 * @author Manuel Wrage (IVIanuu)
 */
@AutoService(Processor::class)
class DaggerExtensionsProcessor : BasicAnnotationProcessor() {

    private val bindingModuleProcessor by lazy { BindingModuleProcessor(processingEnv) }

    override fun initSteps(): MutableIterable<ProcessingStep> {
        return mutableSetOf(
            MultiBindingProcessingStep(processingEnv),
            AutoContributeProcessingStep(processingEnv),
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