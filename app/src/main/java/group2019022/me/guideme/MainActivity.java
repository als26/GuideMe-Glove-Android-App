package group2019022.me.guideme;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.TextView;

import com.macroyau.blue2serial.BluetoothDeviceListDialog;
import com.macroyau.blue2serial.BluetoothSerial;
import com.macroyau.blue2serial.BluetoothSerialListener;
import com.macroyau.blue2serial.BluetoothSerialRawListener;

import group2019022.me.guideme.fragments.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity implements BluetoothSerialListener, BluetoothSerialRawListener, BluetoothDeviceListDialog.OnDeviceSelectedListener {

    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private BluetoothSerial bluetoothSerial;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private Toolbar mToolbar;
    private Button bluetoothButton;
    private TextView batteryPercent;

    private View vBatteryLevelOne;
    private View vBatteryLevelTwo;
    private View vBatteryLevelThree;

    private String batteryLevelMsg;
    private float recvBatteryLevel;
    private int convBatteryLevel;
    private float batteryMax;
    private float batteryMin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();

        bluetoothSerial = new BluetoothSerial(this, this);

        batteryLevelMsg = "%";
        batteryMax = 4.2f;
        batteryMin = 3.2f;

        //using layout inflator to get reference to views on tab1dashboard
        //LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(this.LAYOUT_INFLATER_SERVICE);
        //View v = layoutInflater.inflate(R.layout.tab_1_dashboard, null);
        View v = (ConstraintLayout)getLayoutInflater().inflate(R.layout.tab_1_dashboard, null);



        vBatteryLevelOne = v.findViewById(R.id.batteryLevelOne);
        vBatteryLevelTwo = v.findViewById(R.id.batteryLevelTwo);
        vBatteryLevelThree = v.findViewById(R.id.batteryLevelThree);
        batteryPercent = (TextView)v.findViewById(R.id.battery_percentage);

        vBatteryLevelOne.setVisibility(View.INVISIBLE);
        vBatteryLevelTwo.setVisibility(View.INVISIBLE);
        vBatteryLevelThree.setVisibility(View.INVISIBLE);
        batteryPercent.setText(batteryLevelMsg);

        bluetoothSerial.setup();
        updateBluetoothState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

//        if (bluetoothSerial != null) {
//            if (bluetoothSerial.checkBluetooth() && bluetoothSerial.isBluetoothEnabled()) {
//                if (!bluetoothSerial.isConnected()) {
//                    bluetoothSerial.setup();
//                }
//            }
//        }
//
//        else {

//        }

        //updateBluetoothState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bluetoothSerial.checkBluetooth() && bluetoothSerial.isBluetoothEnabled()) {
            if (!bluetoothSerial.isConnected()) {
                bluetoothSerial.start();
            }
            updateBluetoothState();
        }
    }

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

    private void bindViews() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        bluetoothButton = findViewById(R.id.button);
        bluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeviceListDialog();
            }
        });

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }


    private void updateBluetoothState() {
        // Get the current Bluetooth state
        final int state;
        if (bluetoothSerial != null) {
            state = bluetoothSerial.getState();
            Log.d("bluetoothState", "updateBluetoothState: " + Integer.toString(state));
        }   else {
            state = BluetoothSerial.STATE_DISCONNECTED;
        }

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

    private void showDeviceListDialog() {
        // Display dialog for selecting a remote Bluetooth device
        BluetoothDeviceListDialog dialog = new BluetoothDeviceListDialog(this);
        dialog.setOnDeviceSelectedListener(this);
        dialog.setTitle("Choose a paired Bluetooth device");
        dialog.setDevices(bluetoothSerial.getPairedDevices());
        dialog.showAddress(true);
        dialog.show();
    }

    // Should this return null?
    public BluetoothSerial getBluetoothSerial() {
        return bluetoothSerial;
    }

    /* Implementation of BluetoothSerialListener */
    @Override
    public void onBluetoothNotSupported() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.no_bluetooth)
                .setPositiveButton(R.string.action_quit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
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
        //batteryPercent.setText(message);
        //Log.d("bluetoothRecv", message);
        if (message.contains("3.") || message.contains("4.")) {
            try {
                recvBatteryLevel = Float.parseFloat(message);
            }
            catch (NumberFormatException e) {
                //do nothing
            }
        }

        convBatteryLevel = (int)((recvBatteryLevel - batteryMin)*100);
        batteryLevelMsg = Float.toString(convBatteryLevel);
        batteryPercent.setText(batteryLevelMsg);

        if (convBatteryLevel <= 25) {
            vBatteryLevelOne.setVisibility(View.INVISIBLE);
            vBatteryLevelTwo.setVisibility(View.INVISIBLE);
            vBatteryLevelThree.setVisibility(View.INVISIBLE);
        }

        else if (convBatteryLevel > 25 && convBatteryLevel <= 50) {
            vBatteryLevelOne.setVisibility(View.VISIBLE);
            vBatteryLevelTwo.setVisibility(View.INVISIBLE);
            vBatteryLevelThree.setVisibility(View.INVISIBLE);
        }

        else if (convBatteryLevel > 50 && convBatteryLevel <= 75) {
            vBatteryLevelOne.setVisibility(View.VISIBLE);
            vBatteryLevelTwo.setVisibility(View.VISIBLE);
            vBatteryLevelThree.setVisibility(View.INVISIBLE);
        }

        else {
            vBatteryLevelOne.setVisibility(View.VISIBLE);
            vBatteryLevelTwo.setVisibility(View.VISIBLE);
            vBatteryLevelThree.setVisibility(View.VISIBLE);
        }
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

    /* Implementation of BluetoothSerialListener - RAW */

    @Override
    public void onBluetoothSerialReadRaw(byte[] bytes) {

    }

    @Override
    public void onBluetoothSerialWriteRaw(byte[] bytes) {

    }
    /* END Implementation of BluetoothSerialListener - RAW */


}
