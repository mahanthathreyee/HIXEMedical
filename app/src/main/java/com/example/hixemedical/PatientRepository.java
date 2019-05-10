package com.example.hixemedical;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

public class PatientRepository {
    private String DB_NAME = "Medical_Patient";

    private PatientDatabase patientDatabase;

    public PatientRepository(Context context){
        patientDatabase = Room.databaseBuilder(context, PatientDatabase.class, DB_NAME).build();
    }

    public void insertTask(final Context context, final Patient patient){
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                try{
                    patientDatabase.patientDAO().insertTask(patient);
                }catch (SQLiteConstraintException e){
                    return -1;
                }
                return 1;
            }

            @Override
            protected void onPostExecute(Integer success) {
                CharSequence text = "";
                if(success == -1)
                    text = "Patient already  exists";
                else if(success == 1) {
                    text = "Patient added";
                    View rootView = ((Activity) context).getWindow().getDecorView().findViewById(R.id.carerDetailsLayout);
                    EditText tempEditText;
                    tempEditText = rootView.findViewById(R.id.name);
                    tempEditText.setText("");
                    tempEditText = rootView.findViewById(R.id.password);
                    tempEditText.setText("");
                    tempEditText = rootView.findViewById(R.id.diagnosis);
                    tempEditText.setText("");
                }

                Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
                toast.show();

            }
        }.execute();
    }

    public void updateTask(final Patient patient){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                patientDatabase.patientDAO().updateTask(patient);
                return null;
            }
        }.execute();
    }

    public List<Patient>getAllPatients(int carerID) throws Exception{
        return new getDataAsyncTask(carerID).execute().get();
    }

    public Patient authorise(int patientID, String patientPassword) throws Exception{
        return patientDatabase.patientDAO().authorise(patientID, patientPassword);
    }

    public Patient getPatient(final int patientID) throws Exception{
        return new AsyncTask<Void, Void, Patient>(){
            @Override
            protected Patient doInBackground(Void... voids) {
                return patientDatabase.patientDAO().getPatient(patientID);
            }
        }.execute().get();
    }

    public void deleteTask(final Patient patient){
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                patientDatabase.patientDAO().deleteTask(patient);
                return null;
            }
        }.execute();
    }

    public class getDataAsyncTask extends AsyncTask<Void, Void, List<Patient>>{
        private int carerID;

        public getDataAsyncTask(int ID){
            carerID = ID;
        }
        @Override
        protected List<Patient> doInBackground(Void... voids) {
            return patientDatabase.patientDAO().fetchAllTasks(carerID);
        }
    }
}
