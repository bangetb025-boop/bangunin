package com.bangunin.app

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.media.RingtoneManager
import android.net.Uri
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import java.util.Calendar

class MainActivity : Activity() {
    private var hour = 6
    private var minute = 0
    private val audioRequest = 202
    private lateinit var timeText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showHome()
        if (Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 100)
        }
    }

    private fun showHome() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(28, 40, 28, 28)
            setBackgroundColor(0xFFF4F7FC.toInt())
        }
        val title = TextView(this).apply {
            text = "⏰  Bangunin"
            textSize = 30f
            setTextColor(0xFF14213D.toInt())
            gravity = Gravity.CENTER
        }
        val subtitle = TextView(this).apply {
            text = "Alarm yang berhenti setelah foto wajah baru diambil."
            textSize = 15f
            gravity = Gravity.CENTER
            setTextColor(0xFF667085.toInt())
            setPadding(0, 14, 0, 28)
        }
        timeText = TextView(this).apply {
            text = String.format("%02d:%02d", hour, minute)
            textSize = 44f
            setTextColor(0xFF246BFD.toInt())
            gravity = Gravity.CENTER
            setPadding(0, 18, 0, 18)
        }
        val choose = Button(this).apply {
            text = "Pilih Jam Alarm"
            setOnClickListener {
                TimePickerDialog(this@MainActivity, { _, h, m ->
                    hour = h
                    minute = m
                    timeText.text = String.format("%02d:%02d", hour, minute)
                }, hour, minute, true).show()
            }
        }
        val save = Button(this).apply {
            text = "Aktifkan Alarm"
            setOnClickListener { scheduleAlarm() }
        }
        val chooseAudio = Button(this).apply {
            text = "Pilih File Nada (MP3 / WAV / OGG)"
            setOnClickListener {
                val pick = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "audio/*"
                }
                startActivityForResult(pick, audioRequest)
            }
        }
        val audioStatus = TextView(this).apply {
            text = "Nada: " + (getSharedPreferences("bangunin", MODE_PRIVATE)
                .getString("audio_name", null) ?: "Nada alarm bawaan Android")
            textSize = 13f
            gravity = Gravity.CENTER
            setTextColor(0xFF667085.toInt())
            setPadding(0, 0, 0, 12)
        }
        val demo = Button(this).apply {
            text = "Tes Layar Alarm"
            setOnClickListener { startActivity(Intent(this@MainActivity, AlarmActivity::class.java)) }
        }
        listOf(title, subtitle, timeText, choose, chooseAudio, audioStatus, save, demo).forEach { view ->
            root.addView(view, LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 12 })
        }
        setContentView(root)
    }


    @Deprecated("Uses startActivityForResult for broad Android compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == audioRequest && resultCode == RESULT_OK) {
            val uri = data?.data ?: return
            try {
                contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) { }
            val name = uri.lastPathSegment?.substringAfterLast('/') ?: "File audio pilihan"
            getSharedPreferences("bangunin", MODE_PRIVATE).edit()
                .putString("audio_uri", uri.toString())
                .putString("audio_name", name)
                .apply()
            Toast.makeText(this, "Nada pilihan berhasil disimpan.", Toast.LENGTH_LONG).show()
            showHome()
        }
    }

    private fun scheduleAlarm() {
        val manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !manager.canScheduleExactAlarms()) {
            Toast.makeText(this, "Izinkan alarm tepat waktu di pengaturan berikut.", Toast.LENGTH_LONG).show()
            startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
            return
        }
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
        }
        val intent = Intent(this, AlarmReceiver::class.java)
        val pending = PendingIntent.getBroadcast(
            this, 1, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pending)
        Toast.makeText(this, "Alarm aktif pukul ${String.format("%02d:%02d", hour, minute)}", Toast.LENGTH_LONG).show()
    }
}
