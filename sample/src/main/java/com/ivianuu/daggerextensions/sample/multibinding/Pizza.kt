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

package com.ivianuu.daggerextensions.sample.multibinding

import com.ivianuu.daggerextensions.AutoBindsIntoMap
import com.ivianuu.daggerextensions.BindingModule
import com.ivianuu.daggerextensions.key.AutoStringKey
import javax.inject.Inject

interface Pizza

@BindingModule
annotation class FunghiPizzaModule

@FunghiPizzaModule
@AutoStringKey("funghi")
@AutoBindsIntoMap(Pizza::class)
class FunghiPizza @Inject constructor() : Pizza

@BindingModule
annotation class SalamiPizzaModule

@SalamiPizzaModule
@AutoStringKey("salami")
@AutoBindsIntoMap(Pizza::class)
class SalamiPizza @Inject constructor() : Pizza