package ml.janewon.schoolhelper;

import android.content.ContentResolver;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

/**
 * Created by alex on 9/22/17.
 */

public class SchoolHelperDatabaseHelper extends SQLiteOpenHelper {

    static final String CONTENT_AUTHORITY = "ml.janewon.schoolhelper";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final String DB_NAME = "student.db";
    private static final int DB_VERSION = 1;
    public static final String DB_ID = "_id";

    static final String TABLE_SUBJECTS = "SUBJECTS";
    static final String SUBJECT_NAME = "NAME";
    static final String SUBJECT_COLOR = "COLOR";

    static final String TABLE_TEACHERS = "TEACHERS";
    static final String TEACHER_NAME = "NAME";
    static final String TEACHER_SURNAME = "SURNAME";
    //static final String TEACHER_SUBJECT = "SUBJECT";
    static final String TEACHER_OFFICE = "OFFICE";
    static final String TEACHER_OFFICE_HOURS = "OFFICE_HOURS";
    static final String TEACHER_EMAIL = "EMAIL";
    static final String TEACHER_PHONE = "PHONE";
    static final String TEACHER_ADDRESS = "ADDRESS";
    static final String TEACHER_WEBSITE = "WEBSITE";
    static final String TEACHER_PHOTO = "PHOTO";

    static final String TABLE_ASSIGNMENTS = "ASSIGNMENTS";
    //static final String ASSIGNMENT_SUBJECT = "SUBJECT";
    static final String ASSIGNMENT_TITLE = "TITLE";
    static final String ASSIGNMENT_DETAIL = "DETAIL";
    static final String ASSIGNMENT_DUE_DATE = "DUE_DATE";
    static final String ASSIGNMENT_DUE_TIME = "DUE_TIME";

    static final String TABLE_EXAMS = "EXAMS";
    static final String EXAM_NAME = "NAME";
    //static final String EXAM_SUBJECT = "SUBJECT";
    static final String EXAM_SCORE = "GRADE";
    static final String EXAM_DATE = "TIME";
    static final String EXAM_TIME_FROM = "TIME_FROM";
    static final String EXAM_TIME_TO = "TIME_TO";
    static final String EXAM_TYPE = "TYPE";
    static final String EXAM_DETAIL = "DETAIL";

    static final String TABLE_CLASS = "CLASS";
    //static final String CLASS_SUBJECT = "SUBJECT";
    static final String CLASS_TEACHER = "TEACHER";
    static final String CLASS_ROOM = "ROOM";
    static final String CLASS_DAY = "DAY";
    static final String CLASS_TIME_FROM = "TIME_FROM";
    static final String CLASS_TIME_TO = "TIME_TO";
    static final String CLASS_REMARKS = "REMARKS";
    static final String CLASS_COLOR = "COLOR";

    static final String SUBJECT_FOREIGN_KEY = "SUBJECT_FOREIGN_KEY";

    static final Uri SUBJECTS_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, TABLE_SUBJECTS);
    static final Uri TEACHERS_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, TABLE_TEACHERS);
    static final Uri ASSIGNMENTS_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, TABLE_ASSIGNMENTS);
    static final Uri EXAMS_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, TABLE_EXAMS);
    static final Uri CLASSES_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, TABLE_CLASS);

    static final String SUBJECTS_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_SUBJECTS;
    static final String SUBJECTS_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_SUBJECTS;
    static final String TEACHERS_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_TEACHERS;
    static final String TEACHERS_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_TEACHERS;
    static final String ASSIGNMENTS_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_ASSIGNMENTS;
    static final String ASSIGNMENTS_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_ASSIGNMENTS;
    static final String EXAMS_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_EXAMS;
    static final String EXAMS_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_EXAMS;
    static final String CLASS_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_CLASS;
    static final String CLASS_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_CLASS;

    SchoolHelperDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create Subject table
        db.execSQL("CREATE TABLE " + TABLE_SUBJECTS + " ("
                + DB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SUBJECT_NAME + " TEXT UNIQUE, "
                + SUBJECT_COLOR + " INTEGER);");

        //Create Teacher table
        db.execSQL("CREATE TABLE " + TABLE_TEACHERS + " ("
                + DB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TEACHER_NAME + " TEXT, "
                + TEACHER_SURNAME + " TEXT, "
                + TEACHER_OFFICE + " TEXT, "
                + TEACHER_OFFICE_HOURS + " TEXT, "
                + TEACHER_EMAIL + " TEXT, "
                + TEACHER_PHONE + " TEXT, "
                + TEACHER_ADDRESS + " TEXT, "
                + TEACHER_WEBSITE + " TEXT, "
                + TEACHER_PHOTO + " TEXT, "
                + SUBJECT_FOREIGN_KEY + " INTEGER, "
                + "FOREIGN KEY (" + SUBJECT_FOREIGN_KEY + ") REFERENCES " + TABLE_SUBJECTS + "(" + DB_ID + ") ON DELETE CASCADE);");

        //Create Assignment table
        db.execSQL("CREATE TABLE " + TABLE_ASSIGNMENTS + " ("
                + DB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ASSIGNMENT_TITLE + " TEXT, "
                + ASSIGNMENT_DETAIL + " TEXT, "
                + ASSIGNMENT_DUE_DATE + " TEXT, "
                + ASSIGNMENT_DUE_TIME + " TEXT, "
                + SUBJECT_FOREIGN_KEY + " INTEGER, "
                + "FOREIGN KEY (" + SUBJECT_FOREIGN_KEY + ") REFERENCES " + TABLE_SUBJECTS + "(" + DB_ID + ") ON DELETE CASCADE);");

        //Create Grade table
        db.execSQL("CREATE TABLE " + TABLE_EXAMS + " ("
                + DB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + EXAM_NAME + " TEXT, "
                + EXAM_SCORE + " TEXT, "
                + EXAM_DATE + " TEXT, "
                + EXAM_TIME_FROM + " TEXT, "
                + EXAM_TIME_TO + " TEXT, "
                + EXAM_TYPE + " TEXT, "
                + EXAM_DETAIL + " TEXT, "
                + SUBJECT_FOREIGN_KEY + " INTEGER, "
                + "FOREIGN KEY (" + SUBJECT_FOREIGN_KEY + ") REFERENCES " + TABLE_SUBJECTS + "(" + DB_ID + ") ON DELETE CASCADE);");

        db.execSQL("CREATE TABLE " + TABLE_CLASS + " ("
                + DB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CLASS_TEACHER + " TEXT, "
                + CLASS_ROOM + " TEXT, "
                + CLASS_DAY + " TEXT, "
                + CLASS_TIME_FROM + " TEXT, "
                + CLASS_TIME_TO + " TEXT, "
                + CLASS_REMARKS + " TEXT, "
                + CLASS_COLOR + " INTEGER, "
                + SUBJECT_FOREIGN_KEY + " INTEGER, "
                + "FOREIGN KEY (" + SUBJECT_FOREIGN_KEY + ") REFERENCES " + TABLE_SUBJECTS + "(" + DB_ID + ") ON DELETE CASCADE);");
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
