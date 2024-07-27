package org.example.bob.e2e.utils

import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import java.util.*

fun encryptData(data: String): String {
    val keyGen = KeyGenerator.getInstance("AES")
    keyGen.init(256)
    val secretKey = keyGen.generateKey()
    val cipher = Cipher.getInstance("AES")
    cipher.init(Cipher.ENCRYPT_MODE, secretKey)
    val encryptedData = cipher.doFinal(data.toByteArray())
    return Base64.getEncoder().encodeToString(encryptedData)
}
