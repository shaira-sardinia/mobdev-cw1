package org.me.gcu.sardinia_shaira_s2264713.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.me.gcu.sardinia_shaira_s2264713.R;
import org.me.gcu.sardinia_shaira_s2264713.utils.CurrencyConverter;
import org.me.gcu.sardinia_shaira_s2264713.data.CurrencyItem;
import org.me.gcu.sardinia_shaira_s2264713.utils.ErrorHandler;
import org.me.gcu.sardinia_shaira_s2264713.viewmodel.CurrencyViewModel;

import java.util.ArrayList;
import java.util.Locale;

public class CurrencyConversionFragment extends Fragment {

    private CurrencyViewModel viewModel;
    private Spinner fromCurrencySpinner;
    private Spinner toCurrencySpinner;
    private EditText fromAmountEditText;
    private EditText toAmountEditText;
    private Button convertButton;
    private ImageView swapButton;
    private TextView exchangeRateTextView;

    private ArrayList<String> currencyCodes;
    private boolean spinnersInitialized = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(CurrencyViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_currency_conversion, container, false);

        fromCurrencySpinner = view.findViewById(R.id.fromCurrencySpinner);
        toCurrencySpinner = view.findViewById(R.id.toCurrencySpinner);
        fromAmountEditText = view.findViewById(R.id.fromAmountEditText);
        toAmountEditText = view.findViewById(R.id.toAmountEditText);
        convertButton = view.findViewById(R.id.convertButton);
        swapButton = view.findViewById(R.id.swapButton);
        exchangeRateTextView = view.findViewById(R.id.exchangeRateTextView);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupSpinners();
        setupConvertButton();
        setupSwapButton();
    }

    /**
     * Set UI
     */
    private void setupSpinners() {
        viewModel.getCurrencyList().observe(getViewLifecycleOwner(), currencyList -> {
            if (currencyList != null && !currencyList.isEmpty()) {

                /* Setting spinners once */
                if (!spinnersInitialized) {
                    currencyCodes = CurrencyConverter.getAllCurrencyCodes(currencyList);

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            currencyCodes
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    fromCurrencySpinner.setAdapter(adapter);
                    toCurrencySpinner.setAdapter(adapter);

                    setDefaultSelections();
                    setupSpinnerListeners();
                    checkForPreSelection();

                    spinnersInitialized = true;
                }
            }
        });
    }

    private void setDefaultSelections() {
        int gbpPosition = currencyCodes.indexOf("GBP");
        if (gbpPosition >= 0) {
            fromCurrencySpinner.setSelection(gbpPosition);
        }

        int usdPosition = currencyCodes.indexOf("USD");
        if (usdPosition >= 0) {
            toCurrencySpinner.setSelection(usdPosition);
        } else if (currencyCodes.size() > 1) {
            toCurrencySpinner.setSelection(gbpPosition == 0 ? 1 : 0);
        }
    }

    private void setupSpinnerListeners() {
        fromCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateExchangeRate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        toCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateExchangeRate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupConvertButton() {
        convertButton.setOnClickListener(v -> performConversion());
    }

    private void setupSwapButton() {
        swapButton.setOnClickListener(v -> {
            int fromPosition = fromCurrencySpinner.getSelectedItemPosition();
            int toPosition = toCurrencySpinner.getSelectedItemPosition();

            fromCurrencySpinner.setSelection(toPosition);
            toCurrencySpinner.setSelection(fromPosition);

            String fromAmount = fromAmountEditText.getText().toString();
            String toAmount = toAmountEditText.getText().toString();

            if (!fromAmount.isEmpty() && !toAmount.isEmpty()) {
                fromAmountEditText.setText(toAmount);
                performConversion();
            }
        });
    }

    private void checkForPreSelection() {
        String preSelectedCode = viewModel.getPreSelectedCurrencyValue();

        if (preSelectedCode != null && currencyCodes != null) {
            /* Set FROM to GBP */
            int gbpPosition = currencyCodes.indexOf("GBP");
            if (gbpPosition >= 0) {
                fromCurrencySpinner.setSelection(gbpPosition);
            }

            /* Set TO to pre-selected currency */
            int toPosition = currencyCodes.indexOf(preSelectedCode);
            if (toPosition >= 0) {
                toCurrencySpinner.setSelection(toPosition);
            }

            /* Clear preselection */
            viewModel.clearPreSelection();

            updateExchangeRate();
        }
    }

    /**
     * Conversion
     */
    private void performConversion() {
        String fromCurrency = (String) fromCurrencySpinner.getSelectedItem();
        String toCurrency = (String) toCurrencySpinner.getSelectedItem();
        String amountStr = fromAmountEditText.getText().toString();

        if (amountStr.isEmpty()) {
            toAmountEditText.setText("");
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            ArrayList<CurrencyItem> currencyList = viewModel.getCurrencyListValue();

            if (amount < 0) {
                ErrorHandler.showConversionError(requireContext());
                return;
            }

            double result = CurrencyConverter.convert(
                    fromCurrency,
                    toCurrency,
                    amount,
                    currencyList
            );
            toAmountEditText.setText(String.format(Locale.US, "%.2f", result));

        } catch (NumberFormatException e) {
            ErrorHandler.showConversionError(requireContext());
            toAmountEditText.setText("");
        }
    }

    private void updateExchangeRate() {
        String fromCurrency = (String) fromCurrencySpinner.getSelectedItem();
        String toCurrency = (String) toCurrencySpinner.getSelectedItem();

        if (fromCurrency == null || toCurrency == null) {
            return;
        }

        ArrayList<CurrencyItem> currencyList = viewModel.getCurrencyListValue();
        double rate = CurrencyConverter.getExchangeRate(fromCurrency, toCurrency, currencyList);

        if (rate > 0) {
            exchangeRateTextView.setText(
                    String.format(Locale.US, "1 %s = %.4f %s", fromCurrency, rate, toCurrency)
            );
        } else {
            exchangeRateTextView.setText("");
        }
    }
}