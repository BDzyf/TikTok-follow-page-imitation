package com.demo.follow;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import com.demo.follow.ui.RelationshipsFragment;

/**
 * 应用主 Activity
 * 作为应用的入口点，负责加载主界面 Fragment
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, new RelationshipsFragment())
                .commit();
    }
}