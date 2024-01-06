package lk.xrontech.watchparadise.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import lk.xrontech.watchparadise.R;
import lk.xrontech.watchparadise.fragment.HomeFragment;
import lk.xrontech.watchparadise.fragment.MyAccountFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, NavigationBarView.OnItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar materialToolbar;
    private BottomNavigationView bottomNavigationView;
    private SharedPreferences userDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userDetails = getSharedPreferences("user_details", MODE_PRIVATE);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        materialToolbar = findViewById(R.id.materialToolbar);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        setSupportActionBar(materialToolbar);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();

        materialToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.open();
            }
        });

        navigationView.setNavigationItemSelectedListener(this);
        bottomNavigationView.setOnItemSelectedListener(this);

        loadFragment(new HomeFragment());
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        String email = userDetails.getString("email", null);
        if ((item.getItemId() == R.id.home) || (item.getItemId() == R.id.sideNavHome)) {
            loadFragment(new HomeFragment());
        } else if ((item.getItemId() == R.id.myAccount) || (item.getItemId() == R.id.sideNavMyAccount)) {
            if (email == null) {
                loadActivity(AuthActivity.class);
            } else {
                loadFragment(new MyAccountFragment());
            }
        } else if ((item.getItemId() == R.id.cart) || (item.getItemId() == R.id.sideNavCart)) {
            if (email == null) {
                loadActivity(AuthActivity.class);
            } else {
                loadActivity(CartActivity.class);
            }
        } else if ((item.getItemId() == R.id.sideNavWishlist) || (item.getItemId() == R.id.wishlist)) {
            if (email == null) {
                loadActivity(AuthActivity.class);
            } else {
                loadActivity(WishlistActivity.class);
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    void loadFragment(Fragment fragment) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
    }

    void loadActivity(Class activity) {
        Intent intent = new Intent(MainActivity.this, activity);
        startActivity(intent);
    }
}