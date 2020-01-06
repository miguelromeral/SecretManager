package es.miguelromeral.secretmanager.classes

import android.util.Base64
import java.util.*

fun encode(content: ByteArray) = Base64.encodeToString(content, Base64.DEFAULT)
