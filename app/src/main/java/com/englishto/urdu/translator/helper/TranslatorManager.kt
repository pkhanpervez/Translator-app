package com.englishto.urdu.translator.helper

import android.app.Activity
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.englishto.urdu.translator.interfaces.LanguageDownloadListener
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslateRemoteModel
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions

class TranslatorManager(val myActivity: Activity,
                        private val linearLayoutProgress: LinearLayout,
                        private val linearLayout: LinearLayout) {

    private var translator: Translator? = null
    lateinit var languageDownloadListener: LanguageDownloadListener

    fun initializeTranslator(sourceLang: String, targetLang: String) {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLang)
            .setTargetLanguage(targetLang)
            .build()
        translator = com.google.mlkit.nl.translate.Translation.getClient(options)


        linearLayoutProgress.visibility = View.VISIBLE
//        linearLayout.visibility = View.GONE
        val model = TranslateRemoteModel.Builder(targetLang).build()
        val modelManager = RemoteModelManager.getInstance()

        languageDownloadListener = myActivity as LanguageDownloadListener


        modelManager.isModelDownloaded(model).addOnSuccessListener { isDownloaded ->
            if (isDownloaded) {
                // If the model is already downloaded, hide loader
                linearLayoutProgress.visibility = View.GONE
//                linearLayout.visibility = View.VISIBLE
                languageDownloadListener.updateDownloadedStatus(true)
            } else {
                // If the model is not downloaded, download it
                downloadModel(model)
            }
        }

    }

    fun downloadForOffline(sourceLang: String, targetLang: String,
                           linearLayoutProgress: LinearLayout,
                           linearLayout: LinearLayout) {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLang)
            .setTargetLanguage(targetLang)
            .build()
        translator = com.google.mlkit.nl.translate.Translation.getClient(options)

        linearLayoutProgress.visibility = View.VISIBLE
//        linearLayout.visibility = View.GONE
        val model = TranslateRemoteModel.Builder(targetLang).build()
        val modelManager = RemoteModelManager.getInstance()

        modelManager.isModelDownloaded(model).addOnSuccessListener { isDownloaded ->
            if (isDownloaded) {
                // If the model is already downloaded, hide loader
                linearLayoutProgress.visibility = View.GONE
//                linearLayout.visibility = View.VISIBLE
            } else {
                // If the model is not downloaded, download it
                downloadModel(model)
            }
        }
    }


    private fun downloadModel(model: TranslateRemoteModel) {
        val conditions = DownloadConditions.Builder()
//            .requireWifi() // Download only on Wi-Fi
            .build()

        val modelManager = RemoteModelManager.getInstance()
        modelManager.download(model, conditions)
            .addOnSuccessListener {
                // Hide loader and show success message
                linearLayoutProgress.visibility = View.GONE
                linearLayout.visibility = View.VISIBLE

                languageDownloadListener.updateDownloadedStatus(true)
            }
            .addOnFailureListener {
                // Hide loader and show error message
                linearLayoutProgress.visibility = View.GONE
//                linearLayout.visibility = View.VISIBLE

                languageDownloadListener.updateDownloadedStatus(false)

            }
    }

    fun translateText(inputText: String, callback: (String?) -> Unit) {
        translator?.translate(inputText)?.addOnSuccessListener { translatedText ->
            callback(translatedText)
        }?.
        addOnFailureListener { exception ->
            // Handle translation error
            callback(null)
        }
    }

    fun close() {
        translator?.close()
    }
}
