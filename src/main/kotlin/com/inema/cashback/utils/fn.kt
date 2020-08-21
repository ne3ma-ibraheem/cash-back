package com.inema.cashback.utils

sealed class Either<ERROR, out RESULT>(open val error: ERROR? = null, open val result: RESULT? = null) {
    class Error<Error, Result>(override val error: Error) : Either<Error, Result>(error)
    class Result<Error, Result>(override val result: Result) : Either<Error, Result>(result = result)

    fun <V> mapResult(fn: (RESULT) -> Either<ERROR, V>): Either<ERROR, V> =
            map(
                    ifResult = fn,
                    ifError = { Error(it) }
            )

    fun <V> map(ifResult: (RESULT) -> V, ifError: (ERROR) -> V): V =
            when (this) {
                is Error -> ifError(error)
                is Result -> ifResult(result)
            }

    infix fun doOnResult(fn: (RESULT) -> Unit) = apply {
        if (this is Result) {
            fn(result)
        }
    }

    infix fun doOnError(fn: (ERROR) -> Unit) = apply {
        if (this is Error) {
            fn(error)
        }
    }

    fun isError() = this is Error

    fun isResult() = this is Result
}

infix fun <X, Y, Z, R> Function1<X, Either<Y, Z>>.then(fn: (Z) -> Either<Y, R>): (X) -> Either<Y, R> = {
    this(it).mapResult(fn)
}

infix fun <X, Y, Z> Function1<X, Either<Y, Z>>.thenAssure(assured: Assured<Y, Z>): (X) -> Either<Y, Z> = {
    this(it).mapResult { z ->
        if (assured.fn(z)) {
            Either.Result<Y, Z>(z)
        } else {
            Either.Error<Y, Z>(assured.or(z))
        }
    }
}

infix fun <X, Y, Z, R> Function1<X, Either<Y, Z>>.thenMapResult(fn: (Z) -> R): (X) -> Either<Y, R> = {
    this(it).let { e ->
        e.map(
                ifResult = { z -> Either.Result(fn(z)) },
                ifError = { _ -> e as Either<Y, R> }
        )
    }
}

infix fun <X, Y, Z> Function1<X, Either<Y, Z>>.andOnResult(fn: (Z) -> Unit): (X) -> Either<Y, Z> = {
    this(it).doOnResult(fn)
}

infix fun <X, Y, Z> Function1<X, Either<Y, Z>>.andOnError(fn: (Y) -> Unit): (X) -> Either<Y, Z> = {
    this(it).doOnError(fn)
}


data class Assured<T, V>(
        val fn: (V) -> Boolean,
        val or: (V) -> T
)

fun <ERROR, FORM> assure(a: () -> Assured<ERROR, FORM>): (FORM) -> Either<ERROR, FORM> = {
    a().let { assured ->
        if (assured.fn(it)) {
            Either.Result(it)
        } else {
            Either.Error(assured.or(it))
        }
    }
}


infix fun <ERROR, FORM> Function1<FORM, Boolean>.orElse(fn: (FORM) -> ERROR): Assured<ERROR, FORM> = Assured(
        fn = this,
        or = fn
)


