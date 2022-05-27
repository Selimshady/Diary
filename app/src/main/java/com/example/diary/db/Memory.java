package com.example.diary.db;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity
public class Memory {
    @PrimaryKey(autoGenerate = true)
    int mid;

    @ColumnInfo(name = "Date")
    String date;
    @ColumnInfo(name = "Emotion")
    int emotion;
    @ColumnInfo(name = "Location")
    String location;
    @ColumnInfo(name = "Title")
    String title;
    @ColumnInfo(name = "Memory")
    String mainText;

    public Memory(String date, int emotion, String location, String title, String mainText) {
        this.date = date;
        this.emotion = emotion;
        this.location = location;
        this.title = title;
        this.mainText = mainText;
    }

    public int getMid() { return  mid;}

    public String getDate() { return date; }

    public void setDate(String date) {
        this.date = date;
    }

    public int getEmotion() {
        return emotion;
    }

    public void setEmotion(int emotion) {
        this.emotion = emotion;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMainText() {
        return mainText;
    }

    public void setMainText(String mainText) {
        this.mainText = mainText;
    }
}
