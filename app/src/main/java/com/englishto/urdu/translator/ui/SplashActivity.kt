package com.englishto.urdu.translator.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.englishto.urdu.translator.BuildConfig
import com.englishto.urdu.translator.MainActivity
import com.englishto.urdu.translator.databinding.ActivitySplashBinding
import com.englishto.urdu.translator.helper.TranslatorManager
import com.englishto.urdu.translator.interfaces.LanguageDownloadListener
import com.google.mlkit.nl.translate.TranslateLanguage

class SplashActivity : AppCompatActivity(), LanguageDownloadListener {

    lateinit var binding: ActivitySplashBinding
    private lateinit var translatorManager: TranslatorManager
    var selectedLanguageCode = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (BuildConfig.FLAVOR == "english_to_urdu") {
            selectedLanguageCode = TranslateLanguage.URDU
        } else if (BuildConfig.FLAVOR == "english_to_hindi") {
            selectedLanguageCode = TranslateLanguage.HINDI
        } else if (BuildConfig.FLAVOR == "arabic_to_english") {
            selectedLanguageCode = TranslateLanguage.ARABIC
        } else if (BuildConfig.FLAVOR == "english_to_marathi") {
            selectedLanguageCode = TranslateLanguage.MARATHI
        }

        translatorManager = TranslatorManager(this, binding.linearLayoutProgress, binding.linearLayout)
        translatorManager.initializeTranslator(
            TranslateLanguage.ENGLISH, // Source language
            selectedLanguageCode  // Target language
        )

    }


    override fun onDestroy() {
        super.onDestroy()
        translatorManager.close()
    }

    override fun updateDownloadedStatus(isDownloaded: Boolean) {
        if(isDownloaded) {
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
//            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        } else {
            translatorManager = TranslatorManager(this, binding.linearLayoutProgress, binding.linearLayout)
            translatorManager.initializeTranslator(
                TranslateLanguage.ENGLISH, // Source language
                selectedLanguageCode  // Target language
            )
        }
    }
}