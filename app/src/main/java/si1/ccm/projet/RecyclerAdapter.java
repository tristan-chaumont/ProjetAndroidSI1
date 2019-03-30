package si1.ccm.projet;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by phil on 07/02/17.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.TodoHolder> {

    private ArrayList<TodoItem> items;
    private static Context context;

    public RecyclerAdapter(Context contxt, ArrayList<TodoItem> items) {
        this.items = items;
        context = contxt;
    }

    @Override
    public TodoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new TodoHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(TodoHolder holder, int position) {
        TodoItem it = items.get(position);
        holder.bindTodo(it);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class TodoHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        private Resources resources;
        private ImageView image;
        private Switch sw;
        private TextView label;
        private TodoItem item;

        public TodoHolder(View itemView) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.imageView);
            sw = (Switch) itemView.findViewById(R.id.switch1);
            label = (TextView) itemView.findViewById(R.id.textView);
            resources = itemView.getResources();
            itemView.setOnLongClickListener(this);

            addOnClickListenerSwitch();
        }

        public void bindTodo(TodoItem todo) {
            item = todo;
            label.setText(todo.getLabel());
            sw.setChecked(todo.isDone());
            switch (todo.getTag()) {
                case Faible:
                    image.setBackgroundColor(resources.getColor(R.color.faible));
                    break;
                case Normal:
                    image.setBackgroundColor(resources.getColor(R.color.normal));
                    break;
                case Important:
                    image.setBackgroundColor(resources.getColor(R.color.important));
                    break;

            }
        }

        @Override
        public boolean onLongClick(View v) {
            final View view = v;
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setCancelable(true);
            builder.setTitle("Suppression de la tâche");
            builder.setMessage("Voulez-vous vraiment supprimer cette tâche ?");
            builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    TodoDbHelper.deleteItem(item, view.getContext());
                    removeAt(getAdapterPosition());
                }
            });
            builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }

        public void addOnClickListenerSwitch() {
            sw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Switch sw1 = v.findViewById(R.id.switch1);
                    TodoItem item = items.get(getAdapterPosition());
                    LinearLayout ll = itemView.findViewById(R.id.layoutRow);
                    if(sw1.isChecked())
                        item.setDone(true);
                    else
                        item.setDone(false);

                    if(ll == null)
                        Log.d("RecyclerAdapter", "LinearLayout null Error");
                    else {
                        if(item.isDone())
                            ll.setBackgroundColor(Color.LTGRAY);
                        else
                            ll.setBackgroundColor(Color.WHITE);
                    }

                    TodoDbHelper.updateItem(item, context);
                }
            });
        }
    }

    public void removeAt(int position) {
        String itemLabel = items.get(position).getLabel();
        items.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, items.size());
        Toast.makeText(context, "Supprimé : \"" + itemLabel + "\"",Toast.LENGTH_SHORT).show();
    }
}
