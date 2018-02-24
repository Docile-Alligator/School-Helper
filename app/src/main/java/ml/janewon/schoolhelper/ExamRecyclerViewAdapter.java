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

public class ExamRecyclerViewAdapter extends RecyclerView.Adapter<ExamRecyclerViewAdapter.ViewHolder> {

    private Cursor examCursor;
    private ArrayList<String> nameList;
    private ArrayList<String> timeFromList;
    private ArrayList<Integer> idList;

    public ExamRecyclerViewAdapter(Cursor cursor) {
        examCursor = cursor;
        nameList = new ArrayList<>();
        timeFromList = new ArrayList<>();
        idList = new ArrayList<>();

        for(int i = 0; i < 3; i++) {
            if (examCursor.moveToNext()) {
                String name = examCursor.getString(examCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.EXAM_NAME));
                String timeFrom = examCursor.getString(examCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.EXAM_TIME_FROM));
                int id = examCursor.getInt(examCursor.getColumnIndexOrThrow(SchoolHelperDatabaseHelper.DB_ID));

                nameList.add(name);
                timeFromList.add(timeFrom);
                idList.add(id);
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exam_overview, parent, false);
        return new ViewHolder(relativeLayout);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final int pos = position;
        final RelativeLayout cardView = holder.relativeLayout;
        TextView nameTextView = (TextView) cardView.findViewById(R.id.item_exam_overview_name);
        TextView timeFromTextView = (TextView) cardView.findViewById(R.id.item_exam_overview_time_from);

        nameTextView.setText(nameList.get(pos));
        timeFromTextView.setText(timeFromList.get(pos));

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(cardView.getContext(), ViewExamDetailActivity.class);
                Uri currentUri = ContentUris.withAppendedId(SchoolHelperDatabaseHelper.EXAMS_CONTENT_URI, idList.get(pos));
                intent.setData(currentUri);
                intent.putExtra(ViewExamDetailActivity.EXAM_NAME_KEY, nameList.get(pos));
                cardView.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        if(examCursor.getCount() > 3) {
            return 3;
        }
        return examCursor.getCount();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout relativeLayout;

        public ViewHolder(RelativeLayout v) {
            super(v);
            relativeLayout = v;
        }
    }
}
