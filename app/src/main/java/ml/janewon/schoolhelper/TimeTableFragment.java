package ml.janewon.schoolhelper;


import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimeTableFragment extends Fragment implements MonthLoader.MonthChangeListener, WeekView.EventClickListener, WeekView.EventLongPressListener {

    private ArrayList<String> days = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;
    private WeekView mWeekView;

    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private int mWeekViewType = TYPE_THREE_DAY_VIEW;

    public TimeTableFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_time_table, container, false);
        setHasOptionsMenu(true);

        days.add("Sunday");
        days.add("Monday");
        days.add("Tuesday");
        days.add("Wednesday");
        days.add("Thursday");
        days.add("Friday");
        days.add("Saturday");

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab_time_table_fragment);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddClassActivity.class);
                startActivity(intent);
            }
        });
        coordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.coordinator_layout_time_table_fragment);
        mWeekView = (WeekView) rootView.findViewById(R.id.weekView_timetable_fragment);
        mWeekView.setMonthChangeListener(this);
        mWeekView.setOnEventClickListener(this);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mWeekView.notifyDatasetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.fragment_timetale, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_today:
                mWeekView.goToToday();
                break;
            case R.id.action_day_view:
                if (mWeekViewType != TYPE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(1);

                    // Let's change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                break;
            case R.id.action_three_day_view:
                if (mWeekViewType != TYPE_THREE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_THREE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(3);

                    // Let's change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                break;
            case R.id.action_week_view:
                if (mWeekViewType != TYPE_WEEK_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_WEEK_VIEW;
                    mWeekView.setNumberOfVisibleDays(7);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        List<WeekViewEvent> classList = new ArrayList<>();

        SchoolHelperDatabaseHelper dbHelper = new SchoolHelperDatabaseHelper(getActivity());
        Cursor classCursor = dbHelper.getReadableDatabase().query(SchoolHelperDatabaseHelper.TABLE_CLASS,
                new String[]{SchoolHelperDatabaseHelper.DB_ID, SchoolHelperDatabaseHelper.SUBJECT_FOREIGN_KEY, SchoolHelperDatabaseHelper.CLASS_ROOM, SchoolHelperDatabaseHelper.CLASS_DAY,
                        SchoolHelperDatabaseHelper.CLASS_TIME_FROM, SchoolHelperDatabaseHelper.CLASS_TIME_TO, SchoolHelperDatabaseHelper.CLASS_COLOR},
                null, null, null, null, null);

        while (classCursor.moveToNext()) {
            int id = classCursor.getInt(classCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.DB_ID));
            String room = classCursor.getString(classCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.CLASS_ROOM));
            String day = classCursor.getString(classCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.CLASS_DAY));
            String startTimeFromDb = classCursor.getString(classCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.CLASS_TIME_FROM));
            String endTimeFromDb = classCursor.getString(classCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.CLASS_TIME_TO));
            int color = classCursor.getInt(classCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.CLASS_COLOR));
            String subject = Utils.getSubject(getActivity(), classCursor);

            Date startTime;
            Date endTime;
            int startHourWithoutDate;
            int startMinuteWithoutDate;
            int endHourWithoutDate;
            int endMinuteWithoutDate;

            try {
                startTime = new SimpleDateFormat(TimePickerFragment.TIME_FORMAT, Locale.US).parse(startTimeFromDb);
                endTime = new SimpleDateFormat(TimePickerFragment.TIME_FORMAT, Locale.US).parse(endTimeFromDb);
                Calendar startAndEndTimeCalendar = Calendar.getInstance();
                startAndEndTimeCalendar.setTime(startTime);
                startHourWithoutDate = startAndEndTimeCalendar.get(Calendar.HOUR_OF_DAY);
                startMinuteWithoutDate = startAndEndTimeCalendar.get(Calendar.MINUTE);

                startAndEndTimeCalendar.setTime(endTime);
                endHourWithoutDate = startAndEndTimeCalendar.get(Calendar.HOUR_OF_DAY);
                endMinuteWithoutDate = startAndEndTimeCalendar.get(Calendar.MINUTE);

            } catch (ParseException e) {
                e.printStackTrace();
                Snackbar.make(coordinatorLayout, "Error loading your classes", Snackbar.LENGTH_LONG).show();
                return null;
            }

            ArrayList<Integer> days = getEverySpecificDayInMonth(day, newMonth - 1, newYear);

            for(int dayInDays : days) {
                Calendar start = Calendar.getInstance();
                start.set(Calendar.MINUTE, startMinuteWithoutDate);
                start.set(Calendar.HOUR_OF_DAY, startHourWithoutDate);
                start.set(Calendar.DAY_OF_MONTH, dayInDays);
                start.set(Calendar.MONTH, newMonth - 1);
                start.set(Calendar.YEAR, newYear);

                Calendar end = Calendar.getInstance();
                end.set(Calendar.MINUTE, endMinuteWithoutDate);
                end.set(Calendar.HOUR_OF_DAY, endHourWithoutDate);
                end.set(Calendar.DAY_OF_MONTH, dayInDays);
                end.set(Calendar.MONTH, newMonth - 1);
                end.set(Calendar.YEAR, newYear);

                WeekViewEvent studentClass = new WeekViewEvent(id, subject + "\n" + room, start, end);

                if(color == 0) {
                    studentClass.setColor(getResources().getColor(R.color.colorPrimary));
                } else {
                    studentClass.setColor(color);
                }

                classList.add(studentClass);
            }
        }

        classCursor.close();
        dbHelper.close();
        return classList;
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        int color = event.getColor();
        ClassInfoDialogFragment classInfoDialogFragment = ClassInfoDialogFragment.newInstance((int) event.getId(), color);
        classInfoDialogFragment.show(getFragmentManager(), "classInfo");
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {

    }

    private ArrayList<Integer> getEverySpecificDayInMonth(String day, int month, int year) {
        ArrayList<Integer> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.YEAR, year);
        int dayOneDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        Calendar actualDayOfWeek = Calendar.getInstance();
        actualDayOfWeek.set(Calendar.DAY_OF_WEEK, days.indexOf(day) + 1);
        actualDayOfWeek.set(Calendar.MONTH, month);
        actualDayOfWeek.set(Calendar.YEAR, year);
        int actualDayOfWeekIndex = actualDayOfWeek.get(Calendar.DAY_OF_WEEK);

        int dayGap = actualDayOfWeekIndex - dayOneDayOfWeek;

        if(dayGap < 0) {
            calendar.set(Calendar.DAY_OF_MONTH, 1 + (7 - (Math.abs(dayGap))));
        } else {
            calendar.set(Calendar.DAY_OF_MONTH, 1 + (Math.abs(dayGap)));
        }

        while (calendar.get((Calendar.MONTH)) == month) {
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            dates.add(dayOfMonth);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 7);
        }
        return dates;
    }
}
