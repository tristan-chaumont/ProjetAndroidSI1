package si1.ccm.projet;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by phil on 07/02/17.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.TodoHolder> {

    private ArrayList<TodoItem> items;
    private static Activity activity;

    public RecyclerAdapter(Activity a, ArrayList<TodoItem> items) {
        this.items = items;
        activity = a;
    }

    @Override
    public TodoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new TodoHolder(inflatedView);
    }

    public void decalerPosition(int position) {
        for(TodoItem item : items) {
            if(item.getPosition() > position) {
                long pos = item.getPosition();
                item.setPosition(pos - 1);
            }
        }
    }

    @Override
    public void onBindViewHolder(final TodoHolder holder, int position) {
        final TodoItem it = items.get(position);
        holder.bindTodo(it);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //on crée un menu popup
                PopupMenu popup = new PopupMenu(activity, holder.itemView);

                //inflate
                popup.inflate(R.menu.item_popup);

                //on ajoute le click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.popup_modifier :
                                Intent dbmanager = new Intent(activity, ModifierItem.class);
                                dbmanager.putExtra("position", it);
                                activity.startActivityForResult(dbmanager, 0);
                                break;
                            case R.id.popup_supprimer :
                                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                                builder.setCancelable(true);
                                builder.setTitle("Suppression de la tâche");
                                builder.setMessage("Voulez-vous vraiment supprimer cette tâche ?");
                                builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        TodoDbHelper.deleteItem(it, activity);
                                        String label = removeAt(holder.getAdapterPosition());
                                        decalerPosition((int) it.getPosition());
                                        Snackbar.make(v, "Supprimé : " + label, Snackbar.LENGTH_LONG).setAction("Action", null).show();
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
                                break;
                        }
                        return false;
                    }
                });

                //on crée les icones sur le popup
                try {
                    Field[] fields = popup.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        if ("mPopup".equals(field.getName())) {
                            field.setAccessible(true);
                            Object menuPopupHelper = field.get(popup);
                            Class<?> classPopupHelper = Class.forName(menuPopupHelper
                                    .getClass().getName());
                            Method setForceIcons = classPopupHelper.getMethod(
                                    "setForceShowIcon", boolean.class);
                            setForceIcons.invoke(menuPopupHelper, true);
                            break;
                        }
                    }
                } catch (Exception e) {

                }

                //on affiche le popup
                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public String removeAt(int position) {
        String itemLabel = items.get(position).getLabel();
        items.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, items.size());
        return itemLabel;
    }

    public class TodoHolder extends RecyclerView.ViewHolder {
        private Resources resources;
        private ImageView image;
        private Switch sw;
        private TextView label, echeance;
        private TodoItem item;

        public TodoHolder(View itemView) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.imageView);
            sw = (Switch) itemView.findViewById(R.id.switch1);
            label = (TextView) itemView.findViewById(R.id.textView);
            resources = itemView.getResources();
            echeance = (TextView) itemView.findViewById(R.id.echeance);

            //itemView.setOnLongClickListener(this);
            addOnClickListenerOnSwitch();
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
                default :
                    break;
            }

            String date = null;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
            sdf.setLenient(false);
            try {
                date = sdf.format(item.getDate());
            } catch(Exception e) {

            }
            CardView cv = itemView.findViewById(R.id.layoutRow);
            if(todo.isDone())
                cv.setBackgroundColor(Color.LTGRAY);
            else
                cv.setBackgroundColor(Color.WHITE);
            echeance.setText(date);
        }

        /*@Override
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
                    String label = removeAt(getAdapterPosition());
                    Snackbar.make(view, "Supprimé : " + label, Snackbar.LENGTH_LONG).setAction("Action", null).show();
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
        }*/

        public void addOnClickListenerOnSwitch() {
            sw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Switch sw1 = v.findViewById(R.id.switch1);
                    TodoItem item = items.get(getAdapterPosition());
                    CardView cv = itemView.findViewById(R.id.layoutRow);
                    if(sw1.isChecked())
                        item.setDone(true);
                    else
                        item.setDone(false);

                    if(cv == null)
                        Log.d("RecyclerAdapter", "LinearLayout null Error");
                    else {
                        if(item.isDone())
                            cv.setBackgroundColor(Color.LTGRAY);
                        else
                            cv.setBackgroundColor(Color.WHITE);
                    }

                    TodoDbHelper.updateItem(item, activity);
                }
            });
        }
    }
}