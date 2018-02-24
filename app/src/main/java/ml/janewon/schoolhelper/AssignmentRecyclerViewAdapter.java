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

public class AssignmentRecyclerViewAdapter extends RecyclerView.Adapter<AssignmentRecyclerViewAdapter.ViewHolder> {

    private Cursor assignmentCursor;
    private ArrayList<String> titleList;
    private ArrayList<String> dueTimeList;
    private ArrayList<Integer> idList;

    public AssignmentRecyclerViewAdapter(Cursor cursor) {
        assignmentCursor = cursor;
        titleList = new ArrayList<>();
        dueTimeList = new ArrayList<>();
        idList = new ArrayList<>();

        for(int i = 0; i < 3; i++) {
            if (assignmentCursor.moveToNext()) {
                String title = assignmentCursor.getString(assignmentCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.ASSIGNMENT_TITLE));
                String dueTime = assignmentCursor.getString(assignmentCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.ASSIGNMENT_DUE_TIME));
                int id = assignmentCursor.getInt(assignmentCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.DB_ID));

                titleList.add(title);
                dueTimeList.add(dueTime);
                idList.add(id);
            } else {
                break;
            }
        }
    }

    @Override
    public AssignmentRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RelativeLayout cardView = (RelativeLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_assignment_overview, parent, false);

        return new AssignmentRecyclerViewAdapter.ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final int pos = position;
        final RelativeLayout relativeLayout = holder.relativeLayout;
        TextView titleTextView = (TextView) relativeLayout.findViewById(R.id.item_assignment_overview_title);
        TextView dueTimeTextView = (TextView) relativeLayout.findViewById(R.id.item_assignment_overview_due_time);

        titleTextView.setText(titleList.get(pos));
        dueTimeTextView.setText(dueTimeList.get(pos));

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(relativeLayout.getContext(), ViewAssignmentDetailActivity.class);
                Uri currentUri = ContentUris.withAppendedId(SchoolHelperDatabaseHelper.ASSIGNMENTS_CONTENT_URI, idList.get(pos));
                intent.setData(currentUri);
                intent.putExtra(ViewAssignmentDetailActivity.ASSIGNMENT_TITLE_KEY, titleList.get(pos));
                relativeLayout.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(assignmentCursor.getCount() > 3) {
            return 3;
        }
        return assignmentCursor.getCount();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout relativeLayout;

        public ViewHolder(RelativeLayout v) {
            super(v);
            relativeLayout = v;
        }
    }
}
