package com.example.hixemedical;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class CarerRepository {
    private String DB_NAME = "Medical";

    private CarerDatabase carerDatabase;

    public CarerRepository(Context context){
        carerDatabase = Room.databaseBuilder(context, CarerDatabase.class, DB_NAME).build();
    }

    public void insertTask(final Context context, final Carer carer){
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids){
                try {
                    carerDatabase.carerDAO().insertTask(carer);
                }catch(SQLiteConstraintException e){
                    return -1;
                }
                return 1;
            }

            @Override
            protected void onPostExecute(Integer success) {
                CharSequence text = "";
                if(success == -1)
                    text = "Carer already  exists";
                else if(success == 1) {
                    text = "Carer added";
                    View rootView = ((Activity) context).getWindow().getDecorView().findViewById(R.id.carerDetailsLayout);
                    EditText tempEditText;
                    tempEditText = rootView.findViewById(R.id.username);
                    tempEditText.setText("");
                    tempEditText = rootView.findViewById(R.id.name);
                    tempEditText.setText("");
                    tempEditText = rootView.findViewById(R.id.password);
                    tempEditText.setText("");
                }

                Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
                toast.show();
            }
        }.execute();
    }

    public void updateTask(final Carer carer) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                carerDatabase.carerDAO().updateTask(carer);
                return null;
            }
        }.execute();
    }

    public List<Carer> getAllUsers() throws Exception{
        return new getDataAsyncTask().execute().get();
    }

    public Carer authorise(String carerUsername, String carerPassword) throws Exception{
        return carerDatabase.carerDAO().authorise(carerUsername, carerPassword);
    }

    public void deleteTask(final Carer carer) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                carerDatabase.carerDAO().deleteTask(carer);
                return null;
            }
        }.execute();
    }

    public class getDataAsyncTask extends AsyncTask<Void, Void, List<Carer>>{
        @Override
        protected List<Carer> doInBackground(Void... voids) {
            return carerDatabase.carerDAO().fetchAllTasks();
        }
    }

    public String SHA256(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
