package com.example.manajemenruang;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = getSharedPreferences("SETTINGS", MODE_PRIVATE);
        boolean useDarkMode = preferences.getBoolean("DARK_MODE", false);

        if (useDarkMode) {
            setTheme(R.style.ActivityThemeDark);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadFragment(new RuangFragment());
        BottomNavigationView bottomNavigationView=findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.action_ruang);
    }

    private boolean loadFragment(Fragment fragment){
        if (fragment!=null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment=null;
        switch (menuItem.getItemId()){
            case R.id.action_peminjam:
                fragment=new PeminjamFragment();
                break;
            case R.id.action_ruang:
                fragment=new RuangFragment();
                break;
            case R.id.action_setting:
                fragment=new SettingFragment();
                break;
        }
        return loadFragment(fragment);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        SharedPreferences preferences = getSharedPreferences("SETTINGS", MODE_PRIVATE);
        boolean useDarkMode = preferences.getBoolean("DARK_MODE", false);

        if (useDarkMode) {
            setTheme(R.style.ActivityThemeDark);
        }
        recreate();
    }
}
