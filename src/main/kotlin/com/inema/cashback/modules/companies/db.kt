package com.inema.cashback.modules.companies

import com.inema.cashback.modules.users.Users
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.select

object Companies : UUIDTable() {
    val name = varchar("name", 100)
    val owner = reference("owner_id", Users.id)
}

