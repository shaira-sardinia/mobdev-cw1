package org.me.gcu.sardinia_shaira_s2264713.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.me.gcu.sardinia_shaira_s2264713.FragmentNavigationListener;
import org.me.gcu.sardinia_shaira_s2264713.R;
import org.me.gcu.sardinia_shaira_s2264713.data.CurrencyItem;
import org.me.gcu.sardinia_shaira_s2264713.utils.ErrorHandler;
import org.me.gcu.sardinia_shaira_s2264713.viewmodel.CurrencyViewModel;

import java.util.ArrayList;

public class SavedFragment extends Fragment
        implements CurrencyAdapterExpandable.OnCurrencyActionListener {

    private CurrencyViewModel viewModel;
    private FragmentNavigationListener navigationListener;

    /* Views */
    private ListView listView;
    private TextView emptyTextView;

    /* Data */
    private CurrencyAdapterExpandable adapter;
    private ArrayList<CurrencyItem> savedList = new ArrayList<>();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof FragmentNavigationListener) {
            navigationListener = (FragmentNavigationListener) context;
        } else {
            throw new RuntimeException(context + " must implement FragmentNavigationListener");
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
        View view = inflater.inflate(R.layout.fragment_saved, container, false);

        listView = view.findViewById(R.id.savedListView);
        emptyTextView = view.findViewById(R.id.emptyTextView);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupListView();
    }

    private void setupListView() {
        /* Observe saved currency codes */
        viewModel.getSavedCurrencyCodes().observe(getViewLifecycleOwner(), savedCodes -> {
            updateSavedList();
        });

        /* Observe main currency list (in case data refreshes) */
        viewModel.getCurrencyList().observe(getViewLifecycleOwner(), currencyList -> {
            updateSavedList();
        });
    }

    private void updateSavedList() {
        savedList.clear();
        savedList.addAll(viewModel.getSavedCurrencies());

        if (savedList.isEmpty()) {
            listView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);

            if (adapter == null) {
                adapter = new CurrencyAdapterExpandable(requireContext(), savedList, this);
                adapter.setIsSavedPage(true);
                listView.setAdapter(adapter);
            } else {
                adapter.updateList(savedList);
            }
        }
    }

    /* Adapter callbacks */
    @Override
    public void onSaveClicked(CurrencyItem item) {
        viewModel.removeSavedCurrency(item.getTargetCurrencyCode());
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