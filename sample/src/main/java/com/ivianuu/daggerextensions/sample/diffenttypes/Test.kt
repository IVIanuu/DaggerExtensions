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

package com.ivianuu.daggerextensions.sample.diffenttypes

import android.app.Activity
import com.ivianuu.daggerextensions.AutoBindsIntoMap
import com.ivianuu.daggerextensions.AutoBindsIntoSet
import com.ivianuu.daggerextensions.AutoContribute
import com.ivianuu.daggerextensions.BindingModule
import com.ivianuu.daggerextensions.key.AutoStringKey

@BindingModule
annotation class MyBindingModule

@MyBindingModule
@AutoContribute
class MyActivity : Activity()

interface MyType

@MyBindingModule
@AutoBindsIntoSet(MyType::class)
class MyTypeImpl1 : MyType

@MyBindingModule
@AutoStringKey("key")
@AutoBindsIntoMap(MyType::class)
class MyTypeImpl2 : MyType