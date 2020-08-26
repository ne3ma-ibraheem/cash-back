package com.inema.cashback.modules.companies


import com.inema.cashback.modules.companies.forms.CreateCompanyForm
import com.inema.cashback.modules.companies.forms.UpdateCompanyForm
import com.inema.cashback.modules.users.UsersTable
import com.inema.cashback.utils.BaseController
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.http.ResponseEntity.*
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("api/v1.0/companies")
class CompaniesController(val companies: CompaniesMutationService) : BaseController {
    @PostMapping("")
    fun createCompany(@RequestBody form: CreateCompanyForm) = companies.createCompany(form).response()

    @DeleteMapping("/{id}")
    fun deleteCompany(@PathVariable id: UUID) = companies.deleteCompany(id).response()

    @PutMapping("/{id}")
    fun updateCompany(@PathVariable id: UUID, @RequestBody form: UpdateCompanyForm) = companies.updateCompany(id, form).response()

    @GetMapping("/{id}")
    fun getCompany(@PathVariable id: UUID) = transaction {
        UsersTable.innerJoin(CompaniesTable).select {
            CompaniesTable.id eq id
        }.map { result ->
            ok(result.selectFrom(CompaniesTable) { it ->
                mapOf(
                        "id" to it[this.id].value,
                        "displayName" to it[displayName],
                        "picture" to it[picture],
                        "address" to it[address],
                        "description" to it[description],
                        "name" to it[name],
                        "website" to it[website],
                        "owner" to it.selectFrom(UsersTable) {
                            mapOf("username" to it[this.id], "email" to it[email])
                        }
                )
            })
        }.firstOrNull() ?: notFound().build()
    }

}