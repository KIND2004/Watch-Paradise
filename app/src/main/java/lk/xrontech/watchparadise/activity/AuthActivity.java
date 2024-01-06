package lk.xrontech.watchparadise.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import lk.xrontech.watchparadise.R;
import lk.xrontech.watchparadise.fragment.LogInFragment;
import lk.xrontech.watchparadise.fragment.SignUpFragment;

public class AuthActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, NavigationBarView.OnItemSelectedListener {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        bottomNavigationView = findViewById(R.id.bottomNavigationAuthView);

        bottomNavigationView.setOnItemSelectedListener(this);

        loadFragment(new LogInFragment());

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.signup) {
            loadFragment(new SignUpFragment());
        } else if (item.getItemId() == R.id.login) {
            loadFragment(new LogInFragment());
        }
        return true;
    }

    void loadFragment(Fragment fragment) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.authFragment, fragment);
        fragmentTransaction.commit();
    }
}