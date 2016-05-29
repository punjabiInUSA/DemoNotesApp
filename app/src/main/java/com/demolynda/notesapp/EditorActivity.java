package com.demolynda.notesapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class EditorActivity extends AppCompatActivity {

    private String mAction,noteFilter,oldText;

    private EditText mEditor;

    private AlertDialog.Builder mAlertDialog;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mEditor  = (EditText) findViewById(R.id.editText);

        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(NotesProvider.CONTENT_ITEM_TYPE);

        if(uri == null) {
            mAction = Intent.ACTION_INSERT;
            setTitle("New Note");
        } else {
            mAction = Intent.ACTION_EDIT;
            noteFilter = DBOpenHelper.NOTE_ID + "=" + uri.getLastPathSegment();

            Cursor cursor = getContentResolver().query(uri,
                    DBOpenHelper.ALL_COLUMNS,noteFilter,null,null);
            if(cursor !=null){
            cursor.moveToFirst();
            oldText = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_TEXT));
            cursor.close();
            mEditor.setText(oldText);
            setTitle("Edit note");
            mEditor.requestFocus();
         }
        }
    }

    private void finishEditing(){

        String newText = mEditor.getText().toString().trim();

        switch (mAction) {
            case Intent.ACTION_INSERT:
                if(newText.length()==0){
                    setResult(RESULT_CANCELED);
                } else {
                    insertNote(newText);
                }
                    break;
            case Intent.ACTION_EDIT:
                if(newText.length() == 0){
                    deleteNote();
                } else if (oldText.equals(newText)){
                    setResult(RESULT_CANCELED);
                } else {
                    updateNote(newText);
                }
            }
                finish();

    }

    private void deleteNote() {
    getContentResolver().delete(NotesProvider.CONTENT_URI,
            noteFilter,null);
        Toast.makeText(EditorActivity.this, "Note Deleted!", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    private void updateNote(String noteText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, noteText);
        getContentResolver().update(NotesProvider.CONTENT_URI,
                values,noteFilter,null);
        Toast.makeText(EditorActivity.this, "Note Saved!", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private void insertNote(String noteText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, noteText);
        getContentResolver().insert(NotesProvider.CONTENT_URI,
                values);
        setResult(RESULT_OK);
    }

    @Override
    public void onBackPressed() {
        finishEditing();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finishEditing();
                break;
            case R.id.action_delete_note:
                deleteNote();
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(mAction.equals(Intent.ACTION_EDIT)) {
            getMenuInflater().inflate(R.menu.menu_first, menu);
            menu.findItem(R.id.action_delete_note).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
        return true;
    }

//    private void showEmptyNoteDialog(){
//        mAlertDialog = new AlertDialog.Builder(this);
//        mAlertDialog.setMessage("Current note will be delete, are you sure you want to continue?");
//        mAlertDialog.setPositiveButton(, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                deleteNote();
//            }
//        });
//        mAlertDialog.setNegativeButton("")
//    }
}
