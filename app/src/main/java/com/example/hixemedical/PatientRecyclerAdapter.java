package com.example.hixemedical;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

public class PatientRecyclerAdapter extends RecyclerView.Adapter<PatientRecyclerAdapter.MyViewHolder> {
    private List<Patient> patientList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView id, name, gender, diagnosis;
        public ImageView profileImage;

        public MyViewHolder(View view) {
            super(view);
            id = (TextView) view.findViewById(R.id.patient_view_id);
            name = (TextView) view.findViewById(R.id.patient_view_name);
            gender = (TextView) view.findViewById(R.id.patient_view_gender);
            diagnosis = (TextView) view.findViewById(R.id.patient_view_diagnosis);
            profileImage = (ImageView) view.findViewById(R.id.profile_image);
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
        File f = new File(patient.getPfpImageLoc());
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),bmOptions);
        try {
            int rotate = 0;
            try {
                ExifInterface exif = new ExifInterface(patient.getPfpImageLoc());
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
        holder.profileImage.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }

}
