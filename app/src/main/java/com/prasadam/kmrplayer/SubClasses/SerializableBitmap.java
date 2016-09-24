package com.prasadam.kmrplayer.SubClasses;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/*
 * Created by Prasadam Saiteja on 9/19/2016.
 */

public class SerializableBitmap implements Serializable {

    private static final long serialVersionUID = 1L;
    private transient Bitmap bitmap;

    public SerializableBitmap(Bitmap bitmap) {this.bitmap = bitmap;}

    public Bitmap getBitmap() { return bitmap; }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        Log.d("write bitmap null", String.valueOf(bitmap == null));
        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            BitmapDataObject bitmapDataObject = new BitmapDataObject();
            bitmapDataObject.imageByteArray = stream.toByteArray();
            out.writeObject(bitmapDataObject);
            Log.d("bitmap written", "done");
        }
    }
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        BitmapDataObject bitmapDataObject = (BitmapDataObject) in.readObject();
        Log.d("read bdo null", String.valueOf(bitmapDataObject == null));
        if (bitmapDataObject != null) {
            bitmap = BitmapFactory.decodeByteArray(bitmapDataObject.imageByteArray, 0, bitmapDataObject.imageByteArray.length);
            Log.d("bitmap read", "done");
        }
    }

    protected class BitmapDataObject implements Serializable {
        private static final long serialVersionUID = 111696345129311948L;
        public byte[] imageByteArray;
    }
}