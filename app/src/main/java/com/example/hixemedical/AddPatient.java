package com.example.hixemedical;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class AddPatient extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        final EditText patientNameEdit = findViewById(R.id.name);
        final EditText patientPasswordEdit = findViewById(R.id.password);
        final EditText patientDiagnosisEdit = findViewById(R.id.diagnosis);
        final RadioButton patientMaleRadioBtn = findViewById(R.id.male);

        final SharedPreferences sharedPreferences = this.getSharedPreferences("UAC", Context.MODE_PRIVATE);
        final SharedPreferences.Editor sharedPreferencesEditor = this.getSharedPreferences("UAC", Context.MODE_PRIVATE).edit();

        Button submitBtn = findViewById(R.id.submitPatientDetails);

        final PatientRepository patientRepository = new PatientRepository(this);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Patient tempPatient = new Patient();
                tempPatient.setPatientName(patientNameEdit.getText().toString());
                tempPatient.setMale(patientMaleRadioBtn.isChecked());
                tempPatient.setDiagnosed(patientDiagnosisEdit.getText().toString());
                tempPatient.setPatientPassword(patientPasswordEdit.getText().toString());
                int carerID = sharedPreferences.getInt("ID", -5);

                if(carerID == -5){
                    Toast toast = Toast.makeText(getApplicationContext(), "CarerID Unauthorised", Toast.LENGTH_LONG);
                    toast.show();

                    sharedPreferencesEditor.remove("ID");
                    sharedPreferencesEditor.remove("Type");
                    sharedPreferencesEditor.apply();

                    Intent intent = new Intent(AddPatient.this, LoginActivity.class);
                    startActivity(intent);
                }

                tempPatient.setCarerID(carerID);

                patientRepository.insertTask(AddPatient.this, tempPatient);
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
