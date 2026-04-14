package com.example.assignment

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.adapter.MenuCardAdapter
import com.example.assignment.adapter.StatCardAdapter
import com.example.assignment.service.MenuService
import com.example.assignment.util.SessionManager

class DashboardActivity : BaseDrawerActivity() {

    override fun getContentLayoutId(): Int = R.layout.content_dashboard

    override fun getActiveNavItemId(): Int = BaseDrawerActivity.NAV_DASHBOARD

    override fun onContentReady(savedInstanceState: Bundle?) {
        val user = SessionManager.currentUser ?: return

        setupHeader(user)
        setupDashboardContent(user)
        playEntryAnimations()
    }

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(androidx.core.view.GravityCompat.START)) {
            closeDrawer()
        } else {
            finishAffinity()
        }
    }

    private fun setupHeader(user: com.example.assignment.model.User) {
        // Greeting
        val tvGreeting = findViewById<TextView>(R.id.tvGreeting)
        tvGreeting.text = "Halo, ${user.fullName}! 👋"

        // Role badge
        val tvRoleBadge = findViewById<TextView>(R.id.tvRoleBadge)
        tvRoleBadge.text = user.roleName

        val config = MenuService.getDashboardConfig(user.roleName)
        val badgeBg = tvRoleBadge.background as? GradientDrawable
        badgeBg?.setColor(config.accentColor)

        // Avatar with initials
        val tvAvatarInitial = findViewById<TextView>(R.id.tvAvatarInitial)
        tvAvatarInitial.text = user.fullName.take(1).uppercase()

        val avatarBg = tvAvatarInitial.background as? GradientDrawable
        avatarBg?.setColor(config.accentColor)

        // Avatar click → Profile
        findViewById<View>(R.id.avatarContainer).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // Hamburger menu
        findViewById<ImageView>(R.id.ivMenuHamburger).setOnClickListener {
            openDrawer()
        }
    }

    private fun setupDashboardContent(user: com.example.assignment.model.User) {
        val config = MenuService.getDashboardConfig(user.roleName)

        // Stats RecyclerView — 2-column grid
        val rvStats = findViewById<RecyclerView>(R.id.rvStats)
        rvStats.layoutManager = GridLayoutManager(this, 2)
        rvStats.adapter = StatCardAdapter(config.stats, config.accentColor)

        // Menu RecyclerView — grid 2 columns
        val rvMenu = findViewById<RecyclerView>(R.id.rvMenu)
        rvMenu.layoutManager = GridLayoutManager(this, 2)
        rvMenu.adapter = MenuCardAdapter(config.menuItems) { item ->
            Toast.makeText(this, "Menu: ${item.title}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun playEntryAnimations() {
        val header = findViewById<View>(R.id.dashboardHeader)
        val statsTitle = findViewById<View>(R.id.tvStatsTitle)
        val rvStats = findViewById<View>(R.id.rvStats)
        val menuTitle = findViewById<View>(R.id.tvMenuTitle)
        val rvMenu = findViewById<View>(R.id.rvMenu)

        val views = listOf(header, statsTitle, rvStats, menuTitle, rvMenu)

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
