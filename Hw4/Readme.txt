//  Hiumathy Lam 1001139731
//  CSE 3320 Operating Systems Spring 2016
//  Programming Assignment 4
//  Networks



The following code is built in java. In the command line, go to the following directory where the Hw4 is located. Compile both codes by entering

	javac Encrypt.java

then

	javac Decrypt.java 



Now first execute the Encrypt class:

	java Encrypt


This should print out:

 Server has started and listening to port 3000


Now execute the Decrypt class:

	java Decrypt


This should print out:

	Enter text:

Input anything type of text and then press enter

The text you entered will be sent to the Encrypt class to be encrypted, then the encryption will be sent to the Decrypt class for decrypting. Once it is done decrypting, the Decrypt class will print out the text you entered that was encrypted.