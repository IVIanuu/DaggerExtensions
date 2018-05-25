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

package com.ivianuu.daggerextensions.bindings

import com.ivianuu.daggerextensions.util.toLowerCaseCamel
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import dagger.Binds
import dagger.Module
import javax.lang.model.element.Modifier

/**
 * @author Manuel Wrage (IVIanuu)
 */
class BindingsGenerator(private val descriptor: BindingsDescriptor) {

    fun generate(): JavaFile {
        val module = TypeSpec.classBuilder(descriptor.moduleName)
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addAnnotation(Module::class.java)

        descriptor.types.forEach { module.addMethod(bindsMethod(it)) }

        return JavaFile.builder(descriptor.moduleName.packageName(), module.build())
            .build()
    }

    private fun bindsMethod(to: ClassName): MethodSpec {
        return MethodSpec.methodBuilder("bind${to.simpleName()}")
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addAnnotation(Binds::class.java)
            .addParameter(descriptor.type, descriptor.type.simpleName().toLowerCaseCamel())
            .returns(to)
            .build()
    }

}