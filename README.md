# Bangunin Android

Project Android native Kotlin dengan konfigurasi Gradle dan workflow GitHub Actions untuk membangun APK debug.

## Build APK melalui GitHub (paling mudah dari Termux)
1. Buat repository GitHub bernama `Bangunin`.
2. Upload seluruh isi folder project ini ke branch `main`.
3. Buka tab **Actions** pada repository.
4. Pilih workflow **Build Bangunin APK**, lalu tekan **Run workflow** (atau tunggu build otomatis setelah push).
5. Buka hasil workflow yang berhasil dan unduh artifact `Bangunin-debug-apk`. Ekstrak ZIP artifact untuk mendapatkan `app-debug.apk`.

## Build lokal
Dibutuhkan JDK 17, Android SDK platform 35, Android Build Tools, dan Gradle 8.9 atau lebih baru yang kompatibel dengan Android Gradle Plugin 8.7.3.
Jalankan `gradle assembleDebug`.

## Memilih nada sendiri\nDi aplikasi, tekan **Pilih File Nada (MP3 / WAV / OGG)** lalu pilih file audio dari penyimpanan HP. Jika belum memilih file, aplikasi memakai nada alarm bawaan Android.\n\n## Status fitur
- Termasuk: layar utama, pemilih waktu, penjadwalan alarm tepat waktu (memerlukan izin khusus Android), pemilih file nada audio dari HP (MP3/WAV/OGG yang didukung perangkat), pemutaran nada berulang, membuka kamera, berhenti setelah kamera mengembalikan hasil foto.
- Prototype: foto kamera menggunakan intent kamera sistem dan thumbnail hasil. Belum ada validasi biometrik/wajah, penyimpanan foto permanen, pemulihan setelah reboot, atau jaminan tetap berbunyi di semua kondisi perangkat. Uji pada perangkat sendiri dan beri izin yang diminta.
