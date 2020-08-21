package com.inema.cashback.utils

import org.springframework.http.ResponseEntity


interface BaseController {
    fun <T> Either<CashBackError, T>.response(
            handleError: (CashBackError) -> ResponseEntity<Any> = { ResponseEntity.badRequest().body(error) }
    ) = when (this) {
        is Either.Error -> handleError(error)
        is Either.Result -> ResponseEntity.ok(result)
    }
}