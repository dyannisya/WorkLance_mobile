package com.example.kelolajasa.model;

public class Booking {

    private int idBooking;
    private int idPengguna;
    private int idLayanan;

    private String tanggalBooking;
    private String alamatBooking;
    private String catatan;
    private String statusBooking;

    public Booking(int idBooking, int idPengguna, int idLayanan, String tanggalBooking, String alamatBooking, String catatan, String statusBooking) {
        this.idBooking = idBooking;
        this.idPengguna = idPengguna;
        this.idLayanan = idLayanan;
        this.tanggalBooking = tanggalBooking;
        this.alamatBooking = alamatBooking;
        this.catatan = catatan;
        this.statusBooking = statusBooking;
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

    public int getIdLayanan() {
        return idLayanan;
    }

    public void setIdLayanan(int idLayanan) {
        this.idLayanan = idLayanan;
    }

    public String getTanggalBooking() {
        return tanggalBooking;
    }

    public void setTanggalBooking(String tanggalBooking) {
        this.tanggalBooking = tanggalBooking;
    }

    public String getAlamatBooking() {
        return alamatBooking;
    }

    public void setAlamatBooking(String alamatBooking) {
        this.alamatBooking = alamatBooking;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public String getStatusBooking() {
        return statusBooking;
    }

    public void setStatusBooking(String statusBooking) {
        this.statusBooking = statusBooking;
    }
}
