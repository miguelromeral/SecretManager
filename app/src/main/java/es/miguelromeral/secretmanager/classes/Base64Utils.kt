package es.miguelromeral.secretmanager.classes

import android.util.Base64
import java.lang.Exception
import java.util.*

fun encode(content: ByteArray) = Base64.encodeToString(content, Base64.DEFAULT).trim()

fun decode(content: String) = Base64.decode(content, Base64.DEFAULT)


fun IsBase64Encoded(str: String): Boolean
{
    try
    {
        // https://stackoverflow.com/questions/8571501/how-to-check-whether-a-string-is-base64-encoded-or-not/20145780
        val regex = """^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?${'$'}""".toRegex()
        return regex.containsMatchIn(str)
    }
    catch(e: Exception)
    {
        return false
    }
}