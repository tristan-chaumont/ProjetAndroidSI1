package si1.ccm.projet;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ModifierItem extends AppCompatActivity {

    private EditText etDate, etTime;
    private Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifier_item);

        final TodoItem item = (TodoItem) getIntent().getExtras().get("position");

        Button mod = (Button) findViewById(R.id.valider_mod);
        Spinner spin = (Spinner) findViewById(R.id.choixTag_mod);

        FloatingActionButton fab = findViewById(R.id.fab_mod);
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

        /*
         * bloc qui set tous les éléments de l'activity avec les valeurs de l'item
         */
        {
            ((EditText) findViewById(R.id.nom_mod)).setText(item.getLabel());
            int tagPosition;
            switch (item.getTag().getDesc()) {
                case "Faible":
                    tagPosition = 0;
                    break;
                case "Normal":
                    tagPosition = 1;
                    break;
                case "Important":
                    tagPosition = 2;
                    break;
                default:
                    tagPosition = 0;
                    break;
            }
            ((Spinner) findViewById(R.id.choixTag_mod)).setSelection(tagPosition);

            String dateItem = null;
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy' 'HH:mm");
            format.setLenient(false);
            try {
                dateItem = format.format(item.getDate());
            } catch (Exception e) {

            }
            ((EditText) findViewById(R.id.datepicker_mod)).setText(dateItem.split(" ")[0]);
            ((EditText) findViewById(R.id.timepicker_mod)).setText(dateItem.split(" ")[1]);

            ((TextView) findViewById(R.id.label_mod)).setText("Nom de la tâche : " + item.getLabel());
        }

        /*
         * clic sur le bouton modifier
         */
        mod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nom = ((EditText)findViewById(R.id.nom_mod)).getText().toString();
                String choix = ((Spinner)findViewById(R.id.choixTag_mod)).getSelectedItem().toString();
                String datePicker = ((EditText) findViewById(R.id.datepicker_mod)).getText().toString();
                String timePicker = ((EditText) findViewById(R.id.timepicker_mod)).getText().toString();

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

                    item.setLabel(nom);
                    item.setTag(TodoItem.getTagFor(choix));
                    item.setDate(date);
                    item.setDone(item.isDone());

                    TodoDbHelper.updateItem(item, getBaseContext());
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

        etDate = (EditText) findViewById(R.id.datepicker_mod);
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ModifierItem.this, datepicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
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

        etTime = (EditText) findViewById(R.id.timepicker_mod);
        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(ModifierItem.this, timepicker, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true).show();
            }
        });
    }
}