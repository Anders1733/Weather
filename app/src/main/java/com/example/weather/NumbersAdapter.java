package com.example.weather;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;

public class NumbersAdapter extends RecyclerView.Adapter<NumbersAdapter.NumberViewHolder> {
    private static int viewHolderCount;
    private int numberItems;
    private final String[] cities;
    private Context parent;

    public NumbersAdapter(String[] cities, Context parent) {
        numberItems=19;
        this.cities = cities;
        viewHolderCount = 0;
        this.parent = parent;
    }

    @Override
    public NumberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.number_list_item;

        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);

        NumberViewHolder viewHolder = new NumberViewHolder(view);
        //viewHolder.viewHolderIndex.setText("ViewHolder index: " + viewHolderCount);

        //viewHolderCount++;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NumberViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return numberItems;
    }

    class NumberViewHolder extends RecyclerView.ViewHolder {

        TextView listItemNumberView;
        TextView viewHolderIndex;

        public NumberViewHolder(View itemView) {
            super(itemView);

            listItemNumberView = itemView.findViewById(R.id.tv_number_item);
            viewHolderIndex = itemView.findViewById(R.id.tv_view_holder_number);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int positionIndex = getAdapterPosition();

                    //Toast toast = Toast.makeText(parent, "Element " + cities[positionIndex] + " was clicked!", Toast.LENGTH_SHORT);
                    //toast.show();




                    Class destinationActivity = DetailActivity.class;

                    Intent detailActivityIntent = new Intent(parent, destinationActivity);
                    detailActivityIntent.putExtra(Intent.EXTRA_TEXT,  cities[positionIndex]);
                    parent.startActivity(detailActivityIntent);
                }
            });

        }

        void bind(int listIndex) {
            listItemNumberView.setText(String.valueOf(cities[listIndex]));
        }
    }
}
