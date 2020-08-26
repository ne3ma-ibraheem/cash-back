package com.inema.cashback.modules.geo.db

import org.jetbrains.exposed.sql.*

fun geoPointColumn(table: Table, longitudeName: String, latitudeName: String) =
        GeoPointColumn(
                longitude = Column(table, longitudeName, FloatColumnType()),
                latitude = Column(table, latitudeName, FloatColumnType())
        )


fun Table.geoPoint(longitudeName: String = "longitude", latitudeName: String = "latitude") =
        registerCompositeColumn(geoPointColumn(this, longitudeName, latitudeName))


