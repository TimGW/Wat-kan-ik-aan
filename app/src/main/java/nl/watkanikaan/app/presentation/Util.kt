package nl.watkanikaan.app.presentation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun LifecycleOwner.launchAfter(
    state: Lifecycle.State = Lifecycle.State.STARTED,
    block: suspend CoroutineScope.() -> Unit,
) {
    lifecycleScope.launch {
        repeatOnLifecycle(state) { block() }
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

fun Context.getThemeColor(@AttrRes res: Int): Int {
    val value = TypedValue()
    theme.resolveAttribute(res, value, true)
    return value.data
}

fun fadeIn(fromView: View, toView: View, duration: Long = 400L) {
    toView.apply {
        alpha = 0f
        visibility = View.VISIBLE

        animate()
            .alpha(1f)
            .setDuration(duration)
            .setListener(null)
    }

    fromView.animate()
        .alpha(0f)
        .setDuration(duration)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                fromView.visibility = View.INVISIBLE
            }
        })
}