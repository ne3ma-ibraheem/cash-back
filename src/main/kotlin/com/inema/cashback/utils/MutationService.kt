package com.inema.cashback.utils

import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.validation.BindException
import org.springframework.validation.SmartValidator

abstract class MutationService(
        val validator: SmartValidator,
        val eventBus: ApplicationEventPublisher
) {
    inline fun <reified T : Any> validate() = { form: T ->
        BindException(form, form::javaClass.name).let {
            validator.validate(form, it)
            if (it.hasErrors()) {
                Left<T>(formErrors(it.collectErrors()))
            } else {
                Right(form)
            }
        }
    }

    fun <T> emit(fn: (T) -> ApplicationEvent) =
            eventBus.emit(fn)
}


