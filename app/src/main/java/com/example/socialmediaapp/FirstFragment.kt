package com.example.socialmediaapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.fragment_first.*
import java.io.File
import kotlin.math.log


class FirstFragment : Fragment() {

    private lateinit var database: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_first, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        database = Firebase.database.reference
        val storage = Firebase.storage
        val localfile = File.createTempFile("tempImage", "jpg")
        val databaseReference = database.child("images")
        var bitmap: Bitmap
        var httpsReference: StorageReference
        var iterator = 1
        databaseReference.get().addOnSuccessListener { res ->
            val split: List<String> = res.toString().split("=".toRegex())
            for (item in split) {
                if (item.startsWith("https://firebasestorage.googleapis.com/v0/b/social-media-app-34102.appspot.com/o/images%2FuserUploaded%2")){
                    httpsReference = storage.getReferenceFromUrl(item)
                    httpsReference.getFile(localfile).addOnSuccessListener {
                        bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                        if (iterator==1){homeImage1.setImageBitmap(bitmap)}
                        if (iterator==2){homeImage2.setImageBitmap(bitmap)}
                        iterator +=1
                    }
                }
            }
        }
    }
}