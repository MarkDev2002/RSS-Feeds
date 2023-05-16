package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;

public class EditActivity extends AppCompatActivity {
    private EditText editTitle = null;
    private EditText editDesc = null;
    private int nPosition;
    private boolean doesNoteExist;
    private String Title = "";
    private String Description = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        editTitle = findViewById(R.id.edit1);
        editDesc = findViewById(R.id.edit2);
        editDesc.setMovementMethod(new ScrollingMovementMethod());
        editDesc.setGravity(Gravity.TOP);
        editDesc.setTextIsSelectable(true);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("IS_EXISTING_NOTE")) {
            doesNoteExist = intent.getBooleanExtra("IS_EXISTING_NOTE", false);
            if (doesNoteExist) {
                Note existingNote = (Note) intent.getSerializableExtra("EXISTING_NOTE");
                nPosition = intent.getIntExtra("EXISTING_NOTE_POSITION", 0);
                if (existingNote != null) {
                    Title = existingNote.getTitle();
                    Description = existingNote.getDesc();
                    editTitle.setText(Title);
                    editDesc.setText(Description);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_save) {
            saveNote(false);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("Note Position", nPosition);
        outState.putBoolean("Existing Note", doesNoteExist);
        outState.putString("Title", Title);
        outState.putString("Descrption", Description);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        super.onRestoreInstanceState(savedState);
        nPosition = savedState.getInt("EXISTING_NOTE_POS");
        doesNoteExist = savedState.getBoolean("IS_EXISTING_NOTE");
        Title = savedState.getString("TITLE_VAL_EXISTING_NOTE");
        Description = savedState.getString("TEXT_VAL_EXISTING_NOTE");
    }

    private void saveNote(boolean backButtonPressed) {
        String titleEntered = editTitle.getText().toString();
        String textEntered = editDesc.getText().toString();
        if (doesNoteExist) {
            if (isTitleEntered(titleEntered)) {
                if (isTitleChanged(titleEntered) || isTextChanged(textEntered)) {
                    if (backButtonPressed) {
                        Save(false);
                    } else {
                        saveExistingNote();
                    }
                } else {
                    ExitActivity();
                }
            } else {
                ExitActivity();
            }

        } else {
            if (isTitleEntered(titleEntered)) {
                if (backButtonPressed) {

                    Save(true);
                } else {
                    saveNewNote();
                }
            } else {

                ExitActivity();
            }
        }
    }


    private void saveExistingNote() {
        String eTitle = editTitle.getText().toString();
        String eDesc = editDesc.getText().toString();
        Intent intent = new Intent();
        Note eNote = new Note(eTitle, eDesc, new Date());
        intent.putExtra("EXISTING_NOTE", eNote);
        intent.putExtra("EXISTING_NOTE_POSITION", nPosition);
        intent.putExtra("NOTE_CHANGED", true);
        MainPage(intent);
    }

    private void saveNewNote() {
        String newTitle = editTitle.getText().toString();
        String newDesc = editDesc.getText().toString();
        Intent intent = new Intent();
        Note newNote = new Note(newTitle, newDesc, new Date());
        intent.putExtra("NEW_NOTE", newNote);
        MainPage(intent);
    }

    public void Save(final boolean newNote) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String titleEntered = editTitle.getText().toString();

        builder.setIcon(R.drawable.ic_save_black_24dp);

        builder.setPositiveButton("Yes", (dialog, id) -> {
            if (newNote) {
                saveNewNote();
            } else {
                saveExistingNote();
            }
        });
        builder.setNegativeButton("No", (dialog, id) -> ExitActivity());
        builder.setMessage("Your note is not saved!\nSave note '" + titleEntered + "'?");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void MainPage(Intent intent) {
        setResult(RESULT_OK, intent);
        finish();
    }

    public void ExitActivity() {
        Intent intent = new Intent();
        intent.putExtra("NOTE_CHANGED", false);
        MainPage(intent);
    }


    private boolean isTitleEntered(String titleEntered) {
        return !TextUtils.isEmpty(titleEntered);
    }

    private boolean isTitleChanged(String titleEntered) {
        return !TextUtils.isEmpty(titleEntered) &&
                !TextUtils.isEmpty(Title) &&
                !TextUtils.equals(titleEntered, Title);
    }

    //
    private boolean isTextChanged(String textEntered) {
        if (textEntered == null) {
            textEntered = "";
        }
        if (Description == null) {
            Description = "";
        }
        return !TextUtils.equals(textEntered, Description);
    }

    @Override
    public void onBackPressed() {
        saveNote(true);
    }


}
