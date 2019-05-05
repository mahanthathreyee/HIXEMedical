package com.example.hixemedical;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AdminMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);

        Button addCarer = findViewById(R.id.admin_menu_add_carer_btn);
        Button viewCarer = findViewById(R.id.admin_menu_view_carer_btn);
        Button logout = findViewById(R.id.admin_menu_logout_btn);

        addCarer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMenu.this, AddCarer.class);
                startActivity(intent);
            }
        });
        viewCarer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMenu.this, ViewCarers.class);
                startActivity(intent);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMenu.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
