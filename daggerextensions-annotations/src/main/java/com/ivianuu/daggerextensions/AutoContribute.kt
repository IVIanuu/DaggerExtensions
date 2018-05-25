package com.ivianuu.daggerextensions

import kotlin.reflect.KClass

/**
 * Automatically creates contributes android injector boilerplate
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class AutoContribute(
    val modules: Array<KClass<*>> = []
)