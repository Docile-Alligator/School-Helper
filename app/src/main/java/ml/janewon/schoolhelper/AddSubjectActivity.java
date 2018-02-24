package ml.janewon.schoolhelper;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

public class AddSubjectActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, ColorPickerDialogListener {
    private static final int EXISTING_SUBJECT_LOADER = 0;
    private static final int DIALOG_ID = 0;

    private Uri mCurrentSubjectUri;
    private int mColorValue;

    private CoordinatorLayout mCoordinatorLayout;

    private GradientDrawable mCircleDrawable;
    private LinearLayout mColorWrapper;
    private View mColorView;
    private TextView mColorText;
    private EditText mSubjectNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mColorValue = getResources().getColor(R.color.colorPrimary);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_add_subject_coordinator_layout);

        mColorWrapper = (LinearLayout) findViewById(R.id.activity_add_subject_color_wrapper);
        mColorView = findViewById(R.id.activity_add_subject_color);
        mColorText = (TextView) findViewById(R.id.activity_add_subject_color_text);
        mCircleDrawable = (GradientDrawable) getResources().getDrawable(R.drawable.circle);
        mSubjectNameTextView = (EditText) findViewById(R.id.activity_add_subject_name);

        Intent intent = getIntent();
        mCurrentSubjectUri = intent.getData();
        if(mCurrentSubjectUri != null) {
            setTitle("Edit Subject");
            getLoaderManager().initLoader(EXISTING_SUBJECT_LOADER, null, this);
        } else {
            mCircleDrawable.setColor(getResources().getColor(R.color.colorPrimary));
            mColorView.setBackground(mCircleDrawable);
            String hexColor = "#" + Integer.toHexString(getResources().getColor(R.color.colorPrimary)).toUpperCase();
            mColorText.setText(hexColor);

            mColorWrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.hideKeyboard(AddSubjectActivity.this);

                    ColorPickerDialog.newBuilder()
                            .setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                            .setAllowPresets(false)
                            .setDialogId(DIALOG_ID)
                            .setColor(getResources().getColor(R.color.colorPrimary))
                            .setShowAlphaSlider(true)
                            .show(AddSubjectActivity.this);
                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String colorText = mColorText.getText().toString();
        outState.putString("colorText", colorText);
        outState.putInt("color", mColorValue);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String colorText = savedInstanceState.getString("colorText");
        int color = savedInstanceState.getInt("color");
        mColorText.setText(colorText);
        mColorValue = color;
        mCircleDrawable.setColor(color);
        mColorView.setBackground(mCircleDrawable);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.button_save_data, menu);
        if(mCurrentSubjectUri != null) {
            getMenuInflater().inflate(R.menu.button_delete_data, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                //Hide Virtual Keyboard
                Utils.hideKeyboard(this);

                String name = mSubjectNameTextView.getText().toString().trim();
                if(name.equals("")) {
                    Snackbar.make(mCoordinatorLayout, "Please type your subject name", Snackbar.LENGTH_LONG).show();
                    return false;
                }

                ContentValues contentValues = new ContentValues();
                contentValues.put(SchoolHelperDatabaseHelper.SUBJECT_NAME, name);
                contentValues.put(SchoolHelperDatabaseHelper.SUBJECT_COLOR, mColorValue);

                if(mCurrentSubjectUri != null) {
                    int rowsUpdated = getContentResolver().update(mCurrentSubjectUri, contentValues, null, null);

                    if(rowsUpdated == 0) {
                        Toast.makeText(this, "Error updating this subject", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                } else {
                    Uri newUri = getContentResolver().insert(SchoolHelperDatabaseHelper.SUBJECTS_CONTENT_URI, contentValues);

                    if(newUri == null) {
                        Toast.makeText(this, "Error saving this subject", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }

                Intent resultIntent = new Intent();
                resultIntent.putExtra(SubjectPickerFragment.ADD_SUBJECT_KEY, name);
                setResult(RESULT_OK, resultIntent);

                finish();
                return true;

            case R.id.action_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Deleting this subject will also remove all the classes, assignments, exams and teachers related to it. Continue?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int rowsDeleted = getContentResolver().delete(mCurrentSubjectUri, null, null);
                                if(rowsDeleted < 1) {
                                    Snackbar.make(mCoordinatorLayout, "Error deleting the subject", Snackbar.LENGTH_LONG).show();
                                    dialog.dismiss();
                                } else {
                                    finish();
                                }
                            }
                        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                mCurrentSubjectUri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data != null && data.moveToFirst()) {
            String name = data.getString(data.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.SUBJECT_NAME));
            final int color = data.getInt(data.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.SUBJECT_COLOR));

            mColorWrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.hideKeyboard(AddSubjectActivity.this);

                    ColorPickerDialog.newBuilder()
                            .setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                            .setAllowPresets(false)
                            .setDialogId(DIALOG_ID)
                            .setColor(color)
                            .setShowAlphaSlider(true)
                            .show(AddSubjectActivity.this);
                }
            });

            mColorValue = color;
            mSubjectNameTextView.setText(name);
            mCircleDrawable.setColor(color);
            mColorView.setBackground(mCircleDrawable);

            String hexColor = "#" + Integer.toHexString(color).toUpperCase();
            mColorText.setText(hexColor);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mSubjectNameTextView.setText("");
        mCircleDrawable.setColor(getResources().getColor(R.color.colorPrimary));
        mColorView.setBackground(mCircleDrawable);
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        mCircleDrawable.setColor(color);
        mColorView.setBackground(mCircleDrawable);
        String hexColor = "#" + Integer.toHexString(color).toUpperCase();
        mColorText.setText(hexColor);
        mColorValue = color;
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }
}
