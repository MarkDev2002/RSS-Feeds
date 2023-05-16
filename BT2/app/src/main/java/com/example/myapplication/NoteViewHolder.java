package com.example.myapplication;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class NoteViewHolder extends RecyclerView.ViewHolder {
    public TextView noteTitle;
    public TextView noteDesc;
    public TextView noteDate;

    // create the layout for activity_note.xml
    public NoteViewHolder(View view) {
        super(view);
        noteTitle = view.findViewById(R.id.noteTitle);
        noteDesc = view.findViewById(R.id.noteDesc);
        noteDate = view.findViewById(R.id.noteDate);
    }
}
