package com.bangunin.app

import android.app.Activity
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Bundle
import android.content.Intent
import android.provider.MediaStore
import android.net.Uri
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Toast

class AlarmActivity : Activity() {
    private var player: MediaPlayer? = null
    private val photoRequest = 501
    private var hasStartedCamera = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(
            android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
            android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
            android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )
        startAlarmAudio()
        showAlarm()
    }

    private fun startAlarmAudio() {
        val prefs = getSharedPreferences("bangunin", MODE_PRIVATE)
        val selected = prefs.getString("audio_uri", null)?.let { Uri.parse(it) }
        val fallback = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        try {
            player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                if (selected != null) {
                    try {
                        setDataSource(this@AlarmActivity, selected)
                    } catch (_: Exception) {
                        setDataSource(this@AlarmActivity, fallback)
                    }
                } else {
                    setDataSource(this@AlarmActivity, fallback)
                }
                isLooping = true
                setOnPreparedListener { it.start() }
                setOnErrorListener { _, _, _ ->
                    try { it.reset(); it.setDataSource(this@AlarmActivity, fallback); it.isLooping = true; it.prepareAsync() } catch (_: Exception) { }
                    true
                }
                prepareAsync()
            }
        } catch (_: Exception) {
            Toast.makeText(this, "Nada gagal diputar. Periksa file audio.", Toast.LENGTH_LONG).show()
        }
    }

    private fun showAlarm() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(28, 30, 28, 30)
            setBackgroundColor(0xFF0C1428.toInt())
        }
        val title = TextView(this).apply {
            text = "🔔\\nAlarm Berbunyi!"
            textSize = 27f
            gravity = Gravity.CENTER
            setTextColor(0xFFFFFFFF.toInt())
        }
        val subtitle = TextView(this).apply {
            text = "Ambil foto wajah BARU untuk menghentikan alarm."
            textSize = 17f
            gravity = Gravity.CENTER
            setTextColor(0xFFD5DDF0.toInt())
            setPadding(0, 24, 0, 24)
        }
        val camera = Button(this).apply {
            text = "📸  Ambil Foto Wajah"
            setOnClickListener { openCamera() }
        }
        root.addView(title, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        root.addView(subtitle, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        root.addView(camera, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        setContentView(root)
    }

    private fun openCamera() {
        if (hasStartedCamera) return
        try {
            hasStartedCamera = true
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, photoRequest)
        } catch (e: Exception) {
            hasStartedCamera = false
            Toast.makeText(this, "Kamera tidak tersedia.", Toast.LENGTH_LONG).show()
        }
    }

    @Deprecated("Uses startActivityForResult for broad Android compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == photoRequest) {
            hasStartedCamera = false
            if (resultCode == RESULT_OK && data?.extras?.get("data") != null) {
                player?.stop()
                player?.release()
                player = null
                Toast.makeText(this, "Foto berhasil diambil. Alarm berhenti.", Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this, "Foto belum berhasil. Alarm tetap aktif.", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroy() {
        player?.let {
            try { if (it.isPlaying) it.stop() } catch (_: Exception) { }
            it.release()
        }
        player = null
        super.onDestroy()
    }

    @Deprecated("Deprecated in Android API")
    override fun onBackPressed() {
        Toast.makeText(this, "Ambil foto baru untuk menyelesaikan alarm.", Toast.LENGTH_SHORT).show()
    }
}
