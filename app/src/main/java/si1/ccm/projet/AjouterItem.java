package si1.ccm.projet;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AjouterItem extends AppCompatActivity {

    private EditText etDate, etTime;
    private Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajouter_item);

        Button val = (Button) findViewById(R.id.valider_add);
        Spinner spin = (Spinner) findViewById(R.id.choixTag_add);

        FloatingActionButton fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.choix, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spin.setAdapter(adapter);
        val.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nom = (String) ((EditText)findViewById(R.id.nom_add)).getText().toString();
                String choix = (String) ((Spinner)findViewById(R.id.choixTag_add)).getSelectedItem().toString();
                String datePicker = ((EditText) findViewById(R.id.datepicker_add)).getText().toString();
                String timePicker = ((EditText) findViewById(R.id.timepicker_add)).getText().toString();

                if(nom.isEmpty())
                    Snackbar.make(view, "Veuillez entrer un nom de tâche", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                else if(datePicker.isEmpty() || timePicker.isEmpty())
                    Snackbar.make(view, "Veuillez entrer une date et une heure d'échéance pour la tâche", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                else {
                    Date date = null;
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy' 'HH:mm");
                    sdf.setLenient(false);
                    try {
                        date = sdf.parse(datePicker + " " + timePicker);
                    } catch(Exception e) {
                        Snackbar.make(view, "Format de date ou heure incorrect", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }

                    TodoItem item = new TodoItem(TodoItem.getTagFor(choix), nom, date);
                    long itemID = TodoDbHelper.addItem(item, getBaseContext());
                    item.setId(itemID);
                    setResult(itemID == -1 ? -1 : 0);
                    finish();
                }
            }
        });

        final DatePickerDialog.OnDateSetListener datepicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "dd/MM/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
                etDate.setText(sdf.format(myCalendar.getTime()));
            }
        };

        etDate = (EditText) findViewById(R.id.datepicker_add);
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AjouterItem.this, datepicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        final TimePickerDialog.OnTimeSetListener timepicker = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                myCalendar.set(Calendar.MINUTE, minute);

                String myFormat = "HH:mm";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
                etTime.setText(sdf.format(myCalendar.getTime()));
            }
        };

        etTime = (EditText) findViewById(R.id.timepicker_add);
        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(AjouterItem.this, timepicker, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true).show();
            }
        });
    }
}