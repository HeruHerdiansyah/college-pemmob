package com.example.assignment

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.assignment.model.Role
import com.example.assignment.service.AuthService

class RegisterActivity : AppCompatActivity() {

    private lateinit var etFullName: EditText
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var spinnerRole: Spinner
    private lateinit var btnRegister: Button
    private lateinit var tvError: TextView
    private lateinit var tvSuccess: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var ivTogglePassword: ImageView
    private lateinit var ivToggleConfirm: ImageView

    private var isPasswordVisible = false
    private var isConfirmVisible = false
    private var roles = listOf<Role>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.registerMain)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            val bottomPadding = maxOf(systemBars.bottom, ime.bottom)
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, bottomPadding)
            insets
        }

        initViews()
        setupListeners()
        loadRoles()
        playEntryAnimations()
    }

    private fun initViews() {
        etFullName = findViewById(R.id.etFullName)
        etUsername = findViewById(R.id.etRegUsername)
        etPassword = findViewById(R.id.etRegPassword)
        etConfirmPassword = findViewById(R.id.etRegConfirmPassword)
        spinnerRole = findViewById(R.id.spinnerRole)
        btnRegister = findViewById(R.id.btnRegister)
        tvError = findViewById(R.id.tvRegError)
        tvSuccess = findViewById(R.id.tvRegSuccess)
        progressBar = findViewById(R.id.regProgressBar)
        ivTogglePassword = findViewById(R.id.ivRegTogglePassword)
        ivToggleConfirm = findViewById(R.id.ivRegToggleConfirm)
    }

    private fun setupListeners() {
        btnRegister.setOnClickListener {
            performRegister()
        }

        ivTogglePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            toggleFieldVisibility(etPassword, ivTogglePassword, isPasswordVisible)
        }

        ivToggleConfirm.setOnClickListener {
            isConfirmVisible = !isConfirmVisible
            toggleFieldVisibility(etConfirmPassword, ivToggleConfirm, isConfirmVisible)
        }

        // Navigate to Login
        findViewById<TextView>(R.id.tvGoToLogin).setOnClickListener {
            finish()
        }

        // Clear errors on focus
        listOf(etFullName, etUsername, etPassword, etConfirmPassword).forEach { field ->
            field.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    hideError()
                    hideSuccess()
                }
            }
        }
    }

    private fun loadRoles() {
        setLoading(true)
        Thread {
            roles = AuthService.getAllRoles()
            runOnUiThread {
                setLoading(false)
                val roleNames = mutableListOf("Pilih role")
                roleNames.addAll(roles.map { it.roleName })

                val adapter = ArrayAdapter(this, R.layout.spinner_dropdown_item, roleNames)
                adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                spinnerRole.adapter = adapter
            }
        }.start()
    }

    private fun performRegister() {
        val fullName = etFullName.text.toString().trim()
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()
        val selectedRoleIndex = spinnerRole.selectedItemPosition

        hideError()
        hideSuccess()

        // Validation 1: Field kosong
        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError(getString(R.string.error_all_fields_required))
            shakeView(findViewById(R.id.cardRegister))
            return
        }

        // Validation 2: Username min 4 char
        if (username.length < 4) {
            showError(getString(R.string.error_username_min))
            shakeView(etUsername.parent as View)
            return
        }

        // Validation 3: Role null
        if (selectedRoleIndex == 0) {
            showError(getString(R.string.error_select_role))
            shakeView(spinnerRole)
            return
        }

        // Validation 3: Password mismatch
        if (password != confirmPassword) {
            showError(getString(R.string.error_password_mismatch))
            shakeView(etConfirmPassword.parent as View)
            return
        }

        // Validation 4: Password < 4
        if (password.length < 4) {
            showError(getString(R.string.error_password_min))
            shakeView(etPassword.parent as View)
            return
        }

        val selectedRole = roles[selectedRoleIndex - 1]
        setLoading(true)

        Thread {
            // Validation 5: Username taken
            val isTaken = AuthService.isUsernameTaken(username)
            if (isTaken) {
                runOnUiThread {
                    setLoading(false)
                    showError(getString(R.string.error_username_taken))
                    shakeView(etUsername.parent as View)
                }
                return@Thread
            }

            // Register
            val success = AuthService.register(fullName, username, password, selectedRole.id)
            runOnUiThread {
                setLoading(false)
                if (success) {
                    showSuccess(getString(R.string.register_success))
                    clearFields()
                    // Navigate to login after delay
                    btnRegister.postDelayed({
                        finish()
                    }, 1500)
                } else {
                    showError(getString(R.string.error_register_failed))
                }
            }
        }.start()
    }

    private fun clearFields() {
        etFullName.text.clear()
        etUsername.text.clear()
        etPassword.text.clear()
        etConfirmPassword.text.clear()
        spinnerRole.setSelection(0)
    }

    private fun toggleFieldVisibility(field: EditText, icon: ImageView, visible: Boolean) {
        if (visible) {
            field.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            icon.setImageResource(R.drawable.ic_eye)
        } else {
            field.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            icon.setImageResource(R.drawable.ic_eye_off)
        }
        field.setSelection(field.text.length)
    }

    private fun setLoading(loading: Boolean) {
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        btnRegister.isEnabled = !loading
        btnRegister.alpha = if (loading) 0.6f else 1f
    }

    private fun showError(message: String) {
        tvError.text = message
        tvError.visibility = View.VISIBLE
        tvError.alpha = 0f
        tvError.animate().alpha(1f).setDuration(300).start()
    }

    private fun hideError() {
        if (tvError.visibility == View.VISIBLE) {
            tvError.animate().alpha(0f).setDuration(200).withEndAction {
                tvError.visibility = View.GONE
            }.start()
        }
    }

    private fun showSuccess(message: String) {
        tvSuccess.text = message
        tvSuccess.visibility = View.VISIBLE
        tvSuccess.alpha = 0f
        tvSuccess.animate().alpha(1f).setDuration(300).start()
    }

    private fun hideSuccess() {
        if (tvSuccess.visibility == View.VISIBLE) {
            tvSuccess.animate().alpha(0f).setDuration(200).withEndAction {
                tvSuccess.visibility = View.GONE
            }.start()
        }
    }

    private fun shakeView(view: View) {
        val shake = ObjectAnimator.ofFloat(view, "translationX", 0f, -12f, 12f, -8f, 8f, -4f, 4f, 0f)
        shake.duration = 450
        shake.start()
    }

    private fun playEntryAnimations() {
        val logo = findViewById<ImageView>(R.id.ivRegLogo)
        val title = findViewById<TextView>(R.id.tvRegTitle)
        val subtitle = findViewById<TextView>(R.id.tvRegSubtitle)
        val card = findViewById<LinearLayout>(R.id.cardRegister)
        val loginLink = findViewById<LinearLayout>(R.id.loginContainer)

        val views = listOf(logo, title, subtitle, card, loginLink)

        views.forEachIndexed { index, view ->
            view.alpha = 0f
            view.translationY = 40f
            view.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(600)
                .setStartDelay((index * 100).toLong())
                .setInterpolator(OvershootInterpolator(1.2f))
                .start()
        }
    }
}
