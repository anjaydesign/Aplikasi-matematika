# Math Battle Offline — Game Matematika Multiplayer 2–4 Pemain

Math Battle Offline adalah game matematika edukatif dan kompetitif untuk platform Android yang dirancang agar dapat dimainkan secara offline (tanpa koneksi internet). Aplikasi mendukung mode permainan single player serta dua mode utama multiplayer: **Pass-and-Play (Satu HP)** dan **Mabar Antar-HP melalui Wi-Fi Lokal**.

---

## 🌟 Fitur Utama

### 1. Mode Multiplayer A: Pass-and-Play (Satu HP)
- Mendukung **2 hingga 4 pemain** dalam satu perangkat Android.
- Pergantian giliran otomatis dengan urutan konsisten.
- **Layar Transisi Aman "Giliran Pemain":** Tombol konfirmasi kesiapan sebelum soal ditampilkan untuk menjaga kerahasiaan jawaban dari pemain lain.
- Timer independen per giliran dengan penanganan waktu habis dan jawaban kosong.
- Papan skor langsung di setiap akhir ronde dan podium klasemen akhir dengan pemecah seri (*tiebreaker*).

### 2. Mode Multiplayer B: Mabar Antar-HP (Wi-Fi Lokal)
- Menghubungkan **2 hingga 4 perangkat Android** melalui jaringan Wi-Fi lokal atau Hotspot HP tanpa kuota internet.
- **Arsitektur Jaringan Otoritatif (Host-Client):**
  - **Host:** Menginisialisasi `LocalMultiplayerServer` (port 8888), menampilkan alamat IP dan kode ruangan, mengatur sinkronisasi soal dan ronde, serta menghitung skor secara adil.
  - **Klien:** Menghubungkan ke IP Host melalui `LocalMultiplayerClient` dan menunggu di lobi pertandingan.
  - **Protokol Pesan Terstruktur (JSON):** `JOIN_ROOM`, `ROOM_STATE`, `PLAYER_READY`, `START_MATCH`, `QUESTION`, `SUBMIT_ANSWER`, `ANSWER_RESULT`, `SCORE_UPDATE`, `NEXT_ROUND`, `MATCH_FINISHED`, `PING`, `PONG`, `LEAVE_ROOM`, `ERROR`.
  - **Keamanan Soal:** Kunci jawaban tidak pernah dikirimkan ke klien sebelum ronde selesai.
  - **Ketahanan Jaringan:** Heartbeat ping/pong, deteksi putus koneksi, dan pembersihan socket/coroutine saat keluar.

### 3. Generator Soal Offline Lengkap
- 100% luring (tanpa API AI atau ketergantungan server awan).
- Kategori materi: Penjumlahan, Pengurangan, Perkalian, Pembagian bulat aman (tanpa pembagian nol), Aritmatika Campuran, Pecahan, Desimal, Persen, Aljabar Sederhana, dan Geometri Dasar.
- Jenjang: SD (Kelas 1–6), SMP (Kelas 7–9), SMA & Umum.
- Tingkat kesulitan: Mudah, Sedang, Sulit, Campuran.
- Jaminan 4 pilihan jawaban unik tanpa duplikasi.

### 4. Audio & Pembaca Soal Bahasa Indonesia (TTS)
- **SoundManager:** `SoundPool` untuk efek suara instan (klik, jawaban benar, jawaban salah, start, victory, detik waktu) dan `MediaPlayer` untuk musik latar instrumental ceria dengan loop.
- **Manajemen Siklus Hidup:** Sumber daya audio otomatis di-pause saat aplikasi masuk ke latar belakang (`onPause`) dan dilanjutkan saat kembali aktif (`onResume`).
- **Indonesian Speech Helper:** Text-to-Speech yang memprioritaskan suara wanita Indonesia dan menerjemahkan notasi matematika secara fonetik natural (contoh: "+" dibaca "tambah", "÷" dibaca "bagi", "1/2" dibaca "setengah").

### 5. Penyimpanan Lokal & Progres (Room Database)
- Profil pemain: nama, koin, level, XP, dan avatar.
- Riwayat pertandingan lengkap (tanggal, mode, pemenang, jumlah soal).
- Pengaturan audio dan preferensi pembaca soal.
- Misi harian offline dan toko avatar.

---

## 🎮 Panduan Cara Bermain

### Cara Bermain Satu HP (Pass-and-Play)
1. Pilih menu **Multiplayer Satu HP** di Menu Utama.
2. Pilih jumlah pemain (2, 3, atau 4 pemain).
3. Masukkan nama pemain atau gunakan nama bawaan.
4. Pilih kategori soal, tingkat kesulitan, jumlah soal (5, 10, 15, 20), dan durasi giliran (10s, 15s, 20s, 30s).
5. Tekan tombol **Mulai Pertandingan**.
6. Pemain 1 menerima HP, menekan tombol **SAYA SIAP! BUKA SOAL**, dan menjawab dalam batas waktu.
7. Setelah giliran selesai, berikan HP ke Pemain 2 untuk menekan tombol siap pada layar transisi berikutnya.
8. Setelah semua ronde selesai, lihat pemenang di podium klasemen akhir!

### Cara Mabar Wi-Fi Lokal (Antar-HP)
#### Perangkat Host (Pembuat Ruangan):
1. Pastikan semua HP terhubung ke jaringan Wi-Fi yang sama, atau satu HP menyalakan Hotspot Pribadi (Tethering) dan HP lain terhubung ke Hotspot tersebut (tidak membutuhkan paket data internet aktif).
2. Di HP Host, pilih menu **Mabar Wi-Fi Lokal** -> **Buat Ruangan (Host)**.
3. Catat alamat IP Host yang tertera di bagian atas layar (contoh: `192.168.43.1`).
4. Tunggu teman-teman bergabung ke ruangan (minimal 2 pemain, maksimal 4 pemain).
5. Atur jumlah soal dan durasi soal.
6. Setelah semua pemain berstatus **SIAP ✓**, tekan **Mulai Pertandingan**.

#### Perangkat Klien (Penggabung Ruangan):
1. Di HP Klien, pilih menu **Mabar Wi-Fi Lokal** -> **Gabung Ruangan**.
2. Masukkan alamat IP Host yang diberikan oleh pembuat ruangan.
3. Tekan **Hubungkan ke Ruangan**.
4. Setelah masuk ke lobi, tekan tombol **KLIK UNTUK SIAP**.
5. Tunggu Host memulai permainan. Soal dan timer akan tersinkronisasi secara otomatis di seluruh layar!

---

## 🛠️ Persyaratan Jaringan Lokal
- Kedua/semua perangkat harus berada di subnet yang sama (contoh IP `192.168.1.x` atau `192.168.43.x`).
- Port TCP default: `8888`.
- Akses internet publik tidak diperlukan.

---

## 🧪 Pengujian yang Telah Dijalankan
- **`ExampleUnitTest`**:
  - `testQuestionGenerator_GeneratesValidOptionsAndNoDuplicates`: Memvalidasi tidak ada jawaban ganda dan jawaban benar selalu tersedia.
  - `testDivisionQuestions_AreValidIntegers`: Memvalidasi pembagian matematika menghasilkan bilangan bulat positif tanpa pembagian nol.
  - `testNetworkMessage_JsonSerialization`: Memvalidasi serialisasi dan deserialisasi JSON protokol jaringan.
  - `testQuestionClientPayload_DoesNotIncludeAnswerKey`: Memvalidasi keamanan soal agar kunci jawaban tidak bocor ke klien sebelum ronde selesai.
  - `testScoringAndTiebreakerRanking`: Memvalidasi urutan pemenang berdasarkan skor tertinggi, jumlah jawaban benar terbanyak, dan waktu tercepat.
  - `testIndonesianSpeechFormatting`: Memvalidasi konversi simbol matematika ke bahasa Indonesia lisan.
- **`ExampleRobolectricTest`**:
  - `readStringFromContext_matchesAppName`: Memvalidasi nama aplikasi `Math Battle`.
  - `testRoomDatabase_initialDataAndPersistence`: Memvalidasi inisialisasi dan persistensi pengaturan audio pada SQLite Room Database.
  - `testQuestionGenerator_allCategoriesSD`: Memvalidasi seluruh kategori soal pada kurikulum SD.

---

## 🚀 Cara Membangun APK (Build)
Jalankan perintah Gradle berikut di terminal proyek:
```bash
gradle :app:assembleDebug
```
File APK hasil build akan berlokasi di:
`app/build/outputs/apk/debug/app-debug.apk` atau `.build-outputs/app-debug.apk`
