package com.example.assignment.service

import android.graphics.Color
import com.example.assignment.R
import com.example.assignment.model.MenuItem

object MenuService {

    data class StatItem(
        val icon: String,
        val label: String,
        val value: String
    )

    data class SidebarMenuItem(
        val title: String,
        val iconResId: Int
    )

    data class DashboardConfig(
        val accentColor: Int,
        val stats: List<StatItem>,
        val menuItems: List<MenuItem>
    )

    fun getDashboardConfig(roleName: String): DashboardConfig {
        return when (roleName) {
            "Admin" -> DashboardConfig(
                accentColor = Color.parseColor("#E94560"),
                stats = listOf(
                    StatItem("👥", "Total Users", "24"),
                    StatItem("🐾", "Total Hewan", "156"),
                    StatItem("🏠", "Total Kandang", "32"),
                    StatItem("🎟️", "Pengunjung Hari Ini", "481")
                ),
                menuItems = listOf(
                    MenuItem("Kelola User", "👥", Color.parseColor("#E94560")),
                    MenuItem("Kelola Role", "🔑", Color.parseColor("#E94560")),
                    MenuItem("Laporan Sistem", "📊", Color.parseColor("#E94560")),
                    MenuItem("Statistik", "📈", Color.parseColor("#E94560"))
                )
            )
            "Veterinarian" -> DashboardConfig(
                accentColor = Color.parseColor("#3B82F6"),
                stats = listOf(
                    StatItem("🩺", "Pemeriksaan Hari Ini", "8"),
                    StatItem("💊", "Hewan Dalam Perawatan", "12"),
                    StatItem("✅", "Hewan Sehat", "144")
                ),
                menuItems = listOf(
                    MenuItem("Rekam Medis", "🏥", Color.parseColor("#3B82F6")),
                    MenuItem("Jadwal Perawatan", "📅", Color.parseColor("#3B82F6")),
                    MenuItem("Laporan Kesehatan", "📋", Color.parseColor("#3B82F6"))
                )
            )
            "Zookeeper" -> DashboardConfig(
                accentColor = Color.parseColor("#22C55E"),
                stats = listOf(
                    StatItem("🦁", "Total Hewan", "156"),
                    StatItem("🍖", "Jadwal Makan", "12"),
                    StatItem("🏠", "Kandang Aktif", "28"),
                    StatItem("📝", "Log Hari Ini", "45")
                ),
                menuItems = listOf(
                    MenuItem("Data Hewan", "🦁", Color.parseColor("#22C55E")),
                    MenuItem("Jadwal Makan", "🍖", Color.parseColor("#22C55E")),
                    MenuItem("Kandang", "🏠", Color.parseColor("#22C55E")),
                    MenuItem("Log Aktivitas", "📝", Color.parseColor("#22C55E"))
                )
            )
            "Ticketing" -> DashboardConfig(
                accentColor = Color.parseColor("#F59E0B"),
                stats = listOf(
                    StatItem("🎫", "Tiket Terjual", "234"),
                    StatItem("👨‍👩‍👧‍👦", "Jumlah Pengunjung", "481"),
                    StatItem("💰", "Pendapatan", "Rp 11.7jt")
                ),
                menuItems = listOf(
                    MenuItem("Penjualan Tiket", "🎫", Color.parseColor("#F59E0B")),
                    MenuItem("Data Pengunjung", "👨‍👩‍👧‍👦", Color.parseColor("#F59E0B")),
                    MenuItem("Laporan Pendapatan", "💰", Color.parseColor("#F59E0B"))
                )
            )
            "Researcher" -> DashboardConfig(
                accentColor = Color.parseColor("#8B5CF6"),
                stats = listOf(
                    StatItem("🔬", "Total Spesies", "48"),
                    StatItem("🔍", "Observasi Aktif", "7"),
                    StatItem("📑", "Laporan Riset", "23")
                ),
                menuItems = listOf(
                    MenuItem("Database Spesies", "🔬", Color.parseColor("#8B5CF6")),
                    MenuItem("Data Observasi", "🔍", Color.parseColor("#8B5CF6")),
                    MenuItem("Laporan Riset", "📑", Color.parseColor("#8B5CF6"))
                )
            )
            else -> DashboardConfig(
                accentColor = Color.parseColor("#E94560"),
                stats = listOf(StatItem("ℹ️", "Info", "N/A")),
                menuItems = emptyList()
            )
        }
    }

    /**
     * Returns sidebar menu items with drawable resource icons for the given role.
     * These are the role-specific items shown in the navigation drawer
     * between the common "Utama" group and the "Lainnya" group.
     */
    fun getSidebarMenuItems(roleName: String): List<SidebarMenuItem> {
        return when (roleName) {
            "Admin" -> listOf(
                SidebarMenuItem("Kelola User", R.drawable.ic_users),
                SidebarMenuItem("Kelola Role", R.drawable.ic_key_role),
                SidebarMenuItem("Laporan Sistem", R.drawable.ic_chart),
                SidebarMenuItem("Statistik", R.drawable.ic_trending)
            )
            "Veterinarian" -> listOf(
                SidebarMenuItem("Rekam Medis", R.drawable.ic_medical),
                SidebarMenuItem("Jadwal Perawatan", R.drawable.ic_calendar),
                SidebarMenuItem("Laporan Kesehatan", R.drawable.ic_clipboard)
            )
            "Zookeeper" -> listOf(
                SidebarMenuItem("Data Hewan", R.drawable.ic_animal),
                SidebarMenuItem("Jadwal Makan", R.drawable.ic_food),
                SidebarMenuItem("Kandang", R.drawable.ic_cage),
                SidebarMenuItem("Log Aktivitas", R.drawable.ic_log)
            )
            "Ticketing" -> listOf(
                SidebarMenuItem("Penjualan Tiket", R.drawable.ic_ticket),
                SidebarMenuItem("Data Pengunjung", R.drawable.ic_visitors),
                SidebarMenuItem("Laporan Pendapatan", R.drawable.ic_money)
            )
            "Researcher" -> listOf(
                SidebarMenuItem("Database Spesies", R.drawable.ic_science),
                SidebarMenuItem("Data Observasi", R.drawable.ic_search),
                SidebarMenuItem("Laporan Riset", R.drawable.ic_clipboard)
            )
            else -> emptyList()
        }
    }
}
