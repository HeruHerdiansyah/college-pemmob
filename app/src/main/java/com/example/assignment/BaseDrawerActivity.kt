package com.example.assignment

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.assignment.model.User
import com.example.assignment.service.MenuService
import com.example.assignment.util.SessionManager
import com.google.android.material.navigation.NavigationView

/**
 * Base activity that provides a reusable navigation drawer with role-based menu items.
 *
 * Any activity that needs a sidebar can extend this class and override:
 * - [getContentLayoutId] — return the layout resource ID for the content area
 * - [getActiveNavItemId] — return the menu item ID that should be highlighted
 * - [onContentReady] — called after content is inflated, setup your views here
 */
abstract class BaseDrawerActivity : AppCompatActivity() {

    protected lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    // Menu item ID constants for programmatic menu
    companion object {
        // Group IDs
        private const val GROUP_MAIN = 1
        private const val GROUP_ROLE = 2
        private const val GROUP_OTHER = 3

        // Fixed menu item IDs
        const val NAV_DASHBOARD = 1001
        const val NAV_PROFILE = 1002
        const val NAV_LOGOUT = 1099

        // Role menu items start from this ID
        private const val ROLE_MENU_ID_START = 2000
    }

    /** Return the layout resource ID for the content (e.g., R.layout.content_dashboard) */
    abstract fun getContentLayoutId(): Int

    /** Return the nav item ID that's currently active (for highlighting) */
    abstract fun getActiveNavItemId(): Int

    /** Called after the content layout has been inflated into the drawer frame */
    abstract fun onContentReady(savedInstanceState: Bundle?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_base_drawer)

        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navView)

        // Inflate child content into the content frame
        val contentFrame = findViewById<View>(R.id.contentFrame)
        layoutInflater.inflate(getContentLayoutId(), contentFrame as android.widget.FrameLayout, true)

        // Apply system bar + IME insets to content frame
        // This ensures the content resizes when the keyboard appears (like login/register)
        ViewCompat.setOnApplyWindowInsetsListener(contentFrame) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            val bottomPadding = maxOf(systemBars.bottom, ime.bottom)
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, bottomPadding)
            insets
        }

        val user = SessionManager.currentUser
        if (user == null) {
            navigateToLogin()
            return
        }

        setupDrawer(user)

        // Let child activity set up its content
        onContentReady(savedInstanceState)
    }

    private fun setupDrawer(user: User) {
        setupNavHeader(user)
        buildMenu(user)
        setupMenuClickListener(user)

        // Highlight the current page
        val activeId = getActiveNavItemId()
        navView.setCheckedItem(activeId)
    }

    private fun setupNavHeader(user: User) {
        val headerView = navView.getHeaderView(0)
        val tvNavName = headerView.findViewById<TextView>(R.id.tvNavName)
        val tvNavRole = headerView.findViewById<TextView>(R.id.tvNavRole)
        val tvNavAvatar = headerView.findViewById<TextView>(R.id.tvNavAvatar)

        tvNavName.text = user.fullName
        tvNavRole.text = user.roleName
        tvNavAvatar.text = user.fullName.take(1).uppercase()

        val config = MenuService.getDashboardConfig(user.roleName)
        val navAvatarBg = tvNavAvatar.background as? GradientDrawable
        navAvatarBg?.setColor(config.accentColor)

        val navRoleBg = tvNavRole.background as? GradientDrawable
        navRoleBg?.setColor(config.accentColor)
    }

    private fun buildMenu(user: User) {
        val menu = navView.menu
        menu.clear()

        // ─── Group 1: Utama ───
        menu.add(GROUP_MAIN, NAV_DASHBOARD, Menu.NONE, getString(R.string.nav_dashboard))
            .setIcon(R.drawable.ic_dashboard)
            .setCheckable(true)

        menu.add(GROUP_MAIN, NAV_PROFILE, Menu.NONE, getString(R.string.nav_profile))
            .setIcon(R.drawable.ic_person)
            .setCheckable(true)

        // ─── Group 2: Role-specific menu ───
        val sidebarItems = MenuService.getSidebarMenuItems(user.roleName)
        if (sidebarItems.isNotEmpty()) {
            val subMenu = menu.addSubMenu(GROUP_ROLE, Menu.NONE, Menu.NONE,
                getString(R.string.nav_group_role_menu, user.roleName))

            sidebarItems.forEachIndexed { index, item ->
                subMenu.add(GROUP_ROLE, ROLE_MENU_ID_START + index, Menu.NONE, item.title)
                    .setIcon(item.iconResId)
                    .setCheckable(true)
            }
        }

        // ─── Group 3: Lainnya ───
        val otherSubMenu = menu.addSubMenu(GROUP_OTHER, Menu.NONE, Menu.NONE,
            getString(R.string.nav_group_other))

        otherSubMenu.add(GROUP_OTHER, NAV_LOGOUT, Menu.NONE, getString(R.string.nav_logout))
            .setIcon(R.drawable.ic_logout)
            .setCheckable(false)
    }

    private fun setupMenuClickListener(user: User) {
        val sidebarItems = MenuService.getSidebarMenuItems(user.roleName)

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                NAV_DASHBOARD -> {
                    if (this !is DashboardActivity) {
                        val intent = Intent(this, DashboardActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        startActivity(intent)
                        finish()
                    }
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                NAV_PROFILE -> {
                    if (this !is ProfileActivity) {
                        startActivity(Intent(this, ProfileActivity::class.java))
                    }
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                NAV_LOGOUT -> {
                    SessionManager.logout()
                    navigateToLogin()
                }
                else -> {
                    // Role-specific menu item clicked
                    val roleIndex = menuItem.itemId - ROLE_MENU_ID_START
                    if (roleIndex in sidebarItems.indices) {
                        val item = sidebarItems[roleIndex]
                        Toast.makeText(this, "Menu: ${item.title}\nFitur belum tersedia", Toast.LENGTH_SHORT).show()
                    }
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
            }
            true
        }
    }

    /** Open the navigation drawer */
    fun openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START)
    }

    /** Close the navigation drawer */
    fun closeDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
