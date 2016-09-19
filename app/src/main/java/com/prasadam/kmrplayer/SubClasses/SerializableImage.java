package com.prasadam.kmrplayer.SubClasses;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

/*
 * Created by Prasadam Saiteja on 9/19/2016.
 */

public class SerializableImage implements Serializable {

    private Bitmap bitmap;

    public SerializableImage(Bitmap bitmap)
    {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap(){
        return bitmap;
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

        byte[] byteArray = stream.toByteArray();

        out.writeInt(byteArray.length);
        out.write(byteArray);

        bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {

        int bufferLength = in.readInt();
        byte[] byteArray = new byte[bufferLength];

        int pos = 0;

        do {
            int read = in.read(byteArray, pos, bufferLength - pos);

            if (read != -1)
                pos += read;
            else
                break;

        } while (pos < bufferLength);

        bitmap = BitmapFactory.decodeByteArray(byteArray, 0, bufferLength);
    }
}
