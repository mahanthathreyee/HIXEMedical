package com.example.hixemedical;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Patient.class}, version = 1, exportSchema = false)

public abstract class PatientDatabase extends RoomDatabase {
    public abstract PatientDAO patientDAO();
}
