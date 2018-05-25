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

import com.squareup.javapoet.*
import com.squareup.javapoet.ClassName
import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap
import javax.lang.model.element.Modifier


/**
 * @author Manuel Wrage (IVIanuu)
 */
class ContributesInjectorGenerator(private val descriptor: ContributesInjectorDescriptor) {

    fun generate(): JavaFile {
        val module = TypeSpec.classBuilder(descriptor.moduleName)
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addAnnotation(
                AnnotationSpec.builder(Module::class.java)
                    .addMember("subcomponents", "\$T.class", descriptor.subcomponentName)
                    .build()
            )
            .addMethod(bindInjectorMethod())
            .addType(subcomponent())

        return JavaFile.builder(descriptor.injectedType.packageName(), module.build())
            .build()
    }

    private fun subcomponent(): TypeSpec {
        val subcomponent = TypeSpec.interfaceBuilder(descriptor.subcomponentName)
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addSuperinterface(
                ParameterizedTypeName.get(
                    ClassName.get(AndroidInjector::class.java),
                    descriptor.injectedType
                )
            )
            .addAnnotation(subcomponentAnnotation())
            .addType(subcomponentBuilder())

        descriptor.scopes.forEach { subcomponent.addAnnotation(AnnotationSpec.get(it)) }

        return subcomponent.build()
    }

    private fun subcomponentAnnotation(): AnnotationSpec {
        val annotation = AnnotationSpec.builder(Subcomponent::class.java)

        descriptor.modules
            .map { it }
            .forEach { annotation.addMember("modules", "\$T.class", it) }

        return annotation.build()
    }

    private fun bindInjectorMethod(): MethodSpec {
        return MethodSpec.methodBuilder("bindInjectorFactory")
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addAnnotation(Binds::class.java)
            .addAnnotation(IntoMap::class.java)
            .addAnnotation(
                AnnotationSpec.builder(descriptor.mapKey)
                    .addMember("value", "\$T.class", descriptor.injectedType)
                    .build()
            )
            .addParameter(
                descriptor.subcomponentName.nestedClass("Builder"),
                "builder"
            )
            .returns(
                ParameterizedTypeName.get(
                    ClassName.get(AndroidInjector.Factory::class.java),
                    WildcardTypeName.subtypeOf(descriptor.baseType)
                )
            )
            .build()
    }

    private fun subcomponentBuilder(): TypeSpec {
        return TypeSpec.classBuilder(descriptor.subcomponentName.nestedClass("Builder"))
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT, Modifier.STATIC)
            .addAnnotation(Subcomponent.Builder::class.java)
            .superclass(
                ParameterizedTypeName.get(
                    ClassName.get(AndroidInjector.Builder::class.java),
                    TypeName.get(descriptor.element.returnType)
                )
            )
            .build()
    }


}