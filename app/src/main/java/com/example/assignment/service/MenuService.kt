package com.example.assignment.service

import com.example.assignment.model.MenuItem

// Mengelola menu items per role
class MenuService {

    fun getMenuForRole(roleName: String): List<MenuItem> {
        return when (roleName) {
            "Admin" -> listOf(
                MenuItem("Kelola User", "👥", "Manajemen data pengguna sistem"),
                MenuItem("Kelola Role", "🔑", "Pengaturan hak akses dan role"),
                MenuItem("Laporan Sistem", "📊", "Laporan aktivitas seluruh sistem"),
                MenuItem("Statistik", "📈", "Statistik ringkasan kebun binatang")
            )
            "Veterinarian" -> listOf(
                MenuItem("Rekam Medis Hewan", "🏥", "Catatan kesehatan dan riwayat medis"),
                MenuItem("Jadwal Perawatan", "📅", "Jadwal pemeriksaan dan vaksinasi"),
                MenuItem("Laporan Kesehatan", "📋", "Laporan kondisi kesehatan hewan")
            )
            "Zookeeper" -> listOf(
                MenuItem("Data Hewan", "🦁", "Database informasi seluruh hewan"),
                MenuItem("Jadwal Makan", "🍖", "Jadwal dan jenis pakan hewan"),
                MenuItem("Kandang", "🏠", "Manajemen kandang dan habitat"),
                MenuItem("Log Aktivitas", "📝", "Catatan aktivitas harian")
            )
            "Ticketing" -> listOf(
                MenuItem("Penjualan Tiket", "🎫", "Penjualan dan pencetakan tiket"),
                MenuItem("Data Pengunjung", "👨‍👩‍👧‍👦", "Informasi dan statistik pengunjung"),
                MenuItem("Laporan Pendapatan", "💰", "Laporan keuangan dan pendapatan")
            )
            "Researcher" -> listOf(
                MenuItem("Database Spesies", "🔬", "Katalog spesies dan klasifikasi"),
                MenuItem("Data Observasi", "👁", "Catatan hasil pengamatan hewan"),
                MenuItem("Laporan Riset", "📑", "Publikasi dan laporan penelitian")
            )
            else -> emptyList()
        }
    }

    fun getStatsForRole(roleName: String): List<Triple<String, String, String>> {
        return when (roleName) {
            "Admin" -> listOf(
                Triple("👥", "${(50..200).random()}", "Total Users"),
                Triple("🦁", "${(100..500).random()}", "Total Hewan"),
                Triple("🏠", "${(20..80).random()}", "Total Kandang"),
                Triple("🎫", "${(200..1000).random()}", "Pengunjung Hari Ini")
            )
            "Veterinarian" -> listOf(
                Triple("🩺", "${(5..20).random()}", "Pemeriksaan Hari Ini"),
                Triple("💊", "${(3..15).random()}", "Hewan Dalam Perawatan"),
                Triple("💚", "${(80..300).random()}", "Hewan Sehat")
            )
            "Zookeeper" -> listOf(
                Triple("🦁", "${(100..500).random()}", "Total Hewan"),
                Triple("🍖", "${(10..50).random()}", "Jadwal Makan Hari Ini"),
                Triple("🏠", "${(20..80).random()}", "Kandang Aktif"),
                Triple("📝", "${(5..30).random()}", "Log Hari Ini")
            )
            "Ticketing" -> listOf(
                Triple("🎫", "${(100..500).random()}", "Tiket Terjual"),
                Triple("👥", "${(200..1000).random()}", "Jumlah Pengunjung"),
                Triple("💰", "Rp ${(5..50).random()}jt", "Pendapatan Hari Ini")
            )
            "Researcher" -> listOf(
                Triple("🔬", "${(50..200).random()}", "Total Spesies"),
                Triple("👁", "${(3..20).random()}", "Observasi Aktif"),
                Triple("📑", "${(10..50).random()}", "Laporan Riset")
            )
            else -> emptyList()
        }
    }
}
