package ml.janewon.schoolhelper;


import android.app.Fragment;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ExamsFragment extends Fragment {

    TextView mEmptyText;

    Cursor mExamCursor;

    CursorAdapter adapter;

    public ExamsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_exams, container, false);
        mEmptyText = (TextView) rootView.findViewById(R.id.empty_exams_view_exams_fragment);

        final FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab_exams_fragment);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddExamActivity.class);
                startActivity(intent);
            }
        });

        ListView listView = (ListView) rootView.findViewById(R.id.list_view_exams_fragment);
        adapter = new ExamsCursorAdapter(getActivity(), null);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ViewExamDetailActivity.class);
                intent.setData(ContentUris.withAppendedId(SchoolHelperDatabaseHelper.EXAMS_CONTENT_URI, (int) id));
                TextView nameTextViewInsideListView = (TextView) view.findViewById(R.id.item_exam_name);
                intent.putExtra(ViewExamDetailActivity.EXAM_NAME_KEY, nameTextViewInsideListView.getText().toString());
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
        new ExamFragmentAsyncTask().execute();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(mExamCursor != null) {
            mExamCursor.close();
        }
    }

    private class ExamFragmentAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            String[] projection = new String[]{SchoolHelperDatabaseHelper.DB_ID,
                    SchoolHelperDatabaseHelper.EXAM_NAME,
                    SchoolHelperDatabaseHelper.EXAM_DATE,
                    SchoolHelperDatabaseHelper.EXAM_TIME_FROM,
                    SchoolHelperDatabaseHelper.EXAM_TIME_TO,
                    SchoolHelperDatabaseHelper.SUBJECT_FOREIGN_KEY};
            if(mExamCursor != null) {
                mExamCursor.close();
            }
            mExamCursor = getActivity().getContentResolver().query(SchoolHelperDatabaseHelper.EXAMS_CONTENT_URI,
                    projection,
                    null, null, null);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(mExamCursor != null && mExamCursor.getCount() >= 1) {
                mEmptyText.setVisibility(View.GONE);
                adapter.swapCursor(mExamCursor);
            } else {
                adapter.swapCursor(null);
                mEmptyText.setVisibility(View.VISIBLE);
            }
        }
    }
}
