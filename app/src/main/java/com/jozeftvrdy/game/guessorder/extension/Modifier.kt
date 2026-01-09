package com.jozeftvrdy.game.guessorder.extension

import androidx.compose.ui.Modifier
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
fun Modifier.conditional(condition: Boolean, modifyCallback: () -> Modifier): Modifier {
    contract {
        callsInPlace(modifyCallback, InvocationKind.AT_MOST_ONCE)
    }

    return if (condition) {
        this.then(modifyCallback())
    } else {
        this
    }
}

@OptIn(ExperimentalContracts::class)
inline fun <T: Any> Modifier.ifNotNull(instance: T?, modifyCallback: (T) -> Modifier): Modifier {
    contract {
        callsInPlace(modifyCallback, InvocationKind.AT_MOST_ONCE)
    }

    return instance?.let {
        this.then(modifyCallback(instance))
    }?:this
}