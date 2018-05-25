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

import com.ivianuu.daggerextensions.util.toLowerCaseCamel
import com.squareup.javapoet.*
import dagger.Binds
import dagger.Module
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

        if (descriptor.bindings.isNotEmpty()) {
            type.addType(buildBindingModule())
        }

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

        val modules = descriptor.modules.toMutableSet()
        if (descriptor.bindings.isNotEmpty()) {
            modules.add(
                com.ivianuu.daggerextensions.util.Module(
                    descriptor.bindingsModule, setOf(
                        Modifier.PUBLIC, Modifier.ABSTRACT
                    )
                )
            )
        }

        modules
            .map { it.name }
            .forEach { annotation.addMember("modules", "\$T.class", it) }

        method.addAnnotation(annotation.build())

        return method.build()
    }

    private fun buildBindingModule(): TypeSpec {
        val type = TypeSpec.classBuilder(descriptor.bindingsModule)
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addAnnotation(Module::class.java)

        descriptor.bindings.forEach { type.addMethod(bindsMethod(it)) }

        return type.build()
    }

    private fun bindsMethod(type: ClassName): MethodSpec {
        return MethodSpec.methodBuilder("bindTo${type.simpleName()}")
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addAnnotation(Binds::class.java)
            .addParameter(descriptor.target, descriptor.target.simpleName().toLowerCaseCamel())
            .returns(type)
            .build()
    }

    private companion object {
        val CLASS_CONTRIBUTES_ANDROID_INJECTOR =
            ClassName.bestGuess("dagger.android.ContributesAndroidInjector")
    }
}