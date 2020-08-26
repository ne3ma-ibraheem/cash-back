package com.inema.cashback.modules.users

import com.inema.cashback.modules.users.forms.RegistrationForm
import com.inema.cashback.modules.users.forms.RegistrationForm.Companion.noUserWithSameNameOrEmail
import com.inema.cashback.utils.*
import org.jetbrains.exposed.sql.insert
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UsersMutationService(val passwordEncoder: PasswordEncoder) : MutationService() {

    val insert = { form: RegistrationForm ->
        UsersTable.insert {
            it[id] = form.username
            it[email] = form.email
            it[password] = form.password
        }.let {
            User(form.username, form.email)
        }
    }
    val encodePassword = { form: RegistrationForm ->
        form.copy(password = passwordEncoder.encode(form.password))
    }

    @Transactional
    fun createUser(form: RegistrationForm) = let {

        val pipeline = validate<RegistrationForm>() then
                assure(noUserWithSameNameOrEmail, orElse = {
                    uniqueConstraint("user with username: ${it.username} or email : ${it.email} already exist")
                }) thenMapResult
                encodePassword then
                insert.intoDb() andOnResult
                { eventBus.publishEvent(UserEvents.UserCreated(it)) }
        pipeline(form)
    }
}


data class User(
        val username: String,
        val email: String
)







