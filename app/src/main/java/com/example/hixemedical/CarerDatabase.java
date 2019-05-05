package com.example.hixemedical;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Carer.class}, version = 1, exportSchema = false)

public abstract class CarerDatabase  extends RoomDatabase{
    public abstract CarerDAO carerDAO();
}