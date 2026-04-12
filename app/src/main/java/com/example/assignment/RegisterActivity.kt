package com.example.assignment

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.assignment.model.Role
import com.example.assignment.service.AuthService

class RegisterActivity : AppCompatActivity() {

    private lateinit var etFullName: EditText
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var spinnerRole: Spinner
    private lateinit var tvError: TextView
    private lateinit var tvSuccess: TextView
    private lateinit var btnRegister: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var progressRole: ProgressBar
    private lateinit var tvRoleLoading: TextView

    private val authService = AuthService()
    private var roles: List<Role> = emptyList()
    private var selectedRole: Role? = null

    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initViews()
        setupListeners()
        loadRoles()
        playEntryAnimations()
    }

    private fun initViews() {
        etFullName = findViewById(R.id.etFullName)
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        spinnerRole = findViewById(R.id.spinnerRole)
        tvError = findViewById(R.id.tvError)
        tvSuccess = findViewById(R.id.tvSuccess)
        btnRegister = findViewById(R.id.btnRegister)
        progressBar = findViewById(R.id.progressBar)
        progressRole = findViewById(R.id.progressRole)
        tvRoleLoading = findViewById(R.id.tvRoleLoading)
    }

    private fun setupListeners() {
        btnRegister.setOnClickListener { handleRegister() }

        findViewById<ImageView>(R.id.ivTogglePassword).setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            etPassword.transformationMethod = if (isPasswordVisible) {
                HideReturnsTransformationMethod.getInstance()
            } else {
                PasswordTransformationMethod.getInstance()
            }
            (it as ImageView).setImageResource(
                if (isPasswordVisible) R.drawable.ic_eye else R.drawable.ic_eye_off
            )
            etPassword.setSelection(etPassword.text.length)
        }

        findViewById<ImageView>(R.id.ivToggleConfirmPassword).setOnClickListener {
            isConfirmPasswordVisible = !isConfirmPasswordVisible
            etConfirmPassword.transformationMethod = if (isConfirmPasswordVisible) {
                HideReturnsTransformationMethod.getInstance()
            } else {
                PasswordTransformationMethod.getInstance()
            }
            (it as ImageView).setImageResource(
                if (isConfirmPasswordVisible) R.drawable.ic_eye else R.drawable.ic_eye_off
            )
            etConfirmPassword.setSelection(etConfirmPassword.text.length)
        }

        findViewById<TextView>(R.id.tvSignIn).setOnClickListener {
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    private fun loadRoles() {
        // Tampilkan loading state
        progressRole.visibility = View.VISIBLE
        tvRoleLoading.visibility = View.VISIBLE
        spinnerRole.visibility = View.INVISIBLE
        btnRegister.isEnabled = false

        Thread {
            roles = authService.getAllRoles()
            Log.d("RegisterActivity", "Roles loaded: ${roles.size} role(s)")

            runOnUiThread {
                // Sembunyikan loading
                progressRole.visibility = View.GONE
                btnRegister.isEnabled = true

                if (roles.isEmpty()) {
                    Log.e("RegisterActivity", "❌ Roles kosong! Cek koneksi DB di Logcat > DatabaseConnection")
                    tvRoleLoading.text = "Gagal memuat role"
                    tvRoleLoading.setTextColor(getColor(R.color.error_red))
                    return@runOnUiThread
                }

                tvRoleLoading.visibility = View.GONE
                spinnerRole.visibility = View.VISIBLE

                val roleNames = mutableListOf(getString(R.string.hint_select_role))
                roleNames.addAll(roles.map { it.roleName })

                val adapter = ArrayAdapter(this, R.layout.spinner_dropdown_item, roleNames)
                adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                spinnerRole.adapter = adapter

                spinnerRole.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        selectedRole = if (position > 0) roles[position - 1] else null
                        (view as? TextView)?.setTextColor(
                            if (position == 0) getColor(R.color.text_hint) else getColor(R.color.text_primary)
                        )
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        selectedRole = null
                    }
                }
            }
        }.start()
    }


    private fun handleRegister() {
        tvError.visibility = View.GONE
        tvSuccess.visibility = View.GONE

        val fullName = etFullName.text.toString().trim()
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()

        // Validasi sama persis dengan Java: RegisterController.java
        // 1. Semua field wajib diisi
        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError(getString(R.string.error_register_empty))
            shakeAnimation()
            return
        }

        // 2. Role harus dipilih
        if (selectedRole == null) {
            showError(getString(R.string.error_role_null))
            shakeAnimation()
            return
        }

        // 3. Password match
        if (password != confirmPassword) {
            showError(getString(R.string.error_password_mismatch))
            shakeAnimation()
            return
        }

        // 4. Password minimal 4 karakter
        if (password.length < 4) {
            showError(getString(R.string.error_password_short))
            shakeAnimation()
            return
        }

        setLoading(true)

        Thread {
            val success = authService.register(username, password, fullName, selectedRole!!.id)
            runOnUiThread {
                setLoading(false)
                if (success) {
                    // Sukses: tampilkan pesan + clear fields
                    tvSuccess.text = getString(R.string.register_success)
                    tvSuccess.visibility = View.VISIBLE
                    clearFields()
                } else {
                    // 5. Username sudah digunakan
                    showError(getString(R.string.error_username_taken))
                }
            }
        }.start()
    }

    private fun showError(message: String) {
        tvError.text = message
        tvError.visibility = View.VISIBLE
    }

    private fun shakeAnimation() {
        val card = findViewById<LinearLayout>(R.id.registerCard)
        val shake = ObjectAnimator.ofFloat(
            card, "translationX",
            0f, 16f, -16f, 12f, -12f, 8f, -8f, 4f, -4f, 0f
        )
        shake.duration = 500
        shake.start()
    }

    private fun setLoading(loading: Boolean) {
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        btnRegister.isEnabled = !loading
        btnRegister.text = if (loading) getString(R.string.registering) else getString(R.string.btn_register)
    }

    private fun clearFields() {
        etFullName.text.clear()
        etUsername.text.clear()
        etPassword.text.clear()
        etConfirmPassword.text.clear()
        spinnerRole.setSelection(0)
        selectedRole = null
    }

    private fun playEntryAnimations() {
        val headerSection = findViewById<LinearLayout>(R.id.headerSection)
        val registerCard = findViewById<LinearLayout>(R.id.registerCard)
        val signInSection = findViewById<LinearLayout>(R.id.signInSection)

        val views = listOf(headerSection, registerCard, signInSection)
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
