package com.inema.cashback.modules.companies

import com.inema.cashback.modules.users.Users
import com.inema.cashback.modules.users.eqIgnoreCase
import org.hibernate.validator.constraints.Length
import org.jetbrains.exposed.sql.select
import java.util.*

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
            Users.findByUsernameOrEmail(it.owner).count() > 0L
        }
        val noCompanyWithSameName = { it: CreateCompanyForm ->
            Companies.select { Companies.name eqIgnoreCase it.name }.count() == 0L
        }
    }
}

data class UpdateCompanyForm(
        val id: UUID? = null,
        val displayName: String? = null,
        val description: String? = null,
        val website: String? = null,
        val address: String? = null,
        val picture: String? = null
) {
    fun asMap(): Map<String, *> = mapOf(
            "id" to id,
            "displayName" to displayName,
            "description" to description,
            "website" to website,
            "address" to address,
            "picture" to picture
    )
    companion object {
        val companyExist = { it: UpdateCompanyForm ->
            Companies.select { Companies.id eq (it.id!!) }.count() > 0
        }
    }
}


