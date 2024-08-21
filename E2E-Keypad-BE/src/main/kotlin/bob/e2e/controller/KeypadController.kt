package org.example.bob.e2e.controller

import org.example.bob.e2e.service.KeypadService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1")
class KeypadController(private val keypadService: KeypadService) {

    @GetMapping("/get_keypad")
    fun getEncryptedKeypad(): ResponseEntity<Map<String, Any>> {
        val response = keypadService.getEncryptedKeypad()
        return ResponseEntity.ok(response)
    }

    @PostMapping("/userinput")
    fun validateAndForward(@RequestBody payload: Map<String, Any>): ResponseEntity<Any> {
        val keypadId = payload["keypadId"] as? String ?: return ResponseEntity.status(400).body("Invalid keypadId")
        val timestamp = (payload["timestamp"] as? Number)?.toLong() ?: return ResponseEntity.status(400).body("Invalid timestamp")
        val receivedHash = payload["hash"] as? String ?: return ResponseEntity.status(400).body("Invalid hash")
        val userInput = payload["userInput"] as? String ?: return ResponseEntity.status(400).body("Invalid userInput")

        return if (keypadService.verifyHash(keypadId, timestamp, receivedHash)) {
            val response = keypadService.sendToEndpoint(userInput, keypadId)
            ResponseEntity.ok(response)
        } else {
            ResponseEntity.status(400).body("Validation failed.")
        }
    }
}
