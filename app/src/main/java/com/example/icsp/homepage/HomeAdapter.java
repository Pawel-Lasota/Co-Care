package com.example.icsp.homepage;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.icsp.ProfileActivity;
import com.example.icsp.R;
import java.util.List;

/**
 * HomeAdapter Adapter Class
 * <p>
 * This class is responsible for appropriately displaying the dashboard within the homepage.
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    private List<HomeModel> dashboardList;
    public HomeAdapter(List<HomeModel> dashboardList){
        this.dashboardList = dashboardList;
    }

    @NonNull
    @Override
    public HomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_layout_recyclerview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeAdapter.ViewHolder holder, int position) {
        //This section of code is responsible for appropriately displaying the text and icon for each dashboard item
        HomeModel model = dashboardList.get(position);
        holder.dashboardTextView.setText(model.getName());
        holder.dashboardImageView.setImageResource(model.getImageId());
        holder.dashboard_element.setOnClickListener(view -> {
            Context context = view.getContext();
            Intent intent;
            //Switch statements which lead to specific activities upon clicking on a specific dashboard list element
            switch(model.getName()){
                case "View Medicine Storage":
                    intent = new Intent(context, MedicineStorageActivity.class);
                    break;
                case "View Care Recipients":
                    intent = new Intent(context, CareRecipientsActivity.class);
                    break;
                case "Edit Profile":
                    intent = new Intent(context, ProfileActivity.class);
                    break;
                default:
                    return;
            }
            context.startActivity(intent);

        });
    }

    @Override
    public int getItemCount() {
        return dashboardList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView dashboardImageView;
        private final TextView dashboardTextView;
        private final LinearLayout dashboard_element;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //Retrieving required variables from layout
            dashboardTextView = itemView.findViewById(R.id.dashboard_text_view);
            dashboardImageView = itemView.findViewById(R.id.dashboard_image_view);
            dashboard_element = itemView.findViewById(R.id.dashboard_element);
        }
    }
}
