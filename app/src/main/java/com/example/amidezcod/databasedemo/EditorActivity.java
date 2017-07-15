package com.example.amidezcod.databasedemo;

import android.Manifest;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.amidezcod.databasedemo.data.PetContract;
import com.example.amidezcod.databasedemo.utility.BitmapUtility;

import java.util.ArrayList;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int EXISTING_PET_LOADER = 0;
    private static final int REQUEST_CODE_FOR_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 1551;
    private static final int REQUEST_CODE_FOR_IMAGE_PICKER = 454;


    /**
     * Content URI for the existing pet (null if it's a new pet)
     */
    private Uri mCurrentPetUri;

    /**
     * EditText field to enter the pet's name
     */
    private EditText mNameEditText;

    /**
     * EditText field to enter the pet's breed
     */
    private EditText mBreedEditText;

    /**
     * EditText field to enter the pet's weight
     */
    private EditText mWeightEditText;

    /**
     * EditText field to enter the pet's gender
     */
    private ImageView mDogsImage;
    private boolean mPetHasChanged = false;
    private final View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mPetHasChanged = true;
            return false;
        }
    };
    private Spinner mGenderSpinner;
    private int mGender = PetContract.PetEntry.GENDER_UNKNOWN;
    private Bitmap imageBitmap;
    private PopupMenu popupMenu;

    /**
     * Boolean flag that keeps track of whether the pet has been edited (true) or not (false)
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);
        mDogsImage = (ImageView) findViewById(R.id.dog_image);

        mDogsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu = new PopupMenu(EditorActivity.this, v);
                popupMenu.getMenuInflater().inflate(R.menu.image_select, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.take_photo:
                                cameraPermission();
                                return true;
                            case R.id.choose_photo:
                                photoChoserFromGallery();
                                return true;
                            default:
                                throw new IllegalArgumentException("Wrong selection");
                        }
                    }
                });

            }
        });

        mNameEditText.setOnTouchListener(onTouchListener);
        mBreedEditText.setOnTouchListener(onTouchListener);
        mWeightEditText.setOnTouchListener(onTouchListener);
        mGenderSpinner.setOnTouchListener(onTouchListener);

        Intent intent = getIntent();
        mCurrentPetUri = intent.getData();
        if (mCurrentPetUri == null) {
            setTitle("Add a Pet");
        } else {
            setTitle("Edit Pet");
            getLoaderManager().initLoader(EXISTING_PET_LOADER, null, this).forceLoad();
        }
        setupSpinner();
    }

    private void cameraPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_FOR_EXTERNAL_STORAGE);
        } else {
            launchCamera();
        }
    }

    private void photoChoserFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra("outputX", 256);
        intent.putExtra("outputY", 256);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, REQUEST_CODE_FOR_IMAGE_PICKER);
    }

    private void launchCamera() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            mDogsImage.setImageBitmap(imageBitmap);
        }
        if (requestCode == REQUEST_CODE_FOR_IMAGE_PICKER && resultCode == RESULT_OK) {
            mDogsImage.setImageURI(data.getData());
            BitmapDrawable drawable = (BitmapDrawable) mDogsImage.getDrawable();
            imageBitmap = drawable.getBitmap();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_FOR_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchCamera();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void setupSpinner() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Unknown");
        arrayList.add("Male");
        arrayList.add("Female");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mGenderSpinner.setAdapter(arrayAdapter);
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    switch (selection) {
                        case "Male":
                            mGender = PetContract.PetEntry.GENDER_MALE;
                            break;
                        case "Female":
                            mGender = PetContract.PetEntry.GENDER_FEMALE;
                            break;
                        default:
                            mGender = PetContract.PetEntry.GENDER_UNKNOWN;
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = PetContract.PetEntry.GENDER_UNKNOWN;

            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection;

        projection = new String[]{
                PetContract.PetEntry._ID,
                PetContract.PetEntry.COLUMN_PET_NAME,
                PetContract.PetEntry.COLUMN_PET_BREED,
                PetContract.PetEntry.COLUMN_PET_GENDER,
                PetContract.PetEntry.COLUMN_PET_IMAGE,
                PetContract.PetEntry.COLUMN_PET_WEIGHT};


        return new CursorLoader(this
                , mCurrentPetUri
                , projection
                , null
                , null
                , null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 1) {
            return;
        }
        if (data.moveToFirst()) {
            if (data.getBlob(data.getColumnIndex(PetContract.PetEntry.COLUMN_PET_IMAGE)) == null) {
                mDogsImage.setImageResource(R.drawable.dog_default_profile);
            } else {
                byte[] imageArray = data.getBlob(data.getColumnIndex(PetContract.PetEntry.COLUMN_PET_IMAGE));
                mDogsImage.setImageBitmap(BitmapUtility.getImage(imageArray));
            }

            mNameEditText.setText(data.getString(data.getColumnIndex(PetContract.PetEntry.COLUMN_PET_NAME)));
            mBreedEditText.setText(data.getString(data.getColumnIndex(PetContract.PetEntry.COLUMN_PET_BREED)));
            mWeightEditText.setText(String.valueOf(data.getString(data.getColumnIndex(PetContract.PetEntry.COLUMN_PET_WEIGHT))));
            switch (data.getInt(data.getColumnIndex(PetContract.PetEntry.COLUMN_PET_GENDER))) {
                case PetContract.PetEntry.GENDER_MALE:
                    mGenderSpinner.setSelection(1);
                    break;
                case PetContract.PetEntry.GENDER_FEMALE:
                    mGenderSpinner.setSelection(2);
                    break;
                default:
                    mGenderSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mBreedEditText.setText("");
        mWeightEditText.setText("");
        mGenderSpinner.setSelection(0); // Select "Unknown" gender
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (mCurrentPetUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                savePet();
                finish();
                break;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                break;
            case android.R.id.home:
                if (mPetHasChanged)
                    navigation();
                else
                    NavUtils.navigateUpFromSameTask(this);
            default:
                super.onOptionsItemSelected(item);
                break;

        }
        return true;
    }

    private void navigation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage("Discard Your changes and quit editing?")
                .setPositiveButton("Discard", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void savePet() {

        String nameString = mNameEditText.getText().toString().trim();
        String breedString = mBreedEditText.getText().toString().trim();
        String weightString = mWeightEditText.getText().toString().trim();
        ContentValues values = new ContentValues();
        if (imageBitmap != null) {
            values.put(PetContract.PetEntry.COLUMN_PET_IMAGE, BitmapUtility.getBytes(imageBitmap));
        }
        if (mCurrentPetUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(breedString) &&
                TextUtils.isEmpty(weightString) && mGender == PetContract.PetEntry.GENDER_UNKNOWN) {
            return;
        }


        values.put(PetContract.PetEntry.COLUMN_PET_NAME, nameString);
        values.put(PetContract.PetEntry.COLUMN_PET_BREED, breedString);
        values.put(PetContract.PetEntry.COLUMN_PET_GENDER, mGender);

        int weight = 0;
        if (!TextUtils.isEmpty(weightString)) {
            weight = Integer.parseInt(weightString);
        }
        values.put(PetContract.PetEntry.COLUMN_PET_WEIGHT, weight);

        if (mCurrentPetUri == null) {

            Uri newUri = getContentResolver().insert(PetContract.PetEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_pet_failed),
                        Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, getString(R.string.editor_insert_pet_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {

            int rowsAffected = getContentResolver().update(mCurrentPetUri, values, null, null);


            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_pet_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_pet_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void deletePet() {

        if (mCurrentPetUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentPetUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_pet_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_pet_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage("Confirm Delete")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletePet();
                        finish();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (mPetHasChanged)
            navigation();
        else {
            super.onBackPressed();
        }

    }
}
