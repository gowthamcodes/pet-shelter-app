package com.gowtham.pets;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.gowtham.pets.data.PetContract.PetEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText mNameEditText;
    private EditText mBreedEditText;
    private EditText mAgeEditText;
    private EditText mWeightEditText;
    private EditText mHeightEditText;
    private EditText mHealthNoteEditText;

    private Spinner mGenderSpinner;
    private CheckBox mAdoptedCheckbox;
    private int mGender = PetEntry.GENDER_UNKNOWN;
    private Uri currentPetUri;
    private boolean mPetHasChanged = false;
    public static final int EXISTING_PET_LOADER = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        currentPetUri = intent.getData();
        if (currentPetUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_pet));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_pet));
            getLoaderManager().initLoader(EXISTING_PET_LOADER, null, this);
        }

        mNameEditText = findViewById(R.id.edit_pet_name);
        mBreedEditText = findViewById(R.id.edit_pet_breed);
        mAgeEditText = findViewById(R.id.edit_pet_age);
        mWeightEditText = findViewById(R.id.edit_pet_weight);
        mHeightEditText = findViewById(R.id.edit_pet_height);
        mHealthNoteEditText = findViewById(R.id.edit_pet_health_note);
        mGenderSpinner = findViewById(R.id.spinner_gender);
        mAdoptedCheckbox = findViewById(R.id.adapted_checkbox);

        mNameEditText.setOnTouchListener(onTouchListener);
        mBreedEditText.setOnTouchListener(onTouchListener);
        mAgeEditText.setOnTouchListener(onTouchListener);
        mWeightEditText.setOnTouchListener(onTouchListener);
        mHeightEditText.setOnTouchListener(onTouchListener);
        mHealthNoteEditText = findViewById(R.id.edit_pet_health_note);
        mGenderSpinner.setOnTouchListener(onTouchListener);
        mAdoptedCheckbox.setOnTouchListener(onTouchListener);
        setupSpinner();
    }

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mPetHasChanged = true;
            return false;
        }
    };

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deletePet();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deletePet() {
        if (currentPetUri != null) {
            int rowsDeleted= getContentResolver().delete(currentPetUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_pet_failed),
                        Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, getString(R.string.editor_delete_pet_successful),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {

        if (!mPetHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void setupSpinner() {

        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.array_gender_options,
                android.R.layout.simple_spinner_item);
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = PetEntry.GENDER_MALE; // Male
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = PetEntry.GENDER_FEMALE;; // Female
                    } else {
                        mGender = PetEntry.GENDER_UNKNOWN; // Unknown
                    }
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = PetEntry.GENDER_UNKNOWN; // Unknown
            }
        });

    }

    private void savePet() {

        String name = mNameEditText.getText().toString().trim();
        String breed = mBreedEditText.getText().toString().trim();
        String healthNote = mHealthNoteEditText.getText().toString().trim();
        int age = convertInteger(mAgeEditText.getText().toString().trim());
        int weight =  convertInteger(mWeightEditText.getText().toString().trim());
        int height =  convertInteger(mHeightEditText.getText().toString().trim());
        int gender = mGender;
        int isAdopted = mAdoptedCheckbox.isChecked() ? PetEntry.STATUS_ADOPTION_TRUE : PetEntry.STATUS_ADOPTION_FALSE;

        if (currentPetUri == null && TextUtils.isEmpty(name) && TextUtils.isEmpty(breed) && mGender == PetEntry.GENDER_UNKNOWN) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_PET_NAME, name);
        values.put(PetEntry.COLUMN_PET_BREED, breed);
        values.put(PetEntry.COLUMN_PET_AGE, age);
        values.put(PetEntry.COLUMN_PET_ADOPTED, isAdopted);
        values.put(PetEntry.COLUMN_PET_GENDER, gender);
        values.put(PetEntry.COLUMN_PET_WEIGHT, weight);
        values.put(PetEntry.COLUMN_PET_HEIGHT, height);
        values.put(PetEntry.COLUMN_PET_HEALTH_NOTE, healthNote);

        if (currentPetUri == null) {
            Uri newUri =  getContentResolver().insert(PetEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_pet_failed), Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, getString(R.string.editor_insert_pet_successful), Toast.LENGTH_SHORT).show();
            }
        }
        else {
            int rowsAffected = getContentResolver().update(currentPetUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_pet_failed),
                        Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, getString(R.string.editor_update_pet_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    private int convertInteger(String str) {
        return !TextUtils.isEmpty(str) ? Integer.parseInt(str) : 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (currentPetUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save:
                savePet();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
               if (!mPetHasChanged) {
                   NavUtils.navigateUpFromSameTask(this);
                   return true;
               }
               DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       NavUtils.navigateUpFromSameTask(EditorActivity.this);
                   }
               };
                showUnsavedChangesDialog(discardButtonClickListener);
               return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                PetEntry._ID,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED,
                PetEntry.COLUMN_PET_GENDER,
                PetEntry.COLUMN_PET_AGE,
                PetEntry.COLUMN_PET_ADOPTED,
                PetEntry.COLUMN_PET_HEIGHT,
                PetEntry.COLUMN_PET_WEIGHT,
                PetEntry.COLUMN_PET_HEALTH_NOTE,
        };

        return new CursorLoader(this, currentPetUri, projection,
                null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME));
            String breed = cursor.getString(cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED));
            int gender = cursor.getInt(cursor.getColumnIndex(PetEntry.COLUMN_PET_GENDER));
            int weight = cursor.getInt(cursor.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT));
            int age = cursor.getInt(cursor.getColumnIndex(PetEntry.COLUMN_PET_AGE));
            int isAdopted = cursor.getInt(cursor.getColumnIndex(PetEntry.COLUMN_PET_ADOPTED));
            int height = cursor.getInt(cursor.getColumnIndex(PetEntry.COLUMN_PET_HEIGHT));
            String healthNote = cursor.getString(cursor.getColumnIndex(PetEntry.COLUMN_PET_HEALTH_NOTE));

            mNameEditText.setText(name);
            mBreedEditText.setText(breed);
            mAgeEditText.setText(Integer.toString(age));
            mWeightEditText.setText(Integer.toString(weight));
            mHeightEditText.setText(Integer.toString(height));
            mHealthNoteEditText.setText(healthNote);

            switch (gender) {
                case PetEntry.GENDER_MALE:
                    mGenderSpinner.setSelection(1);
                    break;
                case PetEntry.GENDER_FEMALE:
                    mGenderSpinner.setSelection(2);
                    break;
                default:
                    mGenderSpinner.setSelection(0);
                    break;
            }

            if (isAdopted == PetEntry.STATUS_ADOPTION_TRUE) {
                mAdoptedCheckbox.setChecked(true);
            }
            else if (isAdopted == PetEntry.STATUS_ADOPTION_FALSE) {
                mAdoptedCheckbox.setChecked(false);
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mNameEditText.setText("");
        mBreedEditText.setText("");
        mWeightEditText.setText("");
        mAgeEditText.setText("");
        mHeightEditText.setText("");
        mHealthNoteEditText.setText("");
        mAdoptedCheckbox.setChecked(false);
        mGenderSpinner.setSelection(0);
    }
}