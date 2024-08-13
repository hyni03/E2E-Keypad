package org.example.bob.e2e.controller

import org.example.bob.e2e.service.KeypadService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class KeypadController(private val keypadService: KeypadService) {

    @GetMapping("/get_keypad")
    fun getEncryptedKeypad(): ResponseEntity<Map<String, Any>> {
        val response = keypadService.getEncryptedKeypad()
        return ResponseEntity.ok(response)
    }
}
