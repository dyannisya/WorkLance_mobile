package com.example.kelolajasa;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "WorkLanceSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_ID_PENGGUNA = "idPengguna";
    private static final String KEY_ID_ROLE = "idRole";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_NAMA = "namaPengguna";
    private static final String KEY_EMAIL = "email";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(int idPengguna, int idRole,
                                   String username, String namaPengguna, String email) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_ID_PENGGUNA, idPengguna);
        editor.putInt(KEY_ID_ROLE, idRole);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_NAMA, namaPengguna);
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public int getIdPengguna() {
        return pref.getInt(KEY_ID_PENGGUNA, -1);
    }

    public int getIdRole() {
        return pref.getInt(KEY_ID_ROLE, -1);
    }

    public String getUsername() {
        return pref.getString(KEY_USERNAME, "");
    }

    public String getNamaPengguna() {
        return pref.getString(KEY_NAMA, "");
    }

    public String getEmail() {
        return pref.getString(KEY_EMAIL, "");
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }

    // Konstanta role untuk dipakai di seluruh app
    public static final int ROLE_ADMIN = 1;
    public static final int ROLE_USER = 2;
    public static final int ROLE_FREELANCER = 3;
}