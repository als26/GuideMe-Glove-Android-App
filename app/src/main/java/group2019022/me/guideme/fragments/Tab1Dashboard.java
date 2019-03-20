package group2019022.me.guideme.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import group2019022.me.guideme.R;

public class Tab1Dashboard extends BaseFragment {

    private TextView batteryPercent2;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.tab_1_dashboard, container, false);

        batteryPercent2 = rootView.findViewById(R.id.battery_percentage);
        batteryPercent2.setText("56%");
        return rootView;
    }
}
