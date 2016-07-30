package com.prasadam.kmrplayer;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.prasadam.kmrplayer.ActivityHelperClasses.ActivityHelper;
import com.prasadam.kmrplayer.ActivityHelperClasses.ActivitySwitcher;
import com.prasadam.kmrplayer.AudioPackages.AudioExtensionMethods;
import com.prasadam.kmrplayer.AudioPackages.modelClasses.Song;
import com.prasadam.kmrplayer.SharedClasses.SharedVariables;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.prasadam.kmrplayer.SharedClasses.ExtensionMethods.setStatusBarTranslucent;

/*
 * Created by Prasadam Saiteja on 7/26/2016.
 */

public class MostPlayedSongsPieChartActivity extends AppCompatActivity {

    @Bind(R.id.pie_chart) PieChart pieChart;
    private ArrayList<Song> songsList = new ArrayList<>();

    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_most_played_pie_chart_layout);
        ButterKnife.bind(this);
        setStatusBarTranslucent(MostPlayedSongsPieChartActivity.this);
        ActivityHelper.setDisplayHome(this);

        configPieChart();
        songsList = AudioExtensionMethods.getMostPlayedSongsList(this);
        setPieChart();
    }
    public void onResume() {
        super.onResume();
        SharedVariables.globalActivityContext = this;
    }
    public boolean onOptionsItemSelected(MenuItem item) {

        final int id = item.getItemId();
        switch (id){

            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    private void configPieChart() {
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setTouchEnabled(true);
        pieChart.setRotationEnabled(true);
        pieChart.setExtraOffsets(20, 20, 20, 20);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.setRotationAngle(180);
        pieChart.setCenterText("Top Most Played");
        pieChart.setEntryLabelColor(Color.DKGRAY);
    }
    private void setPieChart() {
        PieData pieData = new PieData();
        ArrayList<PieEntry> entries = new ArrayList<>();

        int count = 0;
        for (Song song : songsList) {
            String songName;
            if(song.getTitle().trim().length() < 14)
                songName = song.getTitle().trim();
            else
                songName = song.getTitle().trim().substring(0, 14) + "...";
            entries.add(new PieEntry(song.repeatCount, songName));
            count++;
            if(count > 6)
                break;
        }
        PieDataSet dataSet = new PieDataSet(entries, "Most Played");
        dataSet.setValueLinePart1OffsetPercentage(90.f);
        dataSet.setValueLinePart1Length(0.4f);
        dataSet.setValueLinePart2Length(0.4f);
        dataSet.setValueTextSize(11);
        dataSet.setSliceSpace(3f);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieData.addDataSet(dataSet);
        pieChart.setData(pieData);

        ArrayList<Integer> colors = new ArrayList<>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS) colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS) colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS) colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS) colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS) colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);
    }
}