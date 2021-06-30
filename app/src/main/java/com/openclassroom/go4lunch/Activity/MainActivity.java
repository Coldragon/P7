package com.openclassroom.go4lunch.Activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;
import com.openclassroom.go4lunch.Activity.Abstract.AuthBaseActivity;
import com.openclassroom.go4lunch.R;
import com.openclassroom.go4lunch.databinding.ActivityMainBinding;
import com.openclassroom.go4lunch.databinding.HeaderNavViewBinding;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AuthBaseActivity implements GoogleMap.OnMapLoadedCallback, NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding mBinding;
    private HeaderNavViewBinding mHeaderNavViewBinding;
    private ActivityResultLauncher<Intent> mSignInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        configureToolBar();
        configureBottomNavBar();

        configureDrawerLayout();
        configureNavigationView();

        configureAuth();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        CheckIfSignIn(); // To make sure we are still connected when going back to the main Activity
    }

    // --------------------
    // Auth
    // --------------------
    private void configureAuth() {

        mSignInLauncher = registerForActivityResult(
                new FirebaseAuthUIActivityResultContract(),
                this::onSignInResult

        );

        CheckIfSignIn();
    }

    private void CheckIfSignIn() {
        if (isCurrentUserLogged()) {
            updateProfile();
        } else {
            SignIn();
        }
    }

    private void updateProfile() {
        FirebaseUser user = getCurrentUser();
        if (user != null) {
            mHeaderNavViewBinding.headerUserName.setText(user.getDisplayName());
            mHeaderNavViewBinding.headerUserMail.setText(user.getEmail());

            if (user.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(user.getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(mHeaderNavViewBinding.headerUserAvatar);
            }
        }
    }

    private void SignIn() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build());

        // Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false, true)
                .setLogo(R.drawable.go4lunch)
                .setTheme(R.style.AuthTheme)
                .build();

        mSignInLauncher.launch(signInIntent);
    }

    private void SignOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(task -> {
                    Snackbar.make(mBinding.getRoot(), R.string.signed_out, Snackbar.LENGTH_SHORT).show();
                    updateProfile();
                });
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();

        if (response == null) {
            // Canceled Signed In
            SignIn();
            Snackbar.make(mBinding.getRoot(), R.string.canceled_sign_in, Snackbar.LENGTH_SHORT).show();
        } else if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            Snackbar.make(mBinding.getRoot(), getString(R.string.signed_in_as_user, Objects.requireNonNull(getCurrentUser()).getDisplayName()), Snackbar.LENGTH_SHORT).show();
        } else {
            // Sign in failed
            Snackbar.make(mBinding.getRoot(), R.string.sign_in_failed, Snackbar.LENGTH_SHORT).show();
        }

        updateProfile();
    }

    // --------------------
    // Google Map
    // --------------------
    @Override
    public void onMapLoaded() {

    }

    // --------------------
    // Bottom Nav Bar
    // --------------------
    private void configureBottomNavBar() {

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_map_view, R.id.nav_list_view, R.id.nav_workmates)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(mBinding.bottomNavigation, navController);
    }

    // ---------------------
    // ToolBar
    // ---------------------
    private void configureToolBar() {
        setSupportActionBar(mBinding.toolbar);
    }

    // --------------------
    // Navigation View
    // --------------------
    private void configureDrawerLayout() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mBinding.getRoot(), mBinding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mBinding.getRoot().addDrawerListener(toggle);
        toggle.syncState();
    }

    private void configureNavigationView() {
        mHeaderNavViewBinding = HeaderNavViewBinding.bind(mBinding.leftNavView.getHeaderView(0));
        //HeaderNavViewBinding.inflate(getLayoutInflater(), );
        //mBinding.activityMainNavView.addHeaderView(mHeaderNavViewBinding.getRoot());
        mBinding.leftNavView.setNavigationItemSelectedListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.menu_nav_your_lunch:
                Intent restaurantIntent = new Intent(getApplicationContext(), RestaurantDetailActivity.class);
                startActivity(restaurantIntent);
                break;
            case R.id.menu_nav_settings:
                Intent settingIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settingIntent);
                break;
            case R.id.menu_nav_sign_out:
                SignOut();
                SignIn();
                break;
            default:
                break;
        }

        mBinding.getRoot().closeDrawer(GravityCompat.START);

        return true;
    }
}
