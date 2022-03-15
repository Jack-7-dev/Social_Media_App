package com.example.socialmediaapp

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.socialmediaapp.databinding.ActivityStorageBinding
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class StorageActivity : AppCompatActivity() {

    lateinit var binding : ActivityStorageBinding
    lateinit var ImageUri : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStorageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.selectImageBtn.setOnClickListener{

            selectImage()

        }

        binding.uploadImageBtn.setOnClickListener{

            uploadImage()

        }

        binding.goToMainActivity.setOnClickListener{

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        }
    }

    private fun uploadImage() {

        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading file....")
        progressDialog.setCancelable(false)
        progressDialog.show()

        var formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        var now = Date()
        var fileName = formatter.format(now) + ".jpg"

        val storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")

        storageReference.putFile(ImageUri).
                addOnSuccessListener {

                    binding.firebaseImage.setImageURI(null)
                    Toast.makeText(this@StorageActivity, "Successfully uploaded image",Toast.LENGTH_SHORT).show()
                    if (progressDialog.isShowing) progressDialog.dismiss()

                }.addOnFailureListener{

                    if (progressDialog.isShowing) progressDialog.dismiss()
                    Toast.makeText(this@StorageActivity, "Failed to upload image",Toast.LENGTH_SHORT).show()

                }

    }

    private fun selectImage() {

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK){

            ImageUri = data?.data!!
            binding.firebaseImage.setImageURI(ImageUri)



        }

    }
}