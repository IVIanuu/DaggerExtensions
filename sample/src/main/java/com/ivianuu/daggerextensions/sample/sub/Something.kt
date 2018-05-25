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

package com.ivianuu.daggerextensions.sample.sub

import com.ivianuu.daggerextensions.AutoSubcomponent
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
abstract class SomeModule

object Dep

@Module
class SomeModule2 {

    @Provides
    fun provideSomething() = Dep

}

interface ASuperinterface {
    fun inject(string: String)
}

class AClass {
    @Inject lateinit var dep: Dep
}

/**
 * @author Manuel Wrage (IVIanuu)
 */
@AutoSubcomponent(
    modules = [SomeModule::class, SomeModule2::class],
    injects = [AClass::class],
    superInterfaces = [ASuperinterface::class]
)
class Something {
    init {

    }
}