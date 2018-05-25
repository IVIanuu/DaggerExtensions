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

import com.squareup.javapoet.*
import javax.lang.model.element.Modifier

/**
 * @author Manuel Wrage (IVIanuu)
 */
class AutoContributeGenerator(private val descriptor: AutoContributeDescriptor) {

    fun generate(): JavaFile {
        val type = TypeSpec.classBuilder(descriptor.builder)
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addAnnotation(dagger.Module::class.java)
            .addMethod(buildContributeMethod())

        return JavaFile.builder(descriptor.builder.packageName(), type.build())
            .build()
    }

    private fun buildContributeMethod(): MethodSpec {
        val method =
            MethodSpec.methodBuilder("bind${descriptor.target.simpleName()}")
                .addModifiers(Modifier.ABSTRACT)
                .returns(descriptor.target)

        descriptor.scopes.forEach { method.addAnnotation(AnnotationSpec.get(it)) }

        val annotation = AnnotationSpec.builder(CLASS_CONTRIBUTES_ANDROID_INJECTOR)

        descriptor.modules
            .map { it.name }
            .forEach { annotation.addMember("modules", "\$T.class", it) }

        method.addAnnotation(annotation.build())

        return method.build()
    }

    private companion object {
        val CLASS_CONTRIBUTES_ANDROID_INJECTOR =
            ClassName.bestGuess("dagger.android.ContributesAndroidInjector")
    }
}