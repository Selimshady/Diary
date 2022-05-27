package com.example.diary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.diary.db.AppDatabase;
import com.example.diary.db.Memory;

public class MemoryPageActivity extends AppCompatActivity {

    TextView editTextDate, editLocation, editMainText, editTitle;

    ImageButton confirmButton,shareButton;

    String date,location,mainText,title;

    Memory oldMemory,newMemory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_page);

        editTitle = findViewById(R.id.editTitle);
        editMainText = findViewById(R.id.editMemory);
        editLocation = findViewById(R.id.editLocation);
        editTextDate= findViewById(R.id.editTextDate);

        confirmButton = findViewById(R.id.confirmButton);
        shareButton = findViewById(R.id.shareButton);

        getData();

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Memory newMemory = new Memory(editTextDate.getText().toString(),0,editLocation.getText().toString(),
                        editTitle.getText().toString(),editMainText.getText().toString());
                AppDatabase db = AppDatabase.getDbInstance(view.getContext());
                if(oldMemory != null)
                {
                    db.memoryDao().delete(oldMemory);
                }
                db.memoryDao().insertMemory(newMemory);

                Intent intent = new Intent();
                setResult(78,intent);
                MemoryPageActivity.super.onBackPressed();
            }
        });
    }


    private void getData()
    {
        if(getIntent().hasExtra("id"))
        {
            AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());
            oldMemory = db.memoryDao().getMemory(getIntent().getIntExtra("id",0));
            editTitle.setText(oldMemory.getTitle());
            editLocation.setText(oldMemory.getLocation());
            editTextDate.setText(oldMemory.getDate());
            editMainText.setText(oldMemory.getMainText());
        }
    }
}