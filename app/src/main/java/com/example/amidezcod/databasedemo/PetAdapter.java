package com.example.amidezcod.databasedemo;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.amidezcod.databasedemo.data.PetContract;

/**
 * Created by amidezcod on 9/7/17.
 */

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.MyViewHolder> {

    final private ListItemClickListener mOnClickListener;
    int viewHolderCount;
    private Context context;
    private Cursor cursor;
    private int lastPosition = -1;

    public PetAdapter(Context context, Cursor cursor, ListItemClickListener mOnClickListener) {
        this.context = context;
        this.cursor = cursor;
        this.mOnClickListener = mOnClickListener;
        viewHolderCount = 0;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        cursor.moveToPosition(position); // get to the right location in the cursor
        holder.name.setText(cursor.getString(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_NAME)));
        holder.summary.setText(cursor.getString(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_BREED)));
        setAnimation(holder.itemView, position);
    }

    private void setAnimation(View itemView, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            itemView.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        if (cursor == null) {
            return 0;
        }
        return cursor.getCount();
    }

    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor != null) {
            this.cursor = newCursor;
            this.notifyDataSetChanged();
            return cursor;
        } else {
            return null;
        }
    }

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    interface click {
        void clickHandle(int clickIndex);
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;
        TextView summary;

        private MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            name = (TextView) itemView.findViewById(R.id.name);
            summary = (TextView) itemView.findViewById(R.id.summary);
        }

        @Override
        public void onClick(View view) {
        mOnClickListener.onListItemClick(getAdapterPosition());
        }
    }
}
