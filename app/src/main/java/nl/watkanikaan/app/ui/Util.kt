package nl.watkanikaan.app.ui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun LifecycleOwner.launchAfter(
    state: Lifecycle.State = Lifecycle.State.STARTED,
    block: suspend CoroutineScope.() -> Unit,
) {
    lifecycleScope.launch {
        repeatOnLifecycle(state) { block() }
    }
}

fun throttleFirst(
    skipMs: Long = 300L,
    coroutineScope: CoroutineScope,
    destinationFunction: () -> Unit,
    finally: () -> Unit
): () -> Unit {
    var throttleJob: Job? = null
    return {
        if (throttleJob?.isCompleted != false) {
            throttleJob = coroutineScope.launch {
                destinationFunction.invoke()
                delay(skipMs)
            }
        }
        finally.invoke()
    }
}

fun Context.toast(message: String) {
    Toast.makeText(
        this,
        message,
        Toast.LENGTH_SHORT
    ).show()
}

fun View.snackbar(
    message: String = "",
    actionMessage: String = "",
    anchorView: View? = null,
    length: Int = Snackbar.LENGTH_LONG,
    action: (() -> Unit)? = null
): Snackbar {
    val snackbar = Snackbar.make(this, message, length)
    if (action != null) snackbar.setAction(actionMessage) { action.invoke() }
    if (anchorView != null) snackbar.anchorView = anchorView
    snackbar.show()
    return snackbar
}

fun String?.or(input: String = INVALID_STR): String = this ?: input
fun String?.toIntOr(input: Int = INVALID_INT): Int = this?.toIntOrNull() ?: input
fun String?.toDoubleOr(input: Double = INVALID_DOUBLE): Double = this?.toDoubleOrNull() ?: input
const val INVALID_STR = "-"
const val INVALID_INT = -1
const val INVALID_DOUBLE = -1.0

fun <T, R> withNullable(receiver: T?, block: T.() -> R): R? {
    return if(receiver == null) null else receiver.block()
}
