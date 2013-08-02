package com.cyanogenmod.id.util;

import android.util.Base64;
import android.util.Log;
import com.cyanogenmod.id.CMID;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class EncryptionUtils {
    private static final String TAG = EncryptionUtils.class.getSimpleName();

    public static class AES {
        public static String generateAesKey() {
            try {
                KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
                SecureRandom secureRandom = new SecureRandom();
                keyGenerator.init(128, secureRandom);
                byte[] symmetricKey = keyGenerator.generateKey().getEncoded();
                return Base64.encodeToString(symmetricKey, Base64.NO_WRAP);
            } catch (NoSuchAlgorithmException e) {
                Log.e(TAG, "NoSuchAlgorithimException", e);
                throw new AssertionError(e);
            }
        }

        public static String decrypt(String _ciphertext, String _key, String _iv) {
            byte[] key = Base64.decode(_key, Base64.DEFAULT);
            byte[] iv = Base64.decode(_iv, Base64.DEFAULT);
            byte[] ciphertext = Base64.decode(_ciphertext, Base64.DEFAULT);

            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            try {
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
                byte[] plaintext = cipher.doFinal(ciphertext);

                return new String(plaintext);
            } catch (NoSuchAlgorithmException e) {
                Log.e(TAG, "NoSuchAlgorithimException", e);
                throw new AssertionError(e);
            } catch (NoSuchPaddingException e) {
                Log.e(TAG, "NoSuchPaddingException", e);
                throw new AssertionError(e);
            } catch (InvalidKeyException e) {
                Log.e(TAG, "InvalidKeyException", e);
                throw new AssertionError(e);
            } catch (IllegalBlockSizeException e) {
                Log.e(TAG, "IllegalBlockSizeException", e);
                throw new AssertionError(e);
            } catch (BadPaddingException e) {
                Log.e(TAG, "BadPaddingException", e);
                throw new AssertionError(e);
            } catch (InvalidAlgorithmParameterException e) {
                Log.e(TAG, "InvalidAlgorithmParameterException", e);
                throw new AssertionError(e);
            }
        }

        public static CipherResult encrypt(String plaintext, String _key) {
            byte[] key = Base64.decode(_key, Base64.DEFAULT);

            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");

            try {
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, keySpec);
                byte[] iv = cipher.getIV();
                byte[] ciphertext = cipher.doFinal(plaintext.getBytes());

                String encodedCiphertext = Base64.encodeToString(ciphertext, Base64.NO_WRAP);
                String encodedIv = Base64.encodeToString(iv, Base64.NO_WRAP);

                return new CipherResult(encodedCiphertext, encodedIv);
            } catch (NoSuchAlgorithmException e) {
                Log.e(TAG, "NoSuchAlgorithimException", e);
                throw new AssertionError(e);
            } catch (NoSuchPaddingException e) {
                Log.e(TAG, "NoSuchPaddingException", e);
                throw new AssertionError(e);
            } catch (InvalidKeyException e) {
                Log.e(TAG, "InvalidKeyException", e);
                throw new AssertionError(e);
            } catch (IllegalBlockSizeException e) {
                Log.e(TAG, "IllegalBlockSizeException", e);
                throw new AssertionError(e);
            } catch (BadPaddingException e) {
                Log.e(TAG, "BadPaddingException", e);
                throw new AssertionError(e);
            }
        }

        public static class CipherResult {
            private String ciphertext;
            private String iv;

            private CipherResult(String ciphertext, String iv) {
                this.ciphertext = ciphertext;
                this.iv = iv;
            }

            public String getCiphertext() {
                return ciphertext;
            }

            public String getIv() {
                return iv;
            }
        }
    }

    public static class RSA {

        private static PublicKey getPublicKey(String publicKey) {
            try {
                if (CMID.DEBUG) Log.d(TAG, "Building public key from PEM = " + publicKey.toString());
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                return keyFactory.generatePublic(new X509EncodedKeySpec(Base64.decode(publicKey.toString(), Base64.DEFAULT)));
            } catch (NoSuchAlgorithmException e) {
                Log.e(TAG, "NoSuchAlgorithimException", e);
                throw new AssertionError(e);
            } catch (InvalidKeySpecException e) {
                Log.e(TAG, "InvalidKeySpecException", e);
                throw new AssertionError(e);
            }
        }

        public static String encrypt(String _publicKey, String data) {
            PublicKey publicKey = getPublicKey(_publicKey);

            try {
                Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                cipher.init(Cipher.ENCRYPT_MODE, publicKey);
                byte[] result = cipher.doFinal(data.getBytes());
                return Base64.encodeToString(result, Base64.NO_WRAP);
            } catch (NoSuchAlgorithmException e) {
                Log.e(TAG, "NoSuchAlgorithimException", e);
                throw new AssertionError(e);
            } catch (NoSuchPaddingException e) {
                Log.e(TAG, "NoSuchPaddingException", e);
                throw new AssertionError(e);
            } catch (InvalidKeyException e) {
                Log.e(TAG, "InvalidKeyException", e);
                throw new AssertionError(e);
            } catch (IllegalBlockSizeException e) {
                Log.e(TAG, "IllegalBlockSizeException", e);
                throw new AssertionError(e);
            } catch (BadPaddingException e) {
                Log.e(TAG, "BadPaddingException");
                throw new AssertionError(e);
            }
        }
    }
}