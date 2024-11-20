package com.example.shesecure;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class ContactsActivity extends AppCompatActivity {

    private EditText contact1EditText, contact2EditText, contact3EditText, contact4EditText;
    private Button saveButton;
    private ArrayList<String> emergencyContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        // Initialize views
        contact1EditText = findViewById(R.id.contact1);
        contact2EditText = findViewById(R.id.contact2);
        contact3EditText = findViewById(R.id.contact3);
        contact4EditText = findViewById(R.id.contact4);
        saveButton = findViewById(R.id.saveButton);

        emergencyContacts = new ArrayList<>();

        // Handle saving contacts
        saveButton.setOnClickListener(v -> {
            String contact1 = contact1EditText.getText().toString();
            String contact2 = contact2EditText.getText().toString();
            String contact3 = contact3EditText.getText().toString();
            String contact4 = contact4EditText.getText().toString();

            if (validateContacts(contact1, contact2, contact3, contact4)) {
                emergencyContacts.add(contact1);
                emergencyContacts.add(contact2);
                emergencyContacts.add(contact3);
                emergencyContacts.add(contact4);
                String nums = contact1 + "$" + contact2 + "$" + contact3 + "$" + contact4;
                Toast.makeText(ContactsActivity.this, "Contacts saved successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ContactsActivity.this,HomeActivity.class);
                intent.putExtra("numbers",nums);
                startActivity(intent);
                finish();
                // You can now proceed to use this ArrayList as needed
            }
        });
    }

    private boolean validateContacts(String... contacts) {
        for (String contact : contacts) {
            if (TextUtils.isEmpty(contact) || contact.length() != 10) {
                Toast.makeText(this, "Please enter a valid 10-digit phone number for all contacts", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }
}
