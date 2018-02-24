package ml.janewon.schoolhelper;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Fragment mFragmentToSet = null;
    NavigationView navigationView;
    DrawerLayout drawer;
    boolean isSettings = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(0).setChecked(true);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {}

            @Override
            public void onDrawerOpened(View drawerView) {}

            @Override
            public void onDrawerClosed(View drawerView) {
                if(isSettings) {
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    isSettings = false;
                    return;
                }
                if(mFragmentToSet != null) {
                    replaceFragment(mFragmentToSet);
                    mFragmentToSet = null;
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {}
        });

        Intent intent = getIntent();
        if(intent.hasExtra(NotificationUtils.NOTIFICATION_GROUP_INTENT_KEY)) {
            int notificationIntentCategory = intent.getExtras().getInt(NotificationUtils.NOTIFICATION_GROUP_INTENT_KEY);

            switch (notificationIntentCategory) {
                case NotificationUtils.NOTIFICATION_GROUP_ASSIGNMENT_ID:
                    replaceFragment(new AssignmentFragment());
                    setTitle(R.string.fragment_assignments_label);
                    navigationView.getMenu().getItem(2).setChecked(true);
                    break;
                case NotificationUtils.NOTIFICATION_GROUP_EXAM_ID:
                    replaceFragment(new ExamsFragment());
                    setTitle(R.string.fragment_exams_label);
                    navigationView.getMenu().getItem(3).setChecked(true);
                    break;
                case NotificationUtils.NOTIFIACATION_GROUP_CLASS_ID:
                    replaceFragment(new TimeTableFragment());
                    setTitle(R.string.fragment_timetable_label);
                    navigationView.getMenu().getItem(1).setChecked(true);
                    break;
            }
        } else if(intent.hasExtra("fragmentToOpen")) {
            String fragmentValue = intent.getExtras().getString("fragmentToOpen");
            if(fragmentValue.equals("timetable")) {
                replaceFragment(new TimeTableFragment());
                setTitle(R.string.fragment_timetable_label);
                navigationView.getMenu().getItem(1).setChecked(true);
            } else if(fragmentValue.equals("assignments")) {
                replaceFragment(new AssignmentFragment());
                setTitle(R.string.fragment_assignments_label);
                navigationView.getMenu().getItem(2).setChecked(true);
            } else if(fragmentValue.equals("exams")) {
                replaceFragment(new ExamsFragment());
                setTitle(R.string.fragment_exams_label);
                navigationView.getMenu().getItem(3).setChecked(true);
            }
        } else if(savedInstanceState == null) {
            replaceFragment(new OverviewFragment());
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getFragmentManager().findFragmentById(R.id.main_frame_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
        } else if (!(fragment instanceof OverviewFragment)){
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_frame_layout, new OverviewFragment());
            fragmentTransaction.commit();

            setTitle(R.string.fragment_overview_label);
            navigationView.getMenu().getItem(0).setChecked(true);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch(item.getItemId()) {
            case R.id.nav_overview:
                mFragmentToSet = new OverviewFragment();
                setTitle(R.string.app_name);
                break;
            case R.id.nav_timetable:
                mFragmentToSet = new TimeTableFragment();
                setTitle(R.string.fragment_timetable_label);
                break;
            case R.id.nav_assignments:
                mFragmentToSet = new AssignmentFragment();
                setTitle(R.string.fragment_assignments_label);
                break;
            case R.id.nav_exam:
                mFragmentToSet = new ExamsFragment();
                setTitle(R.string.fragment_exams_label);
                break;
            case R.id.nav_subjects:
                mFragmentToSet = new SubjectsFragment();
                setTitle(R.string.fragment_subjects_label);
                break;
            case R.id.nav_teachers:
                mFragmentToSet = new TeachersFragment();
                setTitle(R.string.fragment_teachers_label);
                break;
            case R.id.nav_settings:
                isSettings = true;
                break;
        }
        drawer.closeDrawers();
        return true;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame_layout, fragment).commit();
    }
}
