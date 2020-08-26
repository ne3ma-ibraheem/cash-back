package com.inema.cashback.modules.companies


import com.inema.cashback.modules.users.UsersTable
import org.jetbrains.exposed.dao.id.UUIDTable

object CompaniesTable : UUIDTable() {
    val name = varchar("name", 100)
    val owner = reference("owner_id", UsersTable.id)
    val displayName = varchar("display_name", 100).nullable()
    val address = varchar("address", 100).nullable()
    val website = varchar("website", 100).nullable()
    val description = varchar("description", 1000).nullable()
    val picture = varchar("picture_url", 1000).nullable()
}
