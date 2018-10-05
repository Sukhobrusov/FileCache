package com.selvis

import android.os.Bundle
import android.os.PersistableBundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity

public abstract class SingleFragmentActivity extends AppCompatActivity{

    protected abstract Fragment createFragment()

    @LayoutRes
    protected Integer getLayoutRes(){
        return R.layout.activity_main
    }

    void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState)

        setContentView(R.layout.activity_main)
        def fm = getSupportFragmentManager()
        def fragment = fm.findFragmentById(R.id.main_frame)

        if (fragment == null){
            fragment = createFragment()
            fm.beginTransaction()
                    .add(R.id.main_frame, fragment)
                    .commit()
        }

        transaction.commit()

    }
}