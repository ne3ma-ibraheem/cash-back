package com.inema.cashback.modules.users


import org.jetbrains.exposed.sql.*

object Users : Table() {
    val email = varchar("email", 100)
    val id = varchar("username", 100)
    val password = varchar("password", 100).nullable()

    override val primaryKey by lazy { PrimaryKey(id) }

    fun findByUsernameOrEmail(sid: String) =
            Users.select { (id eqIgnoreCase sid) or (email eqIgnoreCase sid) }
}

infix fun ExpressionWithColumnType<String>.eqIgnoreCase(other: String) =
        EqOp(this.lowerCase(), stringParam(other.toLowerCase()))