package pl.michaloruba.todolist;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import pl.michaloruba.todolist.DAO.DBManager;

public class AddTodo extends AppCompatActivity {

    private static final String TAG = "AddTodo";
    private static final int MAX_CHARS_IN_TODO_TITLE = 60;

    private EditText realizationDate;
    private Spinner spinner;
    private EditText description;
    private Button saveButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_todo);

        saveButton = findViewById(R.id.saveTodo);
        spinner = findViewById(R.id.prioritySpinner);
        description = findViewById(R.id.todoDesc);
        realizationDate = findViewById(R.id.realizationDate);

        spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, TodoPriority.values()));

        realizationDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TodoHelper.showDatePicker(AddTodo.this, realizationDate);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date formattedDate = TodoHelper.formatStringToDate(realizationDate.getText().toString());
                if (validateInput(description.getText().toString(), formattedDate)) {
                    insertNewTodo(formattedDate);
                    TodoHelper.startMainActivity(AddTodo.this);
                }
            }
        });
    }

    private void insertNewTodo(Date formattedDate) {
        DBManager dbManager;
        dbManager = new DBManager(this);
        dbManager.open();
        dbManager.insert(description.getText().toString(), new Date(System.currentTimeMillis()), formattedDate, spinner.getSelectedItem().toString(), TodoStatus.NEW.toString());
    }

    private boolean validateInput(String description, Date realizationDate) {
        return (validateRealizationDate(realizationDate) & validateDescription(description));
    }

    private boolean validateDescription(String description) {
        if (description == null || description.isEmpty()){
            TodoHelper.generateToast(AddTodo.this, R.string.missingTitle);
            return false;
        }
        else if (description.length() > MAX_CHARS_IN_TODO_TITLE) {
            TodoHelper.generateToast(AddTodo.this, R.string.titleTooLong);
            return false;
        } else {
            return true;
        }
    }

    private boolean validateRealizationDate(Date date) {
        Date today = TodoHelper.formatStringToDate(TodoHelper.formatDateToString(new Date()));
        if (date == null){
            TodoHelper.generateToast(AddTodo.this, R.string.deadlineMissing);
            return false;
        }
        else if (today.compareTo(date) > 0) {
            TodoHelper.generateToast(AddTodo.this, R.string.deadlineInThePast);
            return false;
        } else {
            return true;
        }
    }
}