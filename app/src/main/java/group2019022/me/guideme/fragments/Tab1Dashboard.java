package group2019022.me.guideme.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import group2019022.me.guideme.R;
import group2019022.me.guideme.SharedViewModel;

public class Tab1Dashboard extends BaseFragment {
    private SharedViewModel viewModel;

    private TextView batteryPercent;
    private TextView batteryRemaining;

    private View vBatteryLevelOne;
    private View vBatteryLevelTwo;
    private View vBatteryLevelThree;

    private String batteryPercentString;
    private int batteryPercentConv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.tab_1_dashboard, container, false);

        vBatteryLevelOne = rootView.findViewById(R.id.batteryLevelOne);
        vBatteryLevelTwo = rootView.findViewById(R.id.batteryLevelTwo);
        vBatteryLevelThree = rootView.findViewById(R.id.batteryLevelThree);
        batteryPercent = rootView.findViewById(R.id.battery_percentage);
        batteryRemaining = rootView.findViewById(R.id.time_remaining);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
        viewModel.getBatteryLevel().observe(this, new Observer<CharSequence>() {
            @Override
            public void onChanged(@Nullable CharSequence charSequence) {

                batteryPercentString = (String)charSequence;
                batteryPercentConv = Integer.parseInt(batteryPercentString);
                batteryPercent.setText(charSequence + "%");

                if (batteryPercentConv <= 25) {
                    vBatteryLevelOne.setVisibility(View.INVISIBLE);
                    vBatteryLevelTwo.setVisibility(View.INVISIBLE);
                    vBatteryLevelThree.setVisibility(View.INVISIBLE);
                    batteryRemaining.setText("2 Hours Remaining");
                }

                else if (batteryPercentConv > 25 && batteryPercentConv <= 50) {
                    vBatteryLevelOne.setVisibility(View.VISIBLE);
                    vBatteryLevelTwo.setVisibility(View.INVISIBLE);
                    vBatteryLevelThree.setVisibility(View.INVISIBLE);
                    batteryRemaining.setText("4 Hours Remaining");
                }

                else if (batteryPercentConv > 50 && batteryPercentConv <= 75) {
                    vBatteryLevelOne.setVisibility(View.VISIBLE);
                    vBatteryLevelTwo.setVisibility(View.VISIBLE);
                    vBatteryLevelThree.setVisibility(View.INVISIBLE);
                    batteryRemaining.setText("6 Hours Remaining");
                }

                else {
                    vBatteryLevelOne.setVisibility(View.VISIBLE);
                    vBatteryLevelTwo.setVisibility(View.VISIBLE);
                    vBatteryLevelThree.setVisibility(View.VISIBLE);
                    batteryRemaining.setText("8 Hours Remaining");
                }
            }
        });
    }
}
