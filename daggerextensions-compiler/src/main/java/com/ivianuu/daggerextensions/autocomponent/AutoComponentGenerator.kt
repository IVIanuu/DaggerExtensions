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

package com.ivianuu.daggerextensions.autocomponent

import com.ivianuu.daggerextensions.util.toLowerCaseCamel
import com.squareup.javapoet.*
import dagger.Binds
import dagger.BindsInstance
import dagger.Module
import javax.lang.model.element.Modifier

class AutoComponentGenerator(private val descriptor: AutoComponentDescriptor) {

    fun generate(): JavaFile {
        val component = TypeSpec.interfaceBuilder(descriptor.component)
            .addModifiers(Modifier.PUBLIC)
            .addSuperinterface(androidInjector())
            .addSuperinterfaces(descriptor.superInterfaces)
            .addAnnotations(scopes())
            .addAnnotation(componentAnnotation())
            .addMethods(injectMethods())
            .addMethods(subcomponentMethods())
            .addType(componentBuilder())

        if (descriptor.bindings.isNotEmpty()) {
            component.addType(buildBindingModule())
        }

        return JavaFile.builder(descriptor.component.packageName(), component.build())
            .build()
    }

    private fun androidInjector(): TypeName {
        return ParameterizedTypeName.get(
            ClassName.bestGuess("dagger.android.AndroidInjector"),
            descriptor.target
        )
    }

    private fun componentAnnotation(): AnnotationSpec {
        val annotation = AnnotationSpec.builder(descriptor.type.component.java)

        descriptor.dependencies.forEach {
            annotation.addMember("dependencies", "\$T.class", it)
        }

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
            .forEach {
                annotation.addMember("modules", "\$T.class", it)
            }

        return annotation.build()
    }

    private fun scopes() =
        descriptor.scopes.map { AnnotationSpec.get(it) }

    private fun injectMethods(): List<MethodSpec> {
        return descriptor.injectClasses
            .map {
                MethodSpec.methodBuilder("inject")
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .addParameter(it, it.simpleName().toLowerCaseCamel())
                    .build()
            }
    }

    private fun subcomponentMethods(): List<MethodSpec> {
        return descriptor.subcomponents
            .map {
                val builderName = it.nestedClass("Builder")
                MethodSpec.methodBuilder((it.simpleName() + "Builder").toLowerCaseCamel())
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .returns(builderName)
                    .build()
            }
    }

    private fun buildBindingModule(): TypeSpec {
        val type = TypeSpec.classBuilder(descriptor.bindingsModule)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.ABSTRACT)
            .addAnnotation(Module::class.java)

        descriptor.bindings.forEach { type.addMethod(bindsMethod(it)) }

        return type.build()
    }

    private fun bindsMethod(type: ClassName): MethodSpec {
        return MethodSpec.methodBuilder("bind${type.simpleName()}")
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addAnnotation(Binds::class.java)
            .addParameter(descriptor.target, descriptor.target.simpleName().toLowerCaseCamel())
            .returns(type)
            .build()
    }

    private fun componentBuilder(): TypeSpec {
        val builder = TypeSpec.classBuilder("Builder")
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT, Modifier.STATIC)
            .addAnnotation(descriptor.type.builder.java)
            .addMethod(
                MethodSpec.methodBuilder(descriptor.target.simpleName().toLowerCaseCamel())
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .addAnnotation(BindsInstance::class.java)
                    .addParameter(
                        descriptor.target,
                        descriptor.target.simpleName().toLowerCaseCamel()
                    )
                    .returns(descriptor.componentBuilder)
                    .build()
            )
            .addMethod(
                MethodSpec.methodBuilder("build")
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .returns(descriptor.component)
                    .build()
            )
            .addMethods(componentBuilderModuleMethods())
            .addMethods(componentDependencyMethods())

        return builder.build()
    }

    private fun componentDependencyMethods(): List<MethodSpec> {
        return descriptor.dependencies
            .map {
                MethodSpec.methodBuilder(it.simpleName().toLowerCaseCamel())
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .addParameter(it, it.simpleName().toLowerCaseCamel())
                    .returns(descriptor.componentBuilder)
                    .build()
            }
    }

    private fun componentBuilderModuleMethods(): List<MethodSpec> {
        return descriptor.modules
            .filterNot { it.modifiers.contains(Modifier.ABSTRACT) }
            .map {
                MethodSpec.methodBuilder(it.name.simpleName().toLowerCaseCamel())
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .addParameter(it.name, it.name.simpleName().toLowerCaseCamel())
                    .returns(descriptor.componentBuilder)
                    .build()
            }
    }

}