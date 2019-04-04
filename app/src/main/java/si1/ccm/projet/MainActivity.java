package si1.ccm.projet;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    private ArrayList<TodoItem> items;
    private RecyclerView recycler;
    private LinearLayoutManager manager;
    private RecyclerAdapter adapter;
    private final static String CHANNEL_ID = "channel1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //on change d'activité
                Intent myIntent = new Intent(getApplicationContext(), AjouterItem.class);
                startActivityForResult(myIntent, 0);
            }
        });
        Log.i("INIT", "Fin initialisation composantes");

        /* A faire si l'application ne démarre pas du premier coup. Je n'ai pas réussi à trouver d'où venait le problème.
        Il faut exécuter une fois l'application en décommentant la ligne ci-dessous, puis la réexécuter en commentant cette même ligne. */

        //getBaseContext().deleteDatabase(TodoDbHelper.DATABASE_NAME);


        // On récupère les items
        items = TodoDbHelper.getItems(this);
        Log.i("INIT", "Fin initialisation items");

        Collections.sort(items, new Comparator<TodoItem>() {
            @Override
            public int compare(TodoItem o1, TodoItem o2) {
                if(o1.getPosition() < o2.getPosition())
                    return -1;
                else if(o1.getPosition() > o2.getPosition())
                    return 1;
                else
                    return 0;
            }
        });

        // On initialise le RecyclerView
        recycler = (RecyclerView) findViewById(R.id.recycler);
        manager = new LinearLayoutManager(this);
        recycler.setLayoutManager(manager);

        adapter = new RecyclerAdapter(this, items);
        recycler.setAdapter(adapter);

        setRecyclerViewItemTouchListener();

        Log.i("INIT", "Fin initialisation recycler");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_database) {
            Intent dbmanager = new Intent(this, AndroidDatabaseManager.class);
            startActivity(dbmanager);
        }

        if (id == R.id.action_cleardb) {
            TodoDbHelper.clearDatabase(getBaseContext());
            items.clear();
            recycler.getAdapter().notifyDataSetChanged();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int returnCode, Intent data) {
        items.clear();
        items.addAll(TodoDbHelper.getItems(getBaseContext()));
        adapter.notifyDataSetChanged();
    }

    private void setRecyclerViewItemTouchListener() {

        ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = viewHolder1.getAdapterPosition();
                onItemMoved(fromPosition, toPosition);
                changeItemPosition(fromPosition, toPosition);
                return true;
            }

            public void onItemMoved(int fromPosition, int toPosition) {
                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(items, i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(items, i, i - 1);
                    }
                }
                adapter.notifyItemMoved(fromPosition, toPosition);
            }

            private void changeItemPosition(int anciennePosition, int nouvellePosition) {
                TodoItem item1 = items.get(anciennePosition);
                TodoItem item2 = items.get(nouvellePosition);

                int positionItem1 = (int) item1.getPosition();
                item1.setPosition(item2.getPosition());
                TodoDbHelper.updatePosition(item1, getBaseContext());

                item2.setPosition(positionItem1);
                TodoDbHelper.updatePosition(item2, getBaseContext());
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            }

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                //int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                return makeMovementFlags(dragFlags, 0);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recycler);
    }
}