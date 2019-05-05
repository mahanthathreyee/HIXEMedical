package com.example.hixemedical;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class AddCarer extends AppCompatActivity {

    private CarerRepository carerRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

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
}
