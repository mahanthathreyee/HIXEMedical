package com.example.hixemedical;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class PatientRecyclerAdapter extends RecyclerView.Adapter<PatientRecyclerAdapter.MyViewHolder> {
    private List<Patient> patientList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView id, name, gender, diagnosis;

        public MyViewHolder(View view) {
            super(view);
            id = (TextView) view.findViewById(R.id.patient_view_id);
            name = (TextView) view.findViewById(R.id.patient_view_name);
            gender = (TextView) view.findViewById(R.id.patient_view_gender);
            diagnosis = (TextView) view.findViewById(R.id.patient_view_diagnosis);
        }
    }

    public PatientRecyclerAdapter(List<Patient> patients){
        patientList = patients;
    }

    @NonNull
    @Override
    public PatientRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.patient_recycler_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PatientRecyclerAdapter.MyViewHolder holder, int position) {
        Patient patient = patientList.get(position);
        holder.id.setText(""+patient.getPatientID());
        holder.name.setText(patient.getPatientName());
        if(patient.getMale())
            holder.gender.setText("Male");
        else
            holder.gender.setText("Female");
        holder.diagnosis.setText(patient.getDiagnosed());
    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }

}
