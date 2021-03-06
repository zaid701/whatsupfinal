@file:Suppress("ClassName", "DEPRECATION")

package com.example.whatsup.activitys

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import android.os.Bundle
import com.example.whatsup.R
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.core.app.ActivityCompat
import com.example.whatsup.models.User
import kotlinx.android.synthetic.main.activity_setproacti.*
import java.lang.Exception

class setproacti : AppCompatActivity() {

    private var firebaseAuth: FirebaseAuth? = null
    private var firebaseStorage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private var imageuri: Uri? = null
    private var check = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setproacti)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        firebaseAuth = FirebaseAuth.getInstance()
        try {
            firebaseStorage = FirebaseStorage.getInstance()
            storageReference = firebaseStorage!!.reference
            addpropic.setOnLongClickListener {
                check = if (check == 0) {
                    addpropic.setImageResource(R.drawable.woman)
                    1
                } else {
                    addpropic.setImageResource(R.drawable.man)
                    0
                }
                true
            }
            imageView7.setOnClickListener {
                CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1)
                    .start(this@setproacti)
            }
            savebtn.setOnClickListener {
                if (usernametxt.text.toString().isEmpty()) {
                    Toast.makeText(
                        applicationContext,
                        "please enter your username",
                        Toast.LENGTH_SHORT
                    ).show()
                    usernametxt.error = "Enter your username"
                } else if (imageuri != null) {
                    progressBar3.visibility = View.VISIBLE
                    senddatafornewuser()
                }
            }
        } catch (e: Exception) {
        }
    }

    private fun senddatafornewuser() {
        val imageref =
            storageReference!!.child("profilepictures").child((firebaseAuth!!.uid)!!).child("image")
        imageref.putFile((imageuri)!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    imageref.downloadUrl.addOnSuccessListener { uri ->
                        val uriofimg = uri.toString()
                        val username = usernametxt.text.toString()
                        val status = userstattxt2.text.toString()
                        val no = firebaseAuth!!.currentUser!!.phoneNumber
                        val upd = User(
                            firebaseAuth!!.uid, username, uriofimg, status, no, "Online"
                        )
                        val database = FirebaseDatabase.getInstance()
                        database.reference.child("Users").child((firebaseAuth!!.uid)!!)
                            .setValue(null)
                        database.reference.child("Users").child((firebaseAuth!!.uid)!!)
                            .setValue(upd).addOnSuccessListener {
                                progressBar3.visibility = View.INVISIBLE
                                val intent = Intent(this@setproacti, chatmain::class.java)
                                intent.putExtra("imguri",uriofimg)
                                intent.putExtra("uname",username)
                                intent.putExtra("stat",status)
                                startActivity(intent)
                                finishAffinity()
                            }
                    }
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        imageuri = data!!.data
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                imageuri = result.uri
                addpropic.setImageURI(imageuri)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Toast.makeText(this,error.toString(),Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        ActivityCompat.requestPermissions(
            this@setproacti,
            arrayOf(Manifest.permission.READ_CONTACTS),
            1
        )
        ActivityCompat.requestPermissions(
            this@setproacti,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            2
        )
        ActivityCompat.requestPermissions(
            this@setproacti,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            3
        )
    }

}