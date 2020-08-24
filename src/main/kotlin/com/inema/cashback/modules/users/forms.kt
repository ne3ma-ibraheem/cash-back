package com.inema.cashback.modules.users

import org.hibernate.validator.constraints.Length
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import javax.validation.constraints.Email

data class RegistrationForm(
        @Length(min = 8, max = 50)
        val username: String,
        @Length(min = 8, max = 100)
        @Email
        val email: String,
        @Length(min = 8)
        val password: String
) {

    companion object {
        val noUserWithSameNameOrEmail = { form: RegistrationForm ->
            Users.select { (Users.id eqIgnoreCase form.username) or (Users.email eqIgnoreCase form.email) }.count() == 0L
        }
    }
}
