package com.example.hixemedical;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class ViewPatient extends AppCompatActivity {

    private boolean activitySwitch = false;
    private MenuItem musicItem = null;

    private List<Patient> patientList = new ArrayList<>();
    private RecyclerView recyclerView;
    private PatientRecyclerAdapter adapter;
    private PatientRepository patientRepository;
    private int previousSelection = -1;
    SharedPreferences sharedPreferences;
    private int carerID;

    private MenuItem deleteBtn;
    private MenuItem modifyBtn;

    boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    String imgPath = "no image selected";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_patient);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        sharedPreferences = this.getSharedPreferences("UAC", Context.MODE_PRIVATE);
        carerID = sharedPreferences.getInt("ID", -5);

        if(carerID == -5){
            startLogout(this);
            return;
        }

        patientRepository = new PatientRepository(getApplicationContext());
        recyclerView = findViewById(R.id.patientDetailsRecycler);
        adapter = new PatientRecyclerAdapter(patientList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
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

                    view.setBackgroundColor(ViewPatient.this.getResources().getColor(R.color.colorPrimary));
                }
            }
        }));

        try{
            patientList.addAll(patientRepository.getAllPatients(carerID));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    protected void onRestart() {
        patientList.clear();
        try{
            patientList.addAll(patientRepository.getAllPatients(carerID));
            adapter.notifyItemRangeChanged(previousSelection, patientList.size());
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onRestart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout_menu_item: startLogout(this);  return true;
            case R.id.backgroundMusic:
                if(BackGroundMusic.musicServiceToggle(ViewPatient.this))
                    item.setTitle("MUSIC ON");
                else
                    item.setTitle("MUSIC OFF");
                return true;
            case R.id.delete_view_carer:
                patientRepository.deleteTask(patientList.get(previousSelection));
                patientList.remove(previousSelection);
                recyclerView.removeViewAt(previousSelection);
                adapter.notifyItemRemoved(previousSelection);
                adapter.notifyItemRangeChanged(previousSelection, patientList.size());
                previousSelection = -1;
                return true;
            case R.id.modify_view_carer:
                activitySwitch = true;
                Intent intent = new Intent(ViewPatient.this, EditPatient.class);
                intent.putExtra("PatientID", patientList.get(previousSelection).getPatientID());
                startActivity(intent);
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_carer_menu, menu);
        deleteBtn = menu.findItem(R.id.delete_view_carer);
        modifyBtn = menu.findItem(R.id.modify_view_carer);
        musicItem = menu.findItem(R.id.backgroundMusic);
        updateMusicMenuItem();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        patientList.clear();
        try{
            patientList.addAll(patientRepository.getAllPatients(carerID));
            adapter.notifyItemRangeChanged(previousSelection, patientList.size());
        }catch (Exception e){
            e.printStackTrace();
        }
        activitySwitch = false;
        updateMusicMenuItem();
        super.onResume();
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
