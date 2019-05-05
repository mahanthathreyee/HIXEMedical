package com.example.hixemedical;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_carers);

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
                final Button deleteBtn = findViewById(R.id.view_carer_delete_btn);
                final Button modifyBtn = findViewById(R.id.view_carer_modify_btn);
                final Button modifyDoneBtn = findViewById(R.id.view_carer_modify_done_btn);

                final ViewSwitcher usernameSwitcher = view.findViewById(R.id.recyclerUsernameSwitcher);
                final ViewSwitcher nameSwitcher = view.findViewById(R.id.recyclerNameSwitcher);

                if (previousSelection == position) {
                    deleteBtn.setVisibility(View.GONE);
                    modifyBtn.setVisibility(View.GONE);
                    view.setBackgroundColor(ViewCarers.this.getResources().getColor(R.color.whiteText));
                    previousSelection = -1;
                } else {
                    if(previousSelection != -1)
                        recyclerView.findViewHolderForAdapterPosition(previousSelection).itemView.setBackgroundColor(ViewCarers.this.getResources().getColor(R.color.whiteText));
                    previousSelection = position;
                    deleteBtn.setVisibility(View.VISIBLE);
                    modifyBtn.setVisibility(View.VISIBLE);

                    final int tempPosition = position;
                    deleteBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            carerRepository.deleteTask(carerList.get(tempPosition));
                            carerList.remove(tempPosition);
                            recyclerView.removeViewAt(tempPosition);
                            adapter.notifyItemRemoved(tempPosition);
                            adapter.notifyItemRangeChanged(position, carerList.size());
                            previousSelection = -1;
                        }
                    });
                    modifyBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            switcherFlag = 1;
                            deleteBtn.setVisibility(View.GONE);
                            modifyBtn.setVisibility(View.GONE);
                            modifyDoneBtn.setVisibility(View.VISIBLE);

                            EditText tempEdit = usernameSwitcher.findViewById(R.id.recyclerUsernameEdit);
                            TextView tempText = usernameSwitcher.findViewById(R.id.recyclerUsername);
                            tempEdit.setHint(tempText.getText());
                            tempEdit = nameSwitcher.findViewById(R.id.recyclerNameEdit);
                            tempText = nameSwitcher.findViewById(R.id.recyclerName);
                            tempEdit.setHint(tempText.getText());

                            usernameSwitcher.showNext();
                            nameSwitcher.showNext();

                            modifyDoneBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    modifyDoneBtn.setVisibility(View.GONE);
                                    deleteBtn.setVisibility(View.VISIBLE);
                                    modifyBtn.setVisibility(View.VISIBLE);

                                    EditText tempEdit = usernameSwitcher.findViewById(R.id.recyclerUsernameEdit);
                                    TextView tempText = usernameSwitcher.findViewById(R.id.recyclerUsername);
                                    String tempValue = tempEdit.getText().toString();
                                    Carer tempCarer = carerList.get(tempPosition);
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
                                }
                            });
                        }
                    });
                    view.setBackgroundColor(ViewCarers.this.getResources().getColor(R.color.design_default_color_primary));
                }
            }
        }));
        try {
            carerList.addAll(carerRepository.getAllUsers());
        }catch (Exception e){
            Log.v("DEV LOG", "DB FETCH ERROR");
        }
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
