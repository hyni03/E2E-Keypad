package org.example.bob.e2e.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate

@Service
class KeypadService(private val resourceLoader: ResourceLoader) {

    @Value("\${secret.key}")
    private lateinit var secretKey: String

    private val keyMapStore = mutableMapOf<String, KeypadData>()
    private val restTemplate = RestTemplate()
    private val keyMapExpirationMillis: Long = 1 * 60 * 1000

    data class KeypadData(val keyMap: Map<String, String>, val timestamp: Long)

    fun getEncryptedKeypad(): Map<String, Any> {
        val shuffledKeys = generateRandomKeys()
        val keypadId = generateKeypadId()
        val timestamp = System.currentTimeMillis()
        val hash = generateHash(keypadId, timestamp)

        keyMapStore[keypadId] = KeypadData(shuffledKeys, timestamp)

        return mapOf(
            "keys" to shuffledKeys.values.toList(),
            "images" to generateKeypadImages(shuffledKeys.keys.toList()),
            "keypadId" to keypadId,
            "timestamp" to timestamp,
            "hash" to hash
        )
    }

    private fun generateKeypadId(): String {
        return UUID.randomUUID().toString()
    }

    private fun generateHash(keypadId: String, timestamp: Long): String {
        val data = "$keypadId$timestamp"
        val secretKeySpec = SecretKeySpec(Base64.getDecoder().decode(secretKey), "HmacSHA256")
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(secretKeySpec)
        val hashBytes = mac.doFinal(data.toByteArray())
        return Base64.getEncoder().encodeToString(hashBytes)
    }

    fun verifyHash(keypadId: String, timestamp: Long, receivedHash: String): Boolean {
        return try {

            val generatedHash = generateHash(keypadId, timestamp)
            val isHashValid = generatedHash == receivedHash

            if (!isHashValid) {
                println("Hash validation failed. Expected: $generatedHash, Received: $receivedHash")
            }
            isHashValid
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun sendToEndpoint(userInput: String, keypadId: String): ResponseEntity<String> {
        val keypadData = keyMapStore[keypadId] ?: return ResponseEntity.status(400).body("Invalid keypadId")

        // 유효시간이 지났는지 확인
        if (System.currentTimeMillis() - keypadData.timestamp > keyMapExpirationMillis) {
            keyMapStore.remove(keypadId)
            return ResponseEntity.status(400).body("Keypad ID has expired.")
        }

        val payload = mapOf(
            "userInput" to userInput,
            "keyHashMap" to keypadData.keyMap
        )

        val endpointUrl = "http://146.56.119.112:8081/auth"
        val response = restTemplate.postForObject(endpointUrl, payload, String::class.java)

        return if (response != null && response.startsWith("SUCCESS")) {
            keyMapStore.remove(keypadId)
            ResponseEntity.ok(response)
        } else {
            ResponseEntity.status(400).body(response)
        }
    }

    private fun generateKeypadImages(keys: List<String>): String {
        val imageList = mutableListOf<BufferedImage>()

        keys.forEachIndexed { index, key ->
            val imagePath = if (key != " " && key != "  ") {
                "classpath:static/images/_${key}.png"
            } else {
                "classpath:static/images/_blank.png"
            }
            val imgResource = resourceLoader.getResource(imagePath)
            val img = ImageIO.read(imgResource.inputStream)
            imageList.add(img)
        }

        val gridWidth = 4
        val gridHeight = 3
        val imgWidth = imageList[0].width
        val imgHeight = imageList[0].height
        val combinedWidth = imgWidth * gridWidth
        val combinedHeight = imgHeight * gridHeight

        val combinedImage = BufferedImage(combinedWidth, combinedHeight, BufferedImage.TYPE_INT_ARGB)
        val g: Graphics2D = combinedImage.createGraphics()

        var x = 0
        var y = 0
        for (img in imageList) {
            g.drawImage(img, x, y, null)
            x += imgWidth
            if (x >= combinedWidth) {
                x = 0
                y += imgHeight
            }
        }
        g.dispose()

        val baos = java.io.ByteArrayOutputStream()
        ImageIO.write(combinedImage, "png", baos)
        val imageBytes = baos.toByteArray()
        baos.close()

        return Base64.getEncoder().encodeToString(imageBytes)
    }


    private fun generateRandomKeys(): Map<String, String> {
        val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0", " ", "  ")
        val shuffledKeys = keys.shuffled()
        val md = MessageDigest.getInstance("SHA-256")

        val keyMap = mutableMapOf<String, String>()
        shuffledKeys.forEach { key ->
            keyMap[key] = if (key != " " && key != "  ") {
                val randomBytes = ByteArray(16)
                Random().nextBytes(randomBytes)
                val hash = md.digest(randomBytes).joinToString("") { "%02x".format(it) }
                hash.take(32)
            } else {
                key
            }
        }

        return keyMap
    }
}
