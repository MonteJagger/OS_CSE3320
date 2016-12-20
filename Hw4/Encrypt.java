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
import java.net.ServerSocket;
import java.net.Socket;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Encoder;
 
    /*  Mode = CipherMode.CBC,-( Cipher-block chaining)
        Padding = PaddingMode.PKCS7 or PKCS5,
        KeySize = 128,
        BlockSize = 128,
        Key = keyBytes - password,
        IV = keyBytes  - password*/
 
  public class Encrypt {
 
  Cipher cipher; 
  // Input encrypted String
  
  static  String input;
  // password for encryption
  final static String strPassword = "password12345678";
  // put this as key in AES
  static SecretKeySpec key = new SecretKeySpec(strPassword.getBytes(), "AES");
 
  public static void main(String args []) throws Exception{
 
     // Parameter specific algorithm
    AlgorithmParameterSpec paramSpec = new     IvParameterSpec(strPassword.getBytes()); 
    //Whatever you want to encrypt/decrypt
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
 
    // You can use ENCRYPT_MODE or DECRYPT_MODE 
    cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec); 
    
    ServerSocket sersock = new ServerSocket(3000);
      System.out.println(" Server has started and listening to port 3000");
      Socket sock = sersock.accept( );                          
                              // reading from keyboard (keyRead object)
      BufferedReader keyRead = new BufferedReader(new InputStreamReader(System.in));
      OutputStream ostream; 
      ostream = sock.getOutputStream(); 
      PrintWriter pwrite = new PrintWriter(ostream, true);

                              // receiving from server ( receiveRead  object)
      InputStream istream = sock.getInputStream();
      BufferedReader receiveRead = new BufferedReader(new InputStreamReader(istream));

      String receiveMessage, sendMessage;
      
      input = receiveRead.readLine();
    
    // encrypt data 
     byte[] ecrypted = cipher.doFinal(input.getBytes());
 
     // encode data using standard encoder
     String output =  new BASE64Encoder().encode(ecrypted);
     
     pwrite.println(output);
     pwrite.flush();
     
     System.out.println("Message received from Client: " + input);
     System.out.println("Encrypted Message: " + output);
 
      }
 
  }