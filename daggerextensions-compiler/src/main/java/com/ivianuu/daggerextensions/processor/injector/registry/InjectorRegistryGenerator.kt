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

package com.ivianuu.daggerextensions.processor.injector.registry

import com.ivianuu.daggerextensions.processor.util.toLowerCaseCamel
import com.squareup.javapoet.*
import dagger.MapKey
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.Multibinds
import javax.lang.model.element.Modifier

/**
 * @author Manuel Wrage (IVIanuu)
 */
class InjectorRegistryGenerator(private val descriptor: InjectorRegistryDescriptor) {

    fun generate(): List<JavaFile> {
        return listOf(hasInjector(), mapKey(), injectionModule())
            .map {
                JavaFile.builder(descriptor.target.packageName(), it)
                    .build()
            }
    }

    private fun hasInjector(): TypeSpec {
        return TypeSpec.interfaceBuilder(descriptor.hasInjectorName)
            .addModifiers(Modifier.PUBLIC)
            .addMethod(
                MethodSpec.methodBuilder(descriptor.type.simpleName().toLowerCaseCamel() + "Injector")
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .returns(
                        ParameterizedTypeName.get(
                            ClassName.get(AndroidInjector::class.java),
                            descriptor.type
                        )
                    )
                    .build()
            )
            .build()
    }

    private fun mapKey(): TypeSpec {
        return TypeSpec.annotationBuilder(descriptor.mapKeyName)
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(MapKey::class.java)
            .addMethod(
                MethodSpec.methodBuilder("value")
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .returns(
                        ParameterizedTypeName.get(
                            ClassName.get(Class::class.java),
                            WildcardTypeName.subtypeOf(descriptor.type)
                        )
                    )
                    .build()
            )
            .build()
    }

    private fun injectionModule(): TypeSpec {
        return TypeSpec.classBuilder(descriptor.injectionModuleName)
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addAnnotation(Module::class.java)
            .addMethod(
                MethodSpec.methodBuilder(descriptor.type.simpleName()+ "InjectorFactories")
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .addAnnotation(Multibinds::class.java)
                    .returns(
                        ParameterizedTypeName.get(
                            ClassName.get(Map::class.java),
                            ParameterizedTypeName.get(
                                ClassName.get(Class::class.java),
                                WildcardTypeName.subtypeOf(descriptor.type)
                            ),
                            ParameterizedTypeName.get(
                                ClassName.get(AndroidInjector.Factory::class.java),
                                WildcardTypeName.subtypeOf(descriptor.type)
                            )
                        )
                    )
                    .build()
            )
            .build()
    }
}