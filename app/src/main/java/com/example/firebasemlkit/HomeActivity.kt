package com.example.firebasemlkit

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasemlkit.adapter.WordAdapter
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import kotlinx.android.synthetic.main.activity_home.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class HomeActivity : AppCompatActivity(), WordAdapter.ItemClickListener {
    private val REQUEST_IMAGE_CAPTURE = 1000
    lateinit var currentPhotoPath: String
    var recyclerView: RecyclerView? = null
    var adapter: WordAdapter? = null
    var wordsList = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        recyclerView = wordsListView
        // recyclerView.layoutManager = LinearLayoutManager(this)
        wordsList.add("A")
        wordsList.add("A")
        wordsList.add("A")
        wordsList.add("A")
        wordsList.add("A")
        adapter = WordAdapter(this, wordsList)
        adapter?.setClickListener(this)
        recyclerView?.adapter = adapter
        dispatchTakePictureIntent()
        retake.setOnClickListener {dispatchTakePictureIntent()  }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            imageView.setImageBitmap(imageBitmap)
            val image = FirebaseVisionImage.fromBitmap(imageBitmap)
            recognizeText(image)
            createImageFile()
           // setPic()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }
//    private fun dispatchTakePictureIntent() {
//        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
//            // Ensure that there's a camera activity to handle the intent
//            takePictureIntent.resolveActivity(packageManager)?.also {
//                // Create the File where the photo should go
//                val photoFile: File? = try {
//                    createImageFile()
//                } catch (ex: IOException) {
//                    // Error occurred while creating the File
//                    Log.e("Android",ex.toString())
//                    null
//                }
//                // Continue only if the File was successfully created
//                photoFile?.also {
//                    val photoURI: Uri = FileProvider.getUriForFile(
//                        this,
//                        "com.example.android.fileprovider",
//                        it
//                    )
//                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
//                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
//                }
//            }
//        }
//    }

    private fun setPic() {
        // Get the dimensions of the View
        val targetW: Int = imageView.width
        val targetH: Int = imageView.height

        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true

            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // Determine how much to scale down the image
            val scaleFactor: Int = Math.min(photoW / targetW, photoH / targetH)

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
            inPurgeable = true
        }
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions)?.also { bitmap ->
            imageView.setImageBitmap(bitmap)
        }
    }

    private fun recognizeText(image: FirebaseVisionImage) {

        // [START get_detector_default]
        var image: FirebaseVisionImage? = image
        val detector = FirebaseVision.getInstance()
            .onDeviceTextRecognizer
        // [END get_detector_default]
        // [START run_detector]
        wordsList.clear()
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
                        wordsList.add(text)
                        for (line in block.lines) {
                //            Log.e("Android1", line.text)
                            // ...
                            for (element in line.elements) {
                                // ...
                //                Log.e("Android2", element.text)
                            }
                        }
                    }
                    // [END get_text]
                    // [END_EXCLUDE  ]
                    adapter?.updateList(wordsList)
                }
                .addOnFailureListener {
                    // Task failed with an exception
                    // ...
                }
        // [END run_detector]
    }

    fun showText() {
        // Task failed with an exception

    }

    override fun onItemClick(view: View?, position: Int) {
        Toast.makeText(this,wordsList[position],Toast.LENGTH_SHORT).show()
    }
}