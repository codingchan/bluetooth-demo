package com.example.canoestudent;

import static com.example.canoestudent.StringUtils.bytesToHexStringList;
import static com.example.canoestudent.StringUtils.get_signed_int;
import static com.example.canoestudent.StringUtils.subBytes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class ChooseDeviceActivity extends AppCompatActivity {
    private static final String TAG = "ChooseDeviceActivity";
    private BluetoothSPP bt;
    private String currentHexStr = "";
    private String fragmentHexStr = "";

    /*data id define*/
    private final static String ACCEL_ID = "10";
    private final static String ANGLE_ID = "20";
    private final static String MAGNETIC_ID = "30";     /*归一化值*/
    private final static String RAW_MAGNETIC_ID = "31";     /*原始值*/
    private final static String EULER_ID = "40";
    private final static String QUATERNION_ID = "41";
    private final static String UTC_ID = "50";
    private final static String LOCATION_ID = "60";
    private final static String SPEED_ID = "70";

    // for specific data id
    private final static int ACCEL_DATA_LEN = 12;
    private final static int ANGLE_DATA_LEN = 12;
    private final static int MAGNETIC_DATA_LEN = 12;
    private final static int MAGNETIC_RAW_DATA_LEN = 12;
    private final static int EULER_DATA_LEN = 12;
    private final static int QUATERNION_DATA_LEN = 16;
    private final static int UTC_DATA_LEN = 11;
    private final static int LOCATION_DATA_LEN = 12;
    private final static int SPEED_DATA_LEN = 12;

    private final static String PROTOCOL_FIRST_BYTE = "59";
    private final static String PROTOCOL_SECOND_BYTE = "53";
    private final static int PROTOCOL_MIN_LEN = 7;

    /*factor for sensor data*/
    private final static float NOT_MAG_DATA_FACTOR = 0.000001f;
    private final static float MAG_RAW_DATA_FACTOR = 0.001f;

    /*factor for gnss data*/
    private final static float LONG_LAT_DATA_FACTOR = 0.0000001f;
    private final static float ALT_DATA_FACTOR = 0.001f;
    private final static float SPEED_DATA_FACTOR = 0.001f;

    private final static int SINGLE_DATA_BYTES = 4;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devicelist);
        bt = new BluetoothSPP(this);

        if (!bt.isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext()
                    , "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }

//        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
//            public void onDataReceived(byte[] data, String message) {
//                Log.i("Check", "Length : " + data.length);
//                Log.i("Check", "Message : " + message);
//            }
//        });

        Button btnConnect = (Button) findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    bt.disconnect();
                } else {
                    Intent intent = new Intent(ChooseDeviceActivity.this, DeviceList.class);
                    intent.putExtra("bluetooth_devices", "Bluetooth devices");
                    intent.putExtra("no_devices_found", "No device");
                    intent.putExtra("scanning", "Scanning");
                    intent.putExtra("scan_for_devices", "Search");
                    intent.putExtra("select_device", "Select");
                    intent.putExtra("layout_list", R.layout.device_layout_list);
                    intent.putExtra("layout_text", R.layout.device_layout_text);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                }
            }
        });
    }

    public void onDestroy() {
        super.onDestroy();
        bt.stopService();
    }

    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) {
            bt.enable();
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                setup();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public void setup() {
        Button btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bt.send("Text", true);
            }
        });
        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                List<String> stringList = bytesToHexStringList(data);
                int hexLen = stringList.size();
                Log.d(TAG, "onDataReceived, hexLen = " + hexLen);
                if (hexLen < PROTOCOL_MIN_LEN) {
                    return;
                }
                int data_pos = 0;
                while (data_pos + PROTOCOL_MIN_LEN < hexLen) {
                    if (PROTOCOL_FIRST_BYTE.equals(stringList.get(0 + data_pos)) &&
                            PROTOCOL_SECOND_BYTE.equals(stringList.get(1 + data_pos))) {
                        int timeId = Integer.valueOf(stringList.get(2 + data_pos), 16) * 10 +
                                Integer.valueOf(stringList.get(3 + data_pos), 16);
                        int payload_len = Integer.valueOf(stringList.get(4 + data_pos), 16);
                        int payload_pos = data_pos + 5;
                        GOutputInfo gOutputInfo = new GOutputInfo();
                        if (payload_pos + payload_len < hexLen) {
                            while (payload_pos < data_pos + payload_len) { // FIXME: 2019-08-17
                                String data_id = stringList.get(payload_pos);
                                int len = Integer.parseInt(stringList.get(payload_pos + 1), 16);
                                if (payload_pos + len + 2 < hexLen) { // FIXME: 2019-08-17
                                    resolveData(data_id, len, data, payload_pos + 2, gOutputInfo);
                                }
                                payload_pos += len + 2;
                                Log.d(TAG, "onDataReceived, payload_pos = " + payload_pos);
                            }
                        }
                        data_pos += PROTOCOL_MIN_LEN + payload_len;
                    } else {
                        data_pos++;
                    }
                }
            }
        });
        bt.setBluetoothStateListener(new BluetoothSPP.BluetoothStateListener() {

            @Override
            public void onServiceStateChanged(int state) {
                if (state == BluetoothState.STATE_CONNECTED) {
                    Log.i(TAG, "Bluetooth STATE_CONNECTED");
                    // Do something when successfully connected
                } else if (state == BluetoothState.STATE_CONNECTING) {
                    Log.i(TAG, "Bluetooth STATE_CONNECTING");
                    // Do something while connecting
                } else if (state == BluetoothState.STATE_LISTEN) {
                    Log.i(TAG, "Bluetooth STATE_LISTEN");
                    // Do something when device is waiting for connection
                } else if (state == BluetoothState.STATE_NONE) {
                    Log.i(TAG, "Bluetooth STATE_NONE");
                    // Do something when device don't have any connection
                }
            }
        });
    }

    private void resolveData(String id, int len, byte[] data, int pos, GOutputInfo gOutputInfo) {
        switch (id) {
            case ACCEL_ID:
                if (ACCEL_DATA_LEN == len) {
                    gOutputInfo.setAccel_x(get_signed_int(subBytes(data, pos + 0, SINGLE_DATA_BYTES)) * NOT_MAG_DATA_FACTOR);
                    gOutputInfo.setAccel_y(get_signed_int(subBytes(data, pos + SINGLE_DATA_BYTES, SINGLE_DATA_BYTES)) * NOT_MAG_DATA_FACTOR);
                    gOutputInfo.setAccel_z(get_signed_int(subBytes(data, pos + SINGLE_DATA_BYTES * 2, SINGLE_DATA_BYTES)) * NOT_MAG_DATA_FACTOR);
                }
                break;
            case ANGLE_ID:
                if (ANGLE_DATA_LEN == len) {
                    gOutputInfo.setAngle_x(get_signed_int(subBytes(data, pos + 0, SINGLE_DATA_BYTES)) * NOT_MAG_DATA_FACTOR);
                    gOutputInfo.setAngle_y(get_signed_int(subBytes(data, pos + SINGLE_DATA_BYTES, SINGLE_DATA_BYTES)) * NOT_MAG_DATA_FACTOR);
                    gOutputInfo.setAngle_z(get_signed_int(subBytes(data, pos + SINGLE_DATA_BYTES * 2, SINGLE_DATA_BYTES)) * NOT_MAG_DATA_FACTOR);
                }
                break;
            case MAGNETIC_ID:
                if (MAGNETIC_DATA_LEN == len) {
                    gOutputInfo.setMag_x(get_signed_int(subBytes(data, pos + 0, SINGLE_DATA_BYTES)) * NOT_MAG_DATA_FACTOR);
                    gOutputInfo.setMag_y(get_signed_int(subBytes(data, pos + SINGLE_DATA_BYTES, SINGLE_DATA_BYTES)) * NOT_MAG_DATA_FACTOR);
                    gOutputInfo.setMag_z(get_signed_int(subBytes(data, pos + SINGLE_DATA_BYTES * 2, SINGLE_DATA_BYTES)) * NOT_MAG_DATA_FACTOR);
                }
                break;
            case RAW_MAGNETIC_ID:
                if (MAGNETIC_RAW_DATA_LEN == len) {
                    gOutputInfo.setRaw_mag_x(get_signed_int(subBytes(data, pos + 0, SINGLE_DATA_BYTES)) * MAG_RAW_DATA_FACTOR);
                    gOutputInfo.setRaw_mag_y(get_signed_int(subBytes(data, pos + SINGLE_DATA_BYTES, SINGLE_DATA_BYTES)) * MAG_RAW_DATA_FACTOR);
                    gOutputInfo.setRaw_mag_z(get_signed_int(subBytes(data, pos + SINGLE_DATA_BYTES * 2, SINGLE_DATA_BYTES)) * MAG_RAW_DATA_FACTOR);
                }
                break;
            case EULER_ID:
                if (EULER_DATA_LEN == len) {
                    gOutputInfo.setPitch(get_signed_int(subBytes(data, pos + 0, SINGLE_DATA_BYTES)) * NOT_MAG_DATA_FACTOR);
                    Log.d(TAG, "" + get_signed_int(subBytes(data, pos + 0, SINGLE_DATA_BYTES)) * NOT_MAG_DATA_FACTOR);
                    gOutputInfo.setRoll(get_signed_int(subBytes(data, pos + SINGLE_DATA_BYTES, SINGLE_DATA_BYTES)) * NOT_MAG_DATA_FACTOR);
                    Log.d(TAG, "" + get_signed_int(subBytes(data, pos + SINGLE_DATA_BYTES, SINGLE_DATA_BYTES)) * NOT_MAG_DATA_FACTOR);
                    gOutputInfo.setYaw(get_signed_int(subBytes(data, pos + SINGLE_DATA_BYTES * 2, SINGLE_DATA_BYTES)) * NOT_MAG_DATA_FACTOR);
                    Log.d(TAG, "" + get_signed_int(subBytes(data, pos + SINGLE_DATA_BYTES * 2, SINGLE_DATA_BYTES)) * NOT_MAG_DATA_FACTOR);
                }
                break;
            case QUATERNION_ID:
                if (QUATERNION_DATA_LEN == len) {
                    gOutputInfo.setQuaternion_data0(get_signed_int(subBytes(data, pos + 0, SINGLE_DATA_BYTES)) * NOT_MAG_DATA_FACTOR);
                    gOutputInfo.setQuaternion_data1(get_signed_int(subBytes(data, pos + SINGLE_DATA_BYTES, SINGLE_DATA_BYTES)) * NOT_MAG_DATA_FACTOR);
                    gOutputInfo.setQuaternion_data2(get_signed_int(subBytes(data, pos + SINGLE_DATA_BYTES * 2, SINGLE_DATA_BYTES)) * NOT_MAG_DATA_FACTOR);
                    gOutputInfo.setQuaternion_data3(get_signed_int(subBytes(data, pos + SINGLE_DATA_BYTES * 3, SINGLE_DATA_BYTES)) * NOT_MAG_DATA_FACTOR);
                }
                break;
            default:
                break;
        }
    }


}
