package com.example.diary;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import android.widget.Toast;

import com.example.diary.db.AppDatabase;
import com.example.diary.db.Memory;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Memory> memories;

    ImageButton addButton;

    MyAdapter myAdapter;
    AppDatabase db;


    String password;

    ConstraintLayout passwordLayout; //If there is password for app
    Button enterPasswordButton;

    ImageButton createNewLockButton; //If add new lock to app.
    ConstraintLayout newPasswordLayout;
    Button enterNewPasswordButton;
    Button cancelNewPasswordButton;

    public ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult( // For insert new Row
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == 78) {
                        Log.i("Info:", "Inserted");
                        myAdapter.setMemories(db.memoryDao().getAllMemories());
                    }
                }
            });

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { // For Update View
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 79) {
            Log.e("Info:", "Updated");
            myAdapter.setMemories(db.memoryDao().getAllMemories());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getDbInstance(this);

        memories = new ArrayList<>();

        addButton = findViewById(R.id.addButton);
        recyclerView = findViewById(R.id.recyclerview);


        passwordLayout = findViewById(R.id.password_layout);
        enterPasswordButton = findViewById(R.id.enter_button);


        newPasswordLayout = findViewById(R.id.createPassword);
        enterNewPasswordButton = findViewById(R.id.enter_newPassword);
        cancelNewPasswordButton = findViewById(R.id.cancel_newPassword);

        createNewLockButton = findViewById(R.id.lockButton);


        myAdapter = new MyAdapter(MainActivity.this, this, memories);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        addButton.setOnClickListener(view -> someActivityResultLauncher.launch(new Intent(MainActivity.this, MemoryPageActivity.class)));

        SharedPreferences sp = getApplicationContext().getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
        password = sp.getString("main","");

        if(!password.equals(""))
        {
            passwordLayout.setVisibility(View.VISIBLE);
            createNewLockButton.setVisibility(View.GONE);
            addButton.setVisibility(View.GONE);
        }
        else
        {
            myAdapter.setMemories(memories = db.memoryDao().getAllMemories());
            if (memories.isEmpty()) {
                Toast.makeText(this, "No Memory Recorded Yet", Toast.LENGTH_SHORT).show();
            }
        }

        enterPasswordButton.setOnClickListener(view -> { // Entering the app
            EditText editPassword = findViewById(R.id.edit_password);
            if(password.equals(editPassword.getText().toString()))
            {
                passwordLayout.setVisibility(View.GONE);
                myAdapter.setMemories(memories = db.memoryDao().getAllMemories());
                if (memories.isEmpty()) {
                    Toast.makeText(this, "No Memory Recorded Yet", Toast.LENGTH_SHORT).show();
                }
                createNewLockButton.setVisibility(View.VISIBLE);
                addButton.setVisibility(View.VISIBLE);
            }
        });


        // Creating new password
        createNewLockButton.setOnClickListener(view -> newPasswordLayout.setVisibility(View.VISIBLE));


        enterNewPasswordButton.setOnClickListener(new View.OnClickListener() { // Creating new password Entering!!
            final EditText editNewPassword = findViewById(R.id.newPassword);
            final EditText editNewPasswordAgain = findViewById(R.id.newPasswordAgain);
            @Override
            public void onClick(View view) {
                if(editNewPassword.getText().toString().equals(editNewPasswordAgain.getText().toString()))
                {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("main",editNewPassword.getText().toString());
                    newPasswordLayout.setVisibility(View.GONE);
                    editor.apply();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Passwords Do Not Match", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelNewPasswordButton.setOnClickListener(new View.OnClickListener() {
            final EditText editNewPassword = findViewById(R.id.newPassword);
            final EditText editNewPasswordAgain = findViewById(R.id.newPasswordAgain);
            @Override
            public void onClick(View view) {
                newPasswordLayout.setVisibility(View.GONE);
                editNewPassword.setText("");
                editNewPasswordAgain.setText("");
            }
        });

    }
}
