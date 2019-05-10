package com.example.hixemedical;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class AddCarer extends AppCompatActivity {

    private CarerRepository carerRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_carer);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        final EditText usernameText = (EditText) findViewById(R.id.username);
        final EditText nameText = (EditText) findViewById(R.id.name);
        final EditText passwordText = (EditText) findViewById(R.id.password);

        Button submitBtn = (Button) findViewById(R.id.submitCarerDetails);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = usernameText.getText().toString();
                String name = nameText.getText().toString();
                RadioButton male = (RadioButton) findViewById(R.id.male);
                RadioButton female = (RadioButton) findViewById(R.id.female);
                String password = passwordText.getText().toString();

                if(!TextUtils.isEmpty(username)
                    && !TextUtils.isEmpty(name)
                    && !TextUtils.isEmpty(password)
                    && (male.isChecked() || female.isChecked())){
                    Carer carer = new Carer();
                    carer.setUsername(username);
                    carer.setName(name);
                    carer.setMale(male.isChecked());
                    carer.setPassword(password);

                    carerRepository = new CarerRepository(getApplicationContext());
                    carerRepository.insertTask(AddCarer.this, carer);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout_menu_item: startLogout(this);  return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
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
