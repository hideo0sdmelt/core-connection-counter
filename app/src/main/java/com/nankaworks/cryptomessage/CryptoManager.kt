package com.nankaworks.cryptomessage

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class CryptoManager {
    companion object {
        private const val PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256"
        private const val HEADER = "Minnann_"
        private const val ITERATIONS = 1000
        private const val KEY_LENGTH = 256 // ビット数
        private const val IV_LENGTH = 128 // ビット数
        private const val HEADER_LENGTH = 8 // バイト数
        private const val SALT_LENGTH = 8 // バイト数

        fun encrypt(text: String, key: String): String {

            // ランダムでsaltを作成
            val salt = ByteArray(SALT_LENGTH).apply {
                SecureRandom().nextBytes(this)
            }

            // saltからbitKeyとivを生成
            val (bitKey, iv) = generateKeyAndIvFromSaltAndKeyPhrase(salt, key)

            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
            val secretKey = SecretKeySpec(bitKey, "AES")
            val ivSpec = IvParameterSpec(iv)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)
            val encrypted = cipher.doFinal(text.toByteArray())

            return Base64.encodeToString((HEADER
                    + salt.joinToString("") { "%02x".format(it) }
                    + encrypted.joinToString("") { "%02x".format(it) }).toByteArray(), Base64.DEFAULT
            )
        }

        fun decrypt(encryptedText: String, key: String): String {
            // Base64デコード（不正な場合のみ個別にcatch）
            val encryptedBytes = try {
                Base64.decode(encryptedText, Base64.DEFAULT)
            } catch (e: IllegalArgumentException) {
                throw com.nankaworks.cryptomessage.exceptions.InvalidQrFormatException()
            }

            // 暗号文の形式から各部分を取り出す
            val saltString = String(encryptedBytes.copyOfRange(0, HEADER_LENGTH))

            if (saltString != HEADER) {
                throw com.nankaworks.cryptomessage.exceptions.InvalidQrFormatException()
            }

            // saltを取り出す (長さは2倍)
            val salt = encryptedBytes.copyOfRange(HEADER_LENGTH, HEADER_LENGTH + SALT_LENGTH * 2)

            // 暗号文を取り出す
            val ciphertext = encryptedBytes.copyOfRange(HEADER_LENGTH + SALT_LENGTH * 2, encryptedBytes.size)

            // saltとキーフレーズからキーとIVを生成
            val (bitKey, iv) = generateKeyAndIvFromSaltAndKeyPhrase(hexStringToByteArray(String(salt)), key)

            // 復号
            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
            val secretKey = SecretKeySpec(bitKey, "AES")
            val ivSpec = IvParameterSpec(iv)

            try {
                cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)
                val decryptedBytes = cipher.doFinal(hexStringToByteArray(String(ciphertext)))
                return String(decryptedBytes)
            } catch (e: javax.crypto.BadPaddingException) {
                throw com.nankaworks.cryptomessage.exceptions.InvalidPasswordException()
            } catch (e: Exception) {
                throw e
            }
        }

        /**
         * 16進数文字列をByteArrayに変換
         * 例: "1A2B" -> ByteArray[0x1A, 0x2B]
         */
        private fun hexStringToByteArray(hexString: String): ByteArray {
            val len = hexString.length
            if (len % 2 != 0) {
                throw IllegalArgumentException("Hex string must have an even length")
            }

            return ByteArray(len / 2) { i ->
                val j = i * 2
                val high = hexString[j].digitToInt(16)
                val low = hexString[j + 1].digitToInt(16)
                ((high shl 4) or low).toByte()
            }
        }

        /**
         * PBKDF2を使用して、ソルトとキーフレーズからキーとIVを同時に生成
         */
        private fun generateKeyAndIvFromSaltAndKeyPhrase(salt: ByteArray, keyPhrase: String): Pair<ByteArray, ByteArray> {
            // キーとIVの合計長を設定
            val totalLength = KEY_LENGTH + IV_LENGTH

            val spec = PBEKeySpec(
                keyPhrase.toCharArray(),
                salt,
                ITERATIONS,
                totalLength  // キーとIVの合計長
            )

            try {
                val factory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM)
                val combinedKey = factory.generateSecret(spec).encoded

                // キーとIVに分割
                val key = combinedKey.copyOfRange(0, KEY_LENGTH / 8)
                val iv = combinedKey.copyOfRange(KEY_LENGTH / 8, combinedKey.size)

                return Pair(key, iv)
            } finally {
                spec.clearPassword()
            }
        }
    }
}
