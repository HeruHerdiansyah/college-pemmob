package com.example.assignment

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.adapter.MenuCardAdapter
import com.example.assignment.adapter.StatCardAdapter
import com.example.assignment.model.User
import com.example.assignment.service.MenuService
import com.example.assignment.util.SessionManager
import com.google.android.material.navigation.NavigationView
import java.text.SimpleDateFormat
import java.util.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var user: User
    private val menuService = MenuService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Get user from intent or session
        @Suppress("DEPRECATION")
        user = intent.getSerializableExtra("user") as? User ?: SessionManager.currentUser ?: run {
            navigateToLogin()
            return
        }

        setupUI()
        setupDrawer()
        setupRecyclerViews()
        applyRoleTheme()
        playEntryAnimations()
    }

    private fun setupUI() {
        // Welcome text
        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        tvWelcome.text = getString(R.string.dashboard_welcome, user.fullName.split(" ")[0])

        // Date
        val tvDate = findViewById<TextView>(R.id.tvDate)
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
        tvDate.text = dateFormat.format(Date())

        // Avatar (initials)
        val tvAvatar = findViewById<TextView>(R.id.tvAvatar)
        tvAvatar.text = user.fullName.take(1).uppercase()

        // Role badge
        val tvRoleBadge = findViewById<TextView>(R.id.tvRoleBadge)
        tvRoleBadge.text = user.role.roleName

        // Menu toggle
        findViewById<ImageView>(R.id.ivMenuToggle).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Profile avatar click -> ProfileActivity
        tvAvatar.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("user", user)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    private fun setupDrawer() {
        drawerLayout = findViewById(R.id.drawerLayout)
        val navigationView = findViewById<NavigationView>(R.id.navigationView)

        // Setup header
        val headerView = navigationView.getHeaderView(0)
        val tvNavAvatar = headerView.findViewById<TextView>(R.id.tvNavAvatar)
        val tvNavName = headerView.findViewById<TextView>(R.id.tvNavName)
        val tvNavRole = headerView.findViewById<TextView>(R.id.tvNavRole)

        tvNavAvatar.text = user.fullName.take(1).uppercase()
        tvNavName.text = user.fullName
        tvNavRole.text = user.role.roleName

        // Setup menu items programmatically
        navigationView.menu.clear()
        val menuItems = menuService.getMenuForRole(user.role.roleName)
        menuItems.forEachIndexed { index, item ->
            navigationView.menu.add(0, index, index, item.title)
        }
        navigationView.menu.add(1, 999, 999, getString(R.string.btn_logout))

        navigationView.setNavigationItemSelectedListener { menuItem ->
            drawerLayout.closeDrawer(GravityCompat.START)
            if (menuItem.itemId == 999) {
                handleLogout()
            } else {
                Toast.makeText(this, getString(R.string.coming_soon), Toast.LENGTH_SHORT).show()
            }
            true
        }
    }

    private fun setupRecyclerViews() {
        // Stats RecyclerView (horizontal)
        val rvStats = findViewById<RecyclerView>(R.id.rvStats)
        val stats = menuService.getStatsForRole(user.role.roleName)
        rvStats.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvStats.adapter = StatCardAdapter(stats)

        // Menu RecyclerView (grid 2 columns)
        val rvMenu = findViewById<RecyclerView>(R.id.rvMenu)
        val menuItems = menuService.getMenuForRole(user.role.roleName)
        rvMenu.layoutManager = GridLayoutManager(this, 2)
        rvMenu.adapter = MenuCardAdapter(menuItems)

        // Add spacing
        val spacing = (8 * resources.displayMetrics.density).toInt()
        rvStats.addItemDecoration(SpacingDecoration(spacing))
        rvMenu.addItemDecoration(GridSpacingDecoration(2, spacing))
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

        // Update role badge color
        val tvRoleBadge = findViewById<TextView>(R.id.tvRoleBadge)
        tvRoleBadge.setTextColor(color)
        val badgeBg = tvRoleBadge.background as? GradientDrawable ?: GradientDrawable()
        badgeBg.setColor(Color.argb(32, Color.red(color), Color.green(color), Color.blue(color)))
        badgeBg.cornerRadius = 12 * resources.displayMetrics.density
        tvRoleBadge.background = badgeBg

        // Update avatar border color
        val tvAvatar = findViewById<TextView>(R.id.tvAvatar)
        tvAvatar.setTextColor(color)
        val avatarBg = tvAvatar.background as? GradientDrawable ?: GradientDrawable()
        avatarBg.shape = GradientDrawable.OVAL
        avatarBg.setColor(Color.argb(32, Color.red(color), Color.green(color), Color.blue(color)))
        avatarBg.setStroke((2 * resources.displayMetrics.density).toInt(), color)
        tvAvatar.background = avatarBg
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun handleLogout() {
        SessionManager.logout()
        navigateToLogin()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun playEntryAnimations() {
        val views = listOf(
            findViewById<View>(R.id.tvRoleBadge),
            findViewById<View>(R.id.rvStats),
            findViewById<View>(R.id.rvMenu)
        )
        views.forEachIndexed { index, view ->
            view.alpha = 0f
            view.translationY = 30f
            view.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .setStartDelay((200 + index * 150).toLong())
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    // RecyclerView spacing decorations
    class SpacingDecoration(private val spacing: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: android.graphics.Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            outRect.right = spacing
        }
    }

    class GridSpacingDecoration(private val spanCount: Int, private val spacing: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: android.graphics.Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val position = parent.getChildAdapterPosition(view)
            val column = position % spanCount
            outRect.left = spacing - column * spacing / spanCount
            outRect.right = (column + 1) * spacing / spanCount
            if (position >= spanCount) outRect.top = spacing
        }
    }
}
