package com.example.hixemedical;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import org.w3c.dom.Text;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class EditPatient extends AppCompatActivity {

    private boolean activitySwitch = false;
    private MenuItem musicItem = null;

    private PatientRepository patientRepository;

    private MenuItem logoutMenuItem;
    private MenuItem modifyMenuItem;
    private MenuItem doneMenuItem;
    private TextView patientIDText;
    private ViewSwitcher patientNameSwitcher;
    private ViewSwitcher patientGenderSwitcher;
    private ViewSwitcher patientDiagnosisSwitcher;
    private ImageView profileImage;
    private Patient patient = null;

    boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    String imgPath = "no image selected";


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
            activitySwitch = true;
            Intent backIntent = new Intent(this, ViewPatient.class);
            startActivity(backIntent);
        }

        patientIDText = findViewById(R.id.edit_patient_id_text);
        patientNameSwitcher = findViewById(R.id.edit_patient_name_switcher);
        patientGenderSwitcher = findViewById(R.id.edit_patient_gender_switcher);
        patientDiagnosisSwitcher = findViewById(R.id.edit_patient_diagnosis_switcher);
        profileImage = findViewById(R.id.profile_image);

        patientRepository = new PatientRepository(this);
        try{
            patient = patientRepository.getPatient(patientID);
            if(patient != null)
                fillTextViewData();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Patient Detail Error", Toast.LENGTH_LONG).show();
            activitySwitch = true;
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
        setPFP(patient.getPfpImageLoc());
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
        MenuItem temp;
        EditText tempEdit;
        RadioButton tempRadio;
        RadioGroup tempRadioGroup;

        switch (item.getItemId()){
            case R.id.logout_menu_item: startLogout(this);  return true;
            case R.id.backgroundMusic:
                if(BackGroundMusic.musicServiceToggle(EditPatient.this))
                    item.setTitle("MUSIC ON");
                else
                    item.setTitle("MUSIC OFF");
                return true;
            case R.id.modify_edit_profile:
                profileImage.setClickable(true);
                profileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activitySwitch = true;
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.setType("image/*");
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                        } else {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
                        }
                    }
                });
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
                profileImage.setClickable(false);
                tempEdit = patientNameSwitcher.findViewById(R.id.edit_patient_name_edit);
                patient.setPatientName(tempEdit.getText().toString().equals("")?patient.getPatientName():tempEdit.getText().toString());
                tempRadio = patientGenderSwitcher.findViewById(R.id.edit_patient_male);
                patient.setMale(tempRadio.isChecked());
                tempEdit = patientDiagnosisSwitcher.findViewById(R.id.edit_patient_diagnosis_edit);
                patient.setDiagnosed(tempEdit.getText().toString());
                if(!imgPath.equals("no image selected"))
                    patient.setPfpImageLoc(imgPath);
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

    @TargetApi(19)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null && data.getData() != null && resultCode == RESULT_OK) {
            boolean isImageFromGoogleDrive = false;
            Uri uri = data.getData();

            final ImageView profileImage = (ImageView) findViewById(R.id.profile_image);

            if (isKitKat && DocumentsContract.isDocumentUri(EditPatient.this, uri)) {
                if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
                    String docId = DocumentsContract.getDocumentId(uri);
                    String[] split = docId.split(":");
                    String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        imgPath = Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                    else {
                        Pattern DIR_SEPORATOR = Pattern.compile("/");
                        Set<String> rv = new HashSet<>();
                        String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
                        String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
                        String rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
                        if(TextUtils.isEmpty(rawEmulatedStorageTarget))
                        {
                            if(TextUtils.isEmpty(rawExternalStorage))
                            {
                                rv.add("/storage/sdcard0");
                            }
                            else
                            {
                                rv.add(rawExternalStorage);
                            }
                        }
                        else
                        {
                            String rawUserId;
                            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
                            {
                                rawUserId = "";
                            }
                            else
                            {
                                String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                                String[] folders = DIR_SEPORATOR.split(path);
                                String lastFolder = folders[folders.length - 1];
                                boolean isDigit = false;
                                try
                                {
                                    Integer.valueOf(lastFolder);
                                    isDigit = true;
                                }
                                catch(NumberFormatException ignored)
                                {
                                }
                                rawUserId = isDigit ? lastFolder : "";
                            }
                            if(TextUtils.isEmpty(rawUserId))
                            {
                                rv.add(rawEmulatedStorageTarget);
                            }
                            else
                            {
                                rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
                            }
                        }
                        if(!TextUtils.isEmpty(rawSecondaryStoragesStr))
                        {
                            String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
                            Collections.addAll(rv, rawSecondaryStorages);
                        }
                        String[] temp = rv.toArray(new String[rv.size()]);
                        for (int i = 0; i < temp.length; i++)   {
                            File tempf = new File(temp[i] + "/" + split[1]);
                            if(tempf.exists()) {
                                imgPath = temp[i] + "/" + split[1];
                            }
                        }
                    }
                }
                else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                    String id = DocumentsContract.getDocumentId(uri);
                    Uri contentUri = ContentUris.withAppendedId( Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    Cursor cursor = null;
                    String column = "_data";
                    String[] projection = { column };
                    try {
                        cursor = EditPatient.this.getContentResolver().query(contentUri, projection, null, null,
                                null);
                        if (cursor != null && cursor.moveToFirst()) {
                            int column_index = cursor.getColumnIndexOrThrow(column);
                            imgPath = cursor.getString(column_index);
                        }
                    } finally {
                        if (cursor != null)
                            cursor.close();
                    }
                }
                else if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                    String docId = DocumentsContract.getDocumentId(uri);
                    String[] split = docId.split(":");
                    String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    String selection = "_id=?";
                    String[] selectionArgs = new String[]{ split[1] };

                    Cursor cursor = null;
                    String column = "_data";
                    String[] projection = { column };

                    try {
                        cursor = EditPatient.this.getContentResolver().query(contentUri, projection, selection, selectionArgs, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            int column_index = cursor.getColumnIndexOrThrow(column);
                            imgPath = cursor.getString(column_index);
                        }
                    } finally {
                        if (cursor != null)
                            cursor.close();
                    }
                }
                else if("com.google.android.apps.docs.storage".equals(uri.getAuthority()))   {
                    isImageFromGoogleDrive = true;
                }
            }
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                Cursor cursor = null;
                String column = "_data";
                String[] projection = { column };

                try {
                    cursor = EditPatient.this.getContentResolver().query(uri, projection, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        int column_index = cursor.getColumnIndexOrThrow(column);
                        imgPath = cursor.getString(column_index);
                    }
                }
                finally {
                    if (cursor != null)
                        cursor.close();
                }
            }
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                imgPath = uri.getPath();
            }

            if(isImageFromGoogleDrive)  {
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                    profileImage.setImageBitmap(bitmap);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else    {
                File f = new File(imgPath);
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),bmOptions);
                try {
                    int rotate = 0;
                    try {
                        ExifInterface exif = new ExifInterface(imgPath);
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
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_edit_menu, menu);
        logoutMenuItem = menu.findItem(R.id.logout_menu_item);
        modifyMenuItem = menu.findItem(R.id.modify_edit_profile);
        doneMenuItem = menu.findItem(R.id.done_edit_profile);
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
