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

package com.ivianuu.daggerextensions.sample.bindings

import com.ivianuu.daggerextensions.BindingModule
import com.ivianuu.daggerextensions.BindingSet
import com.ivianuu.daggerextensions.CreateBindings

@BindingModule
annotation class TestBindingModule

abstract class SubClass1

abstract class SubClass2 : SubClass1()

abstract class SubClass3 : SubClass2()

@BindingSet([SubClass1::class, SubClass2::class, SubClass3::class])
annotation class SomeBindingSet

@TestBindingModule
@SomeBindingSet
@CreateBindings
class FinalClass : SubClass3()