package com.example.kelolajasa.model;

public class RiwayatDisplay {

    private int idBooking;
    private String namaFreelancer;
    private String namaLayanan;
    private double harga;
    private String namaSatuan;
    private String statusBooking;
    private String tanggalBooking;

    public RiwayatDisplay(int idBooking, String namaFreelancer, String namaLayanan,
                          double harga, String namaSatuan,
                          String statusBooking, String tanggalBooking) {
        this.idBooking = idBooking;
        this.namaFreelancer = namaFreelancer;
        this.namaLayanan = namaLayanan;
        this.harga = harga;
        this.namaSatuan = namaSatuan;
        this.statusBooking = statusBooking;
        this.tanggalBooking = tanggalBooking;
    }

    public String getHargaFormatted() {
        long h = (long) harga;
        return "Rp" + String.format("%,d", h).replace(",", ".")
                + " / " + namaSatuan;
    }

    public String getTanggalFormatted() {
        if (tanggalBooking == null || tanggalBooking.length() < 10) return "-";
        try {
            String[] p = tanggalBooking.split("-");
            String[] bln = {"","Jan","Feb","Mar","Apr","Mei",
                    "Jun","Jul","Agu","Sep","Okt","Nov","Des"};
            return p[2] + " " + bln[Integer.parseInt(p[1])] + " " + p[0];
        } catch (Exception e) { return tanggalBooking; }
    }

    public int getIdBooking() { return idBooking; }
    public String getNamaFreelancer() { return namaFreelancer; }
    public String getNamaLayanan() { return namaLayanan; }
    public double getHarga() { return harga; }
    public String getNamaSatuan() { return namaSatuan; }
    public String getStatusBooking() { return statusBooking; }
    public String getTanggalBooking() { return tanggalBooking; }
}