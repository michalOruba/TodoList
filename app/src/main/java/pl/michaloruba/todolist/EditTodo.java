package pl.michaloruba.todolist;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Date;

import pl.michaloruba.todolist.DAO.DBManager;

public class EditTodo extends AppCompatActivity {
    private static final int MAX_CHARS_IN_TODO_TITLE = 60;
    private DBManager dbManager;
    private EditText realizationDate;
    private Spinner prioritySpinner;
    private Spinner statusSpinner;
    private EditText description;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_todo);

        dbManager = new DBManager(this);
        dbManager.open();
        final TodoItem editedTodo = dbManager.getItemIdByID(getIntent().getLongExtra("todoID", 0L));
        dbManager.close();

        description = findViewById(R.id.todoDescEdit);
        prioritySpinner = findViewById(R.id.prioritySpinnerEdit);
        statusSpinner = findViewById(R.id.statusSpinnerEdit);
        realizationDate = findViewById(R.id.realizationDateEdit);

        description.setText(editedTodo.getDescription());

        prioritySpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, TodoPriority.values()));
        prioritySpinner.setSelection(editedTodo.getPriority().ordinal());

        statusSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, TodoStatus.values()));
        statusSpinner.setSelection(editedTodo.getStatus().ordinal());

        realizationDate.setText(TodoHelper.formatDateToString(editedTodo.getRealizationDate()));
        realizationDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TodoHelper.showDatePicker(EditTodo.this, realizationDate);
            }
        });

        Button saveButton = findViewById(R.id.saveTodoEdit);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date formattedDate = TodoHelper.formatStringToDate(realizationDate.getText().toString());
                if (validateInput(description.getText().toString(), formattedDate)){
                    updateTodo(formattedDate, editedTodo);
                    TodoHelper.startMainActivity(EditTodo.this);
                }
            }
        });
    }

    private void updateTodo(Date formattedDate, TodoItem editedTodo) {
        dbManager.open();
        dbManager.update(editedTodo.getId(), description.getText().toString(), editedTodo.getCreateDate(), formattedDate, prioritySpinner.getSelectedItem().toString(), statusSpinner.getSelectedItem().toString());
        dbManager.close();
    }

    private boolean validateInput(String description, Date realizationDate) {
        return (validateRealizationDate(realizationDate) & validateDescription(description));
    }

    private boolean validateDescription(String description) {
        if (description == null || description.isEmpty()){
            TodoHelper.generateToast(EditTodo.this, R.string.missingTitle);
            return false;
        }
        else if (description.length() > MAX_CHARS_IN_TODO_TITLE) {
            TodoHelper.generateToast(EditTodo.this, R.string.titleTooLong);
            return false;
        } else {
            return true;
        }
    }

    private boolean validateRealizationDate(Date date) {
        Date today = TodoHelper.formatStringToDate(TodoHelper.formatDateToString(new Date()));
        if (date == null){
            TodoHelper.generateToast(EditTodo.this, R.string.deadlineMissing);
            return false;
        }
        else if (today.compareTo(date) > 0) {
            TodoHelper.generateToast(EditTodo.this, R.string.deadlineInThePast);
            return false;
        } else {
            return true;
        }
    }
}