package lk.ble.bnavi.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import lk.ble.bnavi.model.Beacon;

public class PaintView extends View {

    private final Paint beaconPaint;
    private final Paint beaconBorderPaint;
    private final Paint userPaint;
    private final Paint userBorderPaint;
    private final Paint textPaint;
    private final float arcRadius;

    private final Context context;
    private List<Beacon> beacons;

    public PaintView(Context context, List<Beacon> beacons) {
        super(context);

        this.context = context;
        this.beacons = beacons;
        textPaint = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(pxFormDp(context, 14));

        DisplayMetrics displayMetrics = new DisplayMetrics();

        // on below line we are getting display metrics.
        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);

        arcRadius = pxFormDp(context, 8);
        Log.e("PaintView: ", "arc: " + arcRadius);

        beaconPaint = new Paint();
        beaconBorderPaint = new Paint();
        userPaint = new Paint();
        userBorderPaint = new Paint();
    }

    private float pxFormDp(Context context, int dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    private float[] centerOfIntersection(float d, float r1, float r2) {
        double v = Math.pow(d, 2) - Math.pow(r2, 2) + Math.pow(r1, 2);

        //get the xy coordinates of the intersection
        float x0 = (float) v / (2 * d);
        float y0 = (float) ((2 * d * r1) - v) / (2 * d);

        Log.e("centerOfIntersection: ", "x: " + x0 + ", y: " + y0);
        return new float[]{x0, y0};
    }

    private float[] getCenterPoint(float beacon12x, float beacon13y, float beacon13x, float beacon23y, float beacon23x) {
        if (beacon12x < 180) {
            beacon12x = beacon12x * -1;
        } else {
            beacon12x = beacon12x - 180;
        }
        float x0 = (beacon13x + beacon12x + beacon23x) / 3;
        float y0 = (beacon13y + beacon23y) / 3;

        return new float[]{x0, y0};
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //set colors to beacons and user
        beaconPaint.setColor(Color.BLUE);
        beaconPaint.setStyle(Paint.Style.FILL);

        beaconBorderPaint.setColor(Color.BLUE);
        beaconBorderPaint.setStyle(Paint.Style.FILL);
        beaconBorderPaint.setAlpha(20);

        userPaint.setColor(Color.RED);
        userPaint.setStyle(Paint.Style.FILL);

        userBorderPaint.setColor(Color.RED);
        userBorderPaint.setStyle(Paint.Style.FILL);
        userBorderPaint.setAlpha(30);

        float beacon1_X = pxFormDp(context, 120);
        float beacon2_X = getWidth() - pxFormDp(context, 120);

        if (beacons.size() == 3) {
            float beacon1_distance = (float) 0;
            float beacon2_distance = (float) 0;
            float beacon3_distance = (float) 0;

            for (Beacon beacon : beacons) {
                if (beacon.getName().contains("1")) {
                    beacon1_distance = (float) beacon.getDistance();

                    //draw beacon 1's coordination
                    canvas.drawCircle(beacon1_X, getHeight() / 2, arcRadius, beaconPaint);
                    canvas.drawCircle(beacon1_X, getHeight() / 2, beacon1_distance * 360, beaconBorderPaint);
                    canvas.drawText("Beacon 1", (beacon1_X) - (arcRadius * 4), (getHeight() / 2) + (arcRadius * 3), textPaint);
                    canvas.drawText(String.valueOf(beacon1_distance) + " m", (beacon1_X) - (arcRadius * 4), (getHeight() / 2) + (arcRadius * 4.5f), textPaint);
                    canvas.drawText("RSSI: " + String.valueOf(beacon.getRssi()), (beacon1_X) - (arcRadius * 4), (getHeight() / 2) + (arcRadius * 6), textPaint);
                } else if (beacon.getName().contains("2")) {
                    beacon2_distance = (float) beacon.getDistance();

                    //draw beacon 2's coordination
                    canvas.drawCircle(beacon2_X, getHeight() / 2, arcRadius, beaconPaint);
                    canvas.drawCircle(beacon2_X, getHeight() / 2, beacon2_distance * 360, beaconBorderPaint);
                    canvas.drawText("Beacon 2", (beacon2_X) - (arcRadius * 4), (getHeight() / 2) + (arcRadius * 3), textPaint);
                    canvas.drawText(String.valueOf(beacon2_distance) + " m", (beacon2_X) - (arcRadius * 4), (getHeight() / 2) + (arcRadius * 4.5f), textPaint);
                    canvas.drawText("RSSI: " + String.valueOf(beacon.getRssi()), (beacon2_X) - (arcRadius * 4), (getHeight() / 2) + (arcRadius * 6), textPaint);
                } else {
                    beacon3_distance = (float) beacon.getDistance();

                    //draw beacon 3's coordination
                    canvas.drawCircle(getWidth() / 2, getHeight() / 2 - 240, arcRadius, beaconPaint);
                    canvas.drawCircle(getWidth() / 2, getHeight() / 2 - 240, beacon3_distance * 360, beaconBorderPaint);
                    canvas.drawText("Beacon 3", (getWidth() / 2) - (arcRadius * 4), (getHeight() / 2 - 240) - (arcRadius * 6), textPaint);
                    canvas.drawText(String.valueOf(beacon3_distance) + " m", (getWidth() / 2) - (arcRadius * 4), (getHeight() / 2 - 240) - (arcRadius * 4.5f), textPaint);
                    canvas.drawText("RSSI: " + String.valueOf(beacon.getRssi()), (getWidth() / 2) - (arcRadius * 4), (getHeight() / 2 - 240) - (arcRadius * 3), textPaint);
                }
            }

            //get center point of 3 circles' intersections
            float beacon12CenterPoint = centerOfIntersection(360, beacon1_distance * 360, beacon2_distance * 360)[0];    //Beacon 1 to Beacon 2
            float beacon13CenterPoint = centerOfIntersection(300, beacon1_distance * 360, beacon3_distance * 360)[0];    //Beacon 1 to Beacon 3
            float beacon23CenterPoint = centerOfIntersection(300, beacon2_distance * 360, beacon3_distance * 360)[0];    //Beacon 2 to Beacon 3

            //get user's coordinates by passing center point values that previously found
            float[] centerCoordinates = getCenterPoint(beacon12CenterPoint, (beacon13CenterPoint * 3) / 5, (beacon13CenterPoint * 4) / 5, (beacon23CenterPoint * 3) / 5, (beacon23CenterPoint * 4) / 5);

            //draw user's coordination
            canvas.drawCircle((getWidth() / 2) - centerCoordinates[0], getHeight() / 2 - centerCoordinates[1], arcRadius * 3, userBorderPaint);
            canvas.drawCircle((getWidth() / 2) - centerCoordinates[0], getHeight() / 2 - centerCoordinates[1], arcRadius, userPaint);
            canvas.drawText("User", (getWidth() / 2) - centerCoordinates[0], getHeight() / 2 - centerCoordinates[1], textPaint);
        } else if (beacons.size() == 2) {
            float beacon1_distance = (float) 0;
            float beacon2_distance = (float) 0;
            float beacon3_distance = (float) 0;

            for (Beacon beacon : beacons) {
                if (beacon.getName().contains("1")) {
                    beacon1_distance = (float) beacon.getDistance();

                    canvas.drawCircle(beacon1_X, getHeight() / 2, arcRadius, beaconPaint);
                    canvas.drawCircle(beacon1_X, getHeight() / 2, beacon1_distance * 360, beaconBorderPaint);
                    canvas.drawText("Beacon 1", (beacon1_X) - (arcRadius * 4), (getHeight() / 2) + (arcRadius * 3), textPaint);
                    canvas.drawText(String.valueOf(beacon1_distance) + " m", (beacon1_X) - (arcRadius * 4), (getHeight() / 2) + (arcRadius * 4.5f), textPaint);
                    canvas.drawText("RSSI: " + String.valueOf(beacon.getRssi()), (beacon1_X) - (arcRadius * 4), (getHeight() / 2) + (arcRadius * 6), textPaint);
                } else if (beacon.getName().contains("2")) {
                    beacon2_distance = (float) beacon.getDistance();

                    canvas.drawCircle(beacon2_X, getHeight() / 2, arcRadius, beaconPaint);
                    canvas.drawCircle(beacon2_X, getHeight() / 2, beacon2_distance * 360, beaconBorderPaint);
                    canvas.drawText("Beacon 2", (beacon2_X) - (arcRadius * 4), (getHeight() / 2) + (arcRadius * 3), textPaint);
                    canvas.drawText(String.valueOf(beacon2_distance) + " m", (beacon2_X) - (arcRadius * 4), (getHeight() / 2) + (arcRadius * 4.5f), textPaint);
                    canvas.drawText("RSSI: " + String.valueOf(beacon.getRssi()), (beacon2_X) - (arcRadius * 4), (getHeight() / 2) + (arcRadius * 6), textPaint);
                } else {
                    beacon3_distance = (float) beacon.getDistance();

                    canvas.drawCircle(getWidth() / 2, getHeight() / 2 - 240, arcRadius, beaconPaint);
                    canvas.drawCircle(getWidth() / 2, getHeight() / 2 - 240, beacon3_distance * 360, beaconBorderPaint);
                    canvas.drawText("Beacon 3", (getWidth() / 2) - (arcRadius * 4), (getHeight() / 2 - 240) - (arcRadius * 6), textPaint);
                    canvas.drawText(String.valueOf(beacon3_distance) + " m", (getWidth() / 2) - (arcRadius * 4), (getHeight() / 2 - 240) - (arcRadius * 4.5f), textPaint);
                    canvas.drawText("RSSI: " + String.valueOf(beacon.getRssi()), (getWidth() / 2) - (arcRadius * 4), (getHeight() / 2 - 240) - (arcRadius * 3), textPaint);
                }
            }

            if (beacon1_distance == 0) {
                float beacon23CenterPoint = centerOfIntersection(300, beacon2_distance * 360, beacon3_distance * 360)[0];    //Beacon 2 to Beacon 3
                canvas.drawCircle(getWidth() / 2 + 180 - (beacon23CenterPoint * 3) / 5, getHeight() / 2 - (beacon23CenterPoint * 4) / 5, arcRadius * 4, userBorderPaint);
                canvas.drawCircle(getWidth() / 2 + 180 - (beacon23CenterPoint * 3) / 5, getHeight() / 2 - (beacon23CenterPoint * 4) / 5, arcRadius, userPaint);
                canvas.drawText("User", getWidth() / 2 + 180 - (beacon23CenterPoint * 3) / 5 + arcRadius * 2, getHeight() / 2 - (beacon23CenterPoint * 4) / 5, textPaint);
            } else if (beacon2_distance == 0) {
                float beacon13CenterPoint = centerOfIntersection(300, beacon1_distance * 360, beacon3_distance * 360)[0];    //Beacon 1 to Beacon 3
                Log.e("onDraw: ", String.valueOf(beacon13CenterPoint));
                canvas.drawCircle(getWidth() / 2 - 180 + (beacon13CenterPoint * 3) / 5, getHeight() / 2 - (beacon13CenterPoint * 4) / 5, arcRadius * 4, userBorderPaint);
                canvas.drawCircle(getWidth() / 2 - 180 + (beacon13CenterPoint * 3) / 5, getHeight() / 2 - (beacon13CenterPoint * 4) / 5, arcRadius, userPaint);
                canvas.drawText("User", getWidth() / 2 - 180 + (beacon13CenterPoint * 3) / 5, getHeight() / 2 - (beacon13CenterPoint * 4) / 5, textPaint);
            } else {
                float beacon12CenterPoint = centerOfIntersection(360, beacon1_distance * 360, beacon2_distance * 360)[0];    //Beacon 1 to Beacon 2
                canvas.drawCircle(getWidth() / 2 + beacon12CenterPoint, getHeight() / 2, arcRadius * 4, userBorderPaint);
                canvas.drawCircle(getWidth() / 2 + beacon12CenterPoint, getHeight() / 2, arcRadius, userPaint);
                canvas.drawText("User", getWidth() / 2 + beacon12CenterPoint, getHeight() / 2, textPaint);
            }
        } else {
            Toast.makeText(context, "Be in range of at least two beacons", Toast.LENGTH_SHORT).show();
        }

    }
}
