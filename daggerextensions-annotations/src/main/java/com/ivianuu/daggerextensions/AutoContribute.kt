package com.ivianuu.daggerextensions

import kotlin.reflect.KClass

/**
 * Automatically creates @ContributesAndroidInjector boilerplate
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class AutoContribute(
    val modules: Array<KClass<*>> = []
)