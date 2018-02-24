package ml.janewon.schoolhelper;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class DayPickerFragment extends DialogFragment {

    private static final String dayTextViewKey = "dayTextView";
    TextView mDayTextView;
    final String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    public static DayPickerFragment newInstance(int editText) {
        DayPickerFragment dayPickerFragment = new DayPickerFragment();
        Bundle args = new Bundle();
        args.putInt(dayTextViewKey, editText);
        dayPickerFragment.setArguments(args);
        return dayPickerFragment;
    }

    public DayPickerFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        mDayTextView = (TextView) getActivity().findViewById(getArguments().getInt(dayTextViewKey));

        builder.setTitle("Choose a day").setItems(days, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String day = days[which];
                mDayTextView.setText(day);
            }
        });
        return builder.create();
    }


}
