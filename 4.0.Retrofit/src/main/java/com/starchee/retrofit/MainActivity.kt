package com.starchee.retrofit

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    private var imageUri: Uri? = null
    private var isUploadingPhoto = false

    companion object {
        private const val RESULT_IMG_LOAD = 10101
        private const val READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE
            )
        }

        choose_btn_main.setOnClickListener {
            val intent = Intent().apply {
                action = Intent.ACTION_PICK
                type = "image/*"
            }
            startActivityForResult(intent, RESULT_IMG_LOAD)
        }


        upload_btn_main.setOnClickListener {
            if (imageUri == null) {
                Toast.makeText(
                    this,
                    R.string.choose_photo_toast,
                    Toast.LENGTH_LONG
                ).show()
            } else if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(
                    this,
                    getString(R.string.read_external_storage_permission_toast),
                    Toast.LENGTH_LONG
                ).show()
            } else if (!isUploadingPhoto) {
                uploadImage()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RESULT_IMG_LOAD -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    image_main.setImageURI(data.data)
                    title_layout_main.visibility = View.VISIBLE
                    description_layout_main.visibility = View.VISIBLE
                    imageUri = data.data
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(
                        this,
                        getString(R.string.read_external_storage_permission_toast),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun uploadImage() {
        isUploadingPhoto = true
        progress_main.visibility = View.VISIBLE
        var imageFile: File? = null
        imageUri?.let {
            imageFile = getFileByUri(it)
        }
        imageFile?.let { file ->
            RetrofitRepository.getInstance(resources).uploadImage(
                title_et_main.text.toString(),
                description_et_main.text.toString(),
                file
            ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    Toast.makeText(
                        this,
                        "Uploading success, Title: ${response.data.title} Description: ${response.data.description}",
                        Toast.LENGTH_LONG
                    ).show()
                    progress_main.visibility = View.GONE
                    isUploadingPhoto = false
                    openLink(response.data.link)
                }, {
                    Toast.makeText(
                        this,
                        "Error!",
                        Toast.LENGTH_SHORT
                    ).show()
                    progress_main.visibility = View.GONE
                    isUploadingPhoto = false
                })
        }
    }

    private fun getFileByUri(uri: Uri)
            : File
    ? {
        var imagePath: String? = null
        var imageFile: File? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)

        cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            it.moveToFirst()
            imagePath = it.getString(columnIndex)
        }

        imagePath?.let {
            imageFile = File(it)
        }
        return imageFile
    }

    private fun openLink(link: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        startActivity(intent)
    }
}
