package com.example.socialmediaapp

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.fragment_second.*
import kotlinx.android.synthetic.main.fragment_second.view.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class SecondFragment : Fragment() {

    private lateinit var ImageUri : Uri
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_second, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        val user = Firebase.auth.currentUser
        Username.text = user?.displayName.toString()

        selectButton.setOnClickListener(){

            selectImage()

        }

        uploadButton.setOnClickListener(){

            uploadImage()

        }

        retrieveButton.setOnClickListener(){

            retrieveImages()

        }

    }

    private fun retrieveImages() {

        val progressDialog = ProgressDialog(activity)
        progressDialog.setMessage("Fetching image....")
        progressDialog.setCancelable(false)
        progressDialog.show()


        firebaseAnalytics = Firebase.analytics

        val user = Firebase.auth.currentUser
        val storage = Firebase.storage
        val userFolder = user.toString()
        val localfile = File.createTempFile("tempImage", "jpg")
        val storageRef = FirebaseStorage.getInstance().reference.child("images/userUploaded/$userFolder")
        var iterator = 1
        var httpsReference: StorageReference
        var bitmap: Bitmap

        val listAllTask: Task<ListResult> = storageRef.listAll()
        listAllTask.addOnCompleteListener { result ->
            val items: List<StorageReference> = result.result!!.items
            items.forEachIndexed { index, item ->
                item.downloadUrl.addOnSuccessListener {

                    httpsReference = storage.getReferenceFromUrl("$it")
                    httpsReference.getFile(localfile).addOnSuccessListener {
                        bitmap = BitmapFactory.decodeFile(localfile.absolutePath)

                        if (iterator==1){image1.setImageBitmap(bitmap)}
                        if (iterator==2){image2.setImageBitmap(bitmap)}
                        if (iterator==3){image3.setImageBitmap(bitmap)}

                        Log.d("images", "$it")
                        Log.d("iterator", "$iterator")
                        iterator+=1
                    }

                }

            }
        }

            if (progressDialog.isShowing)
                progressDialog.dismiss()


    }

    private fun uploadImage() {

        val progressDialog = ProgressDialog(activity)
        progressDialog.setMessage("Uploading file....")
        progressDialog.setCancelable(false)
        progressDialog.show()

        var formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        var now = Date()
        var fileName = formatter.format(now) + ".jpg"
        val user = Firebase.auth.currentUser
        val userFolder = user.toString()

        val storageReference = FirebaseStorage.getInstance().getReference("images/userUploaded/$userFolder/$fileName")

        storageReference.putFile(ImageUri).
        addOnSuccessListener {
            image1.setImageURI(null)
            if (progressDialog.isShowing) progressDialog.dismiss()

        }.addOnFailureListener{
            Toast.makeText(activity,"It broke sadge", Toast.LENGTH_LONG).show()
            if (progressDialog.isShowing) progressDialog.dismiss()

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

        if (requestCode == 100 && resultCode == AppCompatActivity.RESULT_OK){
            ImageUri= data?.data!!
            image1.setImageURI(ImageUri)
        }
    }
}
