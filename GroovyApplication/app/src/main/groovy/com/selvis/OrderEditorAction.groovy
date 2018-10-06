package com.selvis

import android.graphics.Bitmap
import android.graphics.BitmapFactory;
import android.util.Log
import android.util.NoSuchPropertyException;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken
import com.selvis.entity.OrderEditorLine
import com.selvis.entity.OrderSearchResult

import java.lang.reflect.Type

public class OrderEditorAction {

    private static
    final String URL_FOR_SEARCH_RESULT = "https://selvis.com/web/orderEditor/getDataRange?withTotals=true&class=class+com.selvis.api.OrderEditorAction%24SearchParams&statDeep=0&includeOutOfStock=false&skip=0&fetch=20&search=%D0%93%D0%BE%D1%82%D0%BE%D0%B2%D1%8B%D0%B5+%D0%B7%D0%B0%D0%B2%D1%82%D1%80%D0%B0%D0%BA%D0%B8+"
    public static
    final String URL_FORMAT_FOR_DOWNLOADING_AN_IMAGE = "https://selvis.com/web/images/%s.png"


    static def downloadPictureByUid(String productId, File file) {
        HttpURLConnection conn = null
        try {
            def url = String.format(URL_FORMAT_FOR_DOWNLOADING_AN_IMAGE, productId)
            Log.i("SELVIS", "Starting connection to url $url")

            conn = (HttpURLConnection) new URL(url).openConnection()
            conn.setReadTimeout(60000)
            conn.setConnectTimeout(30000)
            conn.setDoInput(true)

            try {
                conn.connect()
                def responseCode = conn.getResponseCode()

                if (responseCode != 200) {
                    throw new IllegalAccessException("${responseCode}")
                }
                try {
                    InputStream is = conn.getInputStream()
                    FileOutputStream fos = new FileOutputStream(file)
                    try {
                        fos << is
                    } finally {
                        is.close()

                        if (fos != null) {
                            fos.flush()
                            fos.close()
                        }
                    }
                } catch (Throwable e) {
                    if (conn.getResponseCode() >= 400) {
                        throw new IllegalAccessException(e)
                    }
                    throw e
                }

            } finally {
                conn.disconnect()
            }
        } catch (Throwable e) {
            Log.e("Download Exception", e)
        }
        null
    }

    static OrderSearchResult fillSkuSpecs() {

        HttpURLConnection conn = null;
        try {
            //URL url1 = new URL(url)
            conn = (HttpURLConnection) new URL(URL_FOR_SEARCH_RESULT).openConnection();
            conn.setReadTimeout(60000)
            conn.setConnectTimeout(30000)
            conn.setDoInput(true)

            try {
                conn.connect()
                def responseCode = conn.getResponseCode()

                if (responseCode != 200) {
                    throw new IllegalAccessException("${responseCode}")
                }
                ByteArrayOutputStream bout = new ByteArrayOutputStream();

                try {
                    InputStream is = conn.getInputStream()

                    try {
                        bout << is;
                    } finally {
                        is.close();
                        bout.close();
                    }
                } catch (Throwable e) {
                    if (responseCode >= 400) {
                        throw new IllegalAccessException()
                    }
                    throw e
                }

                Log.d("Selvis", bout.toString());
                def someThing
                JsonObject jse = (JsonObject) new JsonParser().parse(bout.toString());
                if (jse.has("result")) {
                    someThing = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                        @Override
                        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                            return json == null ? null : new Date(json.getAsLong());
                        }
                    })
                            .create()
                            .fromJson(jse.result, new TypeToken<OrderSearchResult<OrderEditorLine>>() {
                    }.getType());
                }

                return someThing

            } finally {
                conn.disconnect();
            }

        } catch (Throwable e) {
            Log.e("SELVIS", e.getMessage(), e)
        }
        return null
    }


}