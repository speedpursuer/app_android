package com.lee.cliplay.Encrypt;

import android.content.Context;

import com.facebook.android.crypto.keychain.SecureRandomFix;
import com.facebook.crypto.cipher.NativeGCMCipher;
import com.facebook.crypto.exception.KeyChainException;
import com.facebook.crypto.keychain.KeyChain;
import com.facebook.crypto.mac.NativeMac;

import java.util.Arrays;

/**
 * Created by TheKing on 2014-12-29.
 */
public class CustomSharedPrefsBackedKeyChain implements KeyChain {
    // Visible for testing.
    /* package */ static final String SHARED_PREF_NAME = "crypto";
    /* package */ static final String CIPHER_KEY_PREF = "cipher_key";
    /* package */ static final String MAC_KEY_PREF = "mac_key";

    private String key = null;

    protected byte[] mCipherKey;
    protected boolean mSetCipherKey;

    protected byte[] mMacKey;
    protected boolean mSetMacKey;

    private static final SecureRandomFix sSecureRandomFix = new SecureRandomFix();
    private String log = "KeyChain";

    public CustomSharedPrefsBackedKeyChain(Context context, String key) {
        this.key = key;
    }

    @Override
    public synchronized byte[] getCipherKey() throws KeyChainException {
        if (!mSetCipherKey) {
            mCipherKey = maybeGenerateKey(NativeGCMCipher.KEY_LENGTH);
        }
        mSetCipherKey = true;
        return mCipherKey;
    }

    @Override
    public byte[] getMacKey() throws KeyChainException {
        if (!mSetMacKey) {
            mMacKey = maybeGenerateKey(NativeMac.KEY_LENGTH);
        }
        mSetMacKey = true;
        return mMacKey;
    }

    @Override
    public byte[] getNewIV() throws KeyChainException {
        return maybeGenerateKey(NativeGCMCipher.IV_LENGTH);
    }

    @Override
    public synchronized void destroyKeys() {
        mSetCipherKey = false;
        mSetMacKey = false;
        Arrays.fill(mCipherKey, (byte) 0);
        Arrays.fill(mMacKey, (byte) 0);
        mCipherKey = null;
        mMacKey = null;
    }

    /**
     * Generates a key associated with a preference.
     */
    private byte[] maybeGenerateKey(int length) throws KeyChainException {

        String key = "";
        if(length == NativeGCMCipher.KEY_LENGTH)
            key = this.key;
        else if(length == NativeMac.KEY_LENGTH)
            key = this.key + this.key + this.key + this.key;
        else if(length == NativeGCMCipher.IV_LENGTH)
            key = "fjdaslkfjdkl";

        String base64Key = key.toString();

        return base64Key.getBytes();
    }
}
