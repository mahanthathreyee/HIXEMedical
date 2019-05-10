package com.example.hixemedical;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.util.ArrayList;
import java.util.List;

public class ViewCarers extends AppCompatActivity {

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
                Log.v("DEV LOG", ""+previousSelection);
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
            case R.id.delete_view_carer:
                carerRepository.deleteTask(carerList.get(previousSelection));
                carerList.remove(previousSelection);
                recyclerView.removeViewAt(previousSelection);
                adapter.notifyItemRemoved(previousSelection);
                adapter.notifyItemRangeChanged(previousSelection, carerList.size());
                previousSelection = -1;
                return true;

            case R.id.modify_view_carer:
                Log.v("DEV LOG", "Modify button pressed");
                switcherFlag = 1;
                deleteBtn.setVisible(false);
                modifyBtn.setVisible(false);
                logoutBtn.setVisible(false);
                modifyDoneBtn.setVisible(true);

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
        logoutBtn = menu.findItem(R.id.logout_menu_item);

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
