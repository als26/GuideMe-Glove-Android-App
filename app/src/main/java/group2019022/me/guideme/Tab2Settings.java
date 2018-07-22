package group2019022.me.guideme;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.macroyau.blue2serial.BluetoothDeviceListDialog;
import com.macroyau.blue2serial.BluetoothSerial;
import com.macroyau.blue2serial.BluetoothSerialListener;


public class Tab2Settings extends Fragment implements BluetoothSerialListener, BluetoothDeviceListDialog.OnDeviceSelectedListener {

    SeekBar vibrationSlider;
    SeekBar distanceSlider;
    TextView vibrationValue;
    TextView distanceValue;

    //variables for bluetooth connection
    Button bluetoothButton;
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private BluetoothSerial bluetoothSerial;


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

        distanceSlider = rootView.findViewById(R.id.seekBar2);
        distanceSlider.setMax(7);
        distanceValue = rootView.findViewById(R.id.textView6);

        loadedVibrationSlider = sPreferences.getInt(Key_VIBRATION_SLIDER, 0);
        vibrationSlider.setProgress(loadedVibrationSlider);
        loadedVibrationDisplay = sPreferences.getString(Key_VIBRATION_VALUE,"0");
        vibrationValue.setText(loadedVibrationDisplay);

        loadedDistanceSlider = sPreferences.getInt(Key_DISTANCE_SLIDER, 0);
        distanceSlider.setProgress(loadedDistanceSlider);
        loadedDistanceDisplay = sPreferences.getString(Key_DISTANCE_VALUE, "0");
        distanceValue.setText(loadedDistanceDisplay);

        bluetoothButton = rootView.findViewById(R.id.button);
        bluetoothSerial = new BluetoothSerial(getContext(),this);
//        bluetooth = new BluetoothSPP(getActivity());
//        if (!bluetooth.isBluetoothAvailable()) {
//            Toast.makeText(getActivity().getApplicationContext(), "Bluetooth is not available", Toast.LENGTH_SHORT).show();
//            getActivity().finish();
//        }
        //handling vibration slider changes
        vibrationSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                String newStepProgress;

                if (progress == 0) {
                    newStepProgress = "Weak";
                }

                else if (progress == 1) {
                    newStepProgress = "Medium";
                }
                else {
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
                if (bluetoothSerial.isConnected()) {
                    bluetoothSerial.write(Key_VIBRATION_SLIDER);
                }

            }
        });

        //handling distance slider changes
        distanceSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {

                double min = 1;
                double newStepProgress;

                if(progress < min) {
                    distanceValue.setText(String.valueOf(min) + " m");
                }
                else if(progress == min) {
                    newStepProgress = min + 0.5;
                    distanceValue.setText(String.valueOf(newStepProgress) + " m");
                }
                else {
                    newStepProgress = progress - ((progress - 2)*0.5);
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
            }
        });

        bluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeviceListDialog();
            }
        });


        return rootView;
    }

    private void showDeviceListDialog() {
        // Display dialog for selecting a remote Bluetooth device
        BluetoothDeviceListDialog dialog = new BluetoothDeviceListDialog(getContext());
        dialog.setOnDeviceSelectedListener(this);
        dialog.setTitle("Choose a paired Bluetooth device");
        dialog.setDevices(bluetoothSerial.getPairedDevices());
        dialog.showAddress(true);
        dialog.show();
    }

    private void updateBluetoothState() {
        // Get the current Bluetooth state
        final int state;
        if (bluetoothSerial != null)
            state = bluetoothSerial.getState();
        else
            state = BluetoothSerial.STATE_DISCONNECTED;

        // Display the current state on the app bar as the subtitle
        String subtitle;
        switch (state) {
            case BluetoothSerial.STATE_CONNECTING:
                subtitle = getString(R.string.connecting_device);
                break;
            case BluetoothSerial.STATE_CONNECTED:
                subtitle = getString(R.string.connected_device) + bluetoothSerial.getConnectedDeviceName();
                break;
            default:
                subtitle = getString(R.string.find_device);
                break;
        }

        if (bluetoothButton != null) {
            bluetoothButton.setText(subtitle);
        }
    }

    /* Implementation of BluetoothSerialListener */
    @Override
    public void onBluetoothNotSupported() {
        new AlertDialog.Builder(getContext())
                .setMessage(R.string.no_bluetooth)
                .setPositiveButton(R.string.action_quit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    public void onBluetoothDisabled() {
        Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBluetooth, REQUEST_ENABLE_BLUETOOTH);
    }

    @Override
    public void onBluetoothDeviceDisconnected() {
        updateBluetoothState();
    }

    @Override
    public void onConnectingBluetoothDevice() {
        updateBluetoothState();
    }

    @Override
    public void onBluetoothDeviceConnected(String name, String address) {
        updateBluetoothState();
    }

    @Override
    public void onBluetoothSerialRead(String message) {
        // Print the incoming message on the terminal screen
    }

    @Override
    public void onBluetoothSerialWrite(String message) {
        // Print the outgoing message on the terminal screen
    }

    @Override
    public void onBluetoothDeviceSelected(BluetoothDevice device) {
        // Connect to the selected remote Bluetooth device
        bluetoothSerial.connect(device);
    }

    /* END Implementation of BluetoothSerialListener */




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_ENABLE_BLUETOOTH:
                // Set up Bluetooth serial port when Bluetooth adapter is turned on
                if (resultCode == Activity.RESULT_OK) {
                    bluetoothSerial.setup();
                }
                break;
        }
    }



    @Override
    public void onStart() {
        super.onStart();
        bluetoothSerial.setup();
        updateBluetoothState();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Open a Bluetooth serial port and get ready to establish a connection
        if (bluetoothSerial.checkBluetooth() && bluetoothSerial.isBluetoothEnabled()) {
            if (!bluetoothSerial.isConnected()) {
                bluetoothSerial.start();
            }
        }
        updateBluetoothState();
    }

    @Override
    public void onStop() {
        super.onStop();

        // Disconnect from the remote device and close the serial port
        //bluetoothSerial.stop();
    }
}
