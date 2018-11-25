package com.example.vladsobol.bavus;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.fleetboard.sdk.lib.android.log.Log;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class GraphActivity extends AppCompatActivity {

    private RelativeLayout mainLayout;
    private LineChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        //mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);

        //Create Line Chart
        mChart = (LineChart) findViewById(R.id.chart);
        //add to mainLayout
        //mainLayout.addView(mChart);

        //Customise Line Chart
        Description description = new Description();
        description.setTextColor(ColorTemplate.VORDIPLOM_COLORS[2]);
        description.setText("Chart Data");
        mChart.setDescription(description);

        mChart.setNoDataText("No data for the moment");

        //enable value highlinting

        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setPinchZoom(true);
        mChart.setBackgroundColor(Color.LTGRAY);


        //set Data
        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        //add data to line chart
        mChart.setData(data);

        //get legend object
        Legend l = mChart.getLegend();

        //Custom Legend
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);

        YAxis yl = mChart.getAxisLeft();
        yl.setTextColor(Color.WHITE);
        yl.setAxisMaximum(120f);
        yl.setDrawGridLines(true);


        YAxis yl2 = mChart.getAxisRight();
        yl2.setEnabled(false);

    }

    @Override
    protected void onResume() {
        super.onResume();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i=0; i<100; i++){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addEntity();
                        }
                    });
                    try {
                        Thread.sleep(600);
                    } catch (InterruptedException e) {
                        //manage Error ...
                    }
                }
            }
        }).start();
    }

    private void addEntity() {
        LineData data = mChart.getData();

        if (data != null){
            mChart.getData().setHighlightEnabled(true);
            LineDataSet set = (LineDataSet) data.getDataSetByIndex(0);
            if (set == null){
                set = createSet();
                data.addDataSet(set);
            }
            data.addEntry(new Entry(set.getEntryCount(), (float) (Math.random() * 75) + 60f), 0);
            mChart.notifyDataSetChanged();
            mChart.setVisibleXRange(0,6);
            mChart.moveViewToX(data.getXMax() - 7);//getXValAcount()
            mChart.invalidate();
        }
    }

    private LineDataSet createSet(){
        LineDataSet set = new LineDataSet(null, "SPL Db");
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        set.setAxisDependency(YAxis.AxisDependency.LEFT); //this one
        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(ColorTemplate.getHoloBlue());
        set.setLineWidth(2f);
        set.setCircleSize(4f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244,117,177));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(10f);

        return set;
    }
}
