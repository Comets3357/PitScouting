package com.example.pitscoutingapp2023;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pitscoutingapp2023.common.Team;
import com.example.pitscoutingapp2023.common.TeamPitScout;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class QrCodeActivity extends AppCompatActivity {

    private ImageView qrCodeIV;
    private Button returnToSettings;
    private Button goForwardButton;
    private Button goBackButton;
    private Spinner teams;
    AppDatabase db;

    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_transfer);
        db = AppDatabase.getDatabase(getApplicationContext());
        this.qrCodeIV = (ImageView) findViewById(R.id.idIVQrcode);
        // The data that the QR code will contain
        returnToSettings = (Button) findViewById(R.id.returnToSettingsButton);

        returnToSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToSettings();
            }
        });
        goForwardButton = (Button) findViewById(R.id.nextQrTeamButton);

        goForwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNextTeam();
            }
        });

        goBackButton = (Button) findViewById(R.id.buttonMoveBackTeam);

        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackATeam();
            }
        });

        this.teams = (Spinner) findViewById(R.id.qrCodeActiveTeamSpinner);
        this.teams.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                setQRCode();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        setActiveEventKeys();
    }

    public void setQRCode() {
        String activeTeam = this.teams.getSelectedItem().toString();
        String data = "";
        Gson gson = new Gson();
        data = gson.toJson(db.teamPitScoutDao().getTeamAtEvent(activeTeam, db.activeEventKeyDao().getActiveEventKey()));
        // The path where the image will get saved
        String path = "activeQRTeam.png";
        // Encoding charset
        String charset = "UTF-8";
        Map<EncodeHintType, ErrorCorrectionLevel> hashMap
                = new HashMap<EncodeHintType,
                ErrorCorrectionLevel>();
        hashMap.put(EncodeHintType.ERROR_CORRECTION,
                ErrorCorrectionLevel.L);
        qrCodeIV.setImageBitmap(generateQRImage(data));
    }



    public Bitmap generateQRImage(final String content) {
        Map<EncodeHintType, ErrorCorrectionLevel> hints = new HashMap<>();

        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 512, 512, hints); //OG 512

            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();

            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {

                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }


            return bmp;
        } catch (WriterException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void returnToSettings() {
        finish();
    }

    public String[] getActiveEventKeys() {
        List<TeamPitScout> activeEventTeams = db.teamPitScoutDao().getAllTeams(db.activeEventKeyDao().getActiveEventKey());
        int numTeams = activeEventTeams.size();
        String[] teams = new String[numTeams];
        int i = 0;
        for (TeamPitScout team : activeEventTeams) {

            teams[i] = team.getTeamNumber();
            i++;
        }
        return teams;
    }

    public void setActiveEventKeys() {
        ArrayAdapter<String> teamsAtEventAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getActiveEventKeys());
        teamsAtEventAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.teams.setAdapter(teamsAtEventAdapter);
    }

    public void goToNextTeam() {
        int max = this.teams.getAdapter().getCount();
        int currPos = this.teams.getSelectedItemPosition();
        if (currPos + 1 < max) {
            this.teams.setSelection(currPos+1);
            setQRCode();
        } else {
            Toast errorToast = Toast.makeText(this, "No more teams to transfer QR code!", Toast.LENGTH_LONG);
            errorToast.show();
        }
    }

    public void goBackATeam() {
        int min = -1;
        int currPos = this.teams.getSelectedItemPosition();
        if (currPos - 1 > min) {
            this.teams.setSelection(currPos-1);
            setQRCode();
        } else {
            Toast errorToast = Toast.makeText(this, "No more teams to transfer QR code!", Toast.LENGTH_LONG);
            errorToast.show();
        }
    }


}
