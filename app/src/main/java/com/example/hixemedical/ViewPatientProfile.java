package com.example.hixemedical;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;

public class ViewPatientProfile extends AppCompatActivity {

    private boolean activitySwitch = false;
    private MenuItem musicItem = null;

    private ImageView profileImage;

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

        profileImage = (ImageView) findViewById(R.id.profile_image);
        profileImage.setClickable(false);

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
            setPFP(carer.getPfpImageLoc());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setPFP(String pfpPath){
        File f = new File(pfpPath);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),bmOptions);
        try {
            int rotate = 0;
            try {
                ExifInterface exif = new ExifInterface(pfpPath);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotate = 270;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotate = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotate = 90;
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Matrix matrix = new Matrix();
            matrix.postRotate(rotate);
            bitmap = Bitmap.createBitmap(bitmap , 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);  }
        catch (Exception e) {}
        profileImage.setImageBitmap(bitmap);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout_menu_item: startLogout(this);  return true;
            case R.id.backgroundMusic:
                if(BackGroundMusic.musicServiceToggle(ViewPatientProfile.this))
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
