package com.example.assignment

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.*
import com.example.assignment.service.AuthService
import com.example.assignment.service.MenuService
import com.example.assignment.util.SessionManager

class ProfileActivity : BaseDrawerActivity() {

    private lateinit var etOldPassword: EditText
    private lateinit var etNewPassword: EditText
    private lateinit var etConfirmNewPassword: EditText
    private lateinit var btnChangePassword: Button
    private lateinit var tvPwdError: TextView
    private lateinit var tvPwdSuccess: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var passwordSection: LinearLayout
    private lateinit var btnTogglePasswordSection: LinearLayout

    private var isPasswordSectionVisible = false
    private var isOldPwdVisible = false
    private var isNewPwdVisible = false
    private var isConfirmPwdVisible = false

    override fun getContentLayoutId(): Int = R.layout.content_profile

    override fun getActiveNavItemId(): Int = BaseDrawerActivity.NAV_PROFILE

    override fun onContentReady(savedInstanceState: Bundle?) {
        val user = SessionManager.currentUser ?: return

        initViews()
        setupUserInfo(user)
        setupListeners()
        playEntryAnimations()
    }

    private fun initViews() {
        etOldPassword = findViewById(R.id.etOldPassword)
        etNewPassword = findViewById(R.id.etNewPassword)
        etConfirmNewPassword = findViewById(R.id.etConfirmNewPassword)
        btnChangePassword = findViewById(R.id.btnChangePassword)
        tvPwdError = findViewById(R.id.tvPwdError)
        tvPwdSuccess = findViewById(R.id.tvPwdSuccess)
        progressBar = findViewById(R.id.profileProgressBar)
        passwordSection = findViewById(R.id.passwordFormSection)
        btnTogglePasswordSection = findViewById(R.id.btnTogglePasswordSection)
    }

    private fun setupUserInfo(user: com.example.assignment.model.User) {
        val config = MenuService.getDashboardConfig(user.roleName)

        // Avatar
        val tvAvatar = findViewById<TextView>(R.id.tvProfileAvatar)
        tvAvatar.text = user.fullName.take(1).uppercase()
        val avatarBg = tvAvatar.background as? GradientDrawable
        avatarBg?.setColor(config.accentColor)

        // Name + Role
        findViewById<TextView>(R.id.tvProfileName).text = user.fullName
        val tvRole = findViewById<TextView>(R.id.tvProfileRole)
        tvRole.text = user.roleName
        val roleBg = tvRole.background as? GradientDrawable
        roleBg?.setColor(config.accentColor)

        // Account info card
        findViewById<TextView>(R.id.tvInfoFullName).text = user.fullName
        findViewById<TextView>(R.id.tvInfoUsername).text = user.username
        findViewById<TextView>(R.id.tvInfoRole).text = user.roleName
    }

    private fun setupListeners() {
        // Hamburger menu -> open drawer
        findViewById<ImageView>(R.id.ivMenuHamburger).setOnClickListener {
            openDrawer()
        }

        // Toggle password section
        btnTogglePasswordSection.setOnClickListener {
            isPasswordSectionVisible = !isPasswordSectionVisible
            passwordSection.visibility = if (isPasswordSectionVisible) View.VISIBLE else View.GONE

            val arrow = findViewById<TextView>(R.id.tvPasswordArrow)
            arrow.rotation = if (isPasswordSectionVisible) 180f else 0f
        }

        // Change password
        btnChangePassword.setOnClickListener {
            performChangePassword()
        }

        // Password visibility toggles
        findViewById<ImageView>(R.id.ivToggleOldPwd).setOnClickListener {
            isOldPwdVisible = !isOldPwdVisible
            toggleField(etOldPassword, findViewById(R.id.ivToggleOldPwd), isOldPwdVisible)
        }
        findViewById<ImageView>(R.id.ivToggleNewPwd).setOnClickListener {
            isNewPwdVisible = !isNewPwdVisible
            toggleField(etNewPassword, findViewById(R.id.ivToggleNewPwd), isNewPwdVisible)
        }
        findViewById<ImageView>(R.id.ivToggleConfirmPwd).setOnClickListener {
            isConfirmPwdVisible = !isConfirmPwdVisible
            toggleField(etConfirmNewPassword, findViewById(R.id.ivToggleConfirmPwd), isConfirmPwdVisible)
        }

        // Logout
        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            SessionManager.logout()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun performChangePassword() {
        val oldPwd = etOldPassword.text.toString().trim()
        val newPwd = etNewPassword.text.toString().trim()
        val confirmPwd = etConfirmNewPassword.text.toString().trim()

        hideError()
        hideSuccess()

        // Validation 1: empty fields
        if (oldPwd.isEmpty() || newPwd.isEmpty() || confirmPwd.isEmpty()) {
            showError(getString(R.string.error_all_fields_required))
            shakeView(passwordSection)
            return
        }

        // Validation 3: new password < 4
        if (newPwd.length < 4) {
            showError(getString(R.string.error_new_password_min))
            shakeView(etNewPassword.parent as View)
            return
        }

        // Validation 4: confirm mismatch
        if (newPwd != confirmPwd) {
            showError(getString(R.string.error_new_password_mismatch))
            shakeView(etConfirmNewPassword.parent as View)
            return
        }

        val user = SessionManager.currentUser ?: return
        setLoading(true)

        Thread {
            // Validation 2: old password wrong
            val isOldCorrect = AuthService.verifyPassword(user.id, oldPwd)
            if (!isOldCorrect) {
                runOnUiThread {
                    setLoading(false)
                    showError(getString(R.string.error_old_password_wrong))
                    shakeView(etOldPassword.parent as View)
                }
                return@Thread
            }

            val success = AuthService.changePassword(user.id, newPwd)
            runOnUiThread {
                setLoading(false)
                if (success) {
                    showSuccess(getString(R.string.password_changed_success))
                    etOldPassword.text.clear()
                    etNewPassword.text.clear()
                    etConfirmNewPassword.text.clear()
                } else {
                    showError(getString(R.string.error_change_password_failed))
                }
            }
        }.start()
    }

    private fun toggleField(field: EditText, icon: ImageView, visible: Boolean) {
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
        btnChangePassword.isEnabled = !loading
        btnChangePassword.alpha = if (loading) 0.6f else 1f
    }

    private fun showError(msg: String) {
        tvPwdError.text = msg
        tvPwdError.visibility = View.VISIBLE
        tvPwdError.alpha = 0f
        tvPwdError.animate().alpha(1f).setDuration(300).start()
    }

    private fun hideError() {
        if (tvPwdError.visibility == View.VISIBLE) {
            tvPwdError.animate().alpha(0f).setDuration(200).withEndAction {
                tvPwdError.visibility = View.GONE
            }.start()
        }
    }

    private fun showSuccess(msg: String) {
        tvPwdSuccess.text = msg
        tvPwdSuccess.visibility = View.VISIBLE
        tvPwdSuccess.alpha = 0f
        tvPwdSuccess.animate().alpha(1f).setDuration(300).start()
        // Auto-dismiss after 3 seconds
        tvPwdSuccess.postDelayed({ hideSuccess() }, 3000)
    }

    private fun hideSuccess() {
        if (tvPwdSuccess.visibility == View.VISIBLE) {
            tvPwdSuccess.animate().alpha(0f).setDuration(200).withEndAction {
                tvPwdSuccess.visibility = View.GONE
            }.start()
        }
    }

    private fun shakeView(view: View) {
        val shake = ObjectAnimator.ofFloat(view, "translationX", 0f, -12f, 12f, -8f, 8f, -4f, 4f, 0f)
        shake.duration = 450
        shake.start()
    }

    private fun playEntryAnimations() {
        val header = findViewById<View>(R.id.profileHeaderCard)
        val infoCard = findViewById<View>(R.id.infoCard)
        val pwdCard = findViewById<View>(R.id.passwordCard)
        val logoutBtn = findViewById<View>(R.id.btnLogout)

        val views = listOf(header, infoCard, pwdCard, logoutBtn)

        views.forEachIndexed { index, view ->
            view.alpha = 0f
            view.translationY = 30f
            view.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .setStartDelay((index * 120).toLong())
                .setInterpolator(OvershootInterpolator(1.0f))
                .start()
        }
    }
}
