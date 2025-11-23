package org.me.gcu.sardinia_shaira_s2264713;

import org.me.gcu.sardinia_shaira_s2264713.ui.CurrencyAdapter;
import org.me.gcu.sardinia_shaira_s2264713.ui.CurrencyConversionFragment;
import org.me.gcu.sardinia_shaira_s2264713.ui.CurrencyListFragment;
import org.me.gcu.sardinia_shaira_s2264713.ui.SavedFragment;
import org.me.gcu.sardinia_shaira_s2264713.utils.ErrorHandler;
import org.me.gcu.sardinia_shaira_s2264713.viewmodel.CurrencyViewModel;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity
        implements FragmentNavigationListener, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private CurrencyViewModel viewModel;

    /* Drawer */
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;

    /* Views */
    private View homeLayout;
    private View fragmentContainer;
    private ListView previewListView;
    private Button viewAllButton;
    private Button viewSavedButton;
    private Button convertButton;

    private CurrencyAdapter previewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(CurrencyViewModel.class);

        initializeViews();
        setupToolbar();
        setupDrawer();
        setupButtons();
        observeData();
        observeErrors();
        setupBackHandler();

        viewModel.startInitialFetchAndAutoUpdate();
    }

    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        homeLayout = findViewById(R.id.homeLayout);
        fragmentContainer = findViewById(R.id.fragment_container);
        previewListView = findViewById(R.id.previewListView);
        viewAllButton = findViewById(R.id.viewAllButton);
        viewSavedButton = findViewById(R.id.viewSavedButton);
        convertButton = findViewById(R.id.convertButton);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupDrawer() {
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        );
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setupButtons() {
        viewAllButton.setOnClickListener(v -> navigateToFragment(new CurrencyListFragment()));
        viewSavedButton.setOnClickListener(v -> navigateToFragment(new SavedFragment()));
        convertButton.setOnClickListener(v -> onOpenConversionPage());
    }

    private void observeData() {
        viewModel.getCurrencyList().observe(this, currencyList -> {
            if (currencyList != null && !currencyList.isEmpty()) {
                updatePreviewList();
            }
        });
    }

    private void observeErrors() {
        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                switch (error) {
                    case "NETWORK_ERROR":
                        ErrorHandler.showNetworkError(this);
                        break;
                    case "DATA_ERROR":
                        ErrorHandler.showDataError(this);
                        break;
                    default:
                        ErrorHandler.showGenericError(this);
                        break;
                }
                viewModel.clearError();
            }
        });
    }

    private void updatePreviewList() {
        var previewCurrencies = viewModel.getPreviewCurrencies();

        if (previewAdapter == null) {
            previewAdapter = new CurrencyAdapter(this, previewCurrencies);
            previewListView.setAdapter(previewAdapter);
        } else {
            previewAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Drawer Navigation
     */

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            showHome();
        } else if (id == R.id.nav_view_all) {
            navigateToFragment(new CurrencyListFragment());
        } else if (id == R.id.nav_convert) {
            onOpenConversionPage();
        } else if (id == R.id.nav_saved) {
            navigateToFragment(new SavedFragment());
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Fragment Navigation
     */
    private void navigateToFragment(androidx.fragment.app.Fragment fragment) {
        homeLayout.setVisibility(View.GONE);
        fragmentContainer.setVisibility(View.VISIBLE);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();

        updateToolbarForFragment();
    }

    private void showHome() {
        getSupportFragmentManager().popBackStack(null,
                androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);

        homeLayout.setVisibility(View.VISIBLE);
        fragmentContainer.setVisibility(View.GONE);

        updateToolbarForHome();
    }

    private void updateToolbarForFragment() {
        drawerToggle.setDrawerIndicatorEnabled(false);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> showHome());
    }

    private void updateToolbarForHome() {
        drawerToggle.setDrawerIndicatorEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        drawerToggle.syncState();
    }

    /**
     * Interface implementation
     */

    @Override
    public void onOpenConversionPage(String preSelectedCurrency) {
        if (preSelectedCurrency != null) {
            viewModel.setPreSelectedCurrency(preSelectedCurrency);
        }
        navigateToFragment(new CurrencyConversionFragment());
    }

    @Override
    public void onOpenConversionPage() {
        onOpenConversionPage(null);
    }

    /**
     * Back button
     */
    private void setupBackHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else if (fragmentContainer.getVisibility() == View.VISIBLE) {
                    showHome();
                } else {
                    finish();
                }
            }
        });
    }

    /**
     * Life Cycle
     */
    @Override
    protected void onPause() {
        super.onPause();
        viewModel.stopAutoUpdate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.resumeAutoUpdate();
    }
}