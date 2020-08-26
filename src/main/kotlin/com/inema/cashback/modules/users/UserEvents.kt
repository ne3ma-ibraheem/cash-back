package com.inema.cashback.modules.users

import org.springframework.context.ApplicationEvent

sealed class UserEvents(val user: User) : ApplicationEvent(user) {
    class UserCreated(user: User) : UserEvents(user)
}