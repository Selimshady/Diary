package com.example.diary;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diary.db.AppDatabase;
import com.example.diary.db.Memory;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

public class MemoryPageActivity extends AppCompatActivity {

    TextView editTextDate, editLocation, editMainText, editTitle;

    ImageButton confirmButton,shareButton;

    View emojiSelection;
    ImageButton sadface,happyface,tiredface,angryface,lovingface;
    //tired=0,happy=1,sad=2,angry=3,loving=4;

    ConstraintLayout newPasswordLayout;
    Button enterNewPasswordButton;
    Button cancelNewPasswordButton;

    ConstraintLayout passwordLayout; //If there is password for memory
    Button enterPasswordButton;
    String password;

    int emotion = 0;

    Memory oldMemory;

    SharedPreferences sp;

    Geocoder geocoder;
    String longitude,latitude;

    public ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult( // For insert new Row
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == 18) {
                        assert result.getData() != null;
                        latitude = result.getData().getStringExtra("LATITUDE"); // engellendi null üretmeyecek
                        longitude = result.getData().getStringExtra("LONGITUDE"); // engellendi null üretmeyecek
                        try {
                            List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(latitude),Double.parseDouble(longitude),1);
                            if(addresses.size() > 0) {
                                Address address = addresses.get(0);
                                editLocation.setText(address.getCountryName() + "/" + address.getAdminArea());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if(!editLocation.getText().toString().equals(""))
                        {
                            editLocation.setEnabled(false);
                            editLocation.setInputType(InputType.TYPE_NULL);
                        }
                    }
                }
            });


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

        newPasswordLayout = findViewById(R.id.createPassword);
        enterNewPasswordButton = findViewById(R.id.enter_newPassword);
        cancelNewPasswordButton = findViewById(R.id.cancel_newPassword);
        cancelNewPasswordButton.setVisibility(View.GONE);

        passwordLayout = findViewById(R.id.password_layout);
        enterPasswordButton = findViewById(R.id.enter_button);

        geocoder = new Geocoder(this);

        sp = getApplicationContext().getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);

        if(getIntent().hasExtra("id"))
        {
            AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());
            oldMemory = db.memoryDao().getMemory(getIntent().getIntExtra("id", 0));
            password = sp.getString(String.valueOf(oldMemory.getMid()), "");

            if (!password.equals(""))
            {
                passwordLayout.setVisibility(View.VISIBLE);
                confirmButton.setVisibility(View.GONE);
                shareButton.setVisibility(View.GONE);
            }
            else
            {
                getData();
            }
        }
        else
        {
            Calendar calendar = Calendar.getInstance();
            String currentDate = DateFormat.getDateInstance().format(calendar.getTime());
            editTextDate.setText(currentDate);

            longitude = "";
            latitude = "";
        }

        confirmButton.setOnClickListener(view -> {
            if(editLocation.getText().toString().equals(""))
            {
                Toast.makeText(getApplicationContext(), "Please add a location(Double Tap)", Toast.LENGTH_SHORT).show();
            }
            else
            {
                emojiSelection.setVisibility(View.VISIBLE);
                confirmButton.setVisibility(View.GONE);
                shareButton.setVisibility(View.GONE);
            }
        });

        tiredface.setOnClickListener(view -> {
            emotion = 0;
            emojiSelection.setVisibility(View.GONE);
            newPasswordLayout.setVisibility(View.VISIBLE);
        });
        happyface.setOnClickListener(view -> {
            emotion = 1;
            emojiSelection.setVisibility(View.GONE);
            newPasswordLayout.setVisibility(View.VISIBLE);
        });
        sadface.setOnClickListener(view -> {
            emotion = 2;
            emojiSelection.setVisibility(View.GONE);
            newPasswordLayout.setVisibility(View.VISIBLE);
        });
        angryface.setOnClickListener(view -> {
            emotion = 3;
            emojiSelection.setVisibility(View.GONE);
            newPasswordLayout.setVisibility(View.VISIBLE);
        });
        lovingface.setOnClickListener(view -> {
            emotion = 4;
            emojiSelection.setVisibility(View.GONE);
            newPasswordLayout.setVisibility(View.VISIBLE);
        });

        enterNewPasswordButton.setOnClickListener(new View.OnClickListener() {
            final EditText editNewPassword = findViewById(R.id.newPassword);
            final EditText editNewPasswordAgain = findViewById(R.id.newPasswordAgain);
            @Override
            public void onClick(View view) {
                if(editNewPassword.getText().toString().equals(editNewPasswordAgain.getText().toString()))
                {
                    newPasswordLayout.setVisibility(View.GONE);
                    confirmButton.setVisibility(View.VISIBLE);
                    shareButton.setVisibility(View.VISIBLE);
                    finishProcess(emotion,editNewPasswordAgain.getText().toString());
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Passwords Do Not Match", Toast.LENGTH_SHORT).show();
                }
            }
        });


        enterPasswordButton.setOnClickListener(view -> { // Entering the app
            EditText editPassword = findViewById(R.id.edit_password);
            if(password.equals(editPassword.getText().toString()))
            {
                passwordLayout.setVisibility(View.GONE);
                confirmButton.setVisibility(View.VISIBLE);
                shareButton.setVisibility(View.VISIBLE);
                getData();
            }
        });


        shareButton.setOnClickListener(view -> {
            String memo = editTitle.getText().toString() + "\n" + editLocation.getText().toString() + " - " + editTextDate.getText().toString() + "\n" +
                    editMainText.getText().toString();
            memo += "\nSent via Diary App";
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT,memo);

            startActivity(share);
        });

        editLocation.setOnClickListener(view -> someActivityResultLauncher.launch(new Intent(MemoryPageActivity.this, MapsActivity.class)));
    }

    private void finishProcess(int emotion,String newPassword)
    {
        Memory newMemory = new Memory(editTextDate.getText().toString(),emotion,latitude,longitude,
                editTitle.getText().toString(),editMainText.getText().toString());

        SharedPreferences.Editor editor = sp.edit();
        AppDatabase db = AppDatabase.getDbInstance(getApplicationContext());

        if(getIntent().hasExtra("id")) {
            db.memoryDao().update(newMemory.getEmotion(),latitude,longitude,newMemory.getTitle(),
                    newMemory.getMainText(),getIntent().getIntExtra("id",0));
            editor.putString(String.valueOf(oldMemory.getMid()),newPassword);
            Toast.makeText(this, "Memory Updated" ,Toast.LENGTH_SHORT).show();
        }
        else {
            db.memoryDao().insertMemory(newMemory);
            List<Memory> memories = db.memoryDao().getAllMemories(); // to extract the id of most inserted memory. We would be getting it from mid if it was not new.
            editor.putString(String.valueOf(memories.get(memories.size()-1).getMid()),newPassword);
            Toast.makeText(this, "New Memory Added" ,Toast.LENGTH_SHORT).show();
        }
        editor.apply();

        Intent intent = new Intent();
        setResult(78,intent);
        MemoryPageActivity.super.onBackPressed();
    }

    @SuppressLint("SetTextI18n")
    private void getData()
    {
        editTitle.setText(oldMemory.getTitle());
        editTextDate.setText(oldMemory.getDate());
        editMainText.setText(oldMemory.getMainText());
        longitude = oldMemory.getLongitude();
        latitude = oldMemory.getLatitude();
        try {
            List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(latitude),Double.parseDouble(longitude),1);
            if(addresses.size() > 0) {
                Address address = addresses.get(0);
                editLocation.setText(address.getCountryName() + "/" + address.getAdminArea());

                if(!editLocation.getText().toString().equals(""))
                {
                    editLocation.setEnabled(false);
                    editLocation.setInputType(InputType.TYPE_NULL);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}