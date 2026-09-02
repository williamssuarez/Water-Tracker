package com.example.ui.i18n

enum class AppLanguage(
    val code: String,
    val displayName: String,
    val flagEmoji: String
) {
    ENGLISH("en", "English", "🇺🇸"),
    SPANISH("es", "Español", "🇪🇸");

    companion object {
        fun fromCode(code: String?): AppLanguage {
            return values().firstOrNull { it.code.equals(code, ignoreCase = true) } ?: ENGLISH
        }
    }
}
