package com.testgithub.common

import android.app.Activity
import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import java.util.*


private const val CLICK_DEBOUNCE_MILLIS = 500L

fun FragmentActivity.replaceFragment(
    fragment: Fragment,
    @IdRes layoutId: Int = android.R.id.content,
    addToBackStack: Boolean = true,
    tag: String = fragment::class.java.name
) {
    supportFragmentManager
        .beginTransaction()
        .replace(layoutId, fragment, tag)
        .apply { if (addToBackStack) addToBackStack(tag) }
        .commit()
}

fun Fragment.addFragment(
    fragment: Fragment,
    @IdRes layoutId: Int = android.R.id.content,
    addToBackStack: Boolean = true,
    tag: String = fragment::class.java.name
) {
    requireActivity().supportFragmentManager
        .beginTransaction()
        .add(layoutId, fragment, tag)
        .apply { if (addToBackStack) addToBackStack(tag) }
        .commit()
}

fun Context.getColorCompat(@ColorRes colorRes: Int) = ContextCompat.getColor(this, colorRes)

fun Activity.hideKeyboard() {
    currentFocus?.hideKeyboard()
}

fun View.hideKeyboard() {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        .hideSoftInputFromWindow(windowToken, 0)
}

fun Fragment.toast(textResource: Int) = requireActivity().toast(textResource)

fun Context.toast(message: Int): Toast = Toast
    .makeText(this, message, Toast.LENGTH_SHORT)
    .apply {
        show()
    }

fun TextView.setSpannableText(text: String, highlightText: String, color: Int) {
    if (highlightText.isEmpty() || text.isEmpty()) {
        this.text = text
        return
    }
    val spannable = SpannableString(text)
    val spannableLowerCase = SpannableString(text.toLowerCase(Locale.getDefault()))
    var indexOfPath = -1
    do {
        indexOfPath =
            spannableLowerCase.toString()
                .indexOf(highlightText.toLowerCase(Locale.getDefault()), indexOfPath + 1)
        if (indexOfPath != -1) {
            spannable.setSpan(
                ForegroundColorSpan(color),
                indexOfPath,
                indexOfPath + highlightText.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    } while (indexOfPath != -1)
    this.text = spannable
}

fun View.setDebouncedOnClickListener(callback: (view: View) -> Unit) {
    var lastClickTime = 0L
    this.setOnClickListener {
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - lastClickTime > CLICK_DEBOUNCE_MILLIS) {
            lastClickTime = currentTimeMillis
            callback.invoke(it)
        }
    }
}