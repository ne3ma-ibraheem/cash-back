package com.inema.cashback.modules.companies

import org.springframework.context.ApplicationEvent

sealed class CompanyEvent(data: Map<String, *>) : ApplicationEvent(data), Map<String, Any?> by data {
    class CompanyCreated(data: Map<String, *>) : CompanyEvent(data)
    class CompanyDeleted(data: Map<String, *>) : CompanyEvent(data)
    class CompanyUpdated(data: Map<String, *>) : CompanyEvent(data)
}