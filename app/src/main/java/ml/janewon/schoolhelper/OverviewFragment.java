package ml.janewon.schoolhelper;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class OverviewFragment extends Fragment {

    private RecyclerView mClassRecyclerView;
    private RecyclerView mAssignmentRecyclerView;
    private RecyclerView mExamRecyclerView;

    private View mClassEmptyTextView;
    private ImageView mClassEmptyImageView;
    private View mAssignmentEmptyTextView;
    private ImageView mAssignmentEmptyImageView;
    private View mExamEmptyTextView;
    private ImageView mExamEmptyImageView;

    private ProgressBar mClassProgressBar;
    private ProgressBar mAssignmentProgressBar;
    private ProgressBar mExamProgressBar;

    private RecyclerView.Adapter mClassAdapter;
    private RecyclerView.Adapter mAssignmentAdapter;
    private RecyclerView.Adapter mExamAdapter;

    public OverviewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_overview, container, false);

        TextView todayTextView = (TextView) rootView.findViewById(R.id.today_text_view_overview_fragment);
        Calendar calendar = Calendar.getInstance();
        String today = new SimpleDateFormat("EEEE", Locale.US).format(calendar.getTime());
        todayTextView.setText(today);

        mClassEmptyTextView = rootView.findViewById(R.id.empty_class_text_view_overview_fragment);
        mClassEmptyImageView = (ImageView) rootView.findViewById(R.id.empty_class_image_view_overview_fragment);
        mAssignmentEmptyTextView = rootView.findViewById(R.id.empty_assignments_text_view_overview_fragment);
        mAssignmentEmptyImageView = (ImageView) rootView.findViewById(R.id.empty_assignments_image_view_overview_fragment);
        mExamEmptyTextView = rootView.findViewById(R.id.empty_exams_text_view_overview_fragment);
        mExamEmptyImageView = (ImageView) rootView.findViewById(R.id.empty_exams_image_view_overview_fragment);

        mClassProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar_classes_overview_fragment);
        mAssignmentProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar_assignments_overview_fragment);
        mExamProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar_exams_overview_fragment);

        final FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab_overview_fragment);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.choose_an_action)
                       .setItems(R.array.overview_fab_menu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent;
                        switch (which) {
                            case 0:
                                intent = new Intent(getActivity(), AddClassActivity.class);
                                startActivity(intent);
                                break;
                            case 1:
                                intent = new Intent(getActivity(), AddAssignmentActivity.class);
                                startActivity(intent);
                                break;
                            case 2:
                                intent = new Intent(getActivity(), AddExamActivity.class);
                                startActivity(intent);
                                break;
                            case 3:
                                intent = new Intent(getActivity(), AddSubjectActivity.class);
                                startActivity(intent);
                                break;
                            case 4:
                                intent = new Intent(getActivity(), AddTeacherActivity.class);
                                startActivity(intent);
                                break;
                        }
                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.create().show();
            }
        });

        NestedScrollView nestedScrollView = (NestedScrollView) rootView.findViewById(R.id.nested_scroll_view_overview_fragment);
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(scrollY > oldScrollY) {
                    //Scroll down
                    fab.hide();
                } else if(scrollY < oldScrollY) {
                    //Scroll up
                    fab.show();
                }
            }
        });

        mClassRecyclerView = (RecyclerView) rootView.findViewById(R.id.list_view_class_overview_fragment);
        mClassRecyclerView.setAdapter(mClassAdapter);
        mClassRecyclerView.setNestedScrollingEnabled(false);
        mAssignmentRecyclerView = (RecyclerView) rootView.findViewById(R.id.list_view_assignments_overview_fragment);
        mAssignmentRecyclerView.setAdapter(mAssignmentAdapter);
        mAssignmentRecyclerView.setNestedScrollingEnabled(false);
        mExamRecyclerView = (RecyclerView) rootView.findViewById(R.id.list_view_exams_overview_fragment);
        mExamRecyclerView.setAdapter(mExamAdapter);
        mExamRecyclerView.setNestedScrollingEnabled(false);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        new OverviewAsyncTask().execute();
    }

    private class OverviewAsyncTask extends AsyncTask<Void, Void, Void> {

        private SchoolHelperDatabaseHelper dbHelper;
        private Cursor classCursor;
        private Cursor assignmentCursor;
        private Cursor examCursor;

        @Override
        protected Void doInBackground(Void... voids) {
            dbHelper = new SchoolHelperDatabaseHelper(getActivity());

            Calendar calendar = Calendar.getInstance();
            String date = new SimpleDateFormat(DatePickerFragment.DATE_FORMAT, Locale.US).format(calendar.getTime());
            String dayOfWeek = new SimpleDateFormat("EEEE", Locale.US).format(calendar.getTime());

            classCursor = dbHelper.getReadableDatabase().query(SchoolHelperDatabaseHelper.TABLE_CLASS,
                    new String[]{SchoolHelperDatabaseHelper.DB_ID,
                            SchoolHelperDatabaseHelper.CLASS_ROOM,
                            SchoolHelperDatabaseHelper.CLASS_TIME_FROM,
                            SchoolHelperDatabaseHelper.SUBJECT_FOREIGN_KEY},
                    SchoolHelperDatabaseHelper.CLASS_DAY + "=?",
                    new String[]{dayOfWeek},
                    null, null, null);

            assignmentCursor = dbHelper.getReadableDatabase().query(SchoolHelperDatabaseHelper.TABLE_ASSIGNMENTS,
                    new String[]{SchoolHelperDatabaseHelper.DB_ID,
                            SchoolHelperDatabaseHelper.ASSIGNMENT_TITLE,
                            SchoolHelperDatabaseHelper.ASSIGNMENT_DUE_TIME},
                    SchoolHelperDatabaseHelper.ASSIGNMENT_DUE_DATE + "=?",
                    new String[]{date},
                    null, null, null);

            examCursor = dbHelper.getReadableDatabase().query(SchoolHelperDatabaseHelper.TABLE_EXAMS,
                    new String[]{SchoolHelperDatabaseHelper.DB_ID,
                            SchoolHelperDatabaseHelper.EXAM_NAME,
                            SchoolHelperDatabaseHelper.EXAM_TIME_FROM},
                    SchoolHelperDatabaseHelper.EXAM_DATE + "=?",
                    new String[]{date},
                    null, null, null);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(classCursor.getCount() == 0) {
                mClassRecyclerView.setVisibility(View.GONE);
                mClassEmptyImageView.setVisibility(View.VISIBLE);
                Glide.with(getActivity()).load(R.drawable.no_class).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        mClassProgressBar.setVisibility(View.GONE);

                        mClassEmptyTextView.setVisibility(View.VISIBLE);
                        return false;
                    }
                }).into(mClassEmptyImageView);
            } else {
                mClassProgressBar.setVisibility(View.GONE);
                mClassRecyclerView.setVisibility(View.VISIBLE);
                mClassEmptyTextView.setVisibility(View.GONE);
                mClassEmptyImageView.setVisibility(View.GONE);
                mClassAdapter = new ClassRecyclerViewAdapter(classCursor);
            }

            if(assignmentCursor.getCount() == 0) {
                mAssignmentRecyclerView.setVisibility(View.GONE);
                mAssignmentEmptyImageView.setVisibility(View.VISIBLE);
                Glide.with(getActivity()).load(R.drawable.no_assignment).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        mAssignmentProgressBar.setVisibility(View.GONE);
                        mAssignmentEmptyTextView.setVisibility(View.VISIBLE);
                        return false;
                    }
                }).into(mAssignmentEmptyImageView);
            } else {
                mAssignmentProgressBar.setVisibility(View.GONE);
                mAssignmentRecyclerView.setVisibility(View.VISIBLE);
                mAssignmentEmptyTextView.setVisibility(View.GONE);
                mAssignmentEmptyImageView.setVisibility(View.GONE);
                mAssignmentAdapter = new AssignmentRecyclerViewAdapter(assignmentCursor);
            }

            if(examCursor.getCount() == 0) {
                mExamRecyclerView.setVisibility(View.GONE);
                mExamEmptyImageView.setVisibility(View.VISIBLE);
                Glide.with(getActivity()).load(R.drawable.no_exam).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        mExamProgressBar.setVisibility(View.GONE);
                        mExamEmptyTextView.setVisibility(View.VISIBLE);
                        return false;
                    }
                }).into(mExamEmptyImageView);
            } else {
                mExamProgressBar.setVisibility(View.GONE);
                mExamRecyclerView.setVisibility(View.VISIBLE);
                mExamEmptyTextView.setVisibility(View.GONE);
                mExamEmptyImageView.setVisibility(View.GONE);
                mExamAdapter = new ExamRecyclerViewAdapter(examCursor);
            }

            mClassRecyclerView.setAdapter(mClassAdapter);
            mAssignmentRecyclerView.setAdapter(mAssignmentAdapter);
            mExamRecyclerView.setAdapter(mExamAdapter);

            mClassRecyclerView.setOnFlingListener(null);
            mAssignmentRecyclerView.setOnFlingListener(null);
            mExamRecyclerView.setOnFlingListener(null);
            LinearSnapHelper snapHelper = new LinearSnapHelper();
            snapHelper.attachToRecyclerView(mClassRecyclerView);
            snapHelper.attachToRecyclerView(mAssignmentRecyclerView);
            snapHelper.attachToRecyclerView(mExamRecyclerView);

            mClassRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mAssignmentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mExamRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            classCursor.close();
            assignmentCursor.close();
            examCursor.close();
            dbHelper.close();
        }
    }

}
