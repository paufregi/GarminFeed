package paufregi.garminfeed.presentation.ui

import android.content.Context
import android.widget.Toast

class ShortToast(private val context: Context) {
    fun show(msg: String) = Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}