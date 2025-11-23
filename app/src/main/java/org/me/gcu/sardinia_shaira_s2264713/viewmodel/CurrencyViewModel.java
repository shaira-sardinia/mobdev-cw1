package org.me.gcu.sardinia_shaira_s2264713.viewmodel;
import org.me.gcu.sardinia_shaira_s2264713.data.CurrencyDataFetcher;
import org.me.gcu.sardinia_shaira_s2264713.data.CurrencyItem;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CurrencyViewModel extends ViewModel implements CurrencyDataFetcher.DataFetchListener {

    private final String TAG = "CurrencyViewModel";
    private final String URL_SOURCE = "https://www.fx-exchange.com/gbp/rss.xml";
    private static final long UPDATE_INTERVAL = 10000;
    private final ExecutorService executorService;
    private final Handler updateHandler;
    private final CurrencyDataFetcher dataFetcher;
    private final Runnable updateRunnable;
    private final MutableLiveData<ArrayList<CurrencyItem>> currencyListLiveData;
    private final MutableLiveData<String> preSelectedCurrencyCode;
    private final MutableLiveData<Boolean> isLoading;
    private final MutableLiveData<String> errorMessage;
    private final MutableLiveData<ArrayList<String>> savedCurrencyCodes = new MutableLiveData<>(new ArrayList<>());
    private static final String[] PREVIEW_CURRENCY_CODES = {"USD", "EUR", "JPY"};


    public CurrencyViewModel() {
        /* Initialize Live Data */
        currencyListLiveData = new MutableLiveData<>(new ArrayList<>());
        preSelectedCurrencyCode = new MutableLiveData<>(null);
        isLoading = new MutableLiveData<>(false);
        errorMessage = new MutableLiveData<>(null);

        /* Initialize Threading Components */
        executorService = Executors.newSingleThreadExecutor();
        updateHandler = new Handler(Looper.getMainLooper());
        dataFetcher = new CurrencyDataFetcher(URL_SOURCE, this);

        updateRunnable = new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Auto-update triggered.");
                fetchCurrencyData();
                updateHandler.postDelayed(this, UPDATE_INTERVAL);
            }
        };
    }

    /**
     * Methods
     */
    public LiveData<ArrayList<CurrencyItem>> getCurrencyList() {
        return currencyListLiveData;
    }

    public ArrayList<CurrencyItem> getCurrencyListValue() {
        ArrayList<CurrencyItem> list = currencyListLiveData.getValue();
        return list != null ? list : new ArrayList<>();
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void setCurrencyList(ArrayList<CurrencyItem> list) {
        currencyListLiveData.postValue(list);
    }

    public void setPreSelectedCurrency(String currencyCode) {
        preSelectedCurrencyCode.postValue(currencyCode);
    }

    public String getPreSelectedCurrencyValue() {
        return preSelectedCurrencyCode.getValue();
    }

    public void clearPreSelection() {
        preSelectedCurrencyCode.postValue(null);
    }

    public void setIsLoading(boolean loading) {
        isLoading.postValue(loading);
    }

    public void setErrorMessage(String message) {
        errorMessage.postValue(message);
    }

    public void clearError() {
        errorMessage.postValue(null);
    }

    /**
     * Data fetching and scheduling
     */
    public void startInitialFetchAndAutoUpdate() {
        fetchCurrencyData();
        updateHandler.postDelayed(updateRunnable, UPDATE_INTERVAL);
    }

    public void stopAutoUpdate() {
        updateHandler.removeCallbacks(updateRunnable);
    }

    @Override
    public void onDataFetched(ArrayList<CurrencyItem> fetchedList) {
        Log.d(TAG, "Data fetched successfully. Items received: " + fetchedList.size());
        setCurrencyList(fetchedList);
        setIsLoading(false);
    }

    @Override
    public void onFetchError(String error) {
        Log.e(TAG, "Data fetching failed: " + error);

        switch (error) {
            case "NETWORK_ERROR":
                setErrorMessage("NETWORK_ERROR");
                break;
            default:
                setErrorMessage("DATA_ERROR");
                break;
        }
        setIsLoading(false);
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        updateHandler.removeCallbacks(updateRunnable);
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
            Log.d(TAG, "ExecutorService shut down.");
        }
    }

    public void resumeAutoUpdate() {
        updateHandler.postDelayed(updateRunnable, UPDATE_INTERVAL);
    }

    private void fetchCurrencyData() {
        setIsLoading(true);
        setErrorMessage(null);
        executorService.execute(dataFetcher::fetchData);
    }

    /* Get preview currencies for home screen */
    public ArrayList<CurrencyItem> getPreviewCurrencies() {
        ArrayList<CurrencyItem> previewList = new ArrayList<>();
        ArrayList<CurrencyItem> fullList = getCurrencyListValue();

        for (String code : PREVIEW_CURRENCY_CODES) {
            for (CurrencyItem item : fullList) {
                if (code.equals(item.getTargetCurrencyCode())) {
                    previewList.add(item);
                    break;
                }
            }
        }

        return previewList;
    }

    /* Get saved currency codes (observable) */
    public LiveData<ArrayList<String>> getSavedCurrencyCodes() {
        return savedCurrencyCodes;
    }

    /* Save a currency by code */
    public void saveCurrency(String currencyCode) {
        ArrayList<String> currentSaved = savedCurrencyCodes.getValue();
        if (currentSaved == null) {
            currentSaved = new ArrayList<>();
        }

        if (!currentSaved.contains(currencyCode)) {
            currentSaved.add(currencyCode);
            savedCurrencyCodes.postValue(currentSaved);
        }
    }

    /* Remove a saved currency */
    public void removeSavedCurrency(String currencyCode) {
        ArrayList<String> currentSaved = savedCurrencyCodes.getValue();
        if (currentSaved != null) {
            currentSaved.remove(currencyCode);
            savedCurrencyCodes.postValue(currentSaved);
        }
    }

    /* Get saved currencies as CurrencyItem list */
    public ArrayList<CurrencyItem> getSavedCurrencies() {
        ArrayList<CurrencyItem> savedItems = new ArrayList<>();
        ArrayList<String> savedCodes = savedCurrencyCodes.getValue();
        ArrayList<CurrencyItem> fullList = getCurrencyListValue();

        if (savedCodes != null) {
            for (String code : savedCodes) {
                for (CurrencyItem item : fullList) {
                    if (code.equals(item.getTargetCurrencyCode())) {
                        savedItems.add(item);
                        break;
                    }
                }
            }
        }

        return savedItems;
    }
}