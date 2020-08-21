package com.inema.cashback.modules.security.authz


import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert

object UserPermissions : UUIDTable(name = "USER_PERMISSIONS") {
    val sid = varchar("sid", 100)
    val target = varchar("target", 100)
    val targetId = varchar("target_id", 100).nullable()
    val permission = varchar("permission", 25)


    fun addPermission(sid: String, permission: String, entity: String) {
        insert {
            it[this.sid] = sid
            it[this.permission] = permission
            it[this.target] = entity
        }
    }

    fun addPermission(sid: String, permission: String, entity: String, entityId: String) {
        insert {
            it[this.sid] = sid
            it[this.permission] = permission
            it[target] = entity
            it[targetId] = entityId
        }
    }

    fun removeByEntityId(id: String) =
            deleteWhere {
                targetId eq id
            }


    fun removeBySid(sid: String) {
        deleteWhere {
            this@UserPermissions.sid eq sid
        }
    }


}