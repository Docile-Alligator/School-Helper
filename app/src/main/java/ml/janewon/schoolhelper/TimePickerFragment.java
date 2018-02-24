package ml.janewon.schoolhelper;


import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

    private static final String timeTextViewKey = "timeTextViewKey";

    TextView mTimeTextView;
    public static final String TIME_FORMAT = "h:mm aa";

    public static TimePickerFragment newInstance(int editText) {
        TimePickerFragment timePickerFragment = new TimePickerFragment();
        Bundle args = new Bundle();
        args.putInt(timeTextViewKey, editText);
        timePickerFragment.setArguments(args);
        return timePickerFragment;
    }

    public TimePickerFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int timeTextViewResource = getArguments().getInt(timeTextViewKey);
        mTimeTextView = (TextView) getActivity().findViewById(timeTextViewResource);
        String time = mTimeTextView.getText().toString();

        switch (timeTextViewResource) {
            case R.id.activity_add_exam_time_from:
                TextView timeToEditText = (TextView) getActivity().findViewById(R.id.activity_add_exam_time_to);
                return setTimeRelatively(timeToEditText, time, -1);
            case R.id.activity_add_exam_time_to:
                TextView timeFromEditText = (TextView) getActivity().findViewById(R.id.activity_add_exam_time_from);
                return setTimeRelatively(timeFromEditText, time, 1);
            case R.id.activity_add_to_timetable_time_from:
                TextView classTimeToEditTextView = (TextView) getActivity().findViewById(R.id.activity_add_to_timetable_time_to);
                return setTimeRelatively(classTimeToEditTextView, time, -1);
            case R.id.activity_add_to_timetable_time_to:
                TextView classTimeFromEditText = (TextView) getActivity().findViewById(R.id.activity_add_to_timetable_time_from);
                return setTimeRelatively(classTimeFromEditText, time, 1);
            default:
                return setTimeRelatively(mTimeTextView, time, 1);
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TIME_FORMAT, getResources().getConfiguration().locale);
        String time = simpleDateFormat.format(calendar.getTime());

        mTimeTextView.setText(time);
    }

    private TimePickerDialog setTimeRelatively(TextView relativeTimeTextView, String time, int compareToRelativeTime) {
        String relativeTime = relativeTimeTextView.getText().toString();

        if(time.equals("") && !relativeTime.equals("")) {
            int relativeHour;
            int relativeMinute;

            Locale locale;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                locale = getResources().getConfiguration().getLocales().get(0);
            } else {
                locale = getResources().getConfiguration().locale;
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TIME_FORMAT, locale);
            Date date;

            try {
                date = simpleDateFormat.parse(relativeTime);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                relativeHour = calendar.get(Calendar.HOUR_OF_DAY);
                relativeMinute = calendar.get(Calendar.MINUTE);
                if(compareToRelativeTime < 0) {
                    return new TimePickerDialog(getActivity(), this, relativeHour - 1, relativeMinute, false);
                } else if(compareToRelativeTime > 0){
                    return new TimePickerDialog(getActivity(), this, relativeHour + 1, relativeMinute, false);
                } else {
                    return new TimePickerDialog(getActivity(), this, relativeHour, relativeMinute, false);
                }
            } catch (ParseException e) {
                e.printStackTrace();
                return new TimePickerDialog(getActivity(), this, 12, 0, DateFormat.is24HourFormat(getActivity()));
            }
        } else {
            int hour;
            int minute;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TIME_FORMAT, getResources().getConfiguration().locale);
            Date date;

            try {
                date = simpleDateFormat.parse(time);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                hour = calendar.get(Calendar.HOUR_OF_DAY);
                minute = calendar.get(Calendar.MINUTE);
                return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
            } catch (ParseException e) {
                e.printStackTrace();
                return new TimePickerDialog(getActivity(), this, 12, 0, DateFormat.is24HourFormat(getActivity()));
            }
        }
    }
}
