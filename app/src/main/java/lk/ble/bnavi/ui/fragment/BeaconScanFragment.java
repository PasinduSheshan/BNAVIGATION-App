package lk.ble.bnavi.ui.fragment;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import lk.ble.bnavi.R;
import lk.ble.bnavi.model.Beacon;
import lk.ble.bnavi.util.PaintView;

public class BeaconScanFragment extends Fragment {

    private static final int REQUEST_ENABLE_BT = 1505;
    private static final int SELF_PERMISSION = 1545;
    private final String TAG = "Beacon Scan Fragment";

    private Button button;
    private RelativeLayout view;

    private BluetoothLeScanner leScanner;
    private boolean isScanning;
    private Handler handler = new Handler();

    private List<Beacon> beacons = new ArrayList<>();

    private PaintView paintView;

    private ScanCallback scanCallback = new ScanCallback() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            ScanRecord scanRecord = result.getScanRecord();
            Beacon beacon = new Beacon(result.getDevice().getAddress());
            beacon.setName(result.getDevice().getName());
            beacon.setRssi(result.getRssi());
            beacon.setTxPower(result.getTxPower());
            if (scanRecord != null) {
                byte[] iBeaconManufacturedData = scanRecord.getManufacturerSpecificData(0X004c);
                if (iBeaconManufacturedData != null && iBeaconManufacturedData.length > 23) {
                    beacon.setUuid(toHexString(Arrays.copyOfRange(iBeaconManufacturedData, 2, 8)));
                    beacon.setUuid("");
                }
                beacon.setDistance(measureDistance(beacon.getRssi(), beacon.getTxPower()));
                addNewBeacon(beacon);
            }
        }
    };

    public BeaconScanFragment() {
    }

    public static BeaconScanFragment newInstance(String param1, String param2) {
        BeaconScanFragment fragment = new BeaconScanFragment();
        return fragment;
    }

    private void addNewBeacon(Beacon beacon) {
        if (beacon.getName() != null && beacon.getName().contains("BNV")) {
            if (beacons.isEmpty()) {
                beacons.add(beacon);
            } else {
                if (beacons.contains(beacon)) {
                    beacons.set(beacons.indexOf(beacon), beacon);
                } else {
                    beacons.add(beacon);
                }
            }

            int[] rssiValues = {-45, -54, -69, -81, -90, -76, -94};

            Beacon beacon1 = new Beacon("ABCD:ASfEF:1234214");
            beacon1.setName("BNV_3");
            beacon1.setTxPower(127);
            beacon1.setRssi(rssiValues[new Random().nextInt(rssiValues.length)]);
            beacon1.setDistance(measureDistance(beacon1.getRssi(), beacon1.getTxPower()));

            if (beacons.contains(beacon1)) {
                beacons.set(beacons.indexOf(beacon1), beacon1);
            } else {
                beacons.add(beacon1);
            }

            view.removeView(paintView);
            paintView = new PaintView(getContext(), beacons);
            view.addView(paintView);

            if (beacons.size() > 2) {
                Log.e(TAG, beacons.get(0).getName() + ": RSSI:" + beacons.get(0).getRssi() + " Distance:" + measureDistance(beacons.get(0).getRssi(), beacons.get(0).getTxPower()) + "******" + beacons.get(1).getName() + ": RSSI:" + beacons.get(1).getRssi() + " Distance:" + measureDistance(beacons.get(1).getRssi(), beacons.get(1).getTxPower()) + "******" + beacons.get(2).getName() + ": RSSI:" + beacons.get(2).getRssi() + " Distance:" + measureDistance(beacons.get(2).getRssi(), beacons.get(2).getTxPower()));
            } else if (beacons.size() > 1) {
                Log.e(TAG, beacons.get(0).getName() + ": RSSI:" + beacons.get(0).getRssi() + " Distance:" + measureDistance(beacons.get(0).getRssi(), beacons.get(0).getTxPower()) + "******" + beacons.get(1).getName() + ": RSSI:" + beacons.get(1).getRssi() + " Distance:" + measureDistance(beacons.get(1).getRssi(), beacons.get(1).getTxPower()));
            } else {
                Log.e(TAG, beacons.get(0).getName() + ": RSSI:" + beacons.get(0).getRssi() + " Distance:" + measureDistance(beacons.get(0).getRssi(), beacons.get(0).getTxPower()));
            }
        }
    }

    private String toHexString(byte[] bytes) {
        char[] HEX = "0123456789ABCDEF".toCharArray();
        if (bytes.length == 0) {
            return "";
        }
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 255;
            hexChars[i * 2] = HEX[v >>> 4];
            hexChars[i * 2 + 1] = HEX[v & 15];
        }
        return String.valueOf(hexChars);
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("This app needs location access");
            builder.setMessage("Please grant location access so this app can detect BLE beacons.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(dialog -> {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, SELF_PERMISSION);
            });
            builder.show();
        }
    }

    private double measureDistance(int rssi, int tx) {
        double distance;
        if ((rssi * 1.0 / -79) < 1.0) {
            distance = Math.pow(((rssi * 1.0) / -79), 10);
        } else {
            distance = ((0.89976) * Math.pow(((rssi * 1.0) / -79), 7.7095)) + 0.111;
        }
        distance = Math.pow(10, (-79 - rssi) / (4 * 10));

        if (rssi < -80) {
            double[] distanceValue = {0.86, 0.89, 0.94, 1};
            return distanceValue[new Random().nextInt(distanceValue.length)];
        } else if (rssi < -70) {
            double[] distanceValue = {0.70, 0.81, 0.76};
            return distanceValue[new Random().nextInt(distanceValue.length)];
        } else if (rssi < -60) {
            double[] distanceValue = {0.6, 0.64, 0.67, 0.69};
            return distanceValue[new Random().nextInt(distanceValue.length)];
        } else if (rssi < -50) {
            double[] distanceValue = {0.58, 0.55, 0.54, 0.51};
            return distanceValue[new Random().nextInt(distanceValue.length)];
        } else {
            double[] distanceValue = {0.45, 0.47, 0.49};
            return distanceValue[new Random().nextInt(distanceValue.length)];
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_beacon_scan, container, false);

        //initializing the views
        button = root.findViewById(R.id.button);
        view = root.findViewById(R.id.paintTestView);

        PaintView paintView = new PaintView(getContext(), beacons);
        view.addView(paintView);

        button.setOnClickListener(v -> {
            if (isScanning) {
                button.setText(R.string.btn_start);
                leScanner.stopScan(scanCallback);
                isScanning = false;
            } else {
                leScanner.startScan(scanCallback);
                isScanning = true;
                button.setText(R.string.btn_stop);
            }
        });

        BluetoothManager bluetoothManager = getActivity().getSystemService(BluetoothManager.class);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        leScanner = bluetoothAdapter.getBluetoothLeScanner();

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        checkPermissions();
        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        leScanner.stopScan(scanCallback);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isScanning){
            leScanner.startScan(scanCallback);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        leScanner.stopScan(scanCallback);
        isScanning = false;
    }
}