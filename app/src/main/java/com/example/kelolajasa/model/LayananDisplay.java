package com.example.kelolajasa.model;

public class LayananDisplay {

    private int idLayanan;
    private int idFreelancer;
    private String namaLayanan;
    private String namaFreelancer;
    private String kategori;
    private String deskripsi;
    private double harga;
    private String namaSatuan;
    private String lokasiKabupaten;
    private float rataRating;
    private int jumlahUlasan;

    public LayananDisplay(int idLayanan, int idFreelancer,
                          String namaLayanan, String namaFreelancer,
                          String kategori, String deskripsi,
                          double harga, String namaSatuan,
                          String lokasiKabupaten, float rataRating,
                          int jumlahUlasan) {
        this.idLayanan = idLayanan;
        this.idFreelancer = idFreelancer;
        this.namaLayanan = namaLayanan;
        this.namaFreelancer = namaFreelancer;
        this.kategori = kategori;
        this.deskripsi = deskripsi;
        this.harga = harga;
        this.namaSatuan = namaSatuan;
        this.lokasiKabupaten = lokasiKabupaten;
        this.rataRating = rataRating;
        this.jumlahUlasan = jumlahUlasan;
    }

    /** Format harga: Rp500.000 / Project */
    public String getHargaFormatted() {
        long h = (long) harga;
        String hargaStr = String.format("%,d", h).replace(",", ".");
        return "Rp" + hargaStr + " / " + namaSatuan;
    }

    /** Format harga ringkas: Rp500.000 */
    public String getHargaShort() {
        long h = (long) harga;
        return "Rp" + String.format("%,d", h).replace(",", ".");
    }

    /** Format rating: "4.5 (5 Ulasan)" */
    public String getRatingFormatted() {
        if (jumlahUlasan == 0) return "Belum ada ulasan";
        return String.format("%.1f (%d Ulasan)", rataRating, jumlahUlasan);
    }

    public int getIdLayanan() { return idLayanan; }
    public int getIdFreelancer() { return idFreelancer; }
    public String getNamaLayanan() { return namaLayanan; }
    public String getNamaFreelancer() { return namaFreelancer; }
    public String getKategori() { return kategori; }
    public String getDeskripsi() { return deskripsi; }
    public double getHarga() { return harga; }
    public String getNamaSatuan() { return namaSatuan; }
    public String getLokasiKabupaten() { return lokasiKabupaten != null ? lokasiKabupaten : "-"; }
    public float getRataRating() { return rataRating; }
    public int getJumlahUlasan() { return jumlahUlasan; }
}