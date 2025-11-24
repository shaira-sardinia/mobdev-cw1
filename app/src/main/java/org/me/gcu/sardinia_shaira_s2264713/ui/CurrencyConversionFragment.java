package org.me.gcu.sardinia_shaira_s2264713.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.me.gcu.sardinia_shaira_s2264713.R;
import org.me.gcu.sardinia_shaira_s2264713.utils.CurrencyConverter;
import org.me.gcu.sardinia_shaira_s2264713.data.CurrencyItem;
import org.me.gcu.sardinia_shaira_s2264713.viewmodel.CurrencyViewModel;

import java.util.ArrayList;
import java.util.Locale;

public class CurrencyConversionFragment extends Fragment {

    private CurrencyViewModel viewModel;

    private TextView fromCurrencyCode;
    private TextView fromCurrencyName;
    private EditText fromAmountEditText;
    private ImageView convertButton;
    private TextView toAmountTextView;
    private TextView toCurrencyCode;
    private TextView toCurrencyName;

    private String selectedFromCurrency = "GBP";
    private String selectedToCurrency = "USD";
    private ArrayList<String> currencyCodes;

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

        fromCurrencyCode = view.findViewById(R.id.fromCurrencyCode);
        fromCurrencyName = view.findViewById(R.id.fromCurrencyName);
        fromAmountEditText = view.findViewById(R.id.fromAmountEditText);
        convertButton = view.findViewById(R.id.convertButton);
        toAmountTextView = view.findViewById(R.id.toAmountTextView);
        toCurrencyCode = view.findViewById(R.id.toCurrencyCode);
        toCurrencyName = view.findViewById(R.id.toCurrencyName);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupCurrencyData();
        setupConvertButton();
        setupCurrencySelectors();
        setupAmountInput();
        checkForPreSelection();
    }

    private void setupCurrencyData() {
        viewModel.getCurrencyList().observe(getViewLifecycleOwner(), currencyList -> {
            if (currencyList != null && !currencyList.isEmpty()) {
                currencyCodes = CurrencyConverter.getAllCurrencyCodes(currencyList);
                updateCurrencyDisplay();
            }
        });
    }

    private void setupConvertButton() {
        convertButton.setOnClickListener(v -> {

            String tempCode = selectedFromCurrency;
            selectedFromCurrency = selectedToCurrency;
            selectedToCurrency = tempCode;

            updateCurrencyDisplay();
            performConversion();
        });
    }

    private void setupCurrencySelectors() {
        fromCurrencyCode.setOnClickListener(v -> showCurrencyPicker(true));
        fromCurrencyName.setOnClickListener(v -> showCurrencyPicker(true));

        toCurrencyCode.setOnClickListener(v -> showCurrencyPicker(false));
        toCurrencyName.setOnClickListener(v -> showCurrencyPicker(false));
    }

    private void setupAmountInput() {
        fromAmountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                performConversion();
            }
        });
    }

    private void showCurrencyPicker(boolean isFromCurrency) {
        if (currencyCodes == null || currencyCodes.isEmpty()) return;

        String[] codesArray = currencyCodes.toArray(new String[0]);

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle(isFromCurrency ? "Select From Currency" : "Select To Currency")
                .setItems(codesArray, (dialog, which) -> {
                    String selectedCode = codesArray[which];
                    if (isFromCurrency) {
                        selectedFromCurrency = selectedCode;
                    } else {
                        selectedToCurrency = selectedCode;
                    }
                    updateCurrencyDisplay();
                    performConversion();
                })
                .show();
    }

    private void updateCurrencyDisplay() {
        /* Update FROM display */
        fromCurrencyCode.setText(selectedFromCurrency);
        fromCurrencyName.setText(getCurrencyFullName(selectedFromCurrency));

        /* Update TO display */
        toCurrencyCode.setText(selectedToCurrency);
        toCurrencyName.setText(getCurrencyFullName(selectedToCurrency));
    }

    private String getCurrencyFullName(String code) {
        ArrayList<CurrencyItem> currencyList = viewModel.getCurrencyListValue();

        if (currencyList != null && !currencyList.isEmpty()) {

            CurrencyItem firstItem = currencyList.get(0);
            if (firstItem.getSourceCurrencyCode() != null &&
                    firstItem.getSourceCurrencyCode().equals(code)) {
                return firstItem.getSourceCurrencyName();
            }

            for (CurrencyItem item : currencyList) {
                if (item.getTargetCurrencyCode() != null &&
                        item.getTargetCurrencyCode().equals(code)) {
                    return item.getTargetCurrencyName();
                }
            }
        }

        return code;
    }

    private void checkForPreSelection() {
        String preSelectedCode = viewModel.getPreSelectedCurrencyValue();

        if (preSelectedCode != null) {
            selectedFromCurrency = "GBP";
            selectedToCurrency = preSelectedCode;

            updateCurrencyDisplay();
            viewModel.clearPreSelection();
        }
    }

    private void performConversion() {
        String amountStr = fromAmountEditText.getText().toString()
                .replace(",", "");

        if (amountStr.isEmpty()) {
            toAmountTextView.setText("0");
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            ArrayList<CurrencyItem> currencyList = viewModel.getCurrencyListValue();

            if (amount < 0) {
                toAmountTextView.setText("0");
                return;
            }

            double result = CurrencyConverter.convert(
                    selectedFromCurrency,
                    selectedToCurrency,
                    amount,
                    currencyList
            );

            toAmountTextView.setText(String.format(Locale.US, "%,.0f", result));

        } catch (NumberFormatException e) {
            toAmountTextView.setText("0");
        }
    }
}