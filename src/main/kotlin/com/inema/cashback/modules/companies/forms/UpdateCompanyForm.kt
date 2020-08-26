package com.inema.cashback.modules.companies.forms

import com.inema.cashback.modules.companies.CompaniesTable
import org.jetbrains.exposed.sql.select
import java.util.*

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
            CompaniesTable.select { CompaniesTable.id eq (it.id!!) }.count() > 0
        }
    }
}


