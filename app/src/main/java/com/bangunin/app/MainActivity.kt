package com.bangunin.app

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.net.Uri
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.util.Calendar

class MainActivity : Activity() {
    private var hour = 6
    private var minute = 0
    private val audioRequest = 202
    private lateinit var timeText: TextView
    private lateinit var audioStatus: TextView
    private lateinit var root: LinearLayout
    private val blue = Color.rgb(40, 120, 255)
    private val ink = Color.rgb(24, 35, 59)
    private val muted = Color.rgb(117, 129, 154)
    private val bg = Color.rgb(244, 247, 252)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("bangunin", MODE_PRIVATE)
        hour = prefs.getInt("hour", 6)
        minute = prefs.getInt("minute", 0)
        window.statusBarColor = bg
        window.navigationBarColor = bg
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        showHome()
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 100)
        }
    }

    private fun showHome() {
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(22), dp(20), dp(22), dp(14))
            setBackgroundColor(bg)
        }
        val scroll = ScrollView(this).apply { addView(root) }
        val header = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        val brandBox = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; layoutParams = LinearLayout.LayoutParams(0, -2, 1f) }
        brandBox.addView(text("Bangunin  ●", 28, ink, true))
        brandBox.addView(text("Bangun tepat waktu, mulai hari lebih baik.", 13, muted, false).apply { setPadding(0, dp(3), 0, 0) })
        val settings = TextView(this).apply { text = "⚙"; textSize = 22f; gravity = Gravity.CENTER; setTextColor(blue); setBackgroundColor(Color.rgb(234,242,255)); setPadding(dp(13), dp(8), dp(13), dp(8)); setOnClickListener { showSettings() } }
        header.addView(brandBox); header.addView(settings)
        root.addView(header, params(bottom = 20))

        val info = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL; setPadding(dp(14), dp(14), dp(14), dp(14)); setBackgroundColor(Color.rgb(234,242,255)) }
        info.addView(text("📸", 28, blue, false), LinearLayout.LayoutParams(dp(45), -2))
        val infoText = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        infoText.addView(text("Alarm anti-snooze", 15, Color.rgb(30,78,153), true))
        infoText.addView(text("Alarm baru berhenti setelah foto wajah baru diambil.", 12, Color.rgb(30,78,153), false))
        info.addView(infoText)
        root.addView(info, params(bottom = 22))

        val section = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        section.addView(text("Daftar Alarm", 18, ink, true), LinearLayout.LayoutParams(0, -2, 1f))
        section.addView(actionButton("＋ Tambah", true) { chooseTime() })
        root.addView(section, params(bottom = 12))

        val card = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL; setPadding(dp(14), dp(14), dp(14), dp(14)); setBackgroundColor(Color.WHITE) }
        card.addView(text("☀️", 25, ink, false), LinearLayout.LayoutParams(dp(48), -2))
        val details = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; layoutParams = LinearLayout.LayoutParams(0, -2, 1f) }
        timeText = text(String.format("%02d:%02d", hour, minute), 25, ink, true)
        details.addView(timeText)
        details.addView(text("Alarm harian · Bangun pagi", 12, muted, false))
        card.addView(details)
        val toggle = Switch(this).apply { isChecked = getSharedPreferences("bangunin", MODE_PRIVATE).getBoolean("enabled", false); text = ""; buttonTintList = android.content.res.ColorStateList.valueOf(blue); setOnCheckedChangeListener { _, checked -> if (checked) scheduleAlarm() else cancelAlarm() } }
        card.addView(toggle)
        root.addView(card, params(bottom = 22))

        root.addView(text("Nada alarm", 16, ink, true), params(bottom = 8))
        val sound = actionButton("♫  Pilih File Nada (MP3 / WAV / OGG)", false) {
            val pick = Intent(Intent.ACTION_OPEN_DOCUMENT).apply { addCategory(Intent.CATEGORY_OPENABLE); type = "audio/*" }
            startActivityForResult(pick, audioRequest)
        }
        root.addView(sound, params(bottom = 7))
        audioStatus = text(getSharedPreferences("bangunin", MODE_PRIVATE).getString("audio_name", null) ?: "Nada alarm bawaan Android", 12, muted, false)
        audioStatus.gravity = Gravity.CENTER
        root.addView(audioStatus, params(bottom = 20))

        root.addView(actionButton("▶  Simulasikan Alarm Berbunyi", true) { startActivity(Intent(this, AlarmActivity::class.java)) }, params(bottom = 12))
        root.addView(actionButton("◴  Riwayat", false) { showHistory() }, params(bottom = 12))
        root.addView(text("Alarm memakai alarm tepat waktu Android. Agar lebih andal saat layar mati, izinkan alarm tepat waktu dan nonaktifkan pembatasan baterai untuk Bangunin jika diperlukan.", 12, muted, false).apply { gravity = Gravity.CENTER; setPadding(dp(8), dp(8), dp(8), dp(8)) })
        setContentView(scroll)
    }

    private fun chooseTime() {
        TimePickerDialog(this, { _, h, m -> hour = h; minute = m; timeText.text = String.format("%02d:%02d", h, m); getSharedPreferences("bangunin", MODE_PRIVATE).edit().putInt("hour", h).putInt("minute", m).apply(); Toast.makeText(this, "Waktu alarm dipilih", Toast.LENGTH_SHORT).show() }, hour, minute, true).show()
    }

    private fun scheduleAlarm() {
        val manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !manager.canScheduleExactAlarms()) {
            Toast.makeText(this, "Izinkan alarm tepat waktu di pengaturan berikut.", Toast.LENGTH_LONG).show()
            startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)); return
        }
        val cal = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, hour); set(Calendar.MINUTE, minute); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0); if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1) }
        val pi = PendingIntent.getBroadcast(this, 1, Intent(this, AlarmReceiver::class.java), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pi)
        getSharedPreferences("bangunin", MODE_PRIVATE).edit().putBoolean("enabled", true).putInt("hour", hour).putInt("minute", minute).apply()
        Toast.makeText(this, "Alarm aktif pukul ${String.format("%02d:%02d", hour, minute)}", Toast.LENGTH_LONG).show()
    }

    private fun cancelAlarm() {
        val pi = PendingIntent.getBroadcast(this, 1, Intent(this, AlarmReceiver::class.java), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        (getSystemService(Context.ALARM_SERVICE) as AlarmManager).cancel(pi)
        getSharedPreferences("bangunin", MODE_PRIVATE).edit().putBoolean("enabled", false).apply()
        Toast.makeText(this, "Alarm dinonaktifkan", Toast.LENGTH_SHORT).show()
    }

    private fun showHistory() {
        val panel = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER; setPadding(dp(24), dp(40), dp(24), dp(24)); setBackgroundColor(bg) }
        panel.addView(text("Riwayat", 28, ink, true), params(bottom = 12))
        panel.addView(text("Riwayat alarm akan tercatat di versi berikutnya.", 15, muted, false), params(bottom = 24))
        panel.addView(actionButton("← Kembali ke Alarm", true) { showHome() })
        setContentView(panel)
    }

    private fun showSettings() {
        val panel = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER; setPadding(dp(24), dp(40), dp(24), dp(24)); setBackgroundColor(bg) }
        panel.addView(text("Pengaturan", 28, ink, true), params(bottom = 18))
        panel.addView(text("Untuk alarm saat layar mati, izinkan notifikasi dan alarm tepat waktu. Pada beberapa HP, atur baterai Bangunin ke ‘Tanpa batas’/‘Unrestricted’ agar sistem tidak menundanya.", 15, muted, false), params(bottom = 24))
        panel.addView(actionButton("Izinkan Alarm Tepat Waktu", true) { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)) else Toast.makeText(this, "Tidak diperlukan di Android ini", Toast.LENGTH_SHORT).show() }, params(bottom = 12))
        panel.addView(actionButton("← Kembali ke Alarm", false) { showHome() })
        setContentView(panel)
    }

    @Deprecated("Uses startActivityForResult for broad Android compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == audioRequest && resultCode == RESULT_OK) {
            val uri: Uri = data?.data ?: return
            try { contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) } catch (_: Exception) {}
            val name = uri.lastPathSegment?.substringAfterLast('/') ?: "File audio pilihan"
            getSharedPreferences("bangunin", MODE_PRIVATE).edit().putString("audio_uri", uri.toString()).putString("audio_name", name).apply()
            showHome(); Toast.makeText(this, "Nada pilihan disimpan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun text(value: String, size: Int, color: Int, bold: Boolean) = TextView(this).apply { text = value; textSize = size.toFloat(); setTextColor(color); if (bold) setTypeface(typeface, Typeface.BOLD) }
    private fun actionButton(label: String, primary: Boolean, click: () -> Unit) = TextView(this).apply { text = label; textSize = 14f; gravity = Gravity.CENTER; setTypeface(typeface, Typeface.BOLD); setPadding(dp(14), dp(14), dp(14), dp(14)); setTextColor(if (primary) Color.WHITE else ink); setBackgroundColor(if (primary) blue else Color.WHITE); setOnClickListener { click() } }
    private fun params(bottom: Int = 0) = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply { bottomMargin = dp(bottom) }
    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
}
