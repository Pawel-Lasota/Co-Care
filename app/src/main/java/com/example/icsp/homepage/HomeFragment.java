package com.example.icsp.homepage;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.icsp.R;
import java.util.ArrayList;
import java.util.List;

/**
 * HomeFragment Fragment Class
 * <p>
 * This class is responsible for adding new dashboard elements into the list
 */

public class HomeFragment extends Fragment {

    private RecyclerView dashboardRecycler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        //TextView updating the textholder within the layout for this fragment to 'Home' as this fragment is the homepage
        TextView textHolder = getActivity().findViewById(R.id.text_holder);
        textHolder.setText("Home");

        dashboardRecycler = view.findViewById(R.id.dashboard_recycler_view);
        dashboardRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        //Dashboard list of all elements within the homepage
        List<HomeModel> dashboardList = new ArrayList<>();
        dashboardList.add(new HomeModel("View Care Recipients", R.drawable.ic_care_recipients));
        dashboardList.add(new HomeModel("View Medicine Storage", R.drawable.ic_medicine_storage));
        dashboardList.add(new HomeModel("Edit Profile", R.drawable.ic_profile));
        //Dashboard elements for future iterations
        dashboardList.add(new HomeModel("*DISABLED* Edit Notifications and Reminders", R.drawable.ic_notifications));
        dashboardList.add(new HomeModel("*DISABLED* Create/Join a Family Circle", R.drawable.ic_family_circle));

        HomeAdapter adapter = new HomeAdapter(dashboardList);
        dashboardRecycler.setAdapter(adapter);

        return view;
    }
}