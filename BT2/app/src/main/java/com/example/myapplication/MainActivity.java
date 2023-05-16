package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.JsonWriter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

// interface for adapter
public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    // The above lines are important - them make this class a listener
    // for click and long click events in the ViewHolders (in the recycler
    private final List<Note> noteList = new ArrayList<>();
    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter; //Data to recyclerview adapter
    private boolean noteChanged = false;
    private static final int REQUEST_CODE_NEW_NOTE = 1;
    private static final int REQUEST_CODE_EXISTING_NOTE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // layout resource
        recyclerView = findViewById(R.id.recycler); //retireve the recyclerview
        noteAdapter = new NoteAdapter(noteList, this); // make an adapter

        //sets up adapter to recycler
        recyclerView.setAdapter(noteAdapter);

        //how to layout the recycler
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadFile();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu); // inflates menu
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("IS_NOTE_LIST_CHANGED", noteChanged);
        super.onSaveInstanceState(outState);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                newNote(false, 0, null);
                return true;
            case R.id.menu_help:
                AboutApplication();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        if (noteList != null) {
            int notePosition = recyclerView.getChildLayoutPosition(v);
            newNote(true, notePosition, noteList.get(notePosition));
        }
    }

    @Override
    public boolean onLongClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        if (noteList != null) {
            String noteTitle = "";
            Note note = noteList.get(pos);
            if (note != null) {
                noteTitle = note.getTitle();
            }
            DeleteNote(pos, noteTitle);
            return true;
        }
        return false;
    }

    @Override
    protected void onPause() {
        saveToFile(); // saving data to JSON file
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    private void reloadRecycler() {
        noteChanged = true;
        Collections.sort(noteList);
        noteAdapter.notifyDataSetChanged();
    }


    private void newNote(boolean existingNote, int notePosition, Note note) {
        int requestCode = REQUEST_CODE_NEW_NOTE;
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra("IS_EXISTING_NOTE", existingNote);
        if (existingNote) {
            requestCode = REQUEST_CODE_EXISTING_NOTE;
            if (note != null) {
                intent.putExtra("EXISTING_NOTE", note);
                intent.putExtra("EXISTING_NOTE_POSITION", notePosition);
            }
        }
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_CODE_NEW_NOTE) {
            if (resultCode == RESULT_OK) {
                Note newNote = (Note) intent.getSerializableExtra("NEW_NOTE");
                if (newNote != null) {
                    noteList.add(newNote);
                    reloadRecycler();
                }
            }
        } else if (requestCode == REQUEST_CODE_EXISTING_NOTE) {
            if (resultCode == RESULT_OK) {
                boolean isNoteChanged = intent.getBooleanExtra("NOTE_CHANGED", false);
                if (isNoteChanged) {
                    Note existingNote = (Note) intent.getSerializableExtra("EXISTING_NOTE");
                    int notePosition = intent.getIntExtra("EXISTING_NOTE_POSITION", 0);
                    if (existingNote != null) {
                        noteList.set(notePosition, existingNote);
                        reloadRecycler();
                    }
                }
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        super.onRestoreInstanceState(savedState);
        noteChanged = savedState.getBoolean("IS_NOTE_LIST_CHANGED");

    }

    public void DeleteNote(final int notePosition, final String noteTitle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_save_black_24dp);
        builder.setPositiveButton("Yes", (dialog, id) -> {
            noteList.remove(notePosition);
            reloadRecycler();
            Toast.makeText(MainActivity.this, "Note '" + noteTitle + "' Deleted", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("No", (dialog, id) -> {
        });
        builder.setMessage("Delete Note '" + noteTitle + "'?");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void loadFile() {
        try {
            InputStream inputStream = getApplicationContext().
                    openFileInput(getString(R.string.filejson));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            JSONObject jsonObject = new JSONObject(sb.toString());
            JSONArray notesJsonArray = jsonObject.getJSONArray("noteList");
            if (notesJsonArray.length() > 0) {
                for (int i = 0; i < notesJsonArray.length(); i++) {
                    JSONObject noteJson = notesJsonArray.getJSONObject(i);
                    if (noteJson != null) {
                        noteList.add(new Note(noteJson.getString("title"), noteJson.getString("desc"),
                                ConvertSD(noteJson.getString("date"))));
                    }
                }
            }
            if (noteList != null && noteList.size() > 0) {
                Collections.sort(noteList);
            }
        } catch (FileNotFoundException e) {
            Toast.makeText(this, getString(R.string.no_file), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveToFile() {
        try {
            if (noteChanged && noteList != null) {
                FileOutputStream fos = getApplicationContext().
                        openFileOutput(getString(R.string.filejson), Context.MODE_PRIVATE);
                JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos, getString(R.string.encoding)));
                writer.setIndent("  ");
                writer.beginObject();
                writer.name("noteList");
                writer.beginArray();

                if (noteList.size() > 0) {
                    for (Note note : noteList) {
                        if (note != null) {
                            writer.beginObject();
                            writer.name("title").value(note.getTitle());
                            writer.name("desc").value(note.getDesc());
                            writer.name("date").value(ConvertDS(note.getDate()));
                            writer.endObject();
                        }
                    }
                }
                writer.endArray();
                writer.endObject();
                writer.flush();
                writer.close();
                noteChanged = false;
                Toast.makeText(this, "Notes Saved", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void AboutApplication() {
        Intent intent = new Intent(MainActivity.this, AboutActivity.class);
        intent.putExtra(android.content.Intent.EXTRA_TEXT, MainActivity.class.getSimpleName());
        startActivity(intent);
    }

    private Date ConvertSD(String latestDate) {
        try {
            if (!TextUtils.isEmpty(latestDate)) {
                SimpleDateFormat sdf = (SimpleDateFormat) DateFormat.getDateTimeInstance();
                sdf.applyPattern("EEE, d MMM yyyy HH:mm:ss");
                return sdf.parse(latestDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //convert dateString
    private String ConvertDS(Date latestDateString) {
        if (latestDateString != null) {
            SimpleDateFormat sdf = (SimpleDateFormat) DateFormat.getDateTimeInstance();
            sdf.applyPattern("EEE, d MMM yyyy HH:mm:ss");
            return sdf.format(latestDateString);
        }

        return null;
    }
}