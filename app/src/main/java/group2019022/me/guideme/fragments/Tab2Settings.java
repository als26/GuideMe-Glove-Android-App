package group2019022.me.guideme.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import group2019022.me.guideme.R;


public class Tab2Settings extends BaseFragment {

    SeekBar vibrationSlider;
    SeekBar distanceSlider;
    TextView vibrationValue;
    TextView distanceValue;
    Button vibrationToggle;
    boolean vibrationState;

    //variables for Shared preferences
    SharedPreferences sPreferences;
    SharedPreferences.Editor editor;
    int savedVibrationSlider;
    int savedDistanceSlider;
    int loadedVibrationSlider;
    int loadedDistanceSlider;
    String savedVibrationDisplay;
    String savedDistanceDisplay;
    String loadedVibrationDisplay;
    String loadedDistanceDisplay;
    String Key_VIBRATION_SLIDER = "slider1_progress";
    String Key_DISTANCE_SLIDER = "slider2_progress";
    String Key_VIBRATION_VALUE = "slider1_value";
    String Key_DISTANCE_VALUE = "slider2_value";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = sPreferences.edit();

        View rootView = inflater.inflate(R.layout.tab_2_settings, container, false);

        vibrationSlider = rootView.findViewById(R.id.seekBar);
        vibrationSlider.setMax(2);
        vibrationValue = rootView.findViewById(R.id.textView2);

        vibrationToggle = rootView.findViewById(R.id.vibrationToggle);
        vibrationToggle.setText("Turn off Vibration Feedback");
        vibrationState = true;

        distanceSlider = rootView.findViewById(R.id.seekBar2);
        distanceSlider.setMax(7);
        distanceValue = rootView.findViewById(R.id.textView6);

        loadedVibrationSlider = sPreferences.getInt(Key_VIBRATION_SLIDER, 0);
        vibrationSlider.setProgress(loadedVibrationSlider);
        loadedVibrationDisplay = sPreferences.getString(Key_VIBRATION_VALUE, "0");
        vibrationValue.setText(loadedVibrationDisplay);

        loadedDistanceSlider = sPreferences.getInt(Key_DISTANCE_SLIDER, 0);
        distanceSlider.setProgress(loadedDistanceSlider);
        loadedDistanceDisplay = sPreferences.getString(Key_DISTANCE_VALUE, "0");
        distanceValue.setText(loadedDistanceDisplay);

        vibrationSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                String newStepProgress;

                if (progress == 0) {
                    newStepProgress = "Weak";
                } else if (progress == 1) {
                    newStepProgress = "Medium";
                } else {
                    newStepProgress = "Strong";
                }

                vibrationValue.setText(String.valueOf(newStepProgress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //for shared preference saving
                savedVibrationSlider = vibrationSlider.getProgress();
                savedVibrationDisplay = vibrationValue.getText().toString();
                editor.putInt(Key_VIBRATION_SLIDER, savedVibrationSlider);
                editor.putString(Key_VIBRATION_VALUE, savedVibrationDisplay);
                editor.commit();

                //send data to bluetooth
                if (getMainActivity().getBluetoothSerial().isConnected()) {
                    getMainActivity().getBluetoothSerial().write("v" + (savedVibrationSlider));
                    Log.d("sentVibrationValues", "Value: " + Integer.toString(savedVibrationSlider));
                }

            }
        });

        vibrationToggle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (vibrationState) {
                    if (getMainActivity().getBluetoothSerial().isConnected()) {
                        vibrationState = false;
                        vibrationToggle.setText("Turn on Vibration Feedback");
                        getMainActivity().getBluetoothSerial().write("v3");
                    }

                    else {
                        Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Please connect Bluetooth Device", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }

                else {
                    if (getMainActivity().getBluetoothSerial().isConnected()) {
                        vibrationState = true;
                        vibrationToggle.setText("Turn off Vibration Feedback");
                        getMainActivity().getBluetoothSerial().write("v" + (vibrationSlider.getProgress()));
                        Log.d("buttonVibrationValue", "Value: " + Integer.toString(vibrationSlider.getProgress()));
                    }
                    else {
                        Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Please connect Bluetooth Device", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            }
        });

        //handling distance slider changes
        distanceSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {

                double min = 1;
                double newStepProgress;

                if (progress < min) {
                    distanceValue.setText(String.valueOf(min) + " m");
                } else if (progress == min) {
                    newStepProgress = min + 0.5;
                    distanceValue.setText(String.valueOf(newStepProgress) + " m");
                } else {
                    newStepProgress = progress - ((progress - 2) * 0.5);
                    distanceValue.setText(String.valueOf(newStepProgress) + " m");
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                savedDistanceSlider = distanceSlider.getProgress();
                savedDistanceDisplay = distanceValue.getText().toString();
                editor.putInt(Key_DISTANCE_SLIDER, savedDistanceSlider);
                editor.putString(Key_DISTANCE_VALUE, savedDistanceDisplay);
                editor.commit();

                if (getMainActivity().getBluetoothSerial().isConnected()) {
                    getMainActivity().getBluetoothSerial().write("d" + (savedDistanceSlider));
                    Log.d("sentDistanceValue", "Value: " + Integer.toString(savedDistanceSlider));
                }
            }
        });

        return rootView;
    }


    @Override
    public void onStop() {
        super.onStop();
    }
}
