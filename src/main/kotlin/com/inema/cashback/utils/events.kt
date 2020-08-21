package com.inema.cashback.utils

import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationEventPublisher


fun <T> ApplicationEventPublisher.emit(fn: (T) -> ApplicationEvent): (T) -> Unit = {
    this.publishEvent(fn(it))
}