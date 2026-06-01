package com.example.kelolajasa.model;

public class Ulasan {

    private int idUlasan;
    private int idBooking;
    private int idPengguna;

    private int rating;

    private String komentar;
    private String tanggalUlasan;

    public Ulasan(int idUlasan, int idBooking, int idPengguna, int rating, String komentar, String tanggalUlasan) {
        this.idUlasan = idUlasan;
        this.idBooking = idBooking;
        this.idPengguna = idPengguna;
        this.rating = rating;
        this.komentar = komentar;
        this.tanggalUlasan = tanggalUlasan;
    }

    public int getIdUlasan() {
        return idUlasan;
    }

    public void setIdUlasan(int idUlasan) {
        this.idUlasan = idUlasan;
    }

    public int getIdBooking() {
        return idBooking;
    }

    public void setIdBooking(int idBooking) {
        this.idBooking = idBooking;
    }

    public int getIdPengguna() {
        return idPengguna;
    }

    public void setIdPengguna(int idPengguna) {
        this.idPengguna = idPengguna;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getKomentar() {
        return komentar;
    }

    public void setKomentar(String komentar) {
        this.komentar = komentar;
    }

    public String getTanggalUlasan() {
        return tanggalUlasan;
    }

    public void setTanggalUlasan(String tanggalUlasan) {
        this.tanggalUlasan = tanggalUlasan;
    }
}
