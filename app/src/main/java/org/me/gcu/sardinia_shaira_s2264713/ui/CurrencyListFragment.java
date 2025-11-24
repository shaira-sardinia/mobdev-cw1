package org.me.gcu.sardinia_shaira_s2264713.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.me.gcu.sardinia_shaira_s2264713.FragmentNavigationListener;
import org.me.gcu.sardinia_shaira_s2264713.R;
import org.me.gcu.sardinia_shaira_s2264713.data.CurrencyItem;
import org.me.gcu.sardinia_shaira_s2264713.viewmodel.CurrencyViewModel;
import org.me.gcu.sardinia_shaira_s2264713.utils.ErrorHandler;

import java.util.ArrayList;

public class CurrencyListFragment extends Fragment
        implements CurrencyAdapterExpandable.OnCurrencyActionListener {

    private CurrencyViewModel viewModel;
    private FragmentNavigationListener navigationListener;

    /* Views */
    private ListView listView;
    private EditText searchEditText;

    /* Data */
    private CurrencyAdapterExpandable adapter;
    private ArrayList<CurrencyItem> fullList = new ArrayList<>();
    private ArrayList<CurrencyItem> filteredList = new ArrayList<>();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof FragmentNavigationListener) {
            navigationListener = (FragmentNavigationListener) context;
        } else {
            throw new RuntimeException(String.valueOf(context));
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(CurrencyViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_currency_list, container, false);

        listView = view.findViewById(R.id.currencyListView);
        searchEditText = view.findViewById(R.id.searchEditText);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupListView();
        setupSearch();
//        observeLoadingState();
    }

    /**
     * Set UI
     */
    private void setupListView() {
        viewModel.getCurrencyList().observe(getViewLifecycleOwner(), currencyList -> {
            if (currencyList != null && !currencyList.isEmpty()) {
                listView.setVisibility(View.VISIBLE);

                fullList.clear();
                fullList.addAll(currencyList);

                filteredList.clear();
                filteredList.addAll(currencyList);

                if (adapter == null) {
                    adapter = new CurrencyAdapterExpandable(requireContext(), filteredList, this);
                    listView.setAdapter(adapter);
                } else {
                    adapter.updateList(filteredList);
                }
            }
        });
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCurrencies(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterCurrencies(String query) {
        filteredList.clear();

        if (query.isEmpty()) {
            filteredList.addAll(fullList);
        } else {
            for (CurrencyItem item : fullList) {
                if (item.matchesSearch(query)) {
                    filteredList.add(item);
                }
            }
        }

        if (adapter != null) {
            adapter.updateList(filteredList);
        }
    }

    /* Adapter callbacks */
    @Override
    public void onSaveClicked(CurrencyItem item) {
        viewModel.saveCurrency(item.getTargetCurrencyCode());
        ErrorHandler.showSaveSuccess(requireContext(), item.getTargetCurrencyCode());
    }

    @Override
    public void onConvertClicked(CurrencyItem item) {
        navigationListener.onOpenConversionPage(item.getTargetCurrencyCode());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        navigationListener = null;
    }
}