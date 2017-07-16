package com.example.amidezcod.databasedemo;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.amidezcod.databasedemo.data.PetContract;
import com.example.amidezcod.databasedemo.utility.BitmapUtility;

/**
 * Created by amidezcod on 9/7/17.
 */

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.MyViewHolder> {

    final private ListItemClickListener mOnClickListener;
    private Context context;
    private Cursor cursor = null;
    private int lastPosition = -1;

    public PetAdapter(Context context, ListItemClickListener mOnClickListener) {
        this.context = context;
        this.mOnClickListener = mOnClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        cursor.moveToPosition(position); // get to the right location in the cursor
        holder.itemView.setTag(cursor.getInt(cursor.getColumnIndex(PetContract.PetEntry._ID)));
        holder.name.setText(cursor.getString(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_NAME)));
        holder.summary.setText(cursor.getString(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_BREED)));
        if (cursor.getBlob(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_IMAGE)) == null) {
            holder.imageView.setImageResource(R.drawable.dog_default_profile);
        } else {
            byte[] imageArray = cursor.getBlob(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_IMAGE));
            holder.imageView.setImageBitmap(BitmapUtility.getImage(imageArray));
        }
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
    public void onViewDetachedFromWindow(MyViewHolder holder) {
        holder.itemView.clearAnimation();
    }

    @Override
    public int getItemCount() {
        if (cursor == null) {
            return 0;
        }
        return cursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        if (newCursor == null)
            return;

        this.cursor = newCursor;
        this.notifyDataSetChanged();
    }

    interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;
        TextView summary;
        ImageView imageView;

        private MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            name = (TextView) itemView.findViewById(R.id.name);
            summary = (TextView) itemView.findViewById(R.id.summary);
            imageView = (ImageView) itemView.findViewById(R.id.image_petttt);
        }

        @Override
        public void onClick(View view) {
            cursor.moveToPosition(getAdapterPosition());
            mOnClickListener.onListItemClick(cursor.getInt(cursor.getColumnIndex(PetContract.PetEntry._ID)));
        }
    }
}
