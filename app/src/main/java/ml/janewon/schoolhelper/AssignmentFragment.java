package ml.janewon.schoolhelper;


import android.app.Fragment;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class AssignmentFragment extends Fragment {

    TextView mEmptyText;

    Cursor mAssignmentCursor;

    AssignmentCursorAdapter adapter;

    public AssignmentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_assignment, container, false);
        mEmptyText = (TextView) rootView.findViewById(R.id.empty_assignments_view_assignments_fragment);

        final FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab_assignment_fragment);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddAssignmentActivity.class);
                startActivity(intent);
            }
        });

        ListView listView = (ListView) rootView.findViewById(R.id.list_view_assignment_fragment);
        adapter = new AssignmentCursorAdapter(getActivity(), null);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ViewAssignmentDetailActivity.class);
                Uri currentUri = ContentUris.withAppendedId(SchoolHelperDatabaseHelper.ASSIGNMENTS_CONTENT_URI, id);
                intent.setData(currentUri);
                TextView titleTextViewInsideListView = (TextView) view.findViewById(R.id.item_assignment_title);
                intent.putExtra(ViewAssignmentDetailActivity.ASSIGNMENT_TITLE_KEY, titleTextViewInsideListView.getText().toString());
                startActivity(intent);
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            private int lastVisibleItem = 0;
            private int lastY = 0;

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int top = 0;
                if(view.getChildAt(0) != null) {
                    top = view.getChildAt(0).getTop();
                }

                if(firstVisibleItem > lastVisibleItem) {
                    fab.hide();
                } else if(firstVisibleItem < lastVisibleItem) {
                    fab.show();
                } else {
                    if(top < lastY) {
                        fab.hide();
                    } else {
                        fab.show();
                    }
                }

                lastVisibleItem = firstVisibleItem;
                lastY = top;
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        new AssignmentFragmentAsyncTask().execute();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(mAssignmentCursor != null) {
            mAssignmentCursor.close();
        }
    }

    private class AssignmentFragmentAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            String[] projection = {
                    SchoolHelperDatabaseHelper.DB_ID,
                    SchoolHelperDatabaseHelper.ASSIGNMENT_TITLE,
                    SchoolHelperDatabaseHelper.SUBJECT_FOREIGN_KEY,
                    SchoolHelperDatabaseHelper.ASSIGNMENT_DUE_DATE,
                    SchoolHelperDatabaseHelper.ASSIGNMENT_DUE_TIME
            };
            if(mAssignmentCursor != null) {
                mAssignmentCursor.close();
            }
            mAssignmentCursor = getActivity().getContentResolver().query(SchoolHelperDatabaseHelper.ASSIGNMENTS_CONTENT_URI,
                    projection,
                    null, null, null);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(mAssignmentCursor != null && mAssignmentCursor.getCount() >= 1) {
                mEmptyText.setVisibility(View.GONE);
                adapter.swapCursor(mAssignmentCursor);
            } else {
                adapter.swapCursor(null);
                mEmptyText.setVisibility(View.VISIBLE);
            }
        }
    }
}
