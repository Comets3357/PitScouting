package com.example.pitscoutingapp2023;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.pitscoutingapp2023.common.Team;
import com.example.pitscoutingapp2023.common.TeamPitScout;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    AppDatabase db;

    Button settingsButton;
    Button submitButton;
    EditText editTextTeamNumber;
    Spinner programmingLanguageSpinner;
    Spinner drivetrainSpinner;
    EditText driveteamEditText;
    EditText eventKeyEditText;
    EditText teamNameEditText;
    boolean editingTeam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent mainActivityIntent = getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = AppDatabase.getDatabase(getApplicationContext());
        this.eventKeyEditText = findViewById(R.id.editTextEventKey);
        if (db.activeEventKeyDao().countEventKey() < 1) {
            db.activeEventKeyDao().initEventKey();
        } else {
            eventKeyEditText.setText(db.activeEventKeyDao().getActiveEventKey());
        }
        String[] arrayDrivetrainSpinner = new String[] {
                "Drivetrain", "Tank", "Swerve", "Mecanum", "Other"
        };
        this.drivetrainSpinner = (Spinner) findViewById(R.id.drivetrainSpinner);
        ArrayAdapter<String> drivetrainAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arrayDrivetrainSpinner);
        drivetrainAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.drivetrainSpinner.setAdapter(drivetrainAdapter);

        String[] arrayProgLangSpinner = new String[] {
               "Programming Language", "C++", "Java", "Python", "LabView", "Other"
        };
        this.programmingLanguageSpinner = (Spinner) findViewById(R.id.programmingLanguageSpinner);
        ArrayAdapter<String> progLangAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arrayProgLangSpinner);
        progLangAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.programmingLanguageSpinner.setAdapter(progLangAdapter);
        this.editTextTeamNumber = findViewById(R.id.editTextNumber);
        this.driveteamEditText = findViewById(R.id.textDriveTeam);
        this.eventKeyEditText = findViewById(R.id.editTextEventKey);
        this.teamNameEditText = findViewById(R.id.teamNameText);


        this.teamNameEditText.setKeyListener(null);

        settingsButton = (Button) findViewById(R.id.settingsButton);

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettings();
            }
        });
        this.submitButton = (Button) findViewById(R.id.submitButton);

        this.submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPitScout();
            }
        });
        importTeamNames();
        this.editingTeam = mainActivityIntent.getBooleanExtra("edit", false);
        if (this.editingTeam && (mainActivityIntent.getStringExtra("editTeam") != null)) {
            editTeam(mainActivityIntent.getStringExtra("editTeam"));

        } else {
            this.editTextTeamNumber.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (db.teamDao().teamExists(charSequence.toString()) > 0) {
                        teamNameEditText.setText(db.teamDao().getTeamName(charSequence.toString()));
                    } else {
                        teamNameEditText.setText("Team Name");
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }
    }

    public void editTeam(String teamToEdit) {
        this.editTextTeamNumber.setKeyListener(null);
        TeamPitScout editingTeam = db.teamPitScoutDao().getTeamAtEvent(teamToEdit, db.activeEventKeyDao().getActiveEventKey());
        this.editTextTeamNumber.setText(teamToEdit);
        if (ProgrammingLanguage.isDefined(editingTeam.programmingLanguage)) {
            ProgrammingLanguage debugProgramLang = ProgrammingLanguage.getProgrammingLanguageFromCommonName(editingTeam.programmingLanguage);
            this.programmingLanguageSpinner.setSelection(ProgrammingLanguage.getProgrammingLanguageFromCommonName(editingTeam.programmingLanguage).position);

        } else {
            this.programmingLanguageSpinner.setSelection(0);
        }
        if (DrivetrainType.isDefined(editingTeam.drivetrainType)) {
            this.drivetrainSpinner.setSelection(DrivetrainType.getDrivetrainTypeFromCommonName(editingTeam.drivetrainType).position);
        } else {
            this.drivetrainSpinner.setSelection(0);
        }
        if (editingTeam.driveteam) {
            this.driveteamEditText.setText("already recorded");
        } else {
            this.driveteamEditText.setText("");
        }
        if (db.teamDao().teamExists(teamToEdit.toString()) > 0) {
            teamNameEditText.setText(db.teamDao().getTeamName(teamToEdit.toString()));
        } else {
            teamNameEditText.setText("Team Name");
        }
    }

    public void openSettings() {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
        finish();
    }

    public void submitPitScout() {
        TeamPitScout teamPitScout;
        String teamNumber = this.editTextTeamNumber.getText().toString().trim();
        String eventKey = this.eventKeyEditText.getText().toString();
        if (teamNumber.length() > 0 && teamNumber.length() < 5 && eventKey.length() > 0) {
            teamPitScout = getTeamPitScout();
            if (this.editingTeam) {
                db.teamPitScoutDao().update(teamPitScout);
                openSettings();
            } else if (db.teamPitScoutDao().getAlreadySubmitted(teamNumber, eventKey) > 0) {
                onDoubleSubmit(teamNumber).show();
            } else {
                db.teamPitScoutDao().insertAll(teamPitScout);
                clearFields();
                if (!teamPitScout.event.equals(db.activeEventKeyDao().getActiveEventKey())) {
                    db.activeEventKeyDao().setActiveEventKey(teamPitScout.event);
                }
            }
        } else {
            Toast errorToast = Toast.makeText(this, "Please insert a teamNumber and eventKey!", Toast.LENGTH_LONG);
            errorToast.show();
        }
    }

    private void importTeamNames() {
        try(InputStream resource = getResources().openRawResource(R.raw.team_names)) {
            Scanner sc = new Scanner(resource);
            while(sc.hasNextLine()) {
                Team team = new Team();
                String[] vals = sc.nextLine().split(",");
                if (vals.length >= 2 && db.teamDao().teamExists(vals[0]) == 0) {
                    team.teamNumber = vals[0];
                    team.teamName = vals[1];
                    db.teamDao().insertAll(team);
                }
            }
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }

    }

    private AlertDialog onDoubleSubmit(String teamSubmitted) {
        // Use the Builder class for convenient dialog construction.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Double submitted team " + teamSubmitted)
                .setPositiveButton("Replace", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //replaces existing entry
                        db.teamPitScoutDao().update(getTeamPitScout());
                        clearFields();
                    }
                })
                .setNeutralButton("Edit current entry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //load current entries
                        editingTeam = true;
                        editTeam(teamSubmitted);
                    }
                })
                .setNegativeButton("Clear fields", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancels the dialog.
                        clearFields();
                        dialog.cancel();
                    }
                });
        // Create the AlertDialog object and return it.
        return builder.create();
    }

    public TeamPitScout getTeamPitScout() {
        TeamPitScout teamPitScout = new TeamPitScout();
        String teamNumber = this.editTextTeamNumber.getText().toString().trim();
        String eventKey = this.eventKeyEditText.getText().toString();
        teamPitScout.teamNumber = teamNumber;
        teamPitScout.event = eventKey;
        teamPitScout.drivetrainType = this.drivetrainSpinner.getSelectedItem().toString();
        teamPitScout.programmingLanguage = this.programmingLanguageSpinner.getSelectedItem().toString();
        teamPitScout.driveteam = this.driveteamEditText.getText().toString().trim().length() > 0;
        return teamPitScout;
    }

    public void clearFields() {
        this.editTextTeamNumber.getText().clear();
        this.drivetrainSpinner.setSelection(0);
        this.driveteamEditText.getText().clear();
        this.programmingLanguageSpinner.setSelection(0);
        this.teamNameEditText.setText("Team Name");
    }
}