package com.inema.cashback.modules.pos

import com.inema.cashback.modules.companies.CompaniesTable
import com.inema.cashback.modules.geo.GeoPoint
import com.inema.cashback.modules.pos.forms.CreatePointOfSalesForm
import com.inema.cashback.modules.pos.forms.CreatePointOfSalesForm.Companion.accountCanCreatePos
import com.inema.cashback.modules.pos.forms.CreatePointOfSalesForm.Companion.companyExist
import com.inema.cashback.modules.pos.forms.UpdatePointOfSalesForm
import com.inema.cashback.modules.pos.forms.UpdatePointOfSalesForm.Companion.pointOfSalesExist
import com.inema.cashback.modules.security.authz.UserPermissionsTable
import com.inema.cashback.utils.*
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.update
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.SmartValidator
import java.util.*

@Service
class PointOfSalesMutationService() : MutationService() {

    val insert = { form: CreatePointOfSalesForm ->
        PointOfSalesTable.insertAndGetId {
            it[company] = EntityID(form.company, CompaniesTable)
            it[name] = form.name
            it[address] = form.address
            it[location] = form.location
        }.let {
            mapOf(
                    "id" to it.value,
                    "company" to form.company,
                    "name" to form.name,
                    "address" to form.address,
                    "location" to form.location
            )
        }
    }

    val update = { form: UpdatePointOfSalesForm ->
        PointOfSalesTable.update(where = {
            PointOfSalesTable.id eq form.id!!
        }) {
            it[name] = form.name ?: ""
            it[address] = form.address ?: ""
            it[location] = form.location ?: GeoPoint(0.0f, 0.0f)
        }.let {
            mapOf(
                    "id" to form.id,
                    "name" to form.name,
                    "address" to form.address,
                    "location" to form.location
            )
        }
    }

    val delete = { id: UUID ->
        PointOfSalesTable.deleteWhere {
            PointOfSalesTable.id eq id
        }.let {
            mapOf("id" to id)
        }
    }

    val addOwnerPermissions = { _: Map<String, *> ->
        // TODO: create permissions for owner on created pos
        Unit
    }

    val removePointOfSalesPermissions = { data: Map<String, *> ->
        UserPermissionsTable.removeByEntityId(data["id"].toString())
        Unit
    }

    @Transactional
    fun createPointOfSales(
            form: CreatePointOfSalesForm
    ) = let {
        val pipeline =
                validate<CreatePointOfSalesForm>() then assure(
                        companyExist,
                        orElse = { missingKey("company with id ${it.company} does not exist") }
                ) then assure(
                        accountCanCreatePos,
                        orElse = { limitReached("account limit reached") }
                ) then insert.intoDb() andOnResult
                        { addOwnerPermissions and emit(PointOfSalesEvents::PointOfSalesCreated) }
        pipeline(form)
    }

    @Transactional
    fun deletePointOfSales(id: UUID) = let {
        val pipeline = delete.fromDb() andOnResult (
                removePointOfSalesPermissions and emit(PointOfSalesEvents::PointOfSalesDeleted))
        pipeline(id)
    }

    @Transactional
    fun updatePointOfSales(id: UUID, form: UpdatePointOfSalesForm) = let {
        val pipeline = validate<UpdatePointOfSalesForm>() thenMapResult {
            it.copy(id = id)
        } then assure(pointOfSalesExist, orElse = {
            missingKey("no point of sales with id ${it.id}")
        }) then update.onDb() andOnResult (emit(PointOfSalesEvents::PointOfSalesUpdated))
        pipeline(form)
    }


}