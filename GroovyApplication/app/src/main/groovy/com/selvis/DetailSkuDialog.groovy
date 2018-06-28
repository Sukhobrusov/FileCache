package com.selvis

import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantReadWriteLock

public class DetailSkuDialog extends DialogFragment{


    def imageView
    def textView
    def bitmap
    def wareName

    @Override
    View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        def v = inflater.inflate(R.layout.sku_detail_fragment,container,false)
        imageView = v.findViewById(R.id.detail_image_view) as ImageView
        textView = v.findViewById(R.id.detail_text_view) as TextView

        imageView.imageBitmap = bitmap
        textView.text = wareName

        return v
    }
}