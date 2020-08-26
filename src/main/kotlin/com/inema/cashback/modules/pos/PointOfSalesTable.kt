package com.inema.cashback.modules.pos

import com.inema.cashback.modules.companies.CompaniesTable
import com.inema.cashback.modules.geo.db.geoPoint
import org.jetbrains.exposed.dao.id.UUIDTable

object PointOfSalesTable : UUIDTable(name = "POINT_OF_SALES") {
    val name = varchar("name", 200)
    val address = varchar("address", 200)
    val location = geoPoint()
    val company = reference("company_id", CompaniesTable)
}
