package com.innovatex.auracast.data

import android.app.LocaleManager
import android.content.Context
import android.content.SharedPreferences
import android.os.LocaleList
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Accessibility preferences, kept in SharedPreferences.
 *
 * A plain object rather than a ViewModel because the theme reads these
 * before any NavHost exists — text size has to be applied above the whole
 * composition, where no ViewModelStoreOwner is in scope yet.
 */
object AppSettings {

    private const val FILE = "auracast.settings"
    private const val KEY_TEXT_SIZE = "text_size"
    private const val KEY_VIBRATE = "vibrate_on_connect"
    private const val KEY_FLASH = "flash_on_connect"
    private const val KEY_KEEP_AWAKE = "keep_screen_on"

    private var prefs: SharedPreferences? = null

    var textSize by mutableStateOf(TextSizeOption.STANDARD)
        private set

    var vibrateOnConnect by mutableStateOf(true)
        private set

    var flashOnConnect by mutableStateOf(false)
        private set

    var keepScreenOn by mutableStateOf(true)
        private set

    /** Called once from the Activity, before the theme is applied. */
    fun load(context: Context) {
        val store = context.applicationContext
            .getSharedPreferences(FILE, Context.MODE_PRIVATE)
        prefs = store

        textSize = runCatching {
            TextSizeOption.valueOf(
                store.getString(KEY_TEXT_SIZE, TextSizeOption.STANDARD.name)!!
            )
        }.getOrDefault(TextSizeOption.STANDARD)

        vibrateOnConnect = store.getBoolean(KEY_VIBRATE, true)
        flashOnConnect = store.getBoolean(KEY_FLASH, false)
        keepScreenOn = store.getBoolean(KEY_KEEP_AWAKE, true)
    }

    /* Named update… rather than set…: a `var` with a private setter
     * already compiles to setTextSize(), and a function of the same name
     * clashes with it on the JVM.
     */
    fun updateTextSize(value: TextSizeOption) {
        textSize = value
        prefs?.edit()?.putString(KEY_TEXT_SIZE, value.name)?.apply()
    }

    fun updateVibrateOnConnect(value: Boolean) {
        vibrateOnConnect = value
        prefs?.edit()?.putBoolean(KEY_VIBRATE, value)?.apply()
    }

    fun updateFlashOnConnect(value: Boolean) {
        flashOnConnect = value
        prefs?.edit()?.putBoolean(KEY_FLASH, value)?.apply()
    }

    fun updateKeepScreenOn(value: Boolean) {
        keepScreenOn = value
        prefs?.edit()?.putBoolean(KEY_KEEP_AWAKE, value)?.apply()
    }

    /**
     * The language the interface is currently shown in.
     *
     * Not stored here. Android owns per-app language from 13 onward, which
     * means it survives reinstalls, appears in system Settings, and stays
     * correct if the rider changes it from there instead of from this app.
     * Keeping a second copy would only let the two disagree.
     */
    fun currentLanguage(context: Context): AppLanguage {
        val tag = context.getSystemService(LocaleManager::class.java)
            ?.applicationLocales
            ?.takeUnless { it.isEmpty }
            ?.get(0)
            ?.language

        return AppLanguage.entries.firstOrNull { it.tag == tag } ?: AppLanguage.ENGLISH
    }

    /** Applying this recreates the Activity; Android handles that itself. */
    fun setLanguage(context: Context, language: AppLanguage) {
        context.getSystemService(LocaleManager::class.java)
            ?.applicationLocales = LocaleList.forLanguageTags(language.tag)
    }
}

enum class TextSizeOption(val scale: Float) {
    STANDARD(1.0f),
    LARGE(1.15f),
    LARGEST(1.3f)
}

/**
 * Each language is labelled with its own endonym, never translated — a
 * rider who cannot read the current interface has to be able to find
 * their own language in the list.
 */
enum class AppLanguage(val tag: String, val endonym: String) {
    ENGLISH("en", "English"),
    CHINESE("zh", "中文"),
    HINDI("hi", "हिन्दी"),
    JAPANESE("ja", "日本語"),
    ARABIC("ar", "العربية"),
    SPANISH("es", "Español"),
    FRENCH("fr", "Français")
}