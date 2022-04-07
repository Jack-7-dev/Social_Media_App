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
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_first.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.File
import java.lang.NullPointerException
import kotlin.math.log


class FirstFragment : Fragment() {

    private lateinit var database: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_first, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        database = Firebase.database.reference
        val storage = Firebase.storage
        val localfile1 = File.createTempFile("tempImage", ".jpg")
        val localfile2 = File.createTempFile("tempImage1", ".jpg")
        val databaseReference = database.child("images")
        var pictureBitmap: Bitmap
        var pfpBitmap: Bitmap
        var httpsReference: StorageReference
        var pfpReference: StorageReference
        var iterator = 1
        var iterator2 = 1
        var itemIterator = -1
        var pictureAuthor: String
        databaseReference.get().addOnSuccessListener { res ->
            val split: List<String> = res.toString().split("=".toRegex())
            Log.d("split",split.toString())
            for (item in split) {
                itemIterator += 1
                Log.d("split[itemIterator]", split[itemIterator])
                Log.d("itemIteratorBeforeIf", itemIterator.toString())
                if (item.startsWith("{author")) {
                    Log.d("https reference", split[itemIterator + 3].split(",")[0])
                    Log.d("itemIteratorAfterIf", itemIterator.toString())
                    httpsReference =
                        storage.getReferenceFromUrl(split[itemIterator + 3].split(",")[0])
                    pfpReference =
                        storage.getReferenceFromUrl(split[itemIterator + 2].split(",")[0])
                    httpsReference.getFile(localfile1).addOnSuccessListener {
                        if(BitmapFactory.decodeFile(localfile1.absolutePath)!=null){pictureBitmap = BitmapFactory.decodeFile(localfile1.absolutePath)
                        pictureAuthor = split[itemIterator - 4].split(",")[0]
                        if (iterator == 1) {
                            homeImage1.setImageBitmap(pictureBitmap)
                            author1.text = pictureAuthor
                        }
                        if (iterator == 2) {
                            homeImage2.setImageBitmap(pictureBitmap)
                            author2.text = pictureAuthor
                        }
                        if (iterator == 3) {
                            homeImage3.setImageBitmap(pictureBitmap)
                            author3.text = pictureAuthor
                        }
                        if (iterator == 4) {
                            homeImage4.setImageBitmap(pictureBitmap)
                            author4.text = pictureAuthor
                        }
                        if (iterator == 5) {
                            homeImage5.setImageBitmap(pictureBitmap)
                            author5.text = pictureAuthor
                        }
                        iterator += 1
                    }}
                    pfpReference.getFile(localfile2).addOnSuccessListener {
                        if(BitmapFactory.decodeFile(localfile2.absolutePath)!=null){pfpBitmap = BitmapFactory.decodeFile(localfile2.absolutePath)
                        if (iterator2 == 1) {
                            pfp1.setImageBitmap(pfpBitmap)
                        }
                        if (iterator2 == 2) {
                            pfp2.setImageBitmap(pfpBitmap)
                        }
                        if (iterator2 == 3) {
                            pfp3.setImageBitmap(pfpBitmap)
                        }
                        if (iterator2 == 4) {
                            pfp4.setImageBitmap(pfpBitmap)
                        }
                        if (iterator2 == 5) {
                            pfp5.setImageBitmap(pfpBitmap)
                        }
                        iterator2 += 1
                    }}
                }
            }
        }
    }
}