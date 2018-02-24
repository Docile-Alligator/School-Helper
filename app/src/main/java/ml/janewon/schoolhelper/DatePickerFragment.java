package ml.janewon.schoolhelper;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private static final String dateEditTextKey = "dateEditText";
    TextView mDateEditText;
    public static final String DATE_FORMAT = "E, MMM d, yyyy";

    public static DatePickerFragment newInstance(int editText) {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putInt(dateEditTextKey, editText);
        datePickerFragment.setArguments(args);
        return datePickerFragment;
    }

    public DatePickerFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mDateEditText = (TextView) getActivity().findViewById(getArguments().getInt(dateEditTextKey));
        String time = mDateEditText.getText().toString();

        int year = 0;
        int month = 0;
        int day = 0;
        if(time.equals("")) {
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        } else {
            Locale locale;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                locale = getResources().getConfiguration().getLocales().get(0);
            } else {
                locale = getResources().getConfiguration().locale;
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT, locale);
            Date date;
            try {
                date = simpleDateFormat.parse(time);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        Locale locale;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = getResources().getConfiguration().locale;
        }
        SimpleDateFormat finalDateFormat = new SimpleDateFormat(DATE_FORMAT, locale);
        String finalDate = finalDateFormat.format(calendar.getTime());
        mDateEditText.setText(finalDate);
    }
}
