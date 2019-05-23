package com.example.hixemedical;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

public class AdminMenu extends AppCompatActivity {

    private boolean activitySwitch = false;
    private MenuItem musicItem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        Button addCarer = findViewById(R.id.admin_menu_add_carer_btn);
        Button viewCarer = findViewById(R.id.admin_menu_view_carer_btn);

        addCarer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMenu.this, AddCarer.class);
                activitySwitch = true;
                startActivity(intent);
            }
        });
        viewCarer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMenu.this, ViewCarers.class);
                activitySwitch = true;
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout_menu_item: startLogout(this);  return true;
            case R.id.backgroundMusic:
                if(BackGroundMusic.musicServiceToggle(AdminMenu.this))
                    item.setTitle("MUSIC ON");
                else
                    item.setTitle("MUSIC OFF");
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        musicItem = menu.findItem(R.id.backgroundMusic);
        updateMusicMenuItem();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        activitySwitch = false;
        updateMusicMenuItem();
    }

    private void updateMusicMenuItem(){
        if(musicItem == null)
            return;
        if(BackGroundMusic.getMusicStatus())
            musicItem.setTitle("MUSIC ON");
        else
            musicItem.setTitle("MUSIC OFF");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!activitySwitch && !this.isFinishing())
            BackGroundMusic.iAmLeaving();
    }

    private void startLogout(Activity activity){
        SharedPreferences.Editor editor = activity.getSharedPreferences("UAC", Context.MODE_PRIVATE).edit();
        editor.remove("ID");
        editor.remove("Type");
        editor.apply();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}