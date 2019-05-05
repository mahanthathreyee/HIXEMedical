package com.example.hixemedical;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class CarerRecyclerAdapter extends RecyclerView.Adapter<CarerRecyclerAdapter.MyViewHolder>{

    private List<Carer> carerList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView username, name, gender;

        public MyViewHolder(View view) {
            super(view);
            username = (TextView) view.findViewById(R.id.recyclerUsername);
            name = (TextView) view.findViewById(R.id.recyclerName);
            gender = (TextView) view.findViewById(R.id.recyclerGender);
        }
    }

    public CarerRecyclerAdapter(List<Carer> carerList){
        this.carerList = carerList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.carer_recycler_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Carer carer = carerList.get(position);
        holder.username.setText(carer.getUsername());
        holder.name.setText(carer.getName());
        if(carer.isMale())
            holder.gender.setText("Male");
        else
            holder.gender.setText("Female");
    }

    @Override
    public int getItemCount() {
        return carerList.size();
    }

}
