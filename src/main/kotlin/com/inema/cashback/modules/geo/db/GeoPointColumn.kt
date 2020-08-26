package com.inema.cashback.modules.geo.db

import com.inema.cashback.modules.geo.GeoPoint
import org.jetbrains.exposed.sql.BiCompositeColumn
import org.jetbrains.exposed.sql.Column

class GeoPointColumn(
        val longitude: Column<Float>,
        val latitude: Column<Float>
) : BiCompositeColumn<Float, Float, GeoPoint>(
        longitude, latitude,
        transformFromValue = {
            it.longitude to it.latitude
        },
        transformToValue = { lon, lat ->
            GeoPoint(lon as Float, lat as Float)
        }
)