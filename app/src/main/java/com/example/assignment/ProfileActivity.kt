package com.example.assignment

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.assignment.model.User
import com.example.assignment.service.AuthService
import com.example.assignment.util.SessionManager

class ProfileActivity : AppCompatActivity() {

    private lateinit var user: User
    private val authService = AuthService()
    private var isPasswordFormVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        @Suppress("DEPRECATION")
        user = intent.getSerializableExtra("user") as? User ?: SessionManager.currentUser ?: run {
            finish()
            return
        }

        setupUI()
        setupChangePassword()
        setupLogout()
        applyRoleTheme()
        playEntryAnimations()
    }

    private fun setupUI() {
        // Back button
        findViewById<ImageView>(R.id.ivBack).setOnClickListener {
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        // Avatar
        val tvAvatar = findViewById<TextView>(R.id.tvAvatar)
        tvAvatar.text = user.fullName.take(1).uppercase()

        // Profile card info
        findViewById<TextView>(R.id.tvFullName).text = user.fullName
        findViewById<TextView>(R.id.tvRoleBadge).text = user.role.roleName

        // Account info
        findViewById<TextView>(R.id.tvInfoFullName).text = user.fullName
        findViewById<TextView>(R.id.tvInfoUsername).text = user.username
        findViewById<TextView>(R.id.tvInfoRole).text = "${user.role.roleName} — ${user.role.description}"
    }

    private fun setupChangePassword() {
        val changePasswordHeader = findViewById<LinearLayout>(R.id.changePasswordHeader)
        val changePasswordForm = findViewById<LinearLayout>(R.id.changePasswordForm)
        val etOldPassword = findViewById<EditText>(R.id.etOldPassword)
        val etNewPassword = findViewById<EditText>(R.id.etNewPassword)
        val etConfirmNewPassword = findViewById<EditText>(R.id.etConfirmNewPassword)
        val tvPasswordError = findViewById<TextView>(R.id.tvPasswordError)
        val tvPasswordSuccess = findViewById<TextView>(R.id.tvPasswordSuccess)
        val btnChangePassword = findViewById<Button>(R.id.btnChangePassword)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        // Toggle expandable form
        changePasswordHeader.setOnClickListener {
            isPasswordFormVisible = !isPasswordFormVisible
            changePasswordForm.visibility = if (isPasswordFormVisible) View.VISIBLE else View.GONE
            val arrow = findViewById<ImageView>(R.id.ivExpandArrow)
            arrow.animate().rotation(if (isPasswordFormVisible) 180f else 0f).setDuration(200).start()
        }

        btnChangePassword.setOnClickListener {
            tvPasswordError.visibility = View.GONE
            tvPasswordSuccess.visibility = View.GONE

            val oldPassword = etOldPassword.text.toString().trim()
            val newPassword = etNewPassword.text.toString().trim()
            val confirmNewPassword = etConfirmNewPassword.text.toString().trim()

            // Validasi
            // 1. Semua field harus diisi
            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
                showPasswordError(tvPasswordError, getString(R.string.error_change_password_empty))
                return@setOnClickListener
            }

            // 2. Password baru minimal 4 karakter
            if (newPassword.length < 4) {
                showPasswordError(tvPasswordError, getString(R.string.error_new_password_short))
                return@setOnClickListener
            }

            // 3. Konfirmasi password harus cocok
            if (newPassword != confirmNewPassword) {
                showPasswordError(tvPasswordError, getString(R.string.error_new_password_mismatch))
                return@setOnClickListener
            }

            // Loading
            progressBar.visibility = View.VISIBLE
            btnChangePassword.isEnabled = false
            btnChangePassword.text = getString(R.string.changing_password)

            Thread {
                // Verify old password first
                val isOldPasswordCorrect = authService.verifyPassword(user.id, oldPassword)

                if (!isOldPasswordCorrect) {
                    runOnUiThread {
                        progressBar.visibility = View.GONE
                        btnChangePassword.isEnabled = true
                        btnChangePassword.text = getString(R.string.btn_change_password)
                        showPasswordError(tvPasswordError, getString(R.string.error_old_password_wrong))
                    }
                    return@Thread
                }

                val success = authService.changePassword(user.id, oldPassword, newPassword)
                runOnUiThread {
                    progressBar.visibility = View.GONE
                    btnChangePassword.isEnabled = true
                    btnChangePassword.text = getString(R.string.btn_change_password)

                    if (success) {
                        tvPasswordSuccess.text = getString(R.string.change_password_success)
                        tvPasswordSuccess.visibility = View.VISIBLE
                        etOldPassword.text.clear()
                        etNewPassword.text.clear()
                        etConfirmNewPassword.text.clear()
                    } else {
                        showPasswordError(tvPasswordError, getString(R.string.error_old_password_wrong))
                    }
                }
            }.start()
        }
    }

    private fun showPasswordError(tvError: TextView, message: String) {
        tvError.text = message
        tvError.visibility = View.VISIBLE
    }

    private fun setupLogout() {
        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            SessionManager.logout()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }

    private fun applyRoleTheme() {
        val roleColor = when (user.role.roleName) {
            "Admin" -> "#10B981"
            "Veterinarian" -> "#3B82F6"
            "Zookeeper" -> "#22C55E"
            "Ticketing" -> "#F59E0B"
            "Researcher" -> "#8B5CF6"
            else -> "#10B981"
        }
        val color = Color.parseColor(roleColor)

        // Role badge
        val tvRoleBadge = findViewById<TextView>(R.id.tvRoleBadge)
        tvRoleBadge.setTextColor(color)
        val badgeBg = GradientDrawable()
        badgeBg.setColor(Color.argb(32, Color.red(color), Color.green(color), Color.blue(color)))
        badgeBg.cornerRadius = 12 * resources.displayMetrics.density
        tvRoleBadge.background = badgeBg

        // Avatar
        val tvAvatar = findViewById<TextView>(R.id.tvAvatar)
        tvAvatar.setTextColor(color)
        val avatarBg = GradientDrawable()
        avatarBg.shape = GradientDrawable.OVAL
        avatarBg.setColor(Color.argb(32, Color.red(color), Color.green(color), Color.blue(color)))
        avatarBg.setStroke((2 * resources.displayMetrics.density).toInt(), color)
        tvAvatar.background = avatarBg
    }

    private fun playEntryAnimations() {
        val contentView = findViewById<LinearLayout>(
            (findViewById<ScrollView>(android.R.id.content).getChildAt(0) as FrameLayout).getChildAt(1).id
        )
        // Animate all direct children
        try {
            val parent = findViewById<View>(R.id.ivBack).parent.parent as? LinearLayout ?: return
            for (i in 0 until parent.childCount) {
                val child = parent.getChildAt(i)
                child.alpha = 0f
                child.translationY = 20f
                child.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(400)
                    .setStartDelay((i * 100).toLong())
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .start()
            }
        } catch (e: Exception) {
            // Fallback: no animation
        }
    }
}
