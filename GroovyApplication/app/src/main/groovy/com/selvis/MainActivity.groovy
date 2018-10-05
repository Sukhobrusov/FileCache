package com.selvis

import android.os.PersistableBundle
import android.os.StrictMode
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.FrameLayout


public class MainActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        Log.d("Selvis","createFragment")
        return new SkuSpecFragment()
    }

}
