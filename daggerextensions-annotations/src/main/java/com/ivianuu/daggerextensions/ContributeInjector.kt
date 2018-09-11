package com.ivianuu.daggerextensions

import kotlin.reflect.KClass

/**
 * Automatically creates @ContributesAndroidInjector boilerplate
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class ContributeInjector(
    val modules: Array<KClass<*>> = []
)