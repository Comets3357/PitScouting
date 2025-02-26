package com.example.pitscoutingapp2023;

import static com.google.android.material.internal.ContextUtils.getActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.pitscoutingapp2023.common.PostDataResponse;
import com.example.pitscoutingapp2023.common.Team;
import com.example.pitscoutingapp2023.common.TeamPitScout;
import com.example.pitscoutingapp2023.dao.ScoutingDataServerRestDao;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity {

    Button qrTransferButton;
    Button returnToScoutingButton;
    Button saveEventKeyButton;
    Button editActiveTeamButton;
    Button clearEventButton;
    Button sendDataOverWifiButton;
    EditText eventKeyEditText;
    EditText ipAddressEditText;
    TextView teamNameTextView;
    TextView drivetrainTextView;
    TextView programmingLanguageTextView;
    Spinner teams;
    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        db = AppDatabase.getDatabase(getApplicationContext());

        qrTransferButton = (Button) findViewById(R.id.transferByQRButton);

        qrTransferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openQRCode();
            }
        });
        this.drivetrainTextView = findViewById(R.id.drivetrainTextView);
        this.programmingLanguageTextView = findViewById(R.id.programmingLanguageTextView);
        this.teamNameTextView = findViewById(R.id.teamNameTextView);
        this.ipAddressEditText = findViewById(R.id.editTextIP);

        returnToScoutingButton = (Button) findViewById(R.id.returnToScoutingButton);

        returnToScoutingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToScouting();
            }
        });

        this.eventKeyEditText = findViewById(R.id.eventKeySettingsEditText);
        this.eventKeyEditText.setText(db.activeEventKeyDao().getActiveEventKey());

        saveEventKeyButton = (Button) findViewById(R.id.saveButton);
        saveEventKeyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { setEventKey(); }
        });

        this.clearEventButton = (Button) findViewById(R.id.buttonClearEvent);
        this.clearEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearEvent();
            }
        });

        this.editActiveTeamButton = (Button) findViewById(R.id.EditTeamsButton);
        this.editActiveTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { editTeam(); }
        });

        this.teams = (Spinner) findViewById(R.id.teamSelectSpinner);

        this.teams.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                setTeamInfo();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        setActiveEventKeys();
        if (this.teams.getAdapter().getCount() > 0) {
            setTeamInfo();
        }

        this.sendDataOverWifiButton = (Button) findViewById(R.id.buttonWifiTransfer);
        this.sendDataOverWifiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendDataToServer();
            }
        });
     }

    public void openQRCode() {
        if (this.teams.getAdapter().getCount() > 0) {
            Intent qrCodeIntent = new Intent(this, QrCodeActivity.class);
            startActivity(qrCodeIntent);
        } else {
            Toast errorToast = Toast.makeText(this, "No teams to transfer QR code!", Toast.LENGTH_LONG);
            errorToast.show();
        }
    }

    public void setEventKey() {
        String newEventKey = this.eventKeyEditText.getText().toString();
        db.activeEventKeyDao().setActiveEventKey(newEventKey);
        setActiveEventKeys();
    }

    public void editTeam() {
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        mainActivityIntent.putExtra("edit", true);
        mainActivityIntent.putExtra("editTeam", this.teams.getSelectedItem().toString());
        startActivity(mainActivityIntent);
        finish();
    }

    public void returnToScouting() {
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(mainActivityIntent);
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

    public void setTeamInfo() {
        if (this.teams.getAdapter().getCount() > 0) {
            if (db.teamDao().teamExists(this.teams.getSelectedItem().toString()) > 0) {
                this.teamNameTextView.setText(db.teamDao().getTeamName(this.teams.getSelectedItem().toString()));
            } else {
                this.teamNameTextView.setText("Team Name");
            }
            TeamPitScout editingTeam = db.teamPitScoutDao().getTeamAtEvent(this.teams.getSelectedItem().toString(), db.activeEventKeyDao().getActiveEventKey());
            if (ProgrammingLanguage.isDefined(editingTeam.programmingLanguage)) {
                ProgrammingLanguage debugProgramLang = ProgrammingLanguage.getProgrammingLanguageFromCommonName(editingTeam.programmingLanguage);
                this.programmingLanguageTextView.setText(ProgrammingLanguage.getProgrammingLanguageFromCommonName(editingTeam.programmingLanguage).commonName);

            } else {
                this.programmingLanguageTextView.setText("NOT DEFINED");
            }
            if (DrivetrainType.isDefined(editingTeam.drivetrainType)) {
                this.drivetrainTextView.setText(DrivetrainType.getDrivetrainTypeFromCommonName(editingTeam.drivetrainType).commonName);
            } else {
                this.drivetrainTextView.setText("NOT DEFINED");
            }
        } else {
            this.teamNameTextView.setText("Team Name");
            this.programmingLanguageTextView.setText("NOT DEFINED");
            this.drivetrainTextView.setText("NOT DEFINED");
        }
    }

    public void clearEvent() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater.
        LayoutInflater inflater = getActivity(this).getLayoutInflater();
        View passwordView = inflater.inflate(R.layout.dialog_signin, null);
        // Inflate and set the layout for the dialog.
        // Pass null as the parent view because it's going in the dialog layout.
        builder.setView(passwordView)
                // Add action buttons
                .setPositiveButton("Clear", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText password = (EditText)passwordView.findViewById(R.id.passwordEditText);
                        if (password.getText() != null && password.getText().toString().equals("admin3357")) {
                            Log.d("password", "bing!");
                            db.teamPitScoutDao().deleteEventRecords(db.activeEventKeyDao().getActiveEventKey());
                            //todo
                        } else {
                            Toast errorToast = Toast.makeText(getApplicationContext(), "Password incorrect.", Toast.LENGTH_LONG);
                            errorToast.show();
                            dialog.cancel();
                            clearEvent();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }
    public void sendDataToServer() {
        List<TeamPitScout> scoutedData = this.db.teamPitScoutDao().getAllTeams(this.db.activeEventKeyDao().getActiveEventKey());
        ScoutingDataServerRestDao.getApiInterface("http://"+this.ipAddressEditText.getText().toString()+":5000").sendDataToServer(scoutedData)
                .enqueue(new Callback<PostDataResponse>() {
                    @Override
                    public void onResponse(Call<PostDataResponse> call, Response<PostDataResponse> response) {
                        if (response.body() != null){
                            Log.d("TAG","Successful data send");
                            Toast.makeText(getApplicationContext(),"Successfully sent data to server",Toast.LENGTH_LONG).show();
                        } else {
                            Log.d("TAG","Failure");
                            Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<PostDataResponse> call, Throwable t) {
                        Log.d("TAG","Failure :-"+t.getMessage());
                        Toast.makeText(getApplicationContext(),"ERROR "+t.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
    }
}