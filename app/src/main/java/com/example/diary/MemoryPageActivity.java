package com.example.diary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.diary.db.AppDatabase;
import com.example.diary.db.Memory;

public class MemoryPageActivity extends AppCompatActivity {

    TextView editTextDate, editLocation, editMainText, editTitle;

    ImageButton confirmButton,shareButton;

    View emojiSelection;
    ImageButton sadface,happyface,tiredface,angryface,lovingface;
    //tired=0,happy=1,sad=2,angry=3,loving=4;

    int emotion = 0;

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

        emojiSelection = findViewById(R.id.emojiSelection);
        sadface = findViewById(R.id.sad);
        happyface = findViewById(R.id.happy);
        tiredface = findViewById(R.id.tired);
        angryface = findViewById(R.id.angry);
        lovingface = findViewById(R.id.loving);

        getData();

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emojiSelection.setVisibility(View.VISIBLE);
            }
        });

        tiredface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishProcess(0);
            }
        });
        happyface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishProcess(1);
            }
        });
        sadface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishProcess(2);
            }
        });
        angryface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishProcess(3);
            }
        });
        lovingface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishProcess(4);
            }
        });
    }

    private void finishProcess(int emotion)
    {
        emojiSelection.setVisibility(View.GONE);
        Memory newMemory = new Memory(editTextDate.getText().toString(),emotion,editLocation.getText().toString(),
                editTitle.getText().toString(),editMainText.getText().toString());

        AppDatabase db = AppDatabase.getDbInstance(getApplicationContext());
        if(getIntent().hasExtra("id")) {
            db.memoryDao().update(newMemory.getDate(),newMemory.getEmotion(),newMemory.getLocation(),newMemory.getTitle(),
                    newMemory.getMainText(),getIntent().getIntExtra("id",0));
        }
        else {
            db.memoryDao().insertMemory(newMemory);
        }
        Intent intent = new Intent();
        setResult(78,intent);
        MemoryPageActivity.super.onBackPressed();
    }

    private void getData()
    {
        if(getIntent().hasExtra("id"))
        {
            AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());
            Memory oldMemory = db.memoryDao().getMemory(getIntent().getIntExtra("id",0));
            editTitle.setText(oldMemory.getTitle());
            editLocation.setText(oldMemory.getLocation());
            editTextDate.setText(oldMemory.getDate());
            editMainText.setText(oldMemory.getMainText());
        }
    }
}