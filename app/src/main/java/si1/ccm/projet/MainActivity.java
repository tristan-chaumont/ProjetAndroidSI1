package si1.ccm.projet;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import java.util.ArrayList;

import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_SWIPE;

public class MainActivity extends AppCompatActivity {

    private ArrayList<TodoItem> items;
    private RecyclerView recycler;
    private LinearLayoutManager manager;
    private RecyclerAdapter adapter;

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

        // On récupère les items

        /* A faire si l'application ne démarre pas du premier coup. Je n'ai pas réussi à trouver d'où venait le problème.
        Il faut exécuter une fois l'application en décommentant la ligne ci-dessous, puis la réexécuter en commentant cette même ligne. */

        //getBaseContext().deleteDatabase(TodoDbHelper.DATABASE_NAME);



        items = TodoDbHelper.getItems(this);
        Log.i("INIT", "Fin initialisation items");

        // On initialise le RecyclerView
        recycler = (RecyclerView) findViewById(R.id.recycler);
        manager = new LinearLayoutManager(this);
        recycler.setLayoutManager(manager);

        adapter = new RecyclerAdapter(getBaseContext(), items);
        recycler.setAdapter(adapter);

        //setRecyclerViewItemTouchListener();

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

    enum ButtonState {
        GONE,
        LEFT_VISIBLE,
        RIGHT_VISIBLE
    }

    /*private void setRecyclerViewItemTouchListener() {

        ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            private boolean swipeBack = false;
            private ButtonState buttonShowedState = ButtonState.GONE;
            private static final float buttonWidth = 300;

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {
                // Non géré dans cet exemple (ce sont les drags) -> on retourne false
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

            }

            @Override
            public int convertToAbsoluteDirection(int flags, int layoutDirection) {
                if(swipeBack) {
                    swipeBack = false;
                    return 0;
                }
                return super.convertToAbsoluteDirection(flags, layoutDirection);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ACTION_STATE_SWIPE) {
                    setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            public void setTouchListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder,
                                         final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
                recyclerView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        swipeBack = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
                        if(swipeBack) {
                            if(dX < -buttonWidth)
                                buttonShowedState = ButtonState.RIGHT_VISIBLE;
                            else if(dX > buttonWidth)
                                buttonShowedState = ButtonState.LEFT_VISIBLE;

                            if(buttonShowedState != ButtonState.GONE) {
                                setTouchDownListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                                setItemsClickable(recyclerView, false);
                            }
                        }
                        return false;
                    }
                });
            }

            private void setTouchDownListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder,
                                              final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
                recyclerView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if(event.getAction() == MotionEvent.ACTION_DOWN) {
                            setTouchUpListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                        }
                        return false;
                    }
                });
            }

            private void setTouchUpListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder,
                                            final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
                recyclerView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if(event.getAction() == MotionEvent.ACTION_UP) {
                            onChildDraw(c, recyclerView, viewHolder, 0F, dY, actionState, isCurrentlyActive);
                            recyclerView.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    return false;
                                }
                            });
                            setItemsClickable(recyclerView, true);
                            swipeBack = false;
                            buttonShowedState = ButtonState.GONE;
                        }
                        return false;
                    }
                });
            }

            private void setItemsClickable(RecyclerView recyclerView, boolean isClickable) {
                for(int i = 0; i < recyclerView.getChildCount(); ++i) {
                    recyclerView.getChildAt(i).setClickable(isClickable);
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recycler);
    }*/
}
