//  Hiumathy Lam 1001139731
//  CSE 3320 Operating Systems Spring 2016
//  Programming Assignment 4
//  Networks
//package com.example.java;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;

    public class Decrypt {
 
  /*  Mode = CipherMode.CBC,-( Cipher-block chaining)
    Padding = PaddingMode.PKCS7 or PKCS5,
    KeySize = 128,
    BlockSize = 128,
    Key = keyBytes,
    IV = keyBytes */

        Cipher cipher;

        // Input encrypted String
        static String input;

        // password to decrypt 16 bit
        final static String strPassword = "password12345678";

        // put this as key in AES
        static SecretKeySpec key = new SecretKeySpec(strPassword.getBytes(), "AES");


        public static void main(String args[]) throws Exception {

            AlgorithmParameterSpec paramSpec = new IvParameterSpec(strPassword.getBytes());
            //Whatever you want to encrypt/decrypt using AES /CBC padding
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            //You can use ENCRYPT_MODE or DECRYPT_MODE
            cipher.init(Cipher.DECRYPT_MODE, key, paramSpec);

            Socket sock = new Socket("127.0.0.1", 3000);// reading from keyboard (keyRead object)
            BufferedReader keyRead = new BufferedReader(new InputStreamReader(System.in));// sending to client (pwrite object)
            OutputStream ostream = sock.getOutputStream();
            PrintWriter pwrite = new PrintWriter(ostream, true); // receiving from server ( receiveRead  object)

            InputStream istream = sock.getInputStream();
            BufferedReader receiveRead = new BufferedReader(new InputStreamReader(istream));

            System.out.println("Enter Text: ");

            String plaintext = keyRead.readLine();

            pwrite.println(plaintext);
            pwrite.flush();

            input = receiveRead.readLine();

            //decode data using standard decoder
            byte[] output = new BASE64Decoder().decodeBuffer(input);

            // Decrypt the data
            byte[] decrypted = cipher.doFinal(output);

            System.out.println("Encrypted message received from Server: " + input);

            // decryptedData .;
            System.out.println("Decrypted message: " + new String(decrypted));

        }
    }