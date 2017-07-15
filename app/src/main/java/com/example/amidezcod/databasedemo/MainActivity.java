package com.example.amidezcod.databasedemo;

import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.amidezcod.databasedemo.data.PetContract;
import com.example.amidezcod.databasedemo.data.PetContract.PetEntry;
import com.example.amidezcod.databasedemo.utility.TestUtil;

public class MainActivity extends AppCompatActivity implements
        android.app.LoaderManager.LoaderCallbacks<Cursor>, PetAdapter.ListItemClickListener {
    private RecyclerView recyclerView;
    private PetAdapter petAdapter;
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupRecyclerView();
        floatingAction();
        adapterSettingAndLoaderInitilization();
        scrollFlags();
        itemDecorate();
        itemSwipeAnimation();
    }

    private void itemSwipeAnimation() {
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(500);
        itemAnimator.setRemoveDuration(600);
        recyclerView.setItemAnimator(itemAnimator);
    }

    private void itemDecorate() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int id = (Integer) viewHolder.itemView.getTag();
                int rowsDeleted = getContentResolver().delete(ContentUris.withAppendedId(PetEntry.CONTENT_URI, id)
                        , null
                        , null);
                petAdapter.notifyItemRemoved(rowsDeleted);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View view = viewHolder.itemView;
                    float height = view.getHeight();
                    float width = height / 3;
                    Paint paint = new Paint();
                    paint.setColor(Color.parseColor("#388E3C"));
                    RectF rectF = new RectF(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                    c.drawRect(rectF, paint);
                    icon = getBitmapFromVector(R.drawable.ic_delete_black_24dp);
                    RectF icon_dest;
                    if (dX > 0) {

                        icon_dest = new RectF(view.getLeft() + width, view.getTop() + width, view.getLeft() + 2 * width
                                , view.getBottom() - width);
                    } else {
                        icon_dest = new RectF(view.getRight() - 2 * width, view.getTop() + width,
                                view.getRight() - width, view.getBottom() - width);

                    }
                    c.drawBitmap(icon, null, icon_dest, paint);
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            private Bitmap getBitmapFromVector(int ic_delete_black_24dp) {
                Drawable drawable = ContextCompat.getDrawable(MainActivity.this, ic_delete_black_24dp);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                    drawable = (DrawableCompat.wrap(drawable)).mutate();

                }

                Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                        Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
                return bitmap;
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void scrollFlags() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        floatingActionButton.show();
                        break;
                    default:
                        floatingActionButton.hide();
                        break;
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    private void adapterSettingAndLoaderInitilization() {
        petAdapter = new PetAdapter(this, this);
        recyclerView.setAdapter(petAdapter);
        getLoaderManager().initLoader(1, null, this).forceLoad();
    }

    private void floatingAction() {
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab2);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{
                PetContract.PetEntry._ID,
                PetContract.PetEntry.COLUMN_PET_NAME,
                PetContract.PetEntry.COLUMN_PET_BREED
        };
        return new CursorLoader(this,
                PetContract.PetEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        petAdapter.swapCursor(data);
        checkEmptyView();
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        petAdapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cmain_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all:
                deleteAll();
                checkEmptyView();
                return true;
            case R.id.insert_bulk_dummy:
                TestUtil.insertBulkFakeData(this);
                checkEmptyView();
                return true;
            case R.id.insert_single_dummy:
                TestUtil.insertSingleDummyData(this);
                checkEmptyView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteAll() {
        getContentResolver().delete(PetContract.PetEntry.CONTENT_URI, null, null);
    }

    private void checkEmptyView() {
        View EmptyView = findViewById(R.id.empty_view);
        if (petAdapter.getItemCount() == 0) {
            EmptyView.setVisibility(View.VISIBLE);
        } else {
            EmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Intent intent = new Intent(MainActivity.this, EditorActivity.class);
        intent.setData(ContentUris.withAppendedId(PetEntry.CONTENT_URI, clickedItemIndex));
        startActivity(intent);
    }
}
