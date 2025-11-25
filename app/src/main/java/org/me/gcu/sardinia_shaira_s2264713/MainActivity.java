package org.me.gcu.sardinia_shaira_s2264713;

import org.me.gcu.sardinia_shaira_s2264713.ui.CurrencyAdapter;
import org.me.gcu.sardinia_shaira_s2264713.ui.CurrencyConversionFragment;
import org.me.gcu.sardinia_shaira_s2264713.ui.CurrencyListFragment;
import org.me.gcu.sardinia_shaira_s2264713.ui.SavedFragment;
import org.me.gcu.sardinia_shaira_s2264713.utils.ErrorHandler;
import org.me.gcu.sardinia_shaira_s2264713.viewmodel.CurrencyViewModel;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
    private ImageView viewAllButton;
    private ImageView viewSavedButton;
    private ImageView convertButton;
    private ImageView navIcon;

    private CurrencyAdapter previewAdapter;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isFragmentVisible", fragmentContainer.getVisibility() == View.VISIBLE);
    }

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
        updateToolbarForHome();
        viewModel.startInitialFetchAndAutoUpdate();

        if (savedInstanceState != null) {
            boolean wasFragmentVisible = savedInstanceState.getBoolean("isFragmentVisible", false);
            if (wasFragmentVisible) {
                homeLayout.setVisibility(View.GONE);
                fragmentContainer.setVisibility(View.VISIBLE);
                updateToolbarForFragment();
            }
        }
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
        navIcon = findViewById(R.id.nav_icon);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
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
        drawerToggle.setDrawerIndicatorEnabled(false);
        drawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setupButtons() {
        viewAllButton.setOnClickListener(v -> {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                navigateToFragment(new CurrencyListFragment());
            }, 200);
        });

        viewSavedButton.setOnClickListener(v -> {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                navigateToFragment(new SavedFragment());
            }, 200);
        });

        convertButton.setOnClickListener(v -> {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                onOpenConversionPage();
            }, 200);
        });
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
                .setCustomAnimations(
                        android.R.anim.fade_in,
                        android.R.anim.fade_out,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                )
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
        navIcon.setImageResource(R.drawable.btn_back);
        navIcon.setOnClickListener(v -> showHome());
    }

    private void updateToolbarForHome() {
        navIcon.setImageResource(R.drawable.btn_hamburger);
        navIcon.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
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
        viewModel.setSelectedFromCurrency("GBP");
        viewModel.setSelectedToCurrency("USD");
        viewModel.setEnteredAmount("");
        viewModel.clearPreSelection();

        navigateToFragment(new CurrencyConversionFragment());
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