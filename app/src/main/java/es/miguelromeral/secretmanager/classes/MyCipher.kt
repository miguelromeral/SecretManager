package es.miguelromeral.secretmanager.classes

import javax.crypto.ShortBufferException
import javax.crypto.NoSuchPaddingException
import javax.crypto.BadPaddingException
import javax.crypto.IllegalBlockSizeException
import java.io.UnsupportedEncodingException
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.encoders.Base64
import android.provider.SyncStateContract.Helpers.update
import java.security.*
import javax.crypto.spec.IvParameterSpec


/*
https://gist.github.com/itarato/abef95871756970a9dad
 */
class MyCipher {

    @Throws(Exception::class)
    fun encrypt(plainText: String, key: String): ByteArray {
        val clean = plainText.toByteArray()

        // Generating IV.
        val ivSize = 16
        val iv = ByteArray(ivSize)
        val random = SecureRandom()
        random.nextBytes(iv)
        val ivParameterSpec = IvParameterSpec(iv)

        // Hashing key.
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(key.toByteArray(charset("UTF-8")))
        val keyBytes = ByteArray(16)
        System.arraycopy(digest.digest(), 0, keyBytes, 0, keyBytes.size)
        val secretKeySpec = SecretKeySpec(keyBytes, "AES")

        // Encrypt.
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec)
        val encrypted = cipher.doFinal(clean)

        // Combine IV and encrypted part.
        val encryptedIVAndText = ByteArray(ivSize + encrypted.size)
        System.arraycopy(iv, 0, encryptedIVAndText, 0, ivSize)
        System.arraycopy(encrypted, 0, encryptedIVAndText, ivSize, encrypted.size)

        return encryptedIVAndText
    }

    @Throws(Exception::class)
    fun decrypt(encryptedIvTextBytes: ByteArray, key: String): String {
        val ivSize = 16
        val keySize = 16

        // Extract IV.
        val iv = ByteArray(ivSize)
        System.arraycopy(encryptedIvTextBytes, 0, iv, 0, iv.size)
        val ivParameterSpec = IvParameterSpec(iv)

        // Extract encrypted part.
        val encryptedSize = encryptedIvTextBytes.size - ivSize
        val encryptedBytes = ByteArray(encryptedSize)
        System.arraycopy(encryptedIvTextBytes, ivSize, encryptedBytes, 0, encryptedSize)

        // Hash key.
        val keyBytes = ByteArray(keySize)
        val md = MessageDigest.getInstance("SHA-256")
        md.update(key.toByteArray())
        System.arraycopy(md.digest(), 0, keyBytes, 0, keyBytes.size)
        val secretKeySpec = SecretKeySpec(keyBytes, "AES")

        // Decrypt.
        val cipherDecrypt = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipherDecrypt.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)
        val decrypted = cipherDecrypt.doFinal(encryptedBytes)

        return String(decrypted)
    }
}
