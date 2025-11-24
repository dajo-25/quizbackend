package com.quizbackend.utils

object CaesarCipher {
    private const val SHIFT = 4

    fun encrypt(text: String): String {
        return text.map { char ->
            when {
                char.isLetter() -> {
                    val base = if (char.isUpperCase()) 'A' else 'a'
                    val offset = (char - base + SHIFT) % 26
                    base + offset
                }
                char.isDigit() -> {
                    val base = '0'
                    val offset = (char - base + SHIFT) % 10
                    base + offset
                }
                else -> char // Keep other characters as is (assuming they are safe or handled by URL encoding elsewhere)
            }
        }.joinToString("")
    }

    fun decrypt(text: String): String {
        return text.map { char ->
            when {
                char.isLetter() -> {
                    val base = if (char.isUpperCase()) 'A' else 'a'
                    val offset = (char - base - SHIFT + 26) % 26
                    base + offset
                }
                char.isDigit() -> {
                    val base = '0'
                    val offset = (char - base - SHIFT + 10) % 10
                    base + offset
                }
                else -> char
            }
        }.joinToString("")
    }

    // Simple hex encoding to ensure URL safety after caesar cipher
    fun encryptToUrlSafe(text: String): String {
        val encrypted = encrypt(text)
        return toHex(encrypted)
    }

    fun decryptFromUrlSafe(hex: String): String {
        val encrypted = fromHex(hex)
        return decrypt(encrypted)
    }

    private fun toHex(str: String): String {
        return str.toByteArray(Charsets.UTF_8).joinToString("") { "%02x".format(it) }
    }

    private fun fromHex(hex: String): String {
        val bytes = ByteArray(hex.length / 2)
        for (i in bytes.indices) {
            val index = i * 2
            val v = hex.substring(index, index + 2).toInt(16)
            bytes[i] = v.toByte()
        }
        return String(bytes, Charsets.UTF_8)
    }
}
