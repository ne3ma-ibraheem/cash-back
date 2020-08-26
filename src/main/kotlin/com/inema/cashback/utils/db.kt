package com.inema.cashback.utils

import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.*

inline fun <reified T : Comparable<T>> IdTable<T>.exist(id: T) =
    this.select {
        this@exist.id eq id
    }.count() > 0


infix fun ExpressionWithColumnType<String>.eqIgnoreCase(other: String) =
        EqOp(this.lowerCase(), stringParam(other.toLowerCase()))