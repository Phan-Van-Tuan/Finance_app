package com.project.financialManagement.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.project.financialManagement.MainActivity
import com.project.financialManagement.databinding.ActivitySigninBinding
import com.project.financialManagement.model.CoinModel
import com.project.financialManagement.model.User

class SigninActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySigninBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = User("Tuan", "Tuan@gmail.com", "123456")

        binding.btnLogin.setOnClickListener {
            val email = binding.inputEmail.text.toString().trim()
            val password = binding.inputPassword.text.toString().trim()
            if(email == user.email && password == user.password) {
                Toast.makeText(this, "Login successfully", Toast.LENGTH_SHORT).show()
                var intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Email or password is wrong", Toast.LENGTH_SHORT).show()
                binding.inputEmail.text = null
                binding.inputPassword.text = null
            }
        }

        binding.btnToSignup.setOnClickListener {
            var intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}