package com.englishto.urdu.translator.ui.home

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.englishto.urdu.translator.BuildConfig
import com.englishto.urdu.translator.Data
import com.englishto.urdu.translator.databinding.FragmentHomeBinding
import com.englishto.urdu.translator.helper.TranslatorManager
import com.englishto.urdu.translator.prefrence.SharedPref
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.Locale
import android.speech.tts.TextToSpeech
import java.io.FileNotFoundException


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var translatorManager: TranslatorManager
    var isEnglish = true
    private var extractedText: String = ""

    private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>
    private lateinit var selectImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var speechRecognizerLauncher: ActivityResultLauncher<Intent>
    var selectedLanguageCode = ""
    var selectedLanguage = ""
    var selectedHint1 = ""
    var selectedHint2 = ""
    var list = mutableListOf<Data>()
    var isHorizontal = false
    private lateinit var tts: TextToSpeech

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (BuildConfig.FLAVOR == "english_to_urdu") {
            selectedLanguageCode = TranslateLanguage.URDU
            selectedLanguage = "اردو"
            selectedHint1 = "انگریزی ٹائپ کریں یا پیسٹ کریں۔"
            selectedHint2 = "اردو ٹائپ کریں یا پیسٹ کریں۔"
        } else if (BuildConfig.FLAVOR == "english_to_hindi") {
            selectedLanguageCode = TranslateLanguage.HINDI
            selectedLanguage = "हिंदी"
            selectedHint1 = "अंग्रेजी टाइप करें या पेस्ट करें।"
            selectedHint2 = "हिंदी टाइप करें या पेस्ट करें।"
//            binding.tvTarjama.text = "भाषान्तर"
        } else if (BuildConfig.FLAVOR == "arabic_to_english") {
            selectedLanguageCode = TranslateLanguage.ARABIC
            selectedLanguage = "عربي"
            selectedHint1 = "اكتب اللغة الإنجليزية أو الصقها."
            selectedHint2 = "اكتب أو الصق عربي."
        } else if (BuildConfig.FLAVOR == "english_to_marathi") {
            selectedLanguageCode = TranslateLanguage.MARATHI
            selectedLanguage = "मराठी"
            selectedHint1 = "इंग्रजीमध्ये टाइप करा किंवा पेस्ट करा"
            selectedHint2 = "मराठीत टाइप करा किंवा पेस्ट करा"
        }

        binding.tvEnglish.text = "ENGLISH"
        binding.etUrdu.hint = selectedLanguage
        binding.etEnglish.hint = selectedHint1

        speechRecognizerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val data = result.data
                    val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    results?.let {
                        binding.etEnglish.setText(it[0]) // Set the recognized text into the EditText
                    }
                } else {
                    Toast.makeText(requireActivity(), "Speech recognition canceled or failed", Toast.LENGTH_SHORT).show()
                }
            }

        takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                try {
                    val imageBitmap = result.data?.extras?.get("data") as Bitmap
                    recognizeTextFromImage(imageBitmap)
                }  catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Unable to capture the image", Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(requireContext(), "Image capture failed or canceled", Toast.LENGTH_SHORT).show()
            }
        }

        selectImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val selectedImageUri = result.data?.data
                selectedImageUri?.let {
                    try {
                        val inputStream = requireActivity().contentResolver.openInputStream(it)
                        val imageBitmap = BitmapFactory.decodeStream(inputStream)
                        inputStream?.close()

                        // Process the image
                        recognizeTextFromImage(imageBitmap)
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                        Toast.makeText(requireContext(), "Unable to access the image", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Unable to open gallery", Toast.LENGTH_SHORT).show()
            }
        }

        translatorManager = TranslatorManager(requireActivity(), binding.linearLayoutProgress, binding.linearLayout)
        translatorManager.initializeTranslator(
            TranslateLanguage.ENGLISH, // Source language
            selectedLanguageCode  // Target language
        )

        binding.ivSend.setOnClickListener {
            translateText()
        }

    }

    override fun onResume() {
        super.onResume()
        val lastOrientation = SharedPref.getInstance(requireContext()).getFromSession("IS_HORIZONTAL", "false")

        if(lastOrientation.equals("true")) {
            isHorizontal = true
            binding.scrollViewVertical.visibility = View.GONE
        } else {
            isHorizontal = false
            binding.scrollViewVertical.visibility = View.VISIBLE
        }

        val inputTextSize = SharedPref.getInstance(requireContext()).getFromSession("INPUT_TEXT_SIZE", "18").toFloat()
        val translatedTextSize = SharedPref.getInstance(requireContext()).getFromSession("TRANSLATED_TEXT_SIZE", "18").toFloat()

        binding.etEnglish.setTextSize(TypedValue.COMPLEX_UNIT_SP, inputTextSize)

        binding.etUrdu.setTextSize(TypedValue.COMPLEX_UNIT_SP, translatedTextSize)

    }
    private fun shareText() {
        var textToShare = ""

        textToShare = binding.etUrdu.text.toString()
        if (textToShare.isNotEmpty()) {
            // Create a share intent
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain" // MIME type for plain text
                putExtra(Intent.EXTRA_TEXT, textToShare) // Text to share
            }

            // Start the share activity
            startActivity(Intent.createChooser(shareIntent, "Share text via"))
        } else {
            Toast.makeText(requireContext(), "No text to share!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun copyContent() {
        var textToCopy = ""
        textToCopy = binding.etUrdu.text.toString()

        if (textToCopy.isNotEmpty()) {
            // Get ClipboardManager instance
            val clipboard =
                requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

            // Create a ClipData object to store the text
            val clip = ClipData.newPlainText("Copied Text", textToCopy)

            // Set the ClipData to the clipboard
            clipboard.setPrimaryClip(clip)

            // Show a Toast message
            Toast.makeText(requireContext(), "Copied!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "No text to copy!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun translateText() {
        var textToTranslate = ""
        textToTranslate = binding.etEnglish.text.toString()

        if (textToTranslate.isNotEmpty()) {
            if(::translatorManager.isInitialized) {
                translatorManager.translateText(textToTranslate) { translatedText ->

                    binding.etUrdu.setText(translatedText ?: "")
                    binding.etEnglish.setText(textToTranslate ?: "")

                }
            }

        }
    }
    private fun swapLanguage() {
        isEnglish = !isEnglish
        if (!isEnglish) { //Urdu at Top
            translatorManager.initializeTranslator(
                selectedLanguageCode, // Source language
                TranslateLanguage.ENGLISH  // Target language
            )
            binding.tvEnglish.text = selectedLanguage
            binding.etUrdu.hint = "ENGLISH"
            binding.etEnglish.hint = selectedHint2

        } else {
            translatorManager.initializeTranslator(
                TranslateLanguage.ENGLISH, // Source language
                selectedLanguageCode  // Target language
            )
            binding.tvEnglish.text = "ENGLISH"
            binding.etUrdu.hint = selectedLanguage
            binding.etEnglish.hint = selectedHint1
        }

        val temp = binding.etEnglish.text.toString()
        binding.etEnglish.setText(binding.etUrdu.text.toString())
        binding.etUrdu.setText(temp)
    }

    private fun startSpeechRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak something...")
        }
        try {
            speechRecognizerLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(
                requireActivity(),
                "Speech recognition is not supported on this device.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                dispatchTakePictureIntent()
            } else {
                Log.e("HomeFragment", "Camera permission not granted")
            }
        }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            takePictureLauncher.launch(takePictureIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "No camera app available", Toast.LENGTH_SHORT).show()
        }
    }

    // Select image from gallery intent
    private fun dispatchSelectFromGalleryIntent() {
        val selectFromGalleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        try {
            selectImageLauncher.launch(selectFromGalleryIntent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Unable to open gallery", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }

        translatorManager.close()
        super.onDestroy()
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun recognizeTextFromImage(imageBitmap: Bitmap) {
        val image = InputImage.fromBitmap(imageBitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                extractedText = visionText.text
                binding.etEnglish.setText(extractedText)
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to recognize text: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}