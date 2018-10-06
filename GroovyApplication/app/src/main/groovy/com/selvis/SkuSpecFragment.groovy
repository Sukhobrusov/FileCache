package com.selvis

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.AsyncTask
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.selvis.entity.OrderEditorLine
import com.selvis.entity.OrderSearchResult
import com.selvis.entity.SkuImage
import com.selvis.util.CacheHelper


class SkuSpecFragment extends DefaultFragment {

    private RecyclerView mRecyclerView
    private CacheHelper cacheHelper
    private DetailSkuDialog detailSkuDialog
    private List<SkuSpecification> skuSpecs = new ArrayList<>()
    private static final def TAG = "Selvis"

    /**
     * Скачиваем данные из интернета и задаем для них adapter
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(TAG, "Enter to SkuSpecFragment.onCreateView")

        cacheHelper = CacheHelper.newInstance(getActivity())
        detailSkuDialog = new DetailSkuDialog()

        View v = inflater.inflate(R.layout.sku_list_fragment, container, false)
        mRecyclerView = v.findViewById(R.id.sku_recycler_view)
        mRecyclerView.layoutManager = new GridLayoutManager(activity, 3)
        setupAdapter()
        new FetchSku().execute(new Object())

        return v
    }


    private def setupAdapter() {
        mRecyclerView.setAdapter(new SkuAdapter(skuSpecs))
    }


    private class FetchSku extends AsyncTask<Object, Void, List<SkuSpecification>> {

        @Override
        protected List<SkuSpecification> doInBackground(Object... voids) {
            def mLines = OrderEditorAction.fillSkuSpecs()
            fillSkuPictures(mLines)
        }

        @Override
        protected void onPostExecute(List<SkuSpecification> skuSpecifications) {
            if (skuSpecifications != null)
                skuSpecs = skuSpecifications
            setupAdapter()
        }

        private List<SkuSpecification> fillSkuPictures(OrderSearchResult<OrderEditorLine> lines) {

            def mSkuSpecs = new ArrayList<SkuSpecification>()
            SkuSpecification skuPicture
            def iterator = lines.list.iterator()

            while (iterator.hasNext()) {
                def i = iterator.next()

                skuPicture = new SkuSpecification()

                skuPicture.line = i
                skuPicture.guid = i.skuId
                skuPicture.image = new SkuImage()
                skuPicture.image.productId = i.productId

                mSkuSpecs.add(skuPicture)
            }
            mSkuSpecs
        }

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
                Log.e(TAG, e.toString(), e)
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

        private List<SkuSpecification> mSpecList = new ArrayList()

        public SkuAdapter(List<SkuSpecification> list) {
            mSpecList = list
        }

        @Override
        SkuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            def v = LayoutInflater.from(activity).inflate(R.layout.sku_detail_fragment, parent, false)
            return new SkuViewHolder(v)
        }

        @Override
        void onBindViewHolder(@NonNull SkuViewHolder holder, int position) {
            def spec = mSpecList[position]
            holder.setContentForViews(spec)
        }

        @Override
        int getItemCount() {
            mSpecList.size()
        }
    }

}
