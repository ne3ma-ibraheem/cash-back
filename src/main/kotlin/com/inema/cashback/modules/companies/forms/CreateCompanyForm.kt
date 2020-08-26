package com.inema.cashback.modules.companies.forms

import com.inema.cashback.modules.companies.CompaniesTable
import com.inema.cashback.utils.eqIgnoreCase
import com.inema.cashback.modules.users.UsersTable
import org.hibernate.validator.constraints.Length
import org.jetbrains.exposed.sql.select

data class CreateCompanyForm(
        @field:Length(min = 8, max = 100, message = "name.length.message")
        val name: String,
        val owner: String,
        val displayName: String? = null,
        val description: String? = null,
        val website: String? = null,
        val address: String? = null,
        val picture: String? = null
) {
    fun asMap(): Map<String, *> = mapOf(
            "name" to name,
            "owner" to owner,
            "displayName" to (displayName ?: name),
            "description" to description,
            "website" to website,
            "address" to address,
            "picture" to picture
    )
    companion object {
        val ownerExist = { it: CreateCompanyForm ->
            UsersTable.findByUsernameOrEmail(it.owner).count() > 0L
        }
        val noCompanyWithSameName = { it: CreateCompanyForm ->
            CompaniesTable.select { CompaniesTable.name eqIgnoreCase it.name }.count() == 0L
        }
    }
}