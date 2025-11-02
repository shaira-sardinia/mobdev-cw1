/*  Starter project for Mobile Platform Development - 1st diet 25/26
    You should use this project as the starting point for your assignment.
    This project simply reads the data from the required URL and displays the
    raw data in a TextField
*/

//
// Name                 Shaira Sardinia
// Student ID           S2264713
// Programme of Study   BSc (Hons) Software Development (GA)
//

package org.me.gcu.sardinia_shaira_s2264713;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.os.Looper;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements CurrencyDataFetcher.DataFetchListener {

    private static final String TAG = "MainActivity";
    private ArrayList<CurrencyItem> currencyList = new ArrayList<>();
    private String URL_SOURCE="https://www.fx-exchange.com/gbp/rss.xml";
    private static final long UPDATE_INTERVAL = 10000;
    private CurrencyDataFetcher dataFetcher;
    private CurrencyAdapter adapter;
    private ListView listView;
    private Button startButton;
    private Handler updateHandler;
    private Runnable updateRunnable;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
//        startButton = findViewById(R.id.startButton);

        initializeData();
        fetchCurrencyData();
        setAutoUpdate();
    }

    private void initializeData() {
        currencyList = new ArrayList<>();
        executorService = Executors.newSingleThreadExecutor();
        updateHandler = new Handler(Looper.getMainLooper());
    }

    private void fetchCurrencyData() {
        Log.d(TAG, "Starting fetchData call on background thread");

        dataFetcher = new CurrencyDataFetcher(URL_SOURCE, this);
        executorService.execute(() -> dataFetcher.fetchData());
    }

    private void setAutoUpdate() {
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Auto-update triggered.");
                fetchCurrencyData();
                updateHandler.postDelayed(this, UPDATE_INTERVAL);
            }
        };
    }

    @Override
    public void onDataFetched(ArrayList<CurrencyItem> fetchedList) {
        updateHandler.post(() -> {
            Log.d(TAG, "Data fetched successfully. Items received: " + fetchedList.size());

            currencyList.clear();
            currencyList.addAll(fetchedList);

            if (adapter == null) {
                adapter = new CurrencyAdapter(MainActivity.this, currencyList);
                listView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onFetchError(String error) {
        updateHandler.post(() -> {
            Log.e(TAG, "Data fetching failed: " + error);
            // error handling!
        });
    }

    @Override
    protected  void onResume() {
        super.onResume();
        updateHandler.postDelayed(updateRunnable, UPDATE_INTERVAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateHandler.removeCallbacks(updateRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateHandler.removeCallbacks(updateRunnable);
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            Log.d(TAG, "ExecutorService shut down.");
        }
    }
}