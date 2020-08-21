package com.inema.cashback.modules

import com.inema.cashback.modules.companies.Companies
import org.jetbrains.exposed.dao.id.UUIDTable

object PointOfSales : UUIDTable() {
    val name = varchar("name", 100)
    val company = reference("company_id", Companies)
}