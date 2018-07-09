package com.example.karlo.stepcounter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private TextView sensorText;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorText = findViewById(R.id.steps);
        listView = findViewById(R.id.listView);
        StepCounterService.startCounting(this);
        setupReceiver();
        setUpList();
        sensorText.setText(String.valueOf(
                SharedPrefsUtility.getInt(this, SharedPrefsUtility.COUNT)));
    }

    private void setupReceiver() {
        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        int counter = intent.getIntExtra(StepCounterService.EXTRA_COUNTER_VALUE, 0);
                        sensorText.setText(String.valueOf(counter));
                        setUpList();
                    }
                }, new IntentFilter(StepCounterService.ACTION_UPDATE_TEXT)
        );
    }

    private void setUpList() {
        if (SharedPrefsUtility.getList(this, SharedPrefsUtility.DAY_COUNTS) != null) {
            List<String> list = new ArrayList<>(SharedPrefsUtility.getList(this, SharedPrefsUtility.DAY_COUNTS));
            Collections.sort(list, COMPARATOR);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    R.layout.support_simple_spinner_dropdown_item,
                    list);
            listView.setAdapter(adapter);
        }
    }

    static final Comparator<Object> COMPARATOR = (o1, o2) -> {
        double num1;
        double num2;
        try {
            num1 = Double.parseDouble(o1.toString());
            num2 = Double.parseDouble(o2.toString());
            return Double.compare(num1, num2);
        } catch (Exception e) {
            return o1.toString().compareTo(o2.toString());
        }
    };
}
