package com.project.financialManagement.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.project.financialManagement.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            var email = binding.inputEmail.text.toString().trim()
            var password = binding.inputPassword.text.toString().trim()
            var confirmPassword = binding.inputConfirmPassword.text.toString().trim()

            if (validateInput(email, password, confirmPassword)) {
                Toast.makeText(this, "successfully", Toast.LENGTH_LONG).show()
            }
        }

        binding.btnToSignIn.setOnClickListener {
            var intent = Intent(this, SigninActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun validateInput(email: String, password: String, confirmPassword: String): Boolean {
        if (!isValidEmail(email)) {
            binding.inputEmail.error = "Invalid email format"
            return false
        }

        if (!isValidPassword(password)) {
            binding.inputPassword.error = "Password must be at least 6 characters"
            return false
        }

        if (password != confirmPassword) {
            binding.inputConfirmPassword.error = "Passwords do not match"
            return false
        }

        // All validations passed
        return true
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
        return emailRegex.matches(email)
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }
}