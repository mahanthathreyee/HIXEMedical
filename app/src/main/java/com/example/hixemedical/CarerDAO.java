package com.example.hixemedical;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface CarerDAO {
    @Insert
    Long insertTask(Carer carer);

    @Query("SELECT * FROM Carer")
    List<Carer> fetchAllTasks();

    @Query("SELECT * FROM Carer WHERE username=:carerUsername AND password=:carerPassword")
    Carer authorise(String carerUsername, String carerPassword);

    @Query("SELECT * FROM Carer WHERE id=:carerID")
    Carer getCarer(int carerID);

    @Update
    void updateTask(Carer carer);


    @Delete
    void deleteTask(Carer carer);
}
