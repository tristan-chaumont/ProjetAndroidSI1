package si1.ccm.projet;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class AjouterItem extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajouter_item);
        Button val = (Button) findViewById(R.id.valider);
        Spinner spin = (Spinner) findViewById(R.id.choixTag);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.choix, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spin.setAdapter(adapter);
        val.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nom = (String) ((EditText)findViewById(R.id.nom)).getText().toString();
                String choix = (String) ((Spinner)findViewById(R.id.choixTag)).getSelectedItem().toString();
                TodoDbHelper.addItem(new TodoItem(TodoItem.getTagFor(choix),nom),getBaseContext());
                setResult(RESULT_OK);
                finish();
            }
        });
    }
}
