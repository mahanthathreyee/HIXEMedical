package com.example.hixemedical;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

public class CarerRecyclerAdapter extends RecyclerView.Adapter<CarerRecyclerAdapter.MyViewHolder>{

    private List<Carer> carerList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView username, name, gender;
        public ImageView profileImage;

        public MyViewHolder(View view) {
            super(view);
            username = (TextView) view.findViewById(R.id.recyclerUsername);
            name = (TextView) view.findViewById(R.id.recyclerName);
            gender = (TextView) view.findViewById(R.id.recyclerGender);
            profileImage = (ImageView) view.findViewById(R.id.profile_image);
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
        File f = new File(carer.getPfpImageLoc());
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),bmOptions);
        try {
            int rotate = 0;
            try {
                ExifInterface exif = new ExifInterface(carer.getPfpImageLoc());
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
        return carerList.size();
    }

}
