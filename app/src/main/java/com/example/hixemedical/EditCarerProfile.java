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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

public class EditCarerProfile extends AppCompatActivity {

    private CarerRepository carerRepository;
    private Carer carer;

    private MenuItem logoutMenuItem;
    private MenuItem modifyMenuItem;
    private MenuItem doneMenuItem;
    private TextView carerIDText;
    private ViewSwitcher carerNameSwitcher;
    private ViewSwitcher carerUsernameSwitcher;
    private ViewSwitcher carerPasswordSwitcher;
    private ViewSwitcher carerGenderSwitcher;
    
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_carer_profile);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        sharedPreferences = this.getSharedPreferences("UAC", Context.MODE_PRIVATE);
        int carerID = sharedPreferences.getInt("ID", -5);
        if(carerID == -5){
            Toast.makeText(this, "Carer Authorization Error", Toast.LENGTH_LONG).show();
            startLogout(this);
            return;
        }
        
        carerIDText = findViewById(R.id.edit_carer_id_text);
        carerNameSwitcher = findViewById(R.id.edit_carer_name_switcher);
        carerUsernameSwitcher = findViewById(R.id.edit_carer_username_switcher);
        carerPasswordSwitcher = findViewById(R.id.edit_carer_password_switcher);
        carerGenderSwitcher = findViewById(R.id.edit_carer_gender_switcher);
        
        carerRepository = new CarerRepository(this);
        try{
            carer = carerRepository.getCarer(carerID);
            if(carer == null)
                throw new Exception("Carer NULL "+carerID);
            fillTextViewData();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "carer Detail Error", Toast.LENGTH_LONG).show();
            Intent backIntent = new Intent(this, CarerMenu.class);
            startActivity(backIntent);
        }
    }

    private void fillTextViewData(){
        carerIDText.setText(Integer.toString(carer.getId()));
        TextView temp = carerNameSwitcher.findViewById(R.id.edit_carer_name_text);
        temp.setText(carer.getName());
        temp = carerUsernameSwitcher.findViewById(R.id.edit_carer_username_text);
        temp.setText(carer.getUsername());
        temp = carerPasswordSwitcher.findViewById(R.id.edit_carer_password_text);
        temp.setText(carer.getPassword());
        temp = carerGenderSwitcher.findViewById(R.id.edit_carer_gender_text);
        if(carer.isMale())
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
                tempEdit = carerNameSwitcher.findViewById(R.id.edit_carer_name_edit);
                tempEdit.setHint(carer.getName());
                tempEdit = carerUsernameSwitcher.findViewById(R.id.edit_carer_username_edit);
                tempEdit.setHint(carer.getUsername());
                tempRadioGroup = carerGenderSwitcher.findViewById(R.id.edit_carer_gender_group);
                if(carer.isMale()) {
                    tempRadioGroup.check(R.id.edit_carer_male);
                }else{
                    tempRadioGroup.check(R.id.edit_carer_female);
                }
                tempEdit = carerPasswordSwitcher.findViewById(R.id.edit_carer_password_edit);
                tempEdit.setHint(carer.getPassword());
                carerNameSwitcher.showNext();
                carerGenderSwitcher.showNext();
                carerUsernameSwitcher.showNext();
                carerPasswordSwitcher.showNext();
                modifyMenuItem.setVisible(false);
                logoutMenuItem.setVisible(false);
                doneMenuItem.setVisible(true);
                return true;
            case R.id.done_edit_profile:
                tempEdit = carerNameSwitcher.findViewById(R.id.edit_carer_name_edit);
                carer.setName(tempEdit.getText().toString().equals("")?carer.getName():tempEdit.getText().toString());
                tempEdit = carerUsernameSwitcher.findViewById(R.id.edit_carer_username_edit);
                carer.setUsername(tempEdit.getText().toString().equals("")?carer.getUsername():tempEdit.getText().toString());
                tempRadio = carerGenderSwitcher.findViewById(R.id.edit_carer_male);
                carer.setMale(tempRadio.isChecked());
                tempEdit = carerPasswordSwitcher.findViewById(R.id.edit_carer_password_edit);
                carer.setPassword(tempEdit.getText().toString().equals("")?carer.getPassword():tempEdit.getText().toString());
                carerRepository.updateTask(carer);
                fillTextViewData();
                doneMenuItem.setVisible(false);
                modifyMenuItem.setVisible(true);
                logoutMenuItem.setVisible(true);
                carerNameSwitcher.showPrevious();
                carerGenderSwitcher.showPrevious();
                carerUsernameSwitcher.showPrevious();
                carerPasswordSwitcher.showPrevious();
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
