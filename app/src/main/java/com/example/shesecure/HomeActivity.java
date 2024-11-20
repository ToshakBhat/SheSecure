package com.example.shesecure;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {
    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final int REQUEST_LOCATION_PERMISSION = 99;
    private String fileName = null;
    private MediaRecorder recorder = null;
    private MediaPlayer player = null;

    private boolean permissionToRecordAccepted = false;
    private boolean permissionToAccessLocation = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_FINE_LOCATION};

    private TextView tv_lat_lon, tv_address;
    private TextView warnings;
    private Button sos;
    

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        String numbers = (String) getIntent().getExtras().get("numbers");
        
       /* String numbers = Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("numbers")).toString();
        String[] numList = numbers.split("$");
        List<Integer> lst = new ArrayList<>();
        for(String i:numList){
            lst.add(Integer.parseInt(i));
        }
        System.out.println(""+lst);
*/

        // Initialize UI components
        tv_lat_lon = findViewById(R.id.tv_lat_lon);
        tv_address = findViewById(R.id.tv_address);
        sos = findViewById(R.id.sos);
        warnings = findViewById(R.id.warning);
        warnings.setVisibility(View.INVISIBLE);

        // Set up file path for audio recording
        fileName = getExternalCacheDir().getAbsolutePath() + "/sos_audio_record.3gp";

        // Request permissions
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        // Set up location request properties
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000 * 30);
        locationRequest.setFastestInterval(1000 * 5);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        // Set up location callback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    updateUIValues(location);
                }
            }
        };

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Set up SOS button click listener
        sos.setOnClickListener(new View.OnClickListener() {
            boolean isRecording = false;

            @Override
            public void onClick(View v) {
                warnings.setVisibility(View.VISIBLE);
                if (!isRecording) {
                    // Start location updates and recording
                    startLocationUpdates();
                    startRecording();
                    sos.setText("Stop Recording");
                    isRecording = true;
                } else {
                    // Stop recording
                    stopRecording();
                    sos.setText("Start Recording");
                    isRecording = false;
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                permissionToAccessLocation = grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED;
                break;
        }

        if (!permissionToRecordAccepted) {
            Toast.makeText(this, "Permission to record audio is required", Toast.LENGTH_LONG).show();
            finish();
        }

        if (!permissionToAccessLocation) {
            Toast.makeText(this, "Permission to access location is required", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Recording failed", e);
        }
    }

    private void stopRecording() {
        try {
            recorder.stop();
        } catch (RuntimeException stopException) {
            Log.e(LOG_TAG, "Stop recording failed", stopException);
        }
        recorder.release();
        recorder = null;

        startPlaying();
    }

    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Playing audio failed", e);
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        } else {
            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUIValues(Location location) {
        if (location != null) {
            String latLonText = "Lat: " + location.getLatitude() + ", Lon: " + location.getLongitude();
            tv_lat_lon.setText(latLonText);

            Geocoder geocoder = new Geocoder(this);
            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses != null && !addresses.isEmpty()) {
                    tv_address.setText("Address: " + addresses.get(0).getAddressLine(0));
                } else {
                    tv_address.setText("Unable to get street address");
                }
            } catch (IOException e) {
                tv_address.setText("Unable to get street address");
            }
        } else {
            tv_lat_lon.setText("Location not available");
            tv_address.setText("Address not available");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }
}
