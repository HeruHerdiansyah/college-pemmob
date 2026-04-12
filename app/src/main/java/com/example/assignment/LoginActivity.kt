package com.example.assignment

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.assignment.service.AuthService
import com.example.assignment.util.DatabaseConnection
import com.example.assignment.util.SessionManager

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var tvError: TextView
    private lateinit var btnLogin: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var ivTogglePassword: ImageView

    private val authService = AuthService()
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initViews()
        setupListeners()
        playEntryAnimations()
        checkDatabaseConnection()
    }

    // Cek koneksi DB saat pertama kali app dibuka — lihat di Logcat filter: DatabaseConnection
    private fun checkDatabaseConnection() {
        Thread {
            Log.d("DatabaseConnection", "=== DB Connection Check ===")
            Log.d("DatabaseConnection", "Host : ${com.example.assignment.BuildConfig.DB_HOST}:${com.example.assignment.BuildConfig.DB_PORT}")
            Log.d("DatabaseConnection", "Database: ${com.example.assignment.BuildConfig.DB_NAME}")
            Log.d("DatabaseConnection", "User    : ${com.example.assignment.BuildConfig.DB_USER}")
            val result = DatabaseConnection.testConnection()
            if (result.startsWith("✅")) {
                Log.d("DatabaseConnection", result)
            } else {
                Log.e("DatabaseConnection", result)
            }
            Log.d("DatabaseConnection", "=========================")
        }.start()
    }

    private fun initViews() {
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        tvError = findViewById(R.id.tvError)
        btnLogin = findViewById(R.id.btnLogin)
        progressBar = findViewById(R.id.progressBar)
        ivTogglePassword = findViewById(R.id.ivTogglePassword)
    }

    private fun setupListeners() {
        btnLogin.setOnClickListener { handleLogin() }

        ivTogglePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            etPassword.transformationMethod = if (isPasswordVisible) {
                HideReturnsTransformationMethod.getInstance()
            } else {
                PasswordTransformationMethod.getInstance()
            }
            ivTogglePassword.setImageResource(
                if (isPasswordVisible) R.drawable.ic_eye else R.drawable.ic_eye_off
            )
            etPassword.setSelection(etPassword.text.length)
        }

        findViewById<TextView>(R.id.tvSignUp).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    private fun handleLogin() {
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()

        // Validasi sama persis dengan Java: LoginController.java
        if (username.isEmpty() || password.isEmpty()) {
            showError(getString(R.string.error_login_empty))
            shakeAnimation()
            return
        }

        // Show loading
        setLoading(true)
        tvError.visibility = View.GONE

        // DB query on background thread
        Thread {
            val user = authService.authenticate(username, password)
            runOnUiThread {
                setLoading(false)
                if (user != null) {
                    // Check role is valid
                    val validRoles = listOf("Admin", "Veterinarian", "Zookeeper", "Ticketing", "Researcher")
                    if (user.role.roleName !in validRoles) {
                        showError(getString(R.string.error_role_unknown))
                        return@runOnUiThread
                    }

                    SessionManager.setUser(user)
                    val intent = Intent(this, DashboardActivity::class.java)
                    intent.putExtra("user", user)
                    startActivity(intent)
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish()
                } else {
                    showError(getString(R.string.error_login_failed))
                    shakeAnimation()
                }
            }
        }.start()
    }

    private fun showError(message: String) {
        tvError.text = message
        tvError.visibility = View.VISIBLE
    }

    private fun setLoading(loading: Boolean) {
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        btnLogin.isEnabled = !loading
        btnLogin.text = if (loading) getString(R.string.logging_in) else getString(R.string.btn_login)
    }

    // Animasi shake saat validasi/login gagal
    private fun shakeAnimation() {
        val loginCard = findViewById<LinearLayout>(R.id.loginCard)
        val shake = ObjectAnimator.ofFloat(
            loginCard, "translationX",
            0f, 16f, -16f, 12f, -12f, 8f, -8f, 4f, -4f, 0f
        )
        shake.duration = 500
        shake.start()
    }

    private fun playEntryAnimations() {
        val headerSection = findViewById<LinearLayout>(R.id.headerSection)
        val loginCard = findViewById<LinearLayout>(R.id.loginCard)
        val signUpSection = findViewById<LinearLayout>(R.id.signUpSection)

        val views = listOf(headerSection, loginCard, signUpSection)
        views.forEachIndexed { index, view ->
            view.alpha = 0f
            view.translationY = 40f
            view.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(600)
                .setStartDelay((index * 150).toLong())
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
        }
    }
}
