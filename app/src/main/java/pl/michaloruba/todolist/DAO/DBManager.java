package pl.michaloruba.todolist.DAO;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import pl.michaloruba.todolist.TodoItem;
import pl.michaloruba.todolist.TodoPriority;
import pl.michaloruba.todolist.TodoStatus;

public class DBManager {

    private DatabaseHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(String desc, Date createDate, Date realizationDate, String priority, String status) {

        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.DESC, desc);
        contentValue.put(DatabaseHelper.CREATE_DATE, sdf.format(createDate));
        contentValue.put(DatabaseHelper.REALIZATION_DATE, sdf.format(realizationDate));
        contentValue.put(DatabaseHelper.PRIORITY, priority);
        contentValue.put(DatabaseHelper.STATUS, status);
        database.insert(DatabaseHelper.TABLE_NAME, null, contentValue);
    }

    public Cursor fetch() {
        String[] columns = new String[] { DatabaseHelper._ID, DatabaseHelper.DESC, DatabaseHelper.CREATE_DATE, DatabaseHelper.REALIZATION_DATE, DatabaseHelper.PRIORITY, DatabaseHelper.STATUS};
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int update(long _id, String desc, Date createDate, Date realizationDate, String priority, String status) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.DESC, desc);
        contentValues.put(DatabaseHelper.CREATE_DATE, sdf.format(createDate));
        contentValues.put(DatabaseHelper.REALIZATION_DATE, sdf.format(realizationDate));
        contentValues.put(DatabaseHelper.PRIORITY, priority);
        contentValues.put(DatabaseHelper.STATUS, status);
        int i = database.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper._ID + " = " + _id, null);
        return i;
    }

    public int updateStatus(long _id, String status) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.STATUS, status);
        int i = database.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper._ID + " = " + _id, null);
        return i;
    }

    public void delete(long _id) {
        database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper._ID + "=" + _id, null);
    }

    public TodoItem getItemIdByID(Long id) {
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_NAME + " WHERE _id = " + id;
        Cursor cursor = database.rawQuery(query, null);

        TodoItem todo = null;
        try {
            if (cursor.moveToFirst()) {
                String todoId = cursor.getString((cursor.getColumnIndex("_id")));
                String desc = cursor.getString(cursor.getColumnIndex("description"));
                String createDate = cursor.getString(cursor.getColumnIndex("create_date"));
                String realizationDate = cursor.getString(cursor.getColumnIndex("realization_date"));
                String priority = cursor.getString(cursor.getColumnIndex("priority"));
                String status = cursor.getString(cursor.getColumnIndex("status"));

                try {
                    todo = new TodoItem(todoId, desc, sdf.parse(createDate), sdf.parse(realizationDate), TodoPriority.fromString(priority), TodoStatus.fromString(status));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            cursor.close();
        }
        return todo;
    }

}
