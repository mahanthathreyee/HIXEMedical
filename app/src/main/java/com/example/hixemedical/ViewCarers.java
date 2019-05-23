package com.example.hixemedical;

import android.annotation.TargetApi;
import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class ViewCarers extends AppCompatActivity {

    private boolean activitySwitch = false;
    private MenuItem musicItem = null;

    private List<Carer> carerList = new ArrayList<>();
    private RecyclerView recyclerView;
    private CarerRecyclerAdapter adapter;
    private CarerRepository carerRepository;
    private int previousSelection = -1;
    private int switcherFlag = 0;

    private MenuItem deleteBtn;
    private MenuItem modifyBtn;
    private MenuItem modifyDoneBtn;
    private MenuItem logoutBtn;
    private ViewSwitcher usernameSwitcher;
    private ViewSwitcher nameSwitcher;
    private ImageView profileImage;

    boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    String imgPath = "no image selected";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_carers);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        carerRepository = new CarerRepository(getApplicationContext());
        recyclerView = findViewById(R.id.carerDetailsRecycler);
        adapter = new CarerRecyclerAdapter(carerList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new ClickListener() {
            @Override
            public void onClick(final View view, final int position) {
                if(switcherFlag == 1)
                    return;
                if (previousSelection == position) {
                    deleteBtn.setVisible(false);
                    modifyBtn.setVisible(false);
                    view.setBackgroundColor(Color.TRANSPARENT);
                    previousSelection = -1;
                } else {
                    if(previousSelection != -1)
                        recyclerView.findViewHolderForAdapterPosition(previousSelection).itemView.setBackgroundColor(Color.TRANSPARENT);
                    previousSelection = position;
                    Log.v("DEV LOG", ""+previousSelection);
                    deleteBtn.setVisible(true);
                    modifyBtn.setVisible(true);

                    usernameSwitcher = view.findViewById(R.id.recyclerUsernameSwitcher);
                    nameSwitcher = view.findViewById(R.id.recyclerNameSwitcher);
                    profileImage = view.findViewById(R.id.profile_image);

                    view.setBackgroundColor(ViewCarers.this.getResources().getColor(R.color.colorPrimary));
                }
            }
        }));
        try {
            carerList.addAll(carerRepository.getAllUsers());
        }catch (Exception e){
            Log.v("DEV LOG", "DB FETCH ERROR");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        EditText tempEdit;
        TextView tempText;
        switch (item.getItemId()){
            case R.id.logout_menu_item: startLogout(this);  return true;
            case R.id.backgroundMusic:
                if(BackGroundMusic.musicServiceToggle(ViewCarers.this))
                    item.setTitle("MUSIC ON");
                else
                    item.setTitle("MUSIC OFF");
                return true;
            case R.id.delete_view_carer:
                carerRepository.deleteTask(carerList.get(previousSelection));
                carerList.remove(previousSelection);
                recyclerView.removeViewAt(previousSelection);
                adapter.notifyItemRemoved(previousSelection);
                adapter.notifyItemRangeChanged(previousSelection, carerList.size());
                previousSelection = -1;
                return true;

            case R.id.modify_view_carer:
                switcherFlag = 1;
                deleteBtn.setVisible(false);
                modifyBtn.setVisible(false);
                logoutBtn.setVisible(false);
                modifyDoneBtn.setVisible(true);

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

                tempEdit = usernameSwitcher.findViewById(R.id.recyclerUsernameEdit);
                tempText = usernameSwitcher.findViewById(R.id.recyclerUsername);
                tempEdit.setHint(tempText.getText());
                tempEdit = nameSwitcher.findViewById(R.id.recyclerNameEdit);
                tempText = nameSwitcher.findViewById(R.id.recyclerName);
                tempEdit.setHint(tempText.getText());

                usernameSwitcher.showNext();
                nameSwitcher.showNext();
                return true;

            case R.id.done_view_carer:
                modifyDoneBtn.setVisible(false);
                deleteBtn.setVisible(true);
                modifyBtn.setVisible(true);
                logoutBtn.setVisible(true);

                profileImage.setClickable(false);

                tempEdit = usernameSwitcher.findViewById(R.id.recyclerUsernameEdit);
                tempText = usernameSwitcher.findViewById(R.id.recyclerUsername);
                String tempValue = tempEdit.getText().toString();
                Carer tempCarer = carerList.get(previousSelection);
                if(!tempValue.equals("")) {
                    tempCarer.setUsername(tempValue);
                    tempText.setText(tempEdit.getText());
                }
                tempEdit = nameSwitcher.findViewById(R.id.recyclerNameEdit);
                tempText = nameSwitcher.findViewById(R.id.recyclerName);
                tempValue = tempEdit.getText().toString();
                if(!tempValue.equals("")){
                    tempCarer.setName(tempValue);
                    tempText.setText(tempEdit.getText());
                }

                if(!imgPath.equals("no image selected")) {
                    tempCarer.setPfpImageLoc(imgPath);
                    setPFP(imgPath);
                }

                carerRepository.updateTask(tempCarer);

                usernameSwitcher.showPrevious();
                nameSwitcher.showPrevious();

                switcherFlag = 0;

            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_carer_menu, menu);
        modifyDoneBtn = menu.findItem(R.id.done_view_carer);
        modifyBtn = menu.findItem(R.id.modify_view_carer);
        deleteBtn = menu.findItem(R.id.delete_view_carer);
        logoutBtn = menu.findItem(R.id.logout_menu_item);;
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
    
    public interface ClickListener{
        void onClick(View view,int position);
    }

    @TargetApi(19)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null && data.getData() != null && resultCode == RESULT_OK) {
            boolean isImageFromGoogleDrive = false;
            Uri uri = data.getData();

            final ImageView profileImage = (ImageView) findViewById(R.id.profile_image);

            if (isKitKat && DocumentsContract.isDocumentUri(ViewCarers.this, uri)) {
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
                        cursor = ViewCarers.this.getContentResolver().query(contentUri, projection, null, null,
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
                        cursor = ViewCarers.this.getContentResolver().query(contentUri, projection, selection, selectionArgs, null);
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
                    cursor = ViewCarers.this.getContentResolver().query(uri, projection, null, null, null);
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

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private ClickListener clicklistener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final ClickListener clicklistener){
            this.clicklistener=clicklistener;
            gestureDetector=new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child=recycleView.findChildViewUnder(e.getX(),e.getY());
                    if(child!=null && clicklistener!=null){
                        clicklistener.onClick(child,recycleView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child=rv.findChildViewUnder(e.getX(),e.getY());
            if(child!=null && clicklistener!=null && gestureDetector.onTouchEvent(e)){
                clicklistener.onClick(child,rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
}
