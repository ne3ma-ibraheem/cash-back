package com.inema.cashback.modules.pos.forms

import com.inema.cashback.modules.geo.GeoPoint
import com.inema.cashback.modules.pos.PointOfSalesTable
import com.inema.cashback.utils.exist
import java.util.*

data class UpdatePointOfSalesForm(
        val id: UUID? = null,
        val name: String? = null,
        val address: String? = null,
        val location: GeoPoint? = null
) {
    companion object {

        val pointOfSalesExist = { form: UpdatePointOfSalesForm ->
            PointOfSalesTable.exist(form.id!!)
        }
    }
}


