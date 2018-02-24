package ml.janewon.schoolhelper;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;

public class ViewTeacherDetailActivity extends AppCompatActivity {

    public static final String TEACHER_NAME_KEY = "teacherName";

    private Uri mCurrentTeacherUri;
    private Uri mPhotoUri;

    CoordinatorLayout mCoordinatorLayout;

    ImageView mPhoto;
    TextView mOfficeTextView;
    TextView mSubjectTextView;
    TextView mPhoneTextView;
    TextView mEmailTextView;
    TextView mOfficeHoursTextView;
    TextView mAddressTextView;
    TextView mWebsiteTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_teacher_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_view_teacher_detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        mCurrentTeacherUri = intent.getData();
        if(intent.hasExtra(TEACHER_NAME_KEY)) {
            setTitle(intent.getExtras().getString(TEACHER_NAME_KEY));
        }

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_view_teacher_detail_coordinator_layout);

        mPhoto = (ImageView) findViewById(R.id.activity_view_teacher_detail_toolbar_image);
        mSubjectTextView = (TextView) findViewById(R.id.activity_view_teacher_subject);
        mOfficeTextView = (TextView) findViewById(R.id.activity_view_teacher_office);
        mPhoneTextView = (TextView) findViewById(R.id.activity_view_teacher_phone);
        mEmailTextView = (TextView) findViewById(R.id.activity_view_teacher_email);
        mOfficeHoursTextView = (TextView) findViewById(R.id.activity_view_teacher_office_hour);
        mAddressTextView = (TextView) findViewById(R.id.activity_view_teacher_address);
        mWebsiteTextView = (TextView) findViewById(R.id.activity_view_teacher_website);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.activity_view_teacher_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewTeacherDetailActivity.this, AddTeacherActivity.class);
                intent.setData(mCurrentTeacherUri);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        new QueryTeacherAsyncTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.button_delete_data, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_delete:
                int rowsDeleted = getContentResolver().delete(mCurrentTeacherUri, null, null);
                if(rowsDeleted < 1) {
                    Snackbar.make(mCoordinatorLayout, "Error deleting the teacher", Snackbar.LENGTH_LONG);
                    return false;
                } else {
                    if(mPhotoUri != null) {
                        new File(mPhotoUri.getPath()).delete();
                    }
                    finish();
                    return true;
                }
            default:
                return false;
        }
    }

    private class QueryTeacherAsyncTask extends AsyncTask<Void, Void, Void> {

        Cursor teacherCursor;
        Cursor subjectCursor;

        @Override
        protected Void doInBackground(Void... voids) {
            teacherCursor = getContentResolver().query(mCurrentTeacherUri, null, null, null, null);
            subjectCursor = null;
            if(teacherCursor != null && teacherCursor.moveToFirst()) {
                int subjectId = teacherCursor.getInt(teacherCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.SUBJECT_FOREIGN_KEY));
                Uri subjectUri = Uri.withAppendedPath(SchoolHelperDatabaseHelper.SUBJECTS_CONTENT_URI, Integer.toString(subjectId));
                subjectCursor = getContentResolver().query(subjectUri, new String[]{SchoolHelperDatabaseHelper.SUBJECT_NAME}, null, null, null);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(teacherCursor.moveToFirst()) {

                String photoUriString = teacherCursor.getString(teacherCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.TEACHER_PHOTO));
                if(photoUriString != null) {
                    mPhotoUri = Uri.parse(photoUriString);
                    Glide.with(ViewTeacherDetailActivity.this).load(mPhotoUri).into(mPhoto);
                }
                String name = teacherCursor.getString(teacherCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.TEACHER_NAME));
                String surname = teacherCursor.getString(teacherCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.TEACHER_SURNAME));
                String fullName;
                if(!name.equals("")) {
                    if(surname.equals("")) {
                        fullName = name;
                    } else {
                        fullName = name + " " + surname;
                    }
                } else {
                    fullName = surname;
                }
                setTitle(fullName);

                String office = teacherCursor.getString(teacherCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.TEACHER_OFFICE));
                final String phone = teacherCursor.getString(teacherCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.TEACHER_PHONE));
                final String email = teacherCursor.getString(teacherCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.TEACHER_EMAIL));
                String officeHours = teacherCursor.getString(teacherCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.TEACHER_OFFICE_HOURS));
                final String address = teacherCursor.getString(teacherCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.TEACHER_ADDRESS));
                final String website = teacherCursor.getString(teacherCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.TEACHER_WEBSITE));

                String subject = "";
                if(subjectCursor != null && subjectCursor.moveToFirst()) {
                    subject = subjectCursor.getString(subjectCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.SUBJECT_NAME));
                }
                if (subject.equals("")) {
                    mSubjectTextView.setVisibility(View.GONE);
                } else {
                    mSubjectTextView.setVisibility(View.VISIBLE);
                    mSubjectTextView.setText(subject);
                }

                if (office.equals("")) {
                    mOfficeTextView.setVisibility(View.GONE);
                } else {
                    mOfficeTextView.setVisibility(View.VISIBLE);
                    mOfficeTextView.setText(office);
                }

                if (phone.equals("")) {
                    mPhoneTextView.setVisibility(View.GONE);
                } else {
                    mPhoneTextView.setVisibility(View.VISIBLE);
                    mPhoneTextView.setText(phone);
                    mPhoneTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + phone));
                            startActivity(intent);
                        }
                    });
                }

                if (email.equals("")) {
                    mEmailTextView.setVisibility(View.GONE);
                } else {
                    mEmailTextView.setVisibility(View.VISIBLE);
                    mEmailTextView.setText(email);
                    mEmailTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_SENDTO);
                            intent.setType("text/plain");
                            intent.setData(Uri.parse("mailto:" + email));
                            try {
                                startActivity(intent);
                            } catch (ActivityNotFoundException e) {
                                Snackbar.make(mCoordinatorLayout, "No email client found", Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
                }

                if (officeHours.equals("")) {
                    mOfficeHoursTextView.setVisibility(View.GONE);
                } else {
                    mOfficeHoursTextView.setVisibility(View.VISIBLE);
                    mOfficeHoursTextView.setText(officeHours);
                }

                if (address.equals("")) {
                    mAddressTextView.setVisibility(View.GONE);
                } else {
                    mAddressTextView.setVisibility(View.VISIBLE);
                    mAddressTextView.setText(address);
                    mAddressTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + Uri.encode(address)));
                            try {
                                startActivity(intent);
                            } catch (ActivityNotFoundException e) {
                                Snackbar.make(mCoordinatorLayout, "Cannot start Google Maps", Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
                }

                if (website.equals("")) {
                    mWebsiteTextView.setVisibility(View.GONE);
                } else {
                    mWebsiteTextView.setVisibility(View.VISIBLE);
                    mWebsiteTextView.setText(website);
                    mWebsiteTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                    /*if(!website.startsWith("http://") && !website.startsWith("https://")) {
                        intent.setData(Uri.parse("http://" + website));
                    } else {
                        intent.setData(Uri.parse(website));
                    }*/
                            intent.setData(Uri.parse(website));
                            try {
                                startActivity(intent);
                            } catch (ActivityNotFoundException e) {
                                Snackbar.make(mCoordinatorLayout, "Cannot open the link", Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
                }

            } else {
                finish();
                Snackbar.make(mCoordinatorLayout, "Failed to display the teacher's info", Snackbar.LENGTH_LONG).show();
            }

            teacherCursor.close();
            if(subjectCursor != null) {
                subjectCursor.close();
            }
        }
    }
}
