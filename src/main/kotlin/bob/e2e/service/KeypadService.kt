package org.example.bob.e2e.service

import org.example.bob.e2e.utils.encryptData
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.util.*
import kotlin.collections.HashMap

@Service
class KeypadService(private val resourceLoader: ResourceLoader) {

    fun getEncryptedKeypad(): Map<String, Any> {
        val keys = generateRandomKeys()
        val shuffledKeys = keys.shuffled(Random(System.currentTimeMillis()))
        val encryptedKeys = encryptData(shuffledKeys.joinToString(","))

        val imageBase64Map = getKeypadImagesBase64()

        return mapOf("encryptedKeys" to encryptedKeys, "images" to imageBase64Map)
    }

    private fun getKeypadImagesBase64(): Map<String, String> {
        val imageBase64Map = HashMap<String, String>()
        for (i in 0..9) {
            val imagePath = "classpath:static/images/_${i}.png"
            val imgResource = resourceLoader.getResource(imagePath)
            val imgBytes = imgResource.inputStream.readBytes()
            val base64Image = Base64.getEncoder().encodeToString(imgBytes)
            imageBase64Map[i.toString()] = base64Image
        }

        // Add the blank image
        val blankImagePath = "classpath:static/images/_blank.png"
        val blankImgResource = resourceLoader.getResource(blankImagePath)
        val blankImgBytes = blankImgResource.inputStream.readBytes()
        val base64BlankImage = Base64.getEncoder().encodeToString(blankImgBytes)
        imageBase64Map["blank"] = base64BlankImage

        return imageBase64Map
    }

    private fun generateRandomKeys(): List<String> {
        val keys = mutableListOf<String>()
        val random = Random()
        val md = MessageDigest.getInstance("SHA-256")

        for (i in 0 until 10) {
            val randomBytes = ByteArray(16)
            random.nextBytes(randomBytes)
            val hash = md.digest(randomBytes).joinToString("") { "%02x".format(it) }
            keys.add(hash)
        }

        return keys
    }
}
