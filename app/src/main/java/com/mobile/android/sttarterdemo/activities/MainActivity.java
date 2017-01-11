package com.mobile.android.sttarterdemo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.mobile.android.sttarterdemo.R;
import com.mobile.android.sttarterdemo.fragments.coupons.CouponsFragment;
import com.mobile.android.sttarterdemo.fragments.communicator.MessagesFragment;
import com.mobile.android.sttarterdemo.fragments.referral.ReferralFragment;
import com.mobile.android.sttarterdemo.fragments.wallet.WalletFragment;
import com.sttarter.init.STTarterManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Fragment fragment = null;
    Fragment currentFragment = null;
    android.support.v4.app.FragmentTransaction fragmentTransaction = null;
    Toolbar toolbar;
    TextView textViewUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        fragment = new MessagesFragment();
        fragment.setArguments(getIntent().getExtras());
        ReplaceFragment(fragment);
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        textViewUsername = (TextView) navigationView.getHeaderView(0).findViewById(R.id.textViewUsername);
        textViewUsername.setText(STTarterManager.getInstance().getUsername());

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
            if (backStackCount > 1)
                getSupportFragmentManager().popBackStack();
            else
                finish();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.navMessages) {
            fragment = new MessagesFragment();
            ReplaceFragment(fragment);
        } else if (id == R.id.navWallet) {
            fragment = new WalletFragment();
            ReplaceFragment(fragment);
        } else if (id == R.id.navCoupons) {
            fragment = new CouponsFragment();
            ReplaceFragment(fragment);
        } else if (id == R.id.navReferral) {
            fragment = new ReferralFragment();
            ReplaceFragment(fragment);
        } else if (id == R.id.navLogout) {
            STTarterManager.getInstance().logout(MainActivity.this);
            startActivity(new Intent(MainActivity.this,SplashActivity.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void ReplaceFragment(Fragment fragment) {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction
                .addToBackStack(getClass().getName())
                .commitAllowingStateLoss() ;
        currentFragment = fragment;
    }
}
