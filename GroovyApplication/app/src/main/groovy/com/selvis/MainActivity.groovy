package com.selvis

import android.os.Bundle
import android.os.PersistableBundle
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log

public class MainActivity extends SingleFragmentActivity{

    @Override
    protected Fragment createFragment() {
        Log.d("Selvis","createFragment")
        return new SkuSpecFragment()
    }

}
