package ml.janewon.schoolhelper;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

/**
 * A simple {@link PreferenceFragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Preference mClassPref;
    private Preference mAssignmentPref;
    private Preference mExamPref;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        mClassPref = findPreference(SettingsUtils.NOTIFICATION_CLASS_TIME_KEY);
        mAssignmentPref = findPreference(SettingsUtils.NOTIFICATION_ASSIGNMENT_TIME_KEY);
        mExamPref = findPreference(SettingsUtils.NOTIFICATION_EXAM_TIME_KEY);


        setNotificationState(getPreferenceManager().getSharedPreferences(), SettingsUtils.NOTIFICATION_SWITCH_KEY);
        setNotificationSummary(mClassPref, " class");
        setNotificationSummary(mAssignmentPref, " assignment");
        setNotificationSummary(mExamPref, " exam");
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(SettingsUtils.NOTIFICATION_SWITCH_KEY)) {
            setNotificationState(sharedPreferences, key);
        } else if(key.equals(SettingsUtils.NOTIFICATION_CLASS_TIME_KEY)) {
            setNotificationSummary(mClassPref, " class");
        } else if(key.equals(SettingsUtils.NOTIFICATION_ASSIGNMENT_TIME_KEY)) {
            setNotificationSummary(mAssignmentPref, " assignment");
        } else if(key.equals(SettingsUtils.NOTIFICATION_EXAM_TIME_KEY)) {
            setNotificationSummary(mExamPref, " exam");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    private void setNotificationSummary(Preference pref, String postfix) {
        if(pref instanceof ListPreference) {
            CharSequence entry = ((ListPreference) pref).getEntry();
            pref.setSummary(entry + postfix);
        }
    }

    private void setNotificationState(SharedPreferences sharedPreferences, String key) {
        if(sharedPreferences.getBoolean(key, false)) {
            mClassPref.setEnabled(true);
            mAssignmentPref.setEnabled(true);
            mExamPref.setEnabled(true);
        } else {
            mClassPref.setEnabled(false);
            mAssignmentPref.setEnabled(false);
            mExamPref.setEnabled(false);
        }
    }
}
