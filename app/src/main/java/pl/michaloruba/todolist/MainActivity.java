package pl.michaloruba.todolist;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import pl.michaloruba.todolist.DAO.DBManager;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    List<TodoItem> todos;
    ListView listView;
    MyToDoListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        populateHillList();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddTodo.class));
            }
        });

        checkExpiredTodos();
    }

    private void checkExpiredTodos() {
        for (TodoItem todoItem : todos){
            Date realizationDate = todoItem.getRealizationDate();

            if (!(TodoStatus.DONE.equals(todoItem.getStatus())) && checkIfExpired(realizationDate)){
                createNotification(todoItem);
            }
        }
    }

    private void createNotification(TodoItem todoItem) {
        String CHANNEL_ID = "my_channel_01";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(getResources().getIdentifier("todo","drawable", getPackageName()))

                .setContentTitle(getString(R.string.expired_todo_title))
                .setContentText(MainActivity.this.getString(R.string.task) + " " + todoItem.getDescription() + getProperState(todoItem.getRealizationDate()))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)
                .setAutoCancel(true);
        NotificationManager notificationManagerCompat = (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManagerCompat.notify(1, builder.build());
    }

    private boolean checkIfExpired(Date date) {
        Date today = Calendar.getInstance().getTime();
        return today.compareTo(date) > 0;
    }

    private String getProperState(Date date){
        Date today = TodoHelper.formatStringToDate(TodoHelper.formatDateToString(new Date()));
        if(today.compareTo(date) > 0) {
            return " " + this.getString(R.string.isOverDeadline);
        }
        else{
            return " " + this.getString(R.string.mustBeDoneToday);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            verifyStoragePermissions(this);
        }
        else {
            if (id == R.id.action_download) {
                String fileName = "TodoListExport.txt";
                String root = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + fileName;
                File file = new File(root);

                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), StandardCharsets.UTF_8))){
                    for (TodoItem todoItem : todos) {
                        writer.write(String.format("ID: %d, Nazwa: %s, Data utworzenia: %s, Data Realizacji: %s, Status: %s, Priorytet: %s", todoItem.getId(), todoItem.getDescription(),
                                TodoHelper.formatDateToString(todoItem.getCreateDate()), TodoHelper.formatDateToString(todoItem.getRealizationDate()), todoItem.getStatus().toString(), todoItem.getPriority().toString()));
                        writer.newLine();
                    }
                    Toast.makeText(this, getString(R.string.savedTo) + " " + root, Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (id == R.id.action_sort) {
                showSortDialog();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public void populateHillList(){
        todos = new ArrayList<>();
        try {
            todos = getTodosFromDB();
        } catch (ParseException e){
            Log.d(TAG, "populateHillList: ", e);
        }

        adapter = new MyToDoListAdapter(this, todos);
        listView = findViewById(R.id.todo_list);
        listView.setAdapter(adapter);
    }

    public List<TodoItem> getTodosFromDB() throws ParseException {
        List<TodoItem> todos = new ArrayList<>();
        DBManager dbManager = new DBManager(this);
        dbManager.open();
        Cursor cursor = dbManager.fetch();
        while (cursor.moveToNext()){
            String id = cursor.getString((cursor.getColumnIndex("_id")));
            String desc = cursor.getString(cursor.getColumnIndex("description"));
            String createDate = cursor.getString(cursor.getColumnIndex("create_date"));
            String realizationDate = cursor.getString(cursor.getColumnIndex("realization_date"));
            String priority = cursor.getString(cursor.getColumnIndex("priority"));
            String status = cursor.getString(cursor.getColumnIndex("status"));

            todos.add(new TodoItem(id, desc, TodoHelper.formatStringToDate(createDate), TodoHelper.formatStringToDate(realizationDate), TodoPriority.fromString(priority), TodoStatus.fromString(status)));
        }
        dbManager.close();
        return todos;
    }

    private void showSortDialog() {
        final Dialog myDialog = new Dialog(this);
        myDialog.setContentView(R.layout.sort_selection);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final RadioGroup sortRadioGroup = myDialog.findViewById(R.id.sortRadioGroup);
        Button dialogButton = myDialog.findViewById(R.id.sort_dialog_button);
        myDialog.show();

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int checkSortMethod = sortRadioGroup.getCheckedRadioButtonId();
                myDialog.cancel();
                sortList(checkSortMethod);
            }
        });
    }

    private void sortList(final int checkSortMethod) {
                Collections.sort(todos, new Comparator<TodoItem>(){
                    @Override
                    public int compare(TodoItem todoItem1, TodoItem todoItem2) {
                        switch (checkSortMethod){
                            case R.id.createDateRadioButton: {
                                return todoItem1.getCreateDate().compareTo(todoItem2.getCreateDate());
                            }
                            case R.id.realizationDateRadioButton:{
                                return todoItem1.getRealizationDate().compareTo(todoItem2.getRealizationDate());
                            }
                            case R.id.priorityRadioButton:{
                                return todoItem1.getPriority().compareTo(todoItem2.getPriority());
                            }
                            case R.id.nameRadioButton:{
                                return todoItem1.getDescription().toLowerCase().compareTo(todoItem2.getDescription().toLowerCase());
                            }
                            case R.id.statusRadioButton:{
                                return todoItem1.getStatus().compareTo(todoItem2.getStatus());
                            }
                            default:
                                return todoItem1.getDescription().compareTo(todoItem2.getDescription());
                        }
                    }
                });
        adapter.notifyDataSetChanged();
    }
}
