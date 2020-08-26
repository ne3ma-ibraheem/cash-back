package com.inema.cashback.modules.pos.forms

import com.inema.cashback.modules.companies.CompaniesTable
import com.inema.cashback.modules.geo.GeoPoint
import org.jetbrains.exposed.sql.select
import java.util.*

data class CreatePointOfSalesForm(
        val company: UUID,
        val name: String,
        val address: String,
        val location: GeoPoint
) {

    companion object {
        val companyExist = { form: CreatePointOfSalesForm ->
            CompaniesTable.select {
                CompaniesTable.id eq form.company
            }.count() == 1L
        }

        val accountCanCreatePos = { _: CreatePointOfSalesForm ->
            // TODO: Check if account don't reach limit
            true
        }

        val constraints = listOf(companyExist, accountCanCreatePos)

    }

}