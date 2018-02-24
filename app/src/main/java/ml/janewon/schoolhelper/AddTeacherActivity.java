package ml.janewon.schoolhelper;

import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddTeacherActivity extends AppCompatActivity {

    public static final int PICK_IMAGE_FROM_CAMERA = 0;
    public static final int PICK_IMAGE_FROM_GALLERY = 1;

    private static final int CROP_IMAGE = 2;

    private Uri mCurrentTeacherUri;
    private String mCurrentPhotoPath;
    private Uri mCroppedImageUri;

    private CoordinatorLayout mCoordinatorLayout;

    private ImageView mAddPhoto;
    private EditText mNameEditText;
    private EditText mSurnameEditText;
    private TextView mSubjectTextView;
    private EditText mOfficeEditText;
    private EditText mOfficeHoursEditText;
    private EditText mEmailEditText;
    private EditText mPhoneEditText;
    private EditText mAddressEditText;
    private EditText mWebsiteEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teacher);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_add_teacher_coordinator_layout);

        mAddPhoto = (ImageView) findViewById(R.id.activity_add_teacher_photo);
        mNameEditText = (EditText) findViewById(R.id.activity_add_teacher_name);
        mSurnameEditText = (EditText) findViewById(R.id.activity_add_teacher_surname);
        mSubjectTextView = (TextView) findViewById(R.id.activity_add_teacher_subject);
        mOfficeEditText = (EditText) findViewById(R.id.activity_add_teacher_office);
        mOfficeHoursEditText = (EditText) findViewById(R.id.activity_add_teacher_office_hours);
        mEmailEditText = (EditText) findViewById(R.id.activity_add_teacher_email);
        mPhoneEditText = (EditText) findViewById(R.id.activity_add_teacher_phone_number);
        mAddressEditText = (EditText) findViewById(R.id.activity_add_teacher_address);
        mWebsiteEditText = (EditText) findViewById(R.id.activity_add_teacher_website);

        mAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v4.app.DialogFragment imagePickerFragment = new ImagePickerFragment();
                imagePickerFragment.show(getSupportFragmentManager(), "imagePicker");
            }
        });
        Intent intent = getIntent();
        mCurrentTeacherUri = intent.getData();

        if(mCurrentTeacherUri != null) {
            setTitle("Edit Teacher");
            new QueryTeacherAsyncTask().execute();
        }

        mSubjectTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment subjectPickerFragment = SubjectPickerFragment.newInstance(R.id.activity_add_teacher_subject);
                subjectPickerFragment.show(getFragmentManager(), "subjectPicker");
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("photoPath", mCurrentPhotoPath);
        String subject = mSubjectTextView.getText().toString();
        outState.putString("subject", subject);
        if(mCroppedImageUri != null) {
            outState.putString("compressedImageUri", mCroppedImageUri.toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCurrentPhotoPath = savedInstanceState.getString("photoPath");
        String subject = savedInstanceState.getString("subject");
        mSubjectTextView.setText(subject);
        String uriString = savedInstanceState.getString("compressedImageUri");
        if(uriString != null) {
            mCroppedImageUri = Uri.parse(savedInstanceState.getString("compressedImageUri"));
            Glide.with(this).load(mCroppedImageUri).into(mAddPhoto);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.button_save_data, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                //Hide Virtual Keyboard
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                String name = mNameEditText.getText().toString().trim();
                String surname = mSurnameEditText.getText().toString().trim();
                String actualName;
                if(surname.equals("")) {
                    if(name.equals("")) {
                        Snackbar.make(mCoordinatorLayout, "The teacher needs to have a name", Snackbar.LENGTH_LONG).show();
                        return false;
                    } else {
                        actualName = name;
                    }
                } else {
                    if(name.equals("")) {
                        actualName = surname;
                    } else {
                        actualName = name + " " + surname;
                    }
                }
                String subject = mSubjectTextView.getText().toString().trim();
                String office = mOfficeEditText.getText().toString().trim();
                String officeHours = mOfficeHoursEditText.getText().toString().trim();
                String email = mEmailEditText.getText().toString().trim();
                String phone = mPhoneEditText.getText().toString().trim();
                String address = mAddressEditText.getText().toString().trim();
                String website = mWebsiteEditText.getText().toString().trim();

                SchoolHelperDatabaseHelper dbHelper = new SchoolHelperDatabaseHelper(this);
                Cursor subjectCursor = dbHelper.getReadableDatabase().query(SchoolHelperDatabaseHelper.TABLE_SUBJECTS,
                        new String[]{SchoolHelperDatabaseHelper.DB_ID},
                        SchoolHelperDatabaseHelper.SUBJECT_NAME + "=?",
                        new String[]{subject},
                        null, null, null);

                ContentValues contentValues = new ContentValues();
                contentValues.put(SchoolHelperDatabaseHelper.TEACHER_NAME, name);
                contentValues.put(SchoolHelperDatabaseHelper.TEACHER_SURNAME, surname);
                contentValues.put(SchoolHelperDatabaseHelper.TEACHER_OFFICE, office);
                contentValues.put(SchoolHelperDatabaseHelper.TEACHER_OFFICE_HOURS, officeHours);
                contentValues.put(SchoolHelperDatabaseHelper.TEACHER_EMAIL, email);
                contentValues.put(SchoolHelperDatabaseHelper.TEACHER_PHONE, phone);
                contentValues.put(SchoolHelperDatabaseHelper.TEACHER_ADDRESS, address);
                contentValues.put(SchoolHelperDatabaseHelper.TEACHER_WEBSITE, website);

                if(subjectCursor.moveToFirst()) {
                    int subjectId = subjectCursor.getInt(subjectCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.DB_ID));
                    contentValues.put(SchoolHelperDatabaseHelper.SUBJECT_FOREIGN_KEY, subjectId);
                }

                if(mCroppedImageUri != null) {
                    contentValues.put(SchoolHelperDatabaseHelper.TEACHER_PHOTO, mCroppedImageUri.toString());
                } else if(mCurrentPhotoPath != null) {
                    contentValues.put(SchoolHelperDatabaseHelper.TEACHER_PHOTO, mCurrentPhotoPath);
                }

                if(mCurrentTeacherUri != null) {
                    int rowsUpdated = getContentResolver().update(mCurrentTeacherUri, contentValues, null, null);

                    if(rowsUpdated < 1) {
                        Snackbar.make(mCoordinatorLayout, "Error updating the teacher", Snackbar.LENGTH_LONG).show();
                        return false;
                    }
                } else {
                    Uri newUri = getContentResolver().insert(SchoolHelperDatabaseHelper.TEACHERS_CONTENT_URI, contentValues);

                    if(newUri == null) {
                        Snackbar.make(mCoordinatorLayout, "Error saving the teacher", Snackbar.LENGTH_LONG).show();
                        return false;
                    }
                }

                subjectCursor.close();
                dbHelper.close();

                Intent resultIntent = new Intent();
                resultIntent.putExtra(TeacherPickerFragment.ADD_TEACHER_KEY, actualName);
                setResult(RESULT_OK, resultIntent);

                finish();
                return true;
            case android.R.id.home:
                if(mCurrentTeacherUri == null && mCroppedImageUri != null) {
                    new File(mCroppedImageUri.getPath()).delete();
                }
                finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(mCurrentTeacherUri == null && mCroppedImageUri != null) {
            new File(mCroppedImageUri.getPath()).delete();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_IMAGE_FROM_GALLERY:
                    if (data.getData() != null) {
                        mCurrentPhotoPath = data.getData().toString();
                        Glide.with(this).load(data.getData()).into(mAddPhoto);
                    } else {
                        Snackbar.make(mCoordinatorLayout, "Failed to load the image", Snackbar.LENGTH_LONG).show();
                    }
                    break;
                case PICK_IMAGE_FROM_CAMERA:
                    Intent intent = new Intent(this, CropImageActivity.class);
                    intent.setData(Uri.parse("file://" + mCurrentPhotoPath));
                    startActivityForResult(intent, CROP_IMAGE);
                    break;
                case CROP_IMAGE:
                    mCroppedImageUri = data.getData();
                    mAddPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    Glide.with(this).load(mCroppedImageUri).into(mAddPhoto);
                    break;
                case SubjectPickerFragment.NEW_SUBJECT:
                    String newSubject = data.getExtras().getString(SubjectPickerFragment.ADD_SUBJECT_KEY);
                    mSubjectTextView.setText(newSubject);
                    break;
            }
        }
    }

    public File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault()).format(new Date());
        String imageFileName = " " + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private class QueryTeacherAsyncTask extends AsyncTask<Void, Void, Void> {

        Cursor teacherCursor;

        @Override
        protected Void doInBackground(Void... voids) {
            teacherCursor = getContentResolver().query(mCurrentTeacherUri, null, null, null, null);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(teacherCursor.moveToFirst()) {
                mCurrentPhotoPath = teacherCursor.getString(teacherCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.TEACHER_PHOTO));
                String name = teacherCursor.getString(teacherCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.TEACHER_NAME));
                String surname = teacherCursor.getString(teacherCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.TEACHER_SURNAME));
                String office = teacherCursor.getString(teacherCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.TEACHER_OFFICE));
                String officeHours = teacherCursor.getString(teacherCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.TEACHER_OFFICE_HOURS));
                String email = teacherCursor.getString(teacherCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.TEACHER_EMAIL));
                String phone = teacherCursor.getString(teacherCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.TEACHER_PHONE));
                String address = teacherCursor.getString(teacherCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.TEACHER_ADDRESS));
                String website = teacherCursor.getString(teacherCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.TEACHER_WEBSITE));
                String subject = Utils.getSubject(AddTeacherActivity.this, teacherCursor);

                if(mCurrentPhotoPath != null && !mCurrentPhotoPath.equals("")) {
                    mCroppedImageUri = Uri.parse(mCurrentPhotoPath);
                    Glide.with(AddTeacherActivity.this).load(mCroppedImageUri).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            mAddPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            return false;
                        }
                    }).into(mAddPhoto);
                }

                mNameEditText.setText(name);
                mSurnameEditText.setText(surname);
                mOfficeEditText.setText(office);
                mOfficeHoursEditText.setText(officeHours);
                mEmailEditText.setText(email);
                mPhoneEditText.setText(phone);
                mAddressEditText.setText(address);
                mWebsiteEditText.setText(website);
                mSubjectTextView.setText(subject);
            }

            teacherCursor.close();
        }
    }
}
