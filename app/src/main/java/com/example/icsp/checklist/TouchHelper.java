package com.example.icsp.checklist;
import android.graphics.Canvas;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.example.icsp.R;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

/**
 * TouchHelper Adapter Class
 * <p>
 * This class is responsible for decorating different functionalities when swiping each task to the left or right
 * <p>
 * Special thanks to xabaras for the RecycleriViewSwipeDecorator library which is used for swiping the checklist tasks to the left to edit the task
 */
public class TouchHelper extends ItemTouchHelper.SimpleCallback {
    private ChecklistAdapter adapter;
    public TouchHelper(ChecklistAdapter adapter) {
        super(0, ItemTouchHelper.LEFT);
        this.adapter = adapter;
    }
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        final int position = viewHolder.getAdapterPosition();
        //If swiped each task to the left the user edits the task
        if (direction == ItemTouchHelper.LEFT) {
            adapter.editTask(position);
            adapter.notifyItemChanged(position);
        }
    }
    //Swiping to the left makes the colour the primary which is blue and for editing the task
    @Override
    public void onChildDraw (Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,float dX, float dY,int actionState, boolean isCurrentlyActive){
        new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                .addSwipeLeftLabel("Edit")
                .setSwipeLeftLabelColor(Color.WHITE)
                .addSwipeLeftBackgroundColor(ContextCompat.getColor(adapter.getContext(), R.color.my_primary))
                .create()
                .decorate();
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
