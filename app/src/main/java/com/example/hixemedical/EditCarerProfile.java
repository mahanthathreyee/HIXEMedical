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

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

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
    private ImageView profileImage;
    
    SharedPreferences sharedPreferences;

    boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    String imgPath = "no image selected";

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


        profileImage = (ImageView) findViewById(R.id.profile_image);
        profileImage.setClickable(false);
        
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
        setPFP(carer.getPfpImageLoc());
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
                profileImage.setClickable(true);
                profileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                profileImage.setClickable(false);
                tempEdit = carerNameSwitcher.findViewById(R.id.edit_carer_name_edit);
                carer.setName(tempEdit.getText().toString().equals("")?carer.getName():tempEdit.getText().toString());
                tempEdit = carerUsernameSwitcher.findViewById(R.id.edit_carer_username_edit);
                carer.setUsername(tempEdit.getText().toString().equals("")?carer.getUsername():tempEdit.getText().toString());
                tempRadio = carerGenderSwitcher.findViewById(R.id.edit_carer_male);
                carer.setMale(tempRadio.isChecked());
                tempEdit = carerPasswordSwitcher.findViewById(R.id.edit_carer_password_edit);
                carer.setPassword(tempEdit.getText().toString().equals("")?carer.getPassword():tempEdit.getText().toString());
                if(!imgPath.equals("no image selected"))
                    carer.setPfpImageLoc(imgPath);
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

    @TargetApi(19)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null && data.getData() != null && resultCode == RESULT_OK) {
            boolean isImageFromGoogleDrive = false;
            Uri uri = data.getData();

            final ImageView profileImage = (ImageView) findViewById(R.id.profile_image);

            if (isKitKat && DocumentsContract.isDocumentUri(EditCarerProfile.this, uri)) {
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
                        cursor = EditCarerProfile.this.getContentResolver().query(contentUri, projection, null, null,
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
                        cursor = EditCarerProfile.this.getContentResolver().query(contentUri, projection, selection, selectionArgs, null);
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
                    cursor = EditCarerProfile.this.getContentResolver().query(uri, projection, null, null, null);
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
