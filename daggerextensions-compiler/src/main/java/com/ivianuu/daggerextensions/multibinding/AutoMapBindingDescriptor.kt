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

package com.ivianuu.daggerextensions.multibinding

import com.google.auto.common.AnnotationMirrors
import com.squareup.javapoet.ClassName
import javax.lang.model.element.AnnotationMirror

/**
 * @author Manuel Wrage (IVIanuu)
 */
data class AutoMapBindingDescriptor(
    val type: ClassName,
    val moduleName: ClassName,
    val items: Map<AnnotationMirror, ClassName>
) {

    class Builder(
        private val type: ClassName,
        private val moduleName: ClassName
    ) {

        private val items = mutableMapOf<AnnotationMirror, ClassName>()

        fun putItem(key: AnnotationMirror, item: String): Builder {
            items[key] = ClassName.bestGuess(item)
            return this
        }

        fun build(): AutoMapBindingDescriptor {
            return AutoMapBindingDescriptor(type, moduleName, items)
        }
    }

    companion object {
        fun builder(type: String, moduleName: ClassName): Builder {
            val baseTypeName = ClassName.bestGuess(type)
            return Builder(baseTypeName, moduleName)
        }
    }
}
