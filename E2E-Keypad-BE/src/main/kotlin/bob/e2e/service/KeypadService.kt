package org.example.bob.e2e.service

import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.util.*

import java.awt.Graphics2D
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.awt.Color

@Service
class KeypadService(private val resourceLoader: ResourceLoader) {

    private val keyMap = mutableMapOf<String, String>() // 로컬에 키와 UUID를 저장

    // 암호화된 키패드와 이미지를 반환하는 메서드
    fun getEncryptedKeypad(): Map<String, Any> {
        val shuffledKeys = generateRandomKeys()

        return mapOf(
            "keys" to shuffledKeys,
            "images" to generateKeypadImages(keyMap.keys.toList())
        )
    }

    private fun generateConcatenatedBase64Images(keys: List<String>): String {
        val imageList = mutableListOf<BufferedImage>()

        // 키에 맞는 이미지를 로드하여 리스트에 추가
        keys.forEachIndexed { _, key ->
            val imagePath = if (key != " " && key != "  ") {
                "classpath:static/images/_${key}.png"
            } else {
                "classpath:static/images/_blank.png"
            }
            val imgResource = resourceLoader.getResource(imagePath)
            val img = ImageIO.read(imgResource.inputStream)
            imageList.add(img)
        }

        val imgWidth = imageList[0].width
        val imgHeight = imageList[0].height
        val combinedWidth = imgWidth * imageList.size
        val combinedHeight = imgHeight

        // 결합된 이미지를 생성
        val combinedImage = BufferedImage(combinedWidth, combinedHeight, BufferedImage.TYPE_INT_ARGB)
        val g: Graphics2D = combinedImage.createGraphics()

        var x = 0
        for (img in imageList) {
            g.drawImage(img, x, 0, null)
            x += imgWidth
        }
        g.dispose()

        // 결합된 이미지를 Base64로 인코딩하여 반환
        val baos = java.io.ByteArrayOutputStream()
        ImageIO.write(combinedImage, "png", baos)
        val imageBytes = baos.toByteArray()
        baos.close()

        return Base64.getEncoder().encodeToString(imageBytes)
    }

    private fun generateKeypadImages(keys: List<String>): String {
        val imageList = mutableListOf<BufferedImage>()

        // 키에 맞는 이미지를 로드하여 리스트에 추가
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

        // 배경색을 지정하여 결합된 이미지를 생성
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

        // 결합된 이미지를 Base64로 인코딩하여 반환
        val baos = java.io.ByteArrayOutputStream()
        ImageIO.write(combinedImage, "png", baos)
        val imageBytes = baos.toByteArray()
        baos.close()

        return Base64.getEncoder().encodeToString(imageBytes)
    }


    private fun generateRandomKeys(): List<String> {
        val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0", " ", "  ")
        val shuffledKeys = keys.shuffled()
        val md = MessageDigest.getInstance("SHA-256")

        keyMap.clear()
        shuffledKeys.forEach { key ->
            keyMap[key] = if (key != " " && key != "  ") {
                // 각 키에 대해 랜덤한 해시 값 생성
                val randomBytes = ByteArray(16)
                Random().nextBytes(randomBytes)
                val hash = md.digest(randomBytes).joinToString("") { "%02x".format(it) }
                hash
            } else {
                key
            }
        }

        return keyMap.values.toList() // 해시 리스트만 반환
    }

}
