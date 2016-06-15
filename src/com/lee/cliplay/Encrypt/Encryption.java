package com.lee.cliplay.Encrypt;

import android.content.Context;

import com.facebook.crypto.Crypto;
import com.facebook.crypto.Entity;
import com.facebook.crypto.util.SystemNativeCryptoLibrary;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by xl on 16/6/8.
 */
public class Encryption {

    public void encrypt(Context context, String key, String filename) throws Exception {

        Crypto crypto = new Crypto(
                new CustomSharedPrefsBackedKeyChain(context, key),
                new SystemNativeCryptoLibrary());

        if (!crypto.isAvailable()) {
            return;
        }

        FileOutputStream fileStream = context.openFileOutput(filename,
                context.MODE_WORLD_READABLE);

        Entity entity = new Entity("dbFile");

        OutputStream outputStream = crypto.getCipherOutputStream(
                fileStream,
                entity);


        byte[] mData;

        InputStream in = context.getResources().getAssets().open(filename);

        InputStream is = new BufferedInputStream(in);
        mData = new byte[is.available()];
        is.read(mData);

        outputStream.write(mData);
        outputStream.close();
        is.close();
        fileStream.close();
        in.close();
    }

    public void decrypt(Context context, String key, String filename) throws Exception {

        File dbFile = new File(context.getFilesDir().getAbsolutePath() + "/" + filename);

        if (dbFile.exists()) {
            return;
        }

        InputStream fileStream = context.getResources().getAssets().open(filename);

        Crypto crypto = new Crypto(
                new CustomSharedPrefsBackedKeyChain(context, key),
                new SystemNativeCryptoLibrary());

        Entity entity = new Entity("dbFile");

        InputStream inputStream = crypto.getCipherInputStream(
                fileStream,
                entity);

        int read;
        byte[] buffer = new byte[1024];

        FileOutputStream outputStream = context.openFileOutput(filename,
                context.MODE_WORLD_READABLE);;

        while ((read = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, read);
        }

        inputStream.close();
        outputStream.close();
        fileStream.close();
    }
}
