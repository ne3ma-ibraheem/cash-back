package com.inema.cashback.utils

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.validation.BindException
import org.springframework.validation.Errors
import org.springframework.validation.SmartValidator

data class CashBackError(
        val error: String,
        val message: String,
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        val errors: List<FormError> = emptyList()
)

typealias Left<T> = Either.Error<CashBackError, T>

typealias Right<T> = Either.Result<CashBackError, T>

data class FormError(
        val code: String?,
        val message: String?,
        val field: String?
)

fun Errors.collectErrors(): List<FormError> =
        this.fieldErrors.map {
            FormError(it.code, it.defaultMessage, it.field)
        } + this.globalErrors.map {
            FormError(it.code, it.defaultMessage, null)
        }


fun cashbackError(error: String, message: String?, errors: List<FormError> = emptyList()) = CashBackError(error, message
        ?: "", errors)

fun formErrors(errors: List<FormError>) = cashbackError("form validation", "form not valid", errors)

fun uniqueConstraint(message: String?) = cashbackError("unique constraint", message)

fun missingKey(message: String?) = cashbackError("missing key", message)

fun persistenceError(message: String?) = cashbackError("transaction error", message)