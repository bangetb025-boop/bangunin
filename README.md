# Bangunin Android

Project Android Kotlin yang dapat dibangun menjadi APK lewat GitHub Actions.

## Build APK di GitHub
1. Ekstrak ZIP ini.
2. Upload/push isi folder `Bangunin-Android` ke repository GitHub (folder `.github/workflows` harus ikut terunggah).
3. Buka tab **Actions** dan pilih **Build Bangunin APK**.
4. Tunggu workflow sukses, lalu unduh artifact **Bangunin-debug-apk** dan ekstrak `app-debug.apk`.

## Fitur
- Tampilan native Android dengan warna biru, kartu alarm, pemilih waktu, pemilih audio, simulasi alarm, riwayat dan pengaturan.
- Menjadwalkan alarm tepat waktu menggunakan `setExactAndAllowWhileIdle`.
- Saat alarm berbunyi, membuat notifikasi kategori alarm dengan full-screen intent agar dapat muncul saat layar terkunci/mati (tergantung izin dan kebijakan Android/perangkat).
- Audio alarm berulang hingga foto baru berhasil diambil melalui aplikasi kamera perangkat.
- Memilih file audio dari penyimpanan melalui document picker.

## Pengaturan penting di HP
- Izinkan notifikasi Bangunin.
- Izinkan **Alarm & pengingat / alarm tepat waktu** jika Android meminta.
- Di Android 14+, bila tersedia, izinkan notifikasi layar penuh untuk Bangunin.
- Untuk beberapa merek HP, buka Info aplikasi > Baterai lalu pilih **Tanpa batas / Unrestricted** agar sistem tidak membatasi aplikasi saat layar mati.
- Jangan aktifkan mode Jangan Ganggu jika ingin alarm berbunyi.

Catatan: Android dan masing-masing produsen dapat membatasi aktivitas yang tampil dari latar belakang. Karena itu tidak ada aplikasi pihak ketiga yang bisa menjamin layar kamera otomatis muncul di semua HP tanpa izin/pengaturan perangkat. Jika layar penuh dibatasi, notifikasi alarm tetap menjadi jalur fallback; ketuk notifikasi untuk membuka alarm.
