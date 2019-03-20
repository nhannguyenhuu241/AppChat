package com.example.nhanne.chatmessage.registerlogin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.example.nhanne.chatmessage.R
import com.example.nhanne.chatmessage.messages.LastnestMessagesActivity
import com.example.nhanne.chatmessage.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        register_button_register.setOnClickListener {
            perfromRegister()
        }

        text_account_login.setOnClickListener {
            Log.d("RegisterActivity", "Success")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        register_button_selectphoto.setOnClickListener {
            Log.d("RegisterActivity","Try to show photo selecttor")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    var selectedPhotoUri : Uri ? =null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data!=null){
            Log.d("RegisterActivity", "Photo was selected")

            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            selectphoto_imageview_register.setImageBitmap(bitmap)
            register_button_selectphoto.alpha = 0f
            //val bitmapDrawable = BitmapDrawable(bitmap)
            //register_button_selectphoto.setBackgroundDrawable(bitmapDrawable)
        }
    }
    private fun perfromRegister(){
        val email = email_edittext_register.text.toString()
        val password = password_edittext_register.text.toString()

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this,"Email and Password null",Toast.LENGTH_LONG).show()
            return
        }
        Log.d("RegisterActivity", "Email is:" + email)
        Log.d("RegisterActivity","Password: $password")
        auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if(!it.isSuccessful) return@addOnCompleteListener
                //else
                Log.d("Main","Success: ${it.result.user.uid}")
                uploadImageToFirebaseStorage()
            }
            .addOnFailureListener(this){
                Log.d("Main", "Failed: ${it.message}")
            }
    }

    private fun uploadImageToFirebaseStorage(){
        if(selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/image/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("RegisterActivity","Success upload image: ${it.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener {
                    Log.d("RegisterActivity", "File location: $it")

                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener{

            }
    }
    private fun saveUserToFirebaseDatabase(profileImageUrl: String){
        val uid = FirebaseAuth.getInstance().uid ?:""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(
            uid,
            username_edittext_register.text.toString(),
            profileImageUrl
        )

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "Finally we saved")

                val intent = Intent(this, LastnestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {

            }
    }

}

