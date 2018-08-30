package com.android.example.themoviedb.util

import android.os.Bundle
import androidx.navigation.NavDirections
import org.mockito.Mockito

private fun Bundle?.eq(other : Bundle?) : Boolean {
    if (this == null) {
        return other == null
    }
    if (other == null) {
        return false
    }
    if (keySet().size != other.keySet().size) {
        return false
    }
    return keySet().all {
        get(it) == other.get(it)
    }
}

/**
 * A convenience method to create a Mockito argument matcher for navigation directions.
 */
fun NavDirections.matcher() = Mockito.argThat<NavDirections> {
    it.actionId == this.actionId && arguments.eq(it.arguments)
}