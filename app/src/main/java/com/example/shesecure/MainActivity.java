package com.example.shesecure;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_AADHAR_CARD = 1;
    private static final int REQUEST_CODE_SELFIE = 2;

    private EditText aadharNumberEditText;
    private Button uploadAadharCardButton, openSelfieCameraButton, registerButton;
    private ImageView uploadedAadharCardImageView;
    private Uri aadharCardUri;
    private Bitmap selfieBitmap;
    private ImageView aadharPreview;
    private ImageView cameraPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        aadharNumberEditText = findViewById(R.id.aadhar_number);
        uploadAadharCardButton = findViewById(R.id.upload_aadhar_card_button);
        openSelfieCameraButton = findViewById(R.id.open_selfie_camera_button);
        registerButton = findViewById(R.id.register_button);
        uploadedAadharCardImageView = findViewById(R.id.imageView2);
        aadharPreview = findViewById(R.id.aadhar_preview);
        cameraPreview = findViewById(R.id.camera_preview);
        aadharPreview.setVisibility(ImageView.GONE);
        cameraPreview.setVisibility(ImageView.GONE);

        // Handle Aadhar card photo upload
        uploadAadharCardButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_CODE_AADHAR_CARD);
        });

        // Handle opening selfie camera
        openSelfieCameraButton.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_CODE_SELFIE);
        });

        // Handle registration
        registerButton.setOnClickListener(v -> {
            String aadharNumber = aadharNumberEditText.getText().toString();

            if (TextUtils.isEmpty(aadharNumber) || aadharNumber.length() != 12) {
                Toast.makeText(MainActivity.this, "Please enter a valid 12-digit Aadhar number", Toast.LENGTH_SHORT).show();
            } else if (aadharCardUri == null) {
                Toast.makeText(MainActivity.this, "Please upload your Aadhar card photo", Toast.LENGTH_SHORT).show();
            } else if (selfieBitmap == null) {
                Toast.makeText(MainActivity.this, "Please take a selfie", Toast.LENGTH_SHORT).show();
            } else {
                // Handle the registration process
                Toast.makeText(MainActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                // You can proceed to save data to your database or server
                Intent intent = new Intent(MainActivity.this,ContactsActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_AADHAR_CARD && data != null) {
                // Handle the Aadhar card photo
                aadharCardUri = data.getData();
                aadharPreview.setImageURI(aadharCardUri);
                aadharPreview.setVisibility(ImageView.VISIBLE);
                Toast.makeText(this, "Aadhar card photo uploaded", Toast.LENGTH_SHORT).show();
            } else if (requestCode == REQUEST_CODE_SELFIE && data != null) {
                // Handle the selfie photo
                selfieBitmap = (Bitmap) data.getExtras().get("data");
                cameraPreview.setImageBitmap(selfieBitmap);
                cameraPreview.setVisibility(ImageView.VISIBLE);
                Toast.makeText(this, "Selfie taken", Toast.LENGTH_SHORT).show();
                // Save selfieBitmap to a file or upload it as needed
            }
        }
    }
}
