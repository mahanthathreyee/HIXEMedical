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
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class ViewPatientProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_patient_profile);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        SharedPreferences sharedPreferences = this.getSharedPreferences("UAC", Context.MODE_PRIVATE);
        int patientID = sharedPreferences.getInt("ID", -5);
        if (patientID == -5) {
            Toast.makeText(this, "Patient Authorisation Error", Toast.LENGTH_LONG).show();
            startLogout(this);
            return;
        }

        PatientRepository patientRepository = new PatientRepository(this);
        CarerRepository carerRepository = new CarerRepository(this);
        Patient patient;
        Carer carer;
        try {
            patient = patientRepository.getPatient(patientID);
            carer = carerRepository.getCarer(patient.getCarerID());
            TextView temp = findViewById(R.id.view_patient_profile_id);
            temp.setText(""+patient.getPatientID());
            temp = findViewById(R.id.view_patient_profile_name);
            temp.setText(patient.getPatientName());
            temp = findViewById(R.id.view_patient_profile_gender);
            if(patient.getMale())
                temp.setText("Male");
            else
                temp.setText("Female");
            temp = findViewById(R.id.view_patient_profile_carer);
            temp.setText(carer.getName());
            temp = findViewById(R.id.view_patient_profile_diagnosis);
            temp.setText(patient.getDiagnosed());
        }catch (Exception e){
            e.printStackTrace();
        }
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
