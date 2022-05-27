package com.example.diary;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.diary.db.AppDatabase;
import com.example.diary.db.Memory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Memory> memories;

    ImageButton addButton;

    MyAdapter myAdapter;
    AppDatabase db;

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.e("geldi","geldi");
                    if(result.getResultCode() == 78)
                    {
                        myAdapter.setMemories(db.memoryDao().getAllMemories());
                    }
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        memories = new ArrayList<>();

        addButton = findViewById(R.id.addButton);
        recyclerView = findViewById(R.id.recyclerview);
        myAdapter = new MyAdapter(this,memories);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        addButton.setOnClickListener(view -> someActivityResultLauncher.launch(new Intent(MainActivity.this,MemoryPageActivity.class)));

        db = AppDatabase.getDbInstance(this);

        myAdapter.setMemories(db.memoryDao().getAllMemories());
    }
}