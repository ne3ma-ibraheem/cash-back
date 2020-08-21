package com.inema.cashback.utils


import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class FnTests {
    @Test
    fun `then should not invoke the pipeline if error is occurred`() {
        var fn1Invoked = false
        var fn2Invoked = false
        val willReturnError: (String) -> Either<String, String> = {
            fn1Invoked = true
            Either.Error("blank")
        }

        val other: (String) -> Either<String, String> = {
            fn2Invoked = true
            Either.Result(it)
        }

        val result = "".run(willReturnError then other)
        assertTrue(result is Either.Error)
        assertTrue(fn1Invoked)
        assertFalse(fn2Invoked)
    }


}