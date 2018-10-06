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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)

        setContentView(getLayoutRes())
        def fm = getSupportFragmentManager()
        def fragment = fm.findFragmentById(R.id.main_frame)

        if (fragment == null){
            fragment = createFragment()
            fm.beginTransaction()
                    .add(R.id.main_frame, fragment)
                    .commit()
        }
    }
}