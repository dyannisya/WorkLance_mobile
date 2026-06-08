package com.example.kelolajasa.model;

public class BookingDisplay {

    private int idBooking;
    private String namaClient;
    private String namaJasa;
    private String statusBooking;
    private String tanggalBooking;

    public BookingDisplay(int idBooking, String namaClient,
                          String namaJasa, String statusBooking,
                          String tanggalBooking) {
        this.idBooking = idBooking;
        this.namaClient = namaClient;
        this.namaJasa = namaJasa;
        this.statusBooking = statusBooking;
        this.tanggalBooking = tanggalBooking;
    }

    public int getIdBooking() { return idBooking; }
    public String getNamaClient() { return namaClient; }
    public String getNamaJasa() { return namaJasa; }
    public String getStatusBooking() { return statusBooking; }
    public String getTanggalBooking() { return tanggalBooking; }
}