package com.example.diary;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diary.db.AppDatabase;
import com.example.diary.db.Memory;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MemoryPageActivity extends AppCompatActivity {

    TextView editTextDate, editLocation, editMainText, editTitle;
    Button editButtonLocation;
    ImageButton confirmButton,shareButton,pdfConverterButton;

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


    // constant code for runtime permissions
    private static final int PERMISSION_REQUEST_CODE = 200;
    int pageHeight = 900;
    int pagewidth = 600;


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
                            editButtonLocation.setEnabled(false);
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
        editButtonLocation = findViewById(R.id.editButtonLocation);

        confirmButton = findViewById(R.id.confirmButton);
        shareButton = findViewById(R.id.shareButton);
        pdfConverterButton = findViewById(R.id.pdfConverter);

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

        editButtonLocation.setOnClickListener(view -> someActivityResultLauncher.launch(new Intent(MemoryPageActivity.this, MapsActivity.class)));

        pdfConverterButton.setOnClickListener(view -> {
            if (checkPermission()) {
                Toast.makeText(view.getContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                generatePDF();
            } else {
                requestPermission();
            }
        });
    }

    private void generatePDF() {
        // creating an object variable
        // for our PDF document.
        PdfDocument pdfDocument = new PdfDocument();

        // two variables for paint "paint" is used
        // for drawing shapes and we will use "title"
        // for adding text in our PDF file.
        Paint title = new Paint();

        // we are adding page info to our PDF file
        // in which we will be passing our pageWidth,
        // pageHeight and number of pages and after that
        // we are calling it to create our PDF.
        PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(pagewidth, pageHeight, 1).create();

        // below line is used for setting
        // start page for our PDF file.
        PdfDocument.Page myPage = pdfDocument.startPage(mypageInfo);

        // creating a variable for canvas
        // from our page of PDF.
        Canvas canvas = myPage.getCanvas();


        // below line is used for adding typeface for
        // our text which we will be adding in our PDF file.
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

        // below line is used for setting text size
        // which we will be displaying in our PDF file.
        title.setTextSize(15);

        // below line is sued for setting color
        // of our text inside our PDF file.
        title.setColor(ContextCompat.getColor(this, R.color.black));

        // below line is used to draw text in our PDF file.
        // the first parameter is our text, second parameter
        // is position from start, third parameter is position from top
        // and then we are passing our variable of paint which is title.
        canvas.drawText(editTitle.getText().toString(), 250, 100, title);
        canvas.drawText(editTextDate.getText().toString(),400,125,title);
        canvas.drawText(editLocation.getText().toString(), 400, 150, title);

        // similarly we are creating another text and in this
        // we are aligning this text to center of our PDF file.
        title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        title.setColor(ContextCompat.getColor(this, R.color.purple_200));
        title.setTextSize(15);

        // below line is used for setting
        // our text to center of PDF.
        title.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(editMainText.getText().toString(), 50, 200, title);

        // after adding all attributes to our
        // PDF file we will be finishing our page.
        pdfDocument.finishPage(myPage);

        // below line is used to set the name of
        // our PDF file and its path.
        String top = editTitle.getText().toString() + ".pdf";
        if(top.equals(""))
            top = "empty.pdf";
        File file = new File(getExternalFilesDir(null), top);

        try {
            // after creating a file name we will
            // write our PDF file to that location.
            pdfDocument.writeTo(new FileOutputStream(file));
            Log.e("geldi","geldi");

            // below line is to print toast message
            // on completion of PDF generation.
            Toast.makeText(this, "PDF file generated successfully.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            // below line is used
            // to handle error
            e.printStackTrace();
        }
        // after storing our pdf to that
        // location we are closing our PDF file.
        pdfDocument.close();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {

                // after requesting permissions we are showing
                // users a toast message of permission granted.
                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage) {
                    Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denined.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    private void requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    private boolean checkPermission() {
        // checking of permissions.
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
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
                    editButtonLocation.setEnabled(false);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}