package com.selvis;

import android.graphics.Bitmap;

import com.selvis.entity.OrderEditorLine;
import com.selvis.entity.SkuImage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SkuSpecification {

    public List<String> guid = new ArrayList<>();

    public List<OrderEditorLine> line = new ArrayList<>();

    public List<SkuImage> image = new ArrayList<>();


    {
        Collections.addAll(guid,"","");
        Collections.addAll(image,new SkuImage(),new SkuImage());
        Collections.addAll(line,null,null);

    }

}
