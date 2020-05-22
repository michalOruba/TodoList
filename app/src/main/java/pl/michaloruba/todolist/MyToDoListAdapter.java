package pl.michaloruba.todolist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pl.michaloruba.todolist.DAO.DBManager;

public class MyToDoListAdapter extends ArrayAdapter<TodoItem> {

    private DBManager dbManager;

    MyToDoListAdapter(Activity context, List<TodoItem> items) {
        super(context,0, items);
        dbManager = new DBManager(context);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        final TodoItem currentItem = getItem(position);

        if (listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.todo_list_item, parent, false);
        }

        final TextView todoDescription = listItemView.findViewById(R.id.todo_description);
        todoDescription.setText(currentItem.getDescription());
        todoDescription.setBackgroundColor(setPriorityColor(currentItem.getPriority()));
        setStatusColor(currentItem, todoDescription);


        final TextView todoRealizationDateTv = listItemView.findViewById(R.id.todo_realization_tv);
        todoRealizationDateTv.setText("Termin: " + TodoHelper.formatDateToString(currentItem.getRealizationDate()));
        todoRealizationDateTv.setBackgroundColor(setPriorityColor(currentItem.getPriority()));
        setStatusColor(currentItem, todoRealizationDateTv);

        ImageView todoEditButton = listItemView.findViewById(R.id.todo_edit);
        todoEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EditTodo.class);
                intent.putExtra("todoID", currentItem.getId());
                getContext().startActivity(intent);
            }
        });

        ImageView todoDeleteButton = listItemView.findViewById(R.id.todo_delete);
        todoDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext())
                        .setMessage(getContext().getString(R.string.doDelete))
                        .setCancelable(true)
                        .setPositiveButton(getContext().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") int id) {
                                dbManager.open();
                                dbManager.delete(currentItem.getId());
                                dbManager.close();
                                remove(getItem(position));
                                deleteAttachments(currentItem);
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(getContext().getString(R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, @SuppressWarnings("unused") int id) {

                            }
                        });
                final AlertDialog alert = dialogBuilder.create();
                alert.show();
            }
        });

        ImageView todoAttachButton = listItemView.findViewById(R.id.todo_attach);
        todoAttachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AttachMedia.class);
                intent.putExtra("todoID", currentItem.getId());
                getContext().startActivity(intent);
            }
        });

        listItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentItem.toggleStatus();
                setStatusColor(currentItem, todoDescription);
                setStatusColor(currentItem, todoRealizationDateTv);
                dbManager.open();
                dbManager.updateStatus(currentItem.getId(), currentItem.getStatus().toString());
                dbManager.close();
            }
        });

        return listItemView;
    }

    private void deleteAttachments(TodoItem currentItem) {
        File sdPictureFiles = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File[] files = sdPictureFiles.listFiles();

        for (File value : files) {
            if (value.getName().charAt(0) == String.valueOf(currentItem.getId()).charAt(0)) {
                value.delete();
            }
        }
    }

    private int setPriorityColor(TodoPriority priority){
        switch (priority){
            case LOW:
                return ContextCompat.getColor(getContext(), R.color.lowPriority);
            case MEDIUM:
                return ContextCompat.getColor(getContext(), R.color.mediumPriority);
            case HIGH:
                return ContextCompat.getColor(getContext(), R.color.highPriority);
            default:
                return ContextCompat.getColor(getContext(), R.color.white);
        }
    }

    private void setStatusColor(TodoItem todoItem, TextView todoDescription){
        if (TodoStatus.DONE.equals(todoItem.getStatus())){
            todoDescription.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.lightGray));
            todoDescription.setPaintFlags(todoDescription.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else {
            todoDescription.setBackgroundColor(setPriorityColor(todoItem.getPriority()));
            todoDescription.setPaintFlags(todoDescription.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }
}
