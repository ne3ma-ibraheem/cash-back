package com.inema.cashback.modules.companies


import com.inema.cashback.modules.users.Users
import com.inema.cashback.utils.BaseController
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.http.ResponseEntity.*
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("api/v1.0/companies")
class CompaniesController(val companies: CompaniesService) : BaseController {

    @PostMapping
    fun createCompany(@RequestBody form: CreateCompanyForm) = companies.createCompany(form).response()

    @GetMapping("/{id}")
    fun getCompany(@PathVariable id: UUID) = transaction {
        Users.innerJoin(Companies).select {
            Companies.id eq id
        }.map {
            ok(mapOf(
                    "id" to it[Companies.id].value,
                    "name" to it[Companies.name],
                    "owner" to mapOf(
                            "username" to it[Users.id],
                            "email" to it[Users.email]
                    )
            ))
        }.firstOrNull() ?: notFound().build()
    }

    @DeleteMapping("/{id}")
    fun deleteCompany(@PathVariable id: UUID) = companies.deleteCompany(id).response()
}