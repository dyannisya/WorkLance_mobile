package com.example.kelolajasa;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class CustomerChatActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Menghubungkan ke file customer_chat.xml yang berisi desain chat dari kamu
        setContentView(R.layout.chat_customer);
    }
}