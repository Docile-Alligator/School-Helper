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
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class TeachersFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    TextView mEmptyText;

    private static final int TEACHER_LOADER = 0;
    CursorAdapter adapter;

    public TeachersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_teachers, container, false);
        mEmptyText = (TextView) rootView.findViewById(R.id.empty_teachers_view_teachers_fragment);

        final FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab_teacher_fragment);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddTeacherActivity.class);
                startActivity(intent);
            }
        });

        ListView listView = (ListView) rootView.findViewById(R.id.list_view_teacher_fragment);
        adapter = new TeacherCursorAdapter(getActivity(), null);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ViewTeacherDetailActivity.class);
                intent.setData(ContentUris.withAppendedId(SchoolHelperDatabaseHelper.TEACHERS_CONTENT_URI, (int) id));
                TextView nameTextViewInsideListView = (TextView) view.findViewById(R.id.item_teacher_name);
                intent.putExtra(ViewTeacherDetailActivity.TEACHER_NAME_KEY, nameTextViewInsideListView.getText().toString());
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

        getLoaderManager().initLoader(TEACHER_LOADER, null, this);
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{SchoolHelperDatabaseHelper.DB_ID,
                SchoolHelperDatabaseHelper.TEACHER_PHOTO,
                SchoolHelperDatabaseHelper.TEACHER_NAME,
                SchoolHelperDatabaseHelper.TEACHER_SURNAME,
                SchoolHelperDatabaseHelper.TEACHER_OFFICE};

        return new CursorLoader(getActivity(),
                SchoolHelperDatabaseHelper.TEACHERS_CONTENT_URI,
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
