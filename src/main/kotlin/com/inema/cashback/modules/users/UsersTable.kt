package com.inema.cashback.modules.users


import com.inema.cashback.utils.eqIgnoreCase
import org.jetbrains.exposed.sql.*

object UsersTable : Table() {
    val email = varchar("email", 100)
    val id = varchar("username", 100)
    val password = varchar("password", 100).nullable()

    override val primaryKey by lazy { PrimaryKey(id) }

    fun findByUsernameOrEmail(sid: String) =
            UsersTable.select { (id eqIgnoreCase sid) or (email eqIgnoreCase sid) }
}
