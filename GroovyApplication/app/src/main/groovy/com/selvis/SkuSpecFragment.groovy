package com.selvis

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import com.selvis.entity.OrderEditorLine
import com.selvis.entity.OrderSearchResult
import com.selvis.util.CacheHelper

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantReadWriteLock


class SkuSpecFragment extends DefaultFragment {


    private RecyclerView mRecyclerView
    private CacheHelper cacheHelper
    private DetailSkuDialog detailSkuDialog
    private OrderSearchResult<OrderEditorLine> lines
    private List<SkuSpecification> skuSpecs = new ArrayList<>()

    /**
     * Скачиваем данные из интернета и задаем для них adapter
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d("SELVIS", "Enter to SkuSpecFragment.groovy.onCreateView")

        cacheHelper = CacheHelper.newInstance(getActivity())
        detailSkuDialog = new DetailSkuDialog()

        View v = inflater.inflate(R.layout.sku_list_fragment, container, false)

        lines = OrderEditorAction.fillSkuSpecs()
        mRecyclerView = v.findViewById(R.id.sku_recycler_view)

        if (lines != null) {
            fillSkuPictures()
        }

        if (skuSpecs != null && !skuSpecs.isEmpty()) {
            mRecyclerView.layoutManager = new GridLayoutManager(activity, 3)
            setupAdapter()
        }

        return v
    }

    /**
     * Метод по заполнению массива skuSpecs
     */
    private void fillSkuPictures() {
        skuSpecs.clear()
        int count = 0
        SkuSpecification skuPicture
        def iterator = lines.list.iterator()

        while (iterator.hasNext()) {
            def i = iterator.next()

            if (count == 0)
                skuPicture = new SkuSpecification()

            skuPicture.line = i
            skuPicture.guid = i.skuId
            //skuPicture.image[count] = null

            skuSpecs.add(skuPicture)

        }
    }

    private def setupAdapter(){
        mRecyclerView.adapter = SkuAdapter(skuSpecs)
    }

    /**
     * ViewHolder для RecyclerView
     */

    private class SkuViewHolder extends RecyclerView.ViewHolder {

        private TextView mDetailTextView
        private ImageView mDetailImageView
        private LinearLayout mDetailLayout

        SkuViewHolder(View itemView) {
            super(itemView)
            this.mDetailImageView = itemView.findViewById(R.id.detail_image_view)
            this.mDetailTextView = itemView.findViewById(R.id.detail_text_view)
            this.mDetailLayout = itemView.findViewById(R.id.detail_layout)
        }

        /**
         * Задает контент для переданных в него textView, imageView, layout из skuSpecification под номером index
         *
         * @param mDetailTextView
         * @param mDetailImageView
         * @param mDetailLayout
         * @param index
         * @param skuSpecification
         * @return
         */
        public def setContentForViews(SkuSpecification skuSpecification) {

            def line = skuSpecification.line
            def image = skuSpecification.image

            //В случае успешного выполнения
            def onSuccess = { Bitmap bm ->
                def bitmap = null
                BitmapDrawable drawable = (BitmapDrawable) mDetailImageView.getDrawable()
                if (drawable != null)
                    bitmap = drawable.getBitmap()

                image.alive = true
                image.lastUpdated = new Date()

                if (bitmap == null || !bitmap.sameAs(bm)) {
                    getActivity().runOnUiThread({
                        mDetailImageView.setImageBitmap(bm)
                    })
                }
            }

            //В случае ошибки
            def onFailure = { Throwable e ->
                getActivity().runOnUiThread({
                    mDetailImageView.setImageResource(R.drawable.default_image)
                })
                image.alive = false
                image.lastUpdated = new Date()
                Log.e("Error", e.toString())
            }

            def wareName = line.wareName
            if (skuSpecification.guid.trim() != "") {
                mDetailTextView.setText(wareName)
                image.productId = line.productId
                cacheHelper.getImage(image, onSuccess, onFailure)
            }
        }
    }

    private class SkuAdapter extends RecyclerView.Adapter<SkuViewHolder> {

        private List<SkuSpecification> mSpecList

        public SkuAdapter(List<SkuSpecification> list){
            mSpecList = list
        }

        @Override
        SkuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            def v = LayoutInflater.from(activity).inflate(R.layout.sku_detail_fragment, parent, false)
            return SkuViewHolder(v)
        }

        @Override
        void onBindViewHolder(@NonNull SkuViewHolder holder, int position) {
            def spec = mSpecList[position]
            holder.setContentForViews(spec)
        }

        @Override
        int getItemCount() {
            return mSpecList.size()
        }
    }

}
