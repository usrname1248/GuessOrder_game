package com.jozeftvrdy.game.guessorder.game.model

import kotlin.reflect.KClass

sealed class Result<out T: Any> {
    data class Success<out T: Any>(
        val value: T,
    ): Result<T>()
    data class Failure(
        val exception: Exception,
    ): Result<Nothing>()
}

inline fun <reified T: Any> Result<T>.onFailure(
    callback: (e: Exception) -> Nothing,
) : T {
    when (this) {
        is Result.Success<T> -> {
            return this.value
        }
        is Result.Failure -> {
            callback(this.exception)
        }
    }
}

inline fun <reified T: Any> Result<T>.isFailureResult(
    callback: (result: Result.Failure) -> Unit,
) {
    if (this is Result.Failure) {
        callback(this)
    }
}

fun <T: Any> T.successResult(): Result.Success<T> = Result.Success(this)
fun <T: Exception> T.failureResult(): Result.Failure = Result.Failure(this)
fun <T: Any, SpecificException: Exception> runCatching(
    vararg exceptions: KClass<SpecificException>,
    callback: () -> T,
) : Result<T> = try {
        callback().successResult()
    } catch (exception: Exception) {
        if (exceptions.isEmpty() || exceptions.contains(exception::class)) {
            exception.failureResult()
        } else {
            throw exception
        }
    }
