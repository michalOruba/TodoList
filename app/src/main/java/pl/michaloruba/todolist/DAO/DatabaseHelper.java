package pl.michaloruba.todolist.DAO;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Table Name
    public static final String TABLE_NAME = "TODO";

    // Table columns
    public static final String _ID = "_id";
    public static final String DESC = "description";
    public static final String CREATE_DATE = "create_date";
    public static final String REALIZATION_DATE = "realization_date";
    public static final String PRIORITY = "priority";
    public static final String STATUS = "status";

    // Database Information
    static final String DB_NAME = "JOURNALDEV_COUNTRIES.DB";

    // database version
    static final int DB_VERSION = 2;

    // Creating table query
    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + DESC + " TEXT NOT NULL, " + CREATE_DATE + " DATE, " + REALIZATION_DATE + " DATE, " + PRIORITY + " TEXT, " + STATUS + " TEXT);";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
