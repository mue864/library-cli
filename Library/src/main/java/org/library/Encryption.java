package org.library;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;


public class Encryption {

    public Encryption() throws Exception {
    }

// key must be stored somewhere at first creation;
    private SecretKey key;
    private final String algorithm = "AES/ECB/PKCS5Padding";


    //    transfers the key to the database when created
    private void keySafe() throws Exception{
        Database database = new Database();
        int isKeyStored = database.getExistConfirmation();
//        checking if the key is already there in the database or not
        if (isKeyStored == 0) {
            key = generateKey(128);
//            Storing the key in the database
            database.getKeyStorage(key);
        } else {
//            get the already stored key
            key = database.getKey();
        }
    }

    private SecretKey generateKey (int n) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(n);
        SecretKey key = keyGenerator.generateKey();
        return key;
    }



    private String encrypt(String input) throws Exception {
        keySafe();
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] cipherText = cipher.doFinal(input.getBytes());
        return Base64.getEncoder().encodeToString(cipherText);
    }

    private String decrypt(String cipherText) throws Exception{
        keySafe();
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));

        return new String(plainText);
    }

    public String doDecrypt(String data) throws Exception{
        return decrypt(data);
    }
    public String doEncrypt(String data) throws Exception {
        return encrypt(data);
    }
}
