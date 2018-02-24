package ml.janewon.schoolhelper;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by alex on 11/16/17.
 */

public class ClassRecyclerViewAdapter extends RecyclerView.Adapter<ClassRecyclerViewAdapter.ViewHolder> {

    private Cursor classCursor;
    private ArrayList<Integer> subjectIdList;
    private ArrayList<String> timeFromList;
    private ArrayList<String> roomList;
    private ArrayList<Integer> idList;

    ClassRecyclerViewAdapter(Cursor cursor) {
        classCursor = cursor;
        subjectIdList = new ArrayList<>();
        timeFromList = new ArrayList<>();
        roomList = new ArrayList<>();
        idList = new ArrayList<>();

        for(int i = 0; i < 3; i++) {
            if (classCursor.moveToNext()) {
                String timeFrom = classCursor.getString(classCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.CLASS_TIME_FROM));
                String room = classCursor.getString(classCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.CLASS_ROOM));
                int subjectId = classCursor.getInt(classCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.SUBJECT_FOREIGN_KEY));
                int id = classCursor.getInt(classCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.DB_ID));

                subjectIdList.add(subjectId);
                timeFromList.add(timeFrom);
                roomList.add(room);
                idList.add(id);
            }
        }
    }

    @Override
    public ClassRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_class_overview, parent, false);
        return new ViewHolder(relativeLayout);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final int pos = position;
        final RelativeLayout relativeLayout = holder.relativeLayout;
        TextView subjectTextView = holder.subjectTextView;
        TextView timeTextView = holder.timeTextView;
        TextView roomTextView = holder.roomTextView;

        Uri subjectUri = Uri.withAppendedPath(SchoolHelperDatabaseHelper.SUBJECTS_CONTENT_URI, Integer.toString(subjectIdList.get(pos)));
        Cursor subjectCursor = relativeLayout.getContext().getContentResolver().query(subjectUri, null, null, null, null);
        String subject = "";
        if(subjectCursor != null && subjectCursor.moveToFirst()) {
            subject = subjectCursor.getString(subjectCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.SUBJECT_NAME));
            subjectTextView.setText(subject);
        }

        final String finalSubject = subject;

        timeTextView.setText(timeFromList.get(pos));
        if(roomList.get(pos).equals("")) {
            roomTextView.setVisibility(View.GONE);
        } else {
            roomTextView.setVisibility(View.VISIBLE);
            roomTextView.setText(roomList.get(pos));
        }

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(relativeLayout.getContext(), ViewClassDetailActivity.class);
                Uri currentClassUri = ContentUris.withAppendedId(SchoolHelperDatabaseHelper.CLASSES_CONTENT_URI, idList.get(pos));
                intent.setData(currentClassUri);
                intent.putExtra(ViewClassDetailActivity.CLASS_SUBJECT_KEY, finalSubject);
                relativeLayout.getContext().startActivity(intent);
            }
        });

        if(subjectCursor != null) {
            subjectCursor.close();
        }
        if(classCursor != null) {
            classCursor.close();
        }
    }

    @Override
    public int getItemCount() {
        if(classCursor.getCount() > 3) {
            return 3;
        }
        return classCursor.getCount();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout relativeLayout;
        private TextView subjectTextView;
        private TextView timeTextView;
        private TextView roomTextView;

        ViewHolder(RelativeLayout v) {
            super(v);
            relativeLayout = v;
            subjectTextView = (TextView) v.findViewById(R.id.item_class_overview_subject);
            timeTextView = (TextView) v.findViewById(R.id.item_class_overview_time);
            roomTextView = (TextView) v.findViewById(R.id.item_class_overview_room);
        }
    }
}
