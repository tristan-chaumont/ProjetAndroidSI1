package si1.ccm.projet;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class ModifierItem extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifier_item);

        Button val = (Button) findViewById(R.id.valider_mod);
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
        val.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nom = (String) ((EditText)findViewById(R.id.nom_mod)).getText().toString();
                String choix = (String) ((Spinner)findViewById(R.id.choixTag_mod)).getSelectedItem().toString();

                if(nom.isEmpty()) {
                    Snackbar.make(view, "Nom de tâche vide", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } else {
                    TodoDbHelper.updateItem(new TodoItem(TodoItem.getTagFor(choix), nom), getBaseContext());
                    finish();
                }
            }
        });
    }
}