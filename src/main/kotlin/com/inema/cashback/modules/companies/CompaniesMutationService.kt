package com.inema.cashback.modules.companies

import com.inema.cashback.modules.companies.forms.CreateCompanyForm.Companion.noCompanyWithSameName
import com.inema.cashback.modules.companies.forms.CreateCompanyForm.Companion.ownerExist
import com.inema.cashback.modules.companies.forms.UpdateCompanyForm.Companion.companyExist
import com.inema.cashback.modules.companies.forms.CreateCompanyForm
import com.inema.cashback.modules.companies.forms.UpdateCompanyForm
import com.inema.cashback.modules.security.authz.UserPermissionsTable
import com.inema.cashback.utils.*
import org.jetbrains.exposed.sql.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class CompaniesMutationService() : MutationService() {

    val insert = { form: CreateCompanyForm ->
        CompaniesTable.insertAndGetId {
            it[name] = form.name
            it[owner] = form.owner
            it[displayName] = form.displayName ?: form.name
            it[address] = form.address
            it[website] = form.website
            it[description] = form.description
            it[picture] = form.picture
        }.let {
            form.asMap() + ("id" to it.value)
        }
    }
    val update = { form: UpdateCompanyForm ->
        CompaniesTable.update(where = { CompaniesTable.id eq form.id }) {
            it[displayName] = form.displayName
            it[website] = form.website
            it[address] = form.address
            it[picture] = form.picture
            it[description] = form.description
        }
        form.asMap()
    }
    val delete = { id: UUID ->
        CompaniesTable.deleteWhere {
            CompaniesTable.id eq id
        }.let { mapOf("id" to id) }
    }
    val addOwnerPermissions = { evt: Map<String, *> ->
        val id = evt["id"] as UUID
        val owner = evt["owner"] as String
        UserPermissionsTable.addPermission(owner, "owner", "company", id.toString())
    }
    val deleteCompanyPermissions = { id: UUID ->
        UserPermissionsTable.removeByEntityId(id.toString())
    }

    @Transactional
    fun createCompany(form: CreateCompanyForm) = let {

        val pipeline = validate<CreateCompanyForm>() then assure(
                ownerExist,
                orElse = { missingKey("there is no owner with username: ${it.owner}") }
        ) then assure(
                noCompanyWithSameName,
                orElse = { uniqueConstraint("company with name ${it.name} already exist ") }
        ) then insert.intoDb() andOnResult (addOwnerPermissions and emit(CompanyEvent::CompanyCreated))
        pipeline(form)
    }


    @Transactional
    fun deleteCompany(id: UUID) = let {
        val pipeline = delete.fromDb() andOnResult
                (emit(CompanyEvent::CompanyDeleted) and {
                    deleteCompanyPermissions(it["id"] as UUID)
                })
        pipeline(id)
    }

    @Transactional
    fun updateCompany(id: UUID, form: UpdateCompanyForm) = let {
        val pipeline = validate<UpdateCompanyForm>() andOnResult {
            println(it)
        } thenMapResult
                { it.copy(id = id) } then
                assure(
                        companyExist,
                        orElse = { missingKey("no such company with id ${it.id}") }
                ) then update.onDb() andOnResult
                emit(CompanyEvent::CompanyUpdated)

        pipeline(form)
    }
}



