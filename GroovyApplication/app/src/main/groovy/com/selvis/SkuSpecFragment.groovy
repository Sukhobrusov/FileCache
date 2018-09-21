package com.selvis

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
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
import com.selvis.entity.SkuImage
import com.selvis.util.CacheHelper

import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantReadWriteLock


class SkuSpecFragment extends DefaultFragment {


    private ListView listView
    private CustomAdapter adapter
    private CacheHelper cacheHelper
    private DetailSkuDialog detailSkuDialog
    private OrderSearchResult<OrderEditorLine> lines
    private List<SkuSpecification> skuSpecs = new ArrayList<>()
    private static ExecutorService execServ = Executors.newCachedThreadPool()

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
        listView = v.findViewById(R.id.sku_list_view)

        if (lines != null) {
            fillSkuPictures()
        }

        if (skuSpecs != null && !skuSpecs.isEmpty()) {
            adapter = new CustomAdapter(this.getActivity(), skuSpecs)
            listView.setAdapter(adapter)
        }

        return v
    }


    /**
     * Метод по заполнению массива skuSpecs
     */
    void fillSkuPictures() {
        skuSpecs.clear()
        int count = 0
        SkuSpecification skuPicture
        def iterator = lines.list.iterator()

        while (iterator.hasNext()) {
            def i = iterator.next()

            if (count == 0)
                skuPicture = new SkuSpecification()

            skuPicture.line[count] = i
            skuPicture.guid[count] = i.skuId
            //skuPicture.image[count] = null

            if (count == 1 || !iterator.hasNext()) {
                count = 0
                skuSpecs.add(skuPicture)
            } else
                count++

        }
    }


    /**
     * Адаптер для ListView
     */
    private class CustomAdapter extends ArrayAdapter<SkuSpecification> {

        Context context
        def currentPosition
        ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock()
        Lock rLock = rwLock.readLock()
        Lock wLock = rwLock.writeLock()
        ArrayList<SkuSpecification> arrayList = new ArrayList<>()


        public CustomAdapter(Context context, ArrayList<SkuSpecification> list) {
            super(context, R.layout.sku_picture, list)
            arrayList = list
            this.context = context
        }


        public void update(ArrayList<SkuSpecification> list) {
            arrayList.addAll(list)
            this.notifyDataSetChanged()
        }

        @Override
        public View getView(int position,
                            View convertView,
                            ViewGroup parent) {
            try {
                rLock.lock()
                Log.d("POSITION", "$position")
                currentPosition = position
                if (convertView == null) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.sku_picture, parent, false)
                }
                convertView = bindSkuPicture(convertView)
                return convertView
            } finally {
                rLock.unlock()
            }
        }

        /**
         * Биндит skuPicture в ListView
         *
         * @param convertView
         * @return
         */
        public def bindSkuPicture(View convertView) {

            SkuSpecification skuSpecification = arrayList.get(currentPosition)

            def onSuccess = { Bitmap bm ->
                detailSkuDialog.bitmap = bm
            }

            def onFailure = { Throwable e ->
                Log.e("Error", e.toString())
            }

            TextView textViewName1 = convertView.findViewById(R.id.text_view_for_adapter1);
            ImageView imageView1 = convertView.findViewById(R.id.image_view_for_adapter1);
            LinearLayout layout1 = convertView.findViewById(R.id.linear_layout1)

            layout1.setOnClickListener({
                cacheHelper.getImage(skuSpecification.image[0], onSuccess, onFailure)
                detailSkuDialog.wareName = skuSpecification.line[0].wareName
                detailSkuDialog.show(fragmentManager, "Details")
            })

            // The same thing for 2nd view
            TextView textViewName2 = convertView.findViewById(R.id.text_view_for_adapter2);
            ImageView imageView2 = convertView.findViewById(R.id.image_view_for_adapter2);
            LinearLayout layout2 = convertView.findViewById(R.id.linear_layout2)

            layout2.setOnClickListener({
                cacheHelper.getImage(skuSpecification.image[1], onSuccess, onFailure)
                detailSkuDialog.wareName = skuSpecification.line[1].wareName
                detailSkuDialog.show(fragmentManager, "Details")
            })

            setContentForViews(textViewName1, imageView1, layout1, 0, skuSpecification)
            setContentForViews(textViewName2, imageView2, layout2, 1, skuSpecification)

            convertView
        }

        /**
         * Задает контент для переданных в него textView, imageView, layout из skuSpecification под номером index
         *
         * @param textView
         * @param imageView
         * @param layout
         * @param index
         * @param skuSpecification
         * @return
         */
        public def setContentForViews(TextView textView, ImageView imageView, LinearLayout layout, int index, SkuSpecification skuSpecification) {

            def line = skuSpecification.line[index]
            def image = skuSpecification.image[index]

            //В случае успешного выполнения
            def onSuccess = { Bitmap bm ->
                def bitmap = null
                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable()
                if (drawable != null)
                    bitmap = drawable.getBitmap()

                image.alive = true
                image.lastUpdated = new Date()

                if (bitmap == null || !bitmap.sameAs(bm)) {
                    getActivity().runOnUiThread({
                        imageView.setImageBitmap(bm)
                    })
                    this.notifyDataSetChanged()
                }
            }

            //В случае ошибки
            def onFailure = { Throwable e ->
                getActivity().runOnUiThread({
                    imageView.setImageResource(R.drawable.default_image)
                })
                image.alive = false
                image.lastUpdated = new Date()
                Log.e("Error", e.toString())
            }

            if (line != null) {
                def wareName = line.wareName
                layout.setVisibility(View.VISIBLE)
                if (skuSpecification.guid[index].trim() != "") {
                    textView.setText(wareName)
                    image.productId = line.productId
                    cacheHelper.getImage(image, onSuccess, onFailure)
                }
            } else {
                layout.setVisibility(View.GONE)
            }
        }

    }

}