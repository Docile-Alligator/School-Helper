package ml.janewon.schoolhelper;


import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
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
public class SubjectsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    TextView mEmptyText;

    SubjectCursorAdapter adapter;

    private static final int SUBJECT_LOADER = 0;

    public SubjectsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_subjects, container, false);
        mEmptyText = (TextView) rootView.findViewById(R.id.empty_subjects_view_subjects_fragment);

        final FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab_subject_fragment);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddSubjectActivity.class);
                startActivity(intent);
            }
        });

        ListView listView = (ListView) rootView.findViewById(R.id.list_view_subject_fragment);
        adapter = new SubjectCursorAdapter(getActivity(), null);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), AddSubjectActivity.class);
                intent.setData(ContentUris.withAppendedId(SchoolHelperDatabaseHelper.SUBJECTS_CONTENT_URI, (int) id));
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
        getLoaderManager().initLoader(SUBJECT_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{SchoolHelperDatabaseHelper.DB_ID,
                SchoolHelperDatabaseHelper.SUBJECT_NAME,
                SchoolHelperDatabaseHelper.SUBJECT_COLOR};
        return new CursorLoader(getActivity(),
                SchoolHelperDatabaseHelper.SUBJECTS_CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.getCount() >= 1) {
            mEmptyText.setVisibility(View.GONE);
            adapter.swapCursor(data);
        } else {
            adapter.swapCursor(null);
            mEmptyText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
