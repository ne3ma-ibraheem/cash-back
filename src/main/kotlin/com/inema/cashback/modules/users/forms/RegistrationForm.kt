package com.inema.cashback.modules.users.forms

import com.inema.cashback.modules.users.UsersTable
import com.inema.cashback.utils.eqIgnoreCase
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
            UsersTable.select { (UsersTable.id eqIgnoreCase form.username) or (UsersTable.email eqIgnoreCase form.email) }.count() == 0L
        }
    }
}
