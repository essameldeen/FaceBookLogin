package com.example.essam.facebooklogin

import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {
    var fireBaseAuth: FirebaseAuth? = null
    var callBackManager: CallbackManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fireBaseAuth = FirebaseAuth.getInstance()
        callBackManager = CallbackManager.Factory.create()

        faceBook_logIn.setReadPermissions("email")

        faceBook_logIn.setOnClickListener {
            signIn()

        }

    }

    private fun signIn() {
        faceBook_logIn.registerCallback(callBackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                handleAccessToken(result!!.accessToken)
            }

            override fun onCancel() {

            }

            override fun onError(error: FacebookException?) {

                Toast.makeText(this@MainActivity, error.toString(), Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun handleAccessToken(accessToken: AccessToken?) {
        //Get Credential
        val credential = FacebookAuthProvider.getCredential(accessToken!!.token)
        fireBaseAuth!!.signInWithCredential(credential)
            .addOnFailureListener {
                Toast.makeText(this,it.message,Toast.LENGTH_LONG).show()
            }.addOnSuccessListener { authResult ->

                  val email = authResult.user.email
                Toast.makeText(this@MainActivity,"Successful log in by :"+email, Toast.LENGTH_LONG).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callBackManager!!.onActivityResult(requestCode,resultCode,data)
    }

    private fun printHash() {
        try {
            var info = packageManager.getPackageInfo("com.example.essam.facebooklogin", PackageManager.GET_SIGNATURES)

            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.e("KEYHASH", Base64.encodeToString(md.digest(), Base64.DEFAULT))

            }
        } catch (e: PackageManager.NameNotFoundException) {

        }
    }
}
