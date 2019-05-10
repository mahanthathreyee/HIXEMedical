package com.example.hixemedical;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface PatientDAO {
    @Insert
    Long insertTask(Patient patient);

    @Query("SELECT * FROM Patient WHERE carerID=:carerID")
    List<Patient> fetchAllTasks(int carerID);

    @Query("SELECT * FROM Patient WHERE patientID=:ID AND patientPassword=:password LIMIT 1")
    Patient authorise(int ID, String password);

    @Query("SELECT * FROM Patient WHERE patientID=:ID LIMIT 1")
    Patient getPatient(int ID);

    @Update
    void updateTask(Patient patient);


    @Delete
    void deleteTask(Patient patient);
}
