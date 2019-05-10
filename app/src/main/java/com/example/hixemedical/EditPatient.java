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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import org.w3c.dom.Text;

public class EditPatient extends AppCompatActivity {

    private PatientRepository patientRepository;

    private MenuItem logoutMenuItem;
    private MenuItem modifyMenuItem;
    private MenuItem doneMenuItem;
    private TextView patientIDText;
    private ViewSwitcher patientNameSwitcher;
    private ViewSwitcher patientGenderSwitcher;
    private ViewSwitcher patientDiagnosisSwitcher;
    private Patient patient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_patient);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        Intent intent = getIntent();
        int patientID = intent.getIntExtra("PatientID", -5);
        if(patientID == -5){
            Toast.makeText(this, "Patient Detail Error", Toast.LENGTH_LONG).show();
            Intent backIntent = new Intent(this, ViewPatient.class);
            startActivity(backIntent);
        }

        patientIDText = findViewById(R.id.edit_patient_id_text);
        patientNameSwitcher = findViewById(R.id.edit_patient_name_switcher);
        patientGenderSwitcher = findViewById(R.id.edit_patient_gender_switcher);
        patientDiagnosisSwitcher = findViewById(R.id.edit_patient_diagnosis_switcher);

        patientRepository = new PatientRepository(this);
        try{
            patient = patientRepository.getPatient(patientID);
            if(patient != null)
                fillTextViewData();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Patient Detail Error", Toast.LENGTH_LONG).show();
            Intent backIntent = new Intent(this, ViewPatient.class);
            startActivity(backIntent);
        }
    }

    private void fillTextViewData(){
        patientIDText.setText(""+patient.getPatientID());
        TextView temp = patientNameSwitcher.findViewById(R.id.edit_patient_name_text);
        temp.setText(patient.getPatientName());
        temp = patientDiagnosisSwitcher.findViewById(R.id.edit_patient_diagnosis_text);
        temp.setText(patient.getDiagnosed());
        temp = patientGenderSwitcher.findViewById(R.id.edit_patient_gender_text);
        if(patient.getMale())
            temp.setText("Male");
        else
            temp.setText("Female");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MenuItem temp;
        EditText tempEdit;
        RadioButton tempRadio;
        RadioGroup tempRadioGroup;

        switch (item.getItemId()){
            case R.id.logout_menu_item: startLogout(this);  return true;
            case R.id.modify_edit_profile:
                tempEdit = patientNameSwitcher.findViewById(R.id.edit_patient_name_edit);
                tempEdit.setHint(patient.getPatientName());
                tempRadioGroup = patientGenderSwitcher.findViewById(R.id.edit_patient_gender_group);
                if(patient.getMale()) {
                    tempRadioGroup.check(R.id.edit_patient_male);
                }else{
                    tempRadioGroup.check(R.id.edit_patient_female);
                }
                tempEdit = patientDiagnosisSwitcher.findViewById(R.id.edit_patient_diagnosis_edit);
                tempEdit.setText(patient.getDiagnosed());
                patientNameSwitcher.showNext();
                patientGenderSwitcher.showNext();
                patientDiagnosisSwitcher.showNext();
                modifyMenuItem.setVisible(false);
                logoutMenuItem.setVisible(false);
                doneMenuItem.setVisible(true);
                return true;
            case R.id.done_edit_profile:
                tempEdit = patientNameSwitcher.findViewById(R.id.edit_patient_name_edit);
                patient.setPatientName(tempEdit.getText().toString().equals("")?patient.getPatientName():tempEdit.getText().toString());
                tempRadio = patientGenderSwitcher.findViewById(R.id.edit_patient_male);
                patient.setMale(tempRadio.isChecked());
                tempEdit = patientDiagnosisSwitcher.findViewById(R.id.edit_patient_diagnosis_edit);
                patient.setDiagnosed(tempEdit.getText().toString());
                patientRepository.updateTask(patient);
                fillTextViewData();
                doneMenuItem.setVisible(false);
                modifyMenuItem.setVisible(true);
                logoutMenuItem.setVisible(true);
                patientNameSwitcher.showPrevious();
                patientGenderSwitcher.showPrevious();
                patientDiagnosisSwitcher.showPrevious();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_edit_menu, menu);
        logoutMenuItem = menu.findItem(R.id.logout_menu_item);
        modifyMenuItem = menu.findItem(R.id.modify_edit_profile);
        doneMenuItem = menu.findItem(R.id.done_edit_profile);
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
