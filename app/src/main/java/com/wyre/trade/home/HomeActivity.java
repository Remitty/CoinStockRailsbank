package com.wyre.trade.home;

import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.wyre.trade.coins.StakeAssetListActivity;
import com.wyre.trade.mtn.MTNActivity;
import com.wyre.trade.predict.PredictActivity;
import com.wyre.trade.profile.ProfileActivity;
import com.wyre.trade.stock.NewsListActivity;
import com.wyre.trade.token.TokenTradingActivity;
import com.wyre.trade.zabo.ZaboActivity;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wyre.trade.R;
import com.wyre.trade.SharedPrefs;
import com.wyre.trade.coins.CoinDepositHistoryActivity;
import com.wyre.trade.coins.CoinWithdrawActivity;
import com.wyre.trade.chat.SocialGroupActivity;
import com.wyre.trade.helper.SharedHelper;
import com.wyre.trade.main.ChangePasswordActivity;
import com.wyre.trade.main.MainActivity;
import com.wyre.trade.main.SupportActivity;
import com.wyre.trade.stock.stockorder.StockOrderHistoryActivity;

import java.util.ArrayList;
import java.util.List;

import me.riddhimanadib.library.BottomNavigationBar;
import me.riddhimanadib.library.NavigationPage;

public class HomeActivity extends AppCompatActivity implements BottomNavigationBar.BottomNavigationMenuClickListener {

    private SharedPrefs sharedPrefs;

    private List<NavigationPage> mNavigationPageList = new ArrayList<>();
    private BottomNavigationBar mBottomNav;
    DrawerLayout drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home) ;
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
//        toolbar.setTitle(getResources().getString(R.string.app_name));
//        toolbar.setLogo(getDrawable(R.drawable.pepe));
        setSupportActionBar(toolbar);
        sharedPrefs = new SharedPrefs(this);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                onNavigationItemsSelected(menuItem);
                return false;
            }
        });

        View header = navigationView.getHeaderView(0);
        if(header != null) {
            TextView userName = header.findViewById(R.id.user_name);
            userName.setText(SharedHelper.getKey(getBaseContext(), "fullName"));
            TextView userEmail = header.findViewById(R.id.user_email);
            userEmail.setText(SharedHelper.getKey(getBaseContext(), "email"));
            ImageView viewProfile = header.findViewById(R.id.view_profile);
            viewProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawer.closeDrawer(GravityCompat.START);
                    startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                }
            });
        }

        NavigationPage page1 = new NavigationPage("Pay", ContextCompat.getDrawable(this, R.drawable.ic_home), TransferCoinFragment.newInstance());
        NavigationPage page2 = new NavigationPage("Coins", ContextCompat.getDrawable(this, R.drawable.ic_coin), CoinsFragment.newInstance());
        NavigationPage page3 = new NavigationPage("Swap", ContextCompat.getDrawable(this, R.drawable.ic_swap), CoinSwapFragment.newInstance());
        NavigationPage page4 = new NavigationPage("Cash", ContextCompat.getDrawable(this, R.drawable.ic_cash), CashFragment.newInstance());
        NavigationPage page5 = new NavigationPage("Stocks", ContextCompat.getDrawable(this, R.drawable.ic_stock), InvestedStockFragment.newInstance());

        List<NavigationPage> navigationPages = new ArrayList<>();
        navigationPages.add(page1);
        navigationPages.add(page2);
        navigationPages.add(page3);
        navigationPages.add(page4);
        navigationPages.add(page5);

        Log.d("navigationpageitem", page1.toString());

        setupBottomBarHolderActivity(navigationPages);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            confirmLogout();

        }

        return super.onOptionsItemSelected(item);
    }

    private void confirmLogout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher_round)
                .setTitle(getString(R.string.app_name))
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        sharedPrefs.clearLogin();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    //
    public boolean onNavigationItemsSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        drawer.closeDrawer(GravityCompat.START);
        switch (id){

            case R.id.nav_stock_activity:
                startActivity(new Intent(getApplicationContext(), StockOrderHistoryActivity.class));
                break;
            case R.id.nav_coin_withdraw:
                startActivity(new Intent(getApplicationContext(), CoinWithdrawActivity.class));
                break;
            case R.id.nav_coin_trade:
                startActivity(new Intent(getApplicationContext(), ZaboActivity.class));
                break;
            case R.id.nav_stock_news:
                startActivity(new Intent(getApplicationContext(), NewsListActivity.class));
                break;
            case R.id.nav_coin_activity:
                startActivity(new Intent(getApplicationContext(), CoinDepositHistoryActivity.class));
                break;
            case R.id.nav_coin_stake:
                startActivity(new Intent(getApplicationContext(), StakeAssetListActivity.class));
                break;
            case R.id.nav_predict:
                startActivity(new Intent(getApplicationContext(), PredictActivity.class));
                break;
            case R.id.nav_mtn:
                startActivity(new Intent(getApplicationContext(), MTNActivity.class));
                break;
            case R.id.nav_exchange:
                startActivity(new Intent(getApplicationContext(), TokenTradingActivity.class));
                break;
            case R.id.nav_help:
                startActivity(new Intent(getApplicationContext(), SupportActivity.class));
                break;
            case R.id.nav_reset:
                startActivity(new Intent(getApplicationContext(), ChangePasswordActivity.class));
                break;
            case R.id.nav_logout:
                confirmLogout();
                break;
            case R.id.nav_group:
                startActivity(new Intent(getApplicationContext(), SocialGroupActivity.class));
                break;
            default:
                break;
        }


        return true;
    }

    @Override
    public void onClickedOnBottomNavigationMenu(int menuType) {
        Fragment fragment = null;
        switch (menuType) {
            case BottomNavigationBar.MENU_BAR_1:
                fragment = mNavigationPageList.get(0).getFragment();
                break;
            case BottomNavigationBar.MENU_BAR_2:
                fragment = mNavigationPageList.get(1).getFragment();
                break;
            case BottomNavigationBar.MENU_BAR_3:
                fragment = mNavigationPageList.get(2).getFragment();
                break;
            case BottomNavigationBar.MENU_BAR_4:
                fragment = mNavigationPageList.get(3).getFragment();
                break;
            case BottomNavigationBar.MENU_BAR_5:
                fragment = mNavigationPageList.get(4).getFragment();
                break;
        }

        // replacing fragment with the current one
        if (fragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(me.riddhimanadib.library.R.id.frameLayout, fragment);
            fragmentTransaction.commit();
        }

    }

    public void setupBottomBarHolderActivity(List<NavigationPage> pages) {

        // throw error if pages does not have 4 elements
//        if (pages.size() != 4) {
//            throw new RuntimeException("List of NavigationPage must contain 4 members.");
//        } else {
            mNavigationPageList = pages;
            mBottomNav = new BottomNavigationBar(this, pages, this);
            setupFragments();
//        }

    }

    private void setupFragments() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(me.riddhimanadib.library.R.id.frameLayout, mNavigationPageList.get(0).getFragment());
        fragmentTransaction.commit();
    }
}
