package paufregi.garminfeed.presentation

import android.content.Context
import android.widget.Toast

fun ShortToast(context: Context, text: String) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}