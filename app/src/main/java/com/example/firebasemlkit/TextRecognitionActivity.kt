package com.example.firebasemlkit

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.document.FirebaseVisionCloudDocumentRecognizerOptions
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentTextRecognizer
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions
import com.google.firebase.ml.vision.text.FirebaseVisionText
import java.io.IOException
import java.io.InputStream
import java.util.*

class TextRecognitionActivity : AppCompatActivity() {
    var image: FirebaseVisionImage? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //recognizeText(image);
        recognizeTextCloud(image)
    }

    private fun recognizeText(image: FirebaseVisionImage) {

        // [START get_detector_default]
        var image: FirebaseVisionImage? = image
        val detector = FirebaseVision.getInstance()
            .onDeviceTextRecognizer
        // [END get_detector_default]
        val assetManager = assets
        var istr: InputStream? = null
        try {
            istr = assetManager.open("card.jpg")
            val bitmap = BitmapFactory.decodeStream(istr)
            image = FirebaseVisionImage.fromBitmap(bitmap)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        assert(istr != null)
        // [START run_detector]
        val result =
            detector.processImage(image!!)
                .addOnSuccessListener { firebaseVisionText ->
                    // Task completed successfully
                    // [START_EXCLUDE]
                    // [START get_text]
                    for (block in firebaseVisionText.textBlocks) {
                        val boundingBox = block.boundingBox
                        val cornerPoints =
                            block.cornerPoints
                        val text = block.text
                        Log.e("Android", text)
                        for (line in block.lines) {
                            Log.e("Android1", line.text)
                            // ...
                            for (element in line.elements) {
                                // ...
                                Log.e("Android2", element.text)
                            }
                        }
                    }
                    // [END get_text]
                    // [END_EXCLUDE]
                }
                .addOnFailureListener {
                    // Task failed with an exception
                    // ...
                }
        // [END run_detector]
    }

    private fun recognizeTextCloud(image: FirebaseVisionImage?) {
        // [START set_detector_options_cloud]
        var image = image
        val options =
            FirebaseVisionCloudTextRecognizerOptions.Builder()
                .setLanguageHints(Arrays.asList("en", "hi"))
                .build()
        // [END set_detector_options_cloud]
        val assetManager = assets
        var istr: InputStream? = null
        try {
            istr = assetManager.open("card.jpg")
            val bitmap = BitmapFactory.decodeStream(istr)
            image = FirebaseVisionImage.fromBitmap(bitmap)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        assert(istr != null)
        // [START get_detector_cloud]
        val detector = FirebaseVision.getInstance()
            .cloudTextRecognizer
        // Or, to change the default settings:
        //   FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
        //          .getCloudTextRecognizer(options);
        // [END get_detector_cloud]

        // [START run_detector_cloud]
        val result =
            detector.processImage(image!!)
                .addOnSuccessListener { result ->
                    // Task completed successfully
                    // [START_EXCLUDE]
                    // [START get_text_cloud]
                    for (block in result.textBlocks) {
                        val boundingBox = block.boundingBox
                        val cornerPoints =
                            block.cornerPoints
                        val text = block.text
                        Log.e("Android", text)
                        for (line in block.lines) {
                            Log.e("Android1", line.text)
                            // ...
                            for (element in line.elements) {
                                Log.e("Android2", element.text)
                                // ...
                            }
                        }
                    }
                    // [END get_text_cloud]
                    // [END_EXCLUDE]
                }
                .addOnFailureListener {
                    // Task failed with an exception
                    // ...
                }
        // [END run_detector_cloud]
    }

    private fun processTextBlock(result: FirebaseVisionText) {
        // [START mlkit_process_text_block]
        val resultText = result.text
        for (block in result.textBlocks) {
            val blockText = block.text
            val blockConfidence = block.confidence
            val blockLanguages =
                block.recognizedLanguages
            val blockCornerPoints = block.cornerPoints
            val blockFrame = block.boundingBox
            for (line in block.lines) {
                val lineText = line.text
                val lineConfidence = line.confidence
                val lineLanguages =
                    line.recognizedLanguages
                val lineCornerPoints = line.cornerPoints
                val lineFrame = line.boundingBox
                for (element in line.elements) {
                    val elementText = element.text
                    val elementConfidence = element.confidence
                    val elementLanguages =
                        element.recognizedLanguages
                    val elementCornerPoints =
                        element.cornerPoints
                    val elementFrame = element.boundingBox
                }
            }
        }
        // [END mlkit_process_text_block]
    }

    // [START mlkit_local_doc_recognizer]
    private val localDocumentRecognizer: FirebaseVisionDocumentTextRecognizer
        private get() =// [START mlkit_local_doc_recognizer]
            // [END mlkit_local_doc_recognizer]
            FirebaseVision.getInstance()
                .cloudDocumentTextRecognizer

    // [START mlkit_cloud_doc_recognizer]
    // Or, to provide language hints to assist with language detection:
    // See https://cloud.google.com/vision/docs/languages for supported languages
    private val cloudDocumentRecognizer: FirebaseVisionDocumentTextRecognizer
        private get() {
            // [START mlkit_cloud_doc_recognizer]
            // Or, to provide language hints to assist with language detection:
            // See https://cloud.google.com/vision/docs/languages for supported languages
            val options =
                FirebaseVisionCloudDocumentRecognizerOptions.Builder()
                    .setLanguageHints(Arrays.asList("en", "hi"))
                    .build()
            // [END mlkit_cloud_doc_recognizer]
            return FirebaseVision.getInstance()
                .getCloudDocumentTextRecognizer(options)
        }

    private fun processDocumentImage() {
        // Dummy variables
        val detector = localDocumentRecognizer
        val myImage = FirebaseVisionImage.fromByteArray(
            byteArrayOf(),
            FirebaseVisionImageMetadata.Builder().build()
        )

        // [START mlkit_process_doc_image]
        detector.processImage(myImage)
            .addOnSuccessListener {
                // Task completed successfully
                // ...
            }
            .addOnFailureListener {
                // Task failed with an exception
                // ...
            }
        // [END mlkit_process_doc_image]
    }

    private fun processDocumentTextBlock(result: FirebaseVisionDocumentText) {
        // [START mlkit_process_document_text_block]
        val resultText = result.text
        for (block in result.blocks) {
            val blockText = block.text
            val blockConfidence = block.confidence
            val blockRecognizedLanguages =
                block.recognizedLanguages
            val blockFrame = block.boundingBox
            for (paragraph in block.paragraphs) {
                val paragraphText = paragraph.text
                val paragraphConfidence = paragraph.confidence
                val paragraphRecognizedLanguages =
                    paragraph.recognizedLanguages
                val paragraphFrame = paragraph.boundingBox
                for (word in paragraph.words) {
                    val wordText = word.text
                    val wordConfidence = word.confidence
                    val wordRecognizedLanguages =
                        word.recognizedLanguages
                    val wordFrame = word.boundingBox
                    for (symbol in word.symbols) {
                        val symbolText = symbol.text
                        val symbolConfidence = symbol.confidence
                        val symbolRecognizedLanguages =
                            symbol.recognizedLanguages
                        val symbolFrame = symbol.boundingBox
                    }
                }
            }
        }
        // [END mlkit_process_document_text_block]
    }
}