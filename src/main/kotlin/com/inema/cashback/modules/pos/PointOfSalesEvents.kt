package com.inema.cashback.modules.pos

import org.springframework.context.ApplicationEvent

sealed class PointOfSalesEvents(val data: Map<String, *>) : ApplicationEvent(data), Map<String, Any?> by data {
    class PointOfSalesCreated(data: Map<String, *>) : PointOfSalesEvents(data)
    class PointOfSalesUpdated(data: Map<String, *>) : PointOfSalesEvents(data)
    class PointOfSalesDeleted(data: Map<String, *>) : PointOfSalesEvents(data)
}