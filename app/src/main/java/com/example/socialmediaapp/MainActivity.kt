package com.example.socialmediaapp

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import com.example.socialmediaapp.databinding.ActivityMainBinding
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toStorageActivity.setOnClickListener{

            val intent = Intent(this, StorageActivity::class.java)
            startActivity(intent)

        }

        binding.getImage.setOnClickListener{

            val progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Fetching image....")
            progressDialog.setCancelable(false)
            progressDialog.show()


            val imageName = binding.etImageId.text.toString()
            val storageRef = FirebaseStorage.getInstance().reference.child("images/$imageName.jpg")

            val localfile = File.createTempFile("tempImage", "jpg")
            storageRef.getFile(localfile).addOnSuccessListener {

                if (progressDialog.isShowing)
                    progressDialog.dismiss()

                val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                binding.imageview.setImageBitmap(bitmap)


            }.addOnFailureListener{

                if (progressDialog.isShowing)
                    progressDialog.dismiss()
                Toast.makeText(this,"Failed to retrieve file", Toast.LENGTH_SHORT).show()

            }

        }
    }
}