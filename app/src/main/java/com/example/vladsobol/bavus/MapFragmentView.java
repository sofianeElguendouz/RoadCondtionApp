/*
 * Copyright (c) 2011-2018 HERE Europe B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.vladsobol.bavus;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPolyline;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;
import com.here.android.mpa.mapping.MapPolyline;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * This class encapsulates the properties and functionality of the Map view.
 */
public class MapFragmentView {
    private MapFragment m_mapFragment;
    private Activity m_activity;
    private Map m_map;

    public MapFragmentView(Activity activity) {
        m_activity = activity;
        initMapFragment();
    }

    // Google has deprecated android.app.Fragment class. It is used in current SDK implementation.
    // Will be fixed in future SDK version.
    @SuppressWarnings("deprecation")
    private MapFragment getMapFragment() {
        return (MapFragment) m_activity.getFragmentManager().findFragmentById(R.id.mapfragment);
    }

    private int ratingToColor(double rating) {
        int orange = 0xffffa801;
        int red = 0xffff5e57;
        int green = 0xff0be881;

        if (rating > 0.5) {
            int diff = red - orange;
            int col = (int)(orange + diff * (rating - 0.5) / 0.5);
            return col;
        } else {
            int diff = orange - green;
            int col = (int)(green + diff * (rating) / 0.5);
            return col;
        }
        /*int alpha = 255;
        int blue = 0;
        int green = Math.round(((1 << 8) - 1) * (float)Math.pow(1 - rating, 2));
        int red = Math.round(((1 << 8) - 1) * (float)(rating));
        int result = (alpha << 24) + (red << 16) + (green << 8) + blue; */
        //return result;

    }

    private void drawLine(MapFragment map, double lat1, double lon1, double lat2, double lon2, double rating) {
        List<GeoCoordinate> testPoints = new ArrayList<GeoCoordinate>();
        testPoints.add(new GeoCoordinate(lat1, lon1, 10));
        testPoints.add(new GeoCoordinate(lat2, lon2, 10));
        GeoPolyline polyline = new GeoPolyline(testPoints);
        MapPolyline mapPolyline = new MapPolyline(polyline);
        mapPolyline.setLineColor(ratingToColor(rating));
        mapPolyline.setLineWidth(10);
        mapPolyline.setZIndex(100);
        map.getMap().addMapObject(mapPolyline);
    }

    private void drawFile(String filename) {
        //drawLine(m_mapFragment, 60.184881, 24.832714, 60.1866719, 25.8254933, 0.7);
        try {
            InputStreamReader is = new InputStreamReader(m_mapFragment
                    .getActivity()
                    .getAssets()
                    .open(filename));

            BufferedReader reader = new BufferedReader(is);

            reader.readLine();
            String line;
            double lastLat = 0, lastLon = 0;
            boolean wasLast = false;
            double average = 0;
            double alpha = 1;

            while ((line = reader.readLine()) != null) {
                Log.i("reading file", line);
                if (Math.random() < 0.9) {
                    String[] values = line.split(",");
                    double[] parsed = new double[values.length];
                    for (int i = 0; i < values.length; i++) {
                        parsed[i] = Double.valueOf(values[i]);
                        Log.i("Parsed a value", "" + parsed[i]);
                    }
                    average = average * (1 - alpha) + alpha * parsed[2];
                    if (wasLast) {
                        drawLine(m_mapFragment, lastLat, lastLon, parsed[0], parsed[1], average);
                    }
                    wasLast = true;
                    lastLat = parsed[0];
                    lastLon = parsed[1];
                }
            }
        } catch (Exception e)
        {
            Log.d("reading file", "didn't work");
        }
    }

    private void initMapFragment() {
        /* Locate the mapFragment UI element */
        m_mapFragment = getMapFragment();



        // Set path of isolated disk cache
        String diskCacheRoot = Environment.getExternalStorageDirectory().getPath()
                + File.separator + ".isolated-here-maps";
        // Retrieve intent name from manifest
        String intentName = "";
        try {
            ApplicationInfo ai = m_activity.getPackageManager().getApplicationInfo(m_activity.getPackageName(),
                    PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            intentName = "NAME";
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(this.getClass().toString(), "Failed to find intent name, NameNotFound: " + e.getMessage());
        }

        // Some check was done here before, not sure what's happening.
        if (m_mapFragment != null) {
        /* Initialize the MapFragment, results will be given via the called back. */
            m_mapFragment.init(new OnEngineInitListener() {
                @Override
                public void onEngineInitializationCompleted(Error error) {

                    if (error == Error.NONE) {
                    /*
                     * If no error returned from map fragment initialization, the map will be
                     * rendered on screen at this moment.Further actions on map can be provided
                     * by calling Map APIs.
                     */
                        m_map = m_mapFragment.getMap();

                        List<String> schemes = m_mapFragment.getMap().getMapSchemes();
                        for (String s : schemes) {
                            Log.i("Scheme", s);
                        }
                        m_map.setMapScheme(schemes.get(2));

                        /*
                         * Map center can be set to a desired location at this point.
                         * It also can be set to the current location ,which needs to be delivered by the PositioningManager.
                         * Please refer to the user guide for how to get the real-time location.
                         */

                        m_map.setCenter(new GeoCoordinate(
                                60.184881, 24.832714), Map.Animation.NONE);
                        m_map.setZoomLevel(16);
                        PositioningManager mPositioningManager = PositioningManager.getInstance();
                        mPositioningManager.start(PositioningManager.LocationMethod.GPS);
                        m_map.getPositionIndicator().setVisible(true);
                    } else {
                        Toast.makeText(m_activity,
                                "ERROR: Cannot initialize Map with error " + error,
                                Toast.LENGTH_LONG).show();
                    }

                    //drawFile("out.csv");
                    //drawFile("out2.csv");

                }

            });
        }
    }
}
