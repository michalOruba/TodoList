package pl.michaloruba.todolist;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

class TodoHelper {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    static void generateToast(Context context, int p) {
        Toast.makeText(context, context.getString(p), Toast.LENGTH_LONG).show();
    }

    static void startMainActivity(Context context) {
        Intent main = new Intent(context, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        context.startActivity(main);
    }

    static Date formatStringToDate(String date) {
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    static String formatDateToString(Date date){
        return sdf.format(date);
    }


    static void showDatePicker(Context context, final EditText realizationDate) {
        final Calendar cldr = Calendar.getInstance();
        final int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        DatePickerDialog picker = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        realizationDate.setText("");
                        cldr.set(Calendar.YEAR, year);
                        cldr.set(Calendar.MONTH, monthOfYear);
                        cldr.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        realizationDate.setText(TodoHelper.formatDateToString(cldr.getTime()));
                    }
                }, year, month, day);
        picker.show();
    }
}
