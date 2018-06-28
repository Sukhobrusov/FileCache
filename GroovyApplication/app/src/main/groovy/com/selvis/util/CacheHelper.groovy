package com.selvis.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.util.NoSuchPropertyException
import com.selvis.OrderEditorAction
import com.selvis.entity.SkuImage

import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantReadWriteLock

public class CacheHelper {

    private def context;
    ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock()
    Lock rLock = rwLock.readLock()
    Lock wLock = rwLock.writeLock()
    private static ExecutorService execServ = Executors.newCachedThreadPool();

    CacheHelper(Context context) {
        this.context = context
        //clearCache()
    }

    /**
     * Полностью очищает кэш
     * @return
     */
    def clearCache() {
        getCacheDir().listFiles().each {
            it.delete()
        }
    }


    public Context getActivity() {
        context
    }

    /**
     * Получает путь к кэшу
     * @return
     */

    synchronized def getCacheDir() {
        Log.i("SELVIS", "GetCacheDir")
        def filename = getActivity().getCacheDir().getPath()
        def string = "${filename}${File.separator}"

        return new File(string)
    }

    /**
     * Метод для скачивания картинок(из кэша или интернета)
     * @param productId
     * @return
     */
    def getImage(SkuImage skuImage, def onSuccess, def onFailure) {

        execServ.submit({
            try {
                wLock.lock()
                if (!(skuImage.lastUpdated != null && !skuImage.alive && skuImage.lastUpdated.time + 18000 >= new Date().time)) {
                    def file = getImageFile(skuImage.productId)
                    if (!Files.exists(Paths.get(file.getPath()))) {
                        try {
                            rLock.lock()
                            if ((!skuImage.alive && skuImage.lastUpdated == null) || (skuImage.lastUpdated != null && !skuImage.alive && skuImage.lastUpdated.time + 180000 <= new Date().time)) {
                                def url = String.format(OrderEditorAction.URL_FORMAT_FOR_DOWNLOADING_AN_IMAGE, skuImage.productId)
                                if (checkConnection(url)) {
                                    OrderEditorAction.downloadPictureByUid(skuImage.productId, file)
                                } else
                                    throw new IllegalAccessException("Cannot connect to URL")
                            }
                        } finally {
                            rLock.unlock()
                        }
                    }

                    Log.d("SELVIS", "Getting bitmap from file")
                    Bitmap image = BitmapFactory.decodeFile(file.path)
                    onSuccess(image)
                }
            } catch (Throwable e) {
                onFailure(e)
            }
            finally {
                wLock.unlock()
            }
        })
    }

    /**
     * Получает файл картинки
     * @param productId
     * @return
     */
    synchronized def getImageFile(String productId) {

        def cacheDirectory = getCacheDir()
        def list = cacheDirectory.listFiles()
        def file = new File(cacheDirectory.getPath() + File.separator + productId + ".png")

        if (list.size() > 1000) {
            deleteLastModifiedFiles()
        }

        if (!Files.exists(Paths.get(file.getPath()))) {
            Log.d("Creating new File", "File ${productId}.png is being created")
        }
        file

    }

    /**
     * Удаляет 1/8 часть кэша
     */
    synchronized def deleteLastModifiedFiles() {
        def dirrectory = getCacheDir().listFiles()

        Arrays.sort(dirrectory, new Comparator<File>() {
            @Override
            int compare(File file, File t1) {
                return file.lastModified - t1.lastModified
            }
        })

        for (int i = 0; i < dirrectory.size() / 8; i++) {
            dirrectory[i].delete()
        }
    }

    def checkConnection(String stringUrl){

        HttpURLConnection urlConnection = null;
        System.setProperty("http.keepAlive", "false")
        try {
            URL url = new URL(stringUrl)
            urlConnection = (HttpURLConnection) url.openConnection()
            urlConnection.setRequestMethod("HEAD")

            urlConnection.connect()
            if (urlConnection.responseCode != 200)
                throw new NoSuchPropertyException("This url doesn't respond right - $stringUrl")

            Log.i("SELVIS","URL HEADERS - "+urlConnection.headerFields)
            Log.d("SELVIS", "Content weight = " + urlConnection.getHeaderField("Content-length"))

            return true
        } catch (Throwable e) {
            return false
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }
}