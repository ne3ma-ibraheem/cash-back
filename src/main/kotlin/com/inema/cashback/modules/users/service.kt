package com.inema.cashback.modules.users

import com.inema.cashback.utils.*
import org.hibernate.validator.constraints.Length
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.SmartValidator
import javax.validation.constraints.Email

@Service
class UserService(
        val validator: SmartValidator,
        val eventBus: ApplicationEventPublisher,
        val passwordEncoder: PasswordEncoder
) {
    @Transactional
    fun createUser(form: RegistrationForm) = let {
        val validateForm = validator.forForm<RegistrationForm>()
        form.run(validateForm then assure {
            noUserWithSameNameOrEmail orElse {
                uniqueConstraint("user with username: ${it.username} or email : ${it.email} already exist")
            }
        } then encodePassword then insertUser andOnResult eventBus.emit(UserEvents::UserCreated))
    }


    val encodePassword: (RegistrationForm) -> Either<CashBackError, RegistrationForm> = {
        Right(
                it.copy(password = passwordEncoder.encode(it.password))
        )
    }
}

data class RegistrationForm(
        @Length(min = 8, max = 50)
        val username: String,
        @Length(min = 8, max = 100)
        @Email
        val email: String,
        @Length(min = 8)
        val password: String
)

class User(
        val username: String,
        val email: String
)


sealed class UserEvents(val user: User) : ApplicationEvent(user) {
    class UserCreated(user: User) : UserEvents(user)
}


val noUserWithSameNameOrEmail: (RegistrationForm) -> Boolean = { form ->
    Users.select {
        (Users.id eqIgnoreCase form.username) or (Users.email eqIgnoreCase form.email)
    }.count() == 0L
}

val insertUser: (RegistrationForm) -> Either<CashBackError, User> =
        { form: RegistrationForm ->
            runCatching {
                Users.insert {
                    it[id] = form.username
                    it[email] = form.email
                    it[password] = form.password
                }.let {
                    Right(User(form.username, form.email))
                }
            }.getOrElse {
                Left(persistenceError(it.message))
            }
        }






