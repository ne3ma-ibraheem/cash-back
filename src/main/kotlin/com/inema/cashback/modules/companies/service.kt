package com.inema.cashback.modules.companies

import com.inema.cashback.modules.security.authz.UserPermissions
import com.inema.cashback.modules.users.Users
import com.inema.cashback.modules.users.eqIgnoreCase
import com.inema.cashback.utils.*
import org.hibernate.validator.constraints.Length
import org.jetbrains.exposed.sql.*
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import org.springframework.validation.SmartValidator
import java.util.*

/**
 * Create Company Form
 *
 * TODO: add other fields
 */
data class CreateCompanyForm(@field:Length(min = 8, max = 100, message = "name.length.message") val name: String, val owner: String)

/**
 * Company entity events
 */
sealed class CompanyEvent(data: Map<String, Any>) : ApplicationEvent(data), Map<String, Any> by data {
    class CompanyCreated(data: Map<String, Any>) : CompanyEvent(data)
    class CompanyDeleted(data: Map<String, Any>) : CompanyEvent(data)
}


@Service
class CompaniesService(val eventBus: ApplicationEventPublisher, val validator: SmartValidator) {

    val deleteCompany: (UUID) -> Either<CashBackError, Map<String, Any>> = { id ->
        runCatching {
            Companies.deleteWhere {
                Companies.id eq id
            }.let { Right(mapOf("id" to id)) }
        }.getOrElse { Left(persistenceError(it.message)) }
    }
    val insertCompany: (CreateCompanyForm) -> Either<CashBackError, Map<String, Any>> = { form ->
        runCatching {
            Companies.insertAndGetId { it[name] = form.name;it[owner] = form.owner }
        }.map {
            Right(mapOf("id" to it.value, "name" to form.name, "owner" to form.owner))
        }.getOrElse {
            Left(persistenceError(it.message
                    ?: ""))
        }
    }
    val addOwnerPermissions: (Map<String, Any>) -> Unit = { evt ->
        val id = evt["id"] as UUID
        val owner = evt["owner"] as String
        UserPermissions.addPermission(owner, "owner", "company", id.toString())
    }
    val deleteCompanyPermissions: (UUID) -> Unit = {
        UserPermissions.removeByEntityId(it.toString())
    }

    @Transactional
    fun createCompany(form: CreateCompanyForm) = let {
        val validateForm = validator.forForm<CreateCompanyForm>()
        val assureOwnerExist = assure {
            { form: CreateCompanyForm ->
                Users.findByUsernameOrEmail(form.owner).count() > 0L
            } orElse {
                missingKey("there is no owner with username: ${it.owner}")
            }
        }
        val assureNoCompanyWithSameName = assure {
            { form: CreateCompanyForm ->
                Companies.select { Companies.name eqIgnoreCase form.name }.count() == 0L
            } orElse { uniqueConstraint("company with name ${it.name} already exist ") }
        }

        form.run(validateForm then
                assureOwnerExist then
                assureNoCompanyWithSameName then
                insertCompany andOnResult
                {
                    addOwnerPermissions(it)
                    eventBus.publishEvent(CompanyEvent.CompanyCreated(it))
                })

    }

    @Transactional
    fun deleteCompany(id: UUID) =
            (deleteCompany andOnResult {
                deleteCompanyPermissions(it["id"] as UUID)
                eventBus.publishEvent(CompanyEvent.CompanyDeleted(it))
            })(id)
}


