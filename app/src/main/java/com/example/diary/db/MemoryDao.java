package com.example.diary.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MemoryDao {

    @Query("Select * from memory")
    List<Memory> getAllMemories();

    @Query("Select * from memory where mid = :memory_id")
    Memory getMemory(int memory_id);

    @Insert
    void insertMemory(Memory... memories);

    @Delete
    void delete(Memory memory);

    @Query("UPDATE memory SET emotion = :emotion, latitude = :latitude, longitude = :longitude, title =:title, " +
            "memory =:memory  WHERE mid =:id")
    void update(String date, int emotion, String latitude, String longitude, String title, String memory, int id );

}
