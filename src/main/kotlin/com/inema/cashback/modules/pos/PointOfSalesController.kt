package com.inema.cashback.modules.pos

import com.inema.cashback.modules.companies.CompaniesTable
import com.inema.cashback.modules.pos.forms.CreatePointOfSalesForm
import com.inema.cashback.modules.pos.forms.UpdatePointOfSalesForm
import com.inema.cashback.utils.BaseController
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
@RequestMapping("api/v1.0/PointOfSales")
class PointOfSalesController(val pos: PointOfSalesMutationService) : BaseController {

    @PostMapping
    fun createPointOfSales(
            @RequestBody form: CreatePointOfSalesForm
    ) = pos.createPointOfSales(form).response()

    @GetMapping("{id}")
    fun fetchPointOfSales(
            @PathVariable id: UUID
    ) = transaction {
        (PointOfSalesTable innerJoin CompaniesTable).select {
            PointOfSalesTable.id eq id
        }.map { r ->
            ok(r.selectFrom(PointOfSalesTable) { pos ->
                mapOf(
                        "id" to pos[this.id].value,
                        "name" to pos[name],
                        "address" to pos[address],
                        "location" to pos[location],
                        "company" to pos.selectFrom(CompaniesTable) { company ->
                            mapOf(
                                    "id" to company[this.id].value,
                                    "displayName" to company[displayName],
                                    "picture" to company[picture]
                            )
                        }
                )
            })
        }.firstOrNull() ?: notFound().build()
    }

    @DeleteMapping("/{id}")
    fun deletePointOfSales(@PathVariable id: UUID) =
            pos.deletePointOfSales(id).response()

    @PutMapping("/{id}")
    fun updatePointOfSales(@PathVariable id: UUID,
                           @RequestBody form: UpdatePointOfSalesForm) =
            pos.updatePointOfSales(id, form).response()
}