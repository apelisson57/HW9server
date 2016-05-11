package hw09.test;

import hw09.*;

import java.io.*;
import java.lang.Thread.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.*;
import java.util.Random;

import junit.framework.TestCase;

import org.junit.Test;

public class MultithreadedServerTests extends TestCase {
    private static final int A = constants.A;
    private static final int Z = constants.Z;
    private static final int numLetters = constants.numLetters;
    private static Account[] accounts;
            
    protected static void dumpAccounts() {
	    // output values:
	    for (int i = A; i <= Z; i++) {
	       System.out.print("    ");
	       if (i < 10) System.out.print("0");
	       System.out.print(i + " ");
	       System.out.print(new Character((char) (i + 'A')) + ": ");
	       accounts[i].print();
	       System.out.print(" (");
	       accounts[i].printMod();
	       System.out.print(")\n");
	    }
	 }    
     
     /*   
     @Test
	 public void testIncrement() throws IOException {
	
		// initialize accounts 
		accounts = new Account[numLetters];
		for (int i = A; i <= Z; i++) {
			accounts[i] = new Account(Z-i);
		}
		
		MultithreadedServer.runServer("src/hw09/data/increment", accounts);
	
		// assert correct account values
		for (int i = A; i <= Z; i++) {
			Character c = new Character((char) (i+'A'));
			assertEquals("Account "+c+" differs",Z-i+1,accounts[i].getValue());
		}		

	 }
	 */
     
     
     @Test
     public void testRotate() throws IOException {
 		// initialize accounts 
 		accounts = new Account[numLetters];
 		for (int i = A; i <= Z; i++) {
 			accounts[i] = new Account(i);
 		}
 		
 		MultithreadedServer.runServer("src/hw09/data/rotate", accounts);
 		
		// assert correct account values
		for (int i = A; i <= Z - 2; i++) {
			Character c = new Character((char) (i+'A'));
			assertEquals("Account "+c+" differs",2*i+3,accounts[i].getValue()); // A-X should be 3, 5, ... 47, 49
		}
		assertEquals("Account Y differs",28,accounts[Z - 1].getValue());
		assertEquals("Account Z differs",8,accounts[Z].getValue());
     }
     
	 	  	 

     /*
     @Test
     public void testDoubleIndirection() throws IOException {
         // initialize accounts
         accounts = new Account[numLetters];
         accounts[0] = new Account(0);   // A = 0
         accounts[1] = new Account(1);   // B = 1
         accounts[5] = new Account(9);   // F = 9
         accounts[9] = new Account(42);  // J = 42
         accounts[11] = new Account(5);  // L = 5
         accounts[16] = new Account(37); // Q = 37
         
         MultithreadedServer.runServer("src/hw09/data/doubleIndirection", accounts);
         
         // assert correct account values
         assertEquals("Account A differs",9,accounts[0].getValue());
         assertEquals("Account B differs",51,accounts[1].getValue());
         assertEquals("Account F differs",9,accounts[5].getValue());
         assertEquals("Account J differs",42,accounts[9].getValue());
         assertEquals("Account L differs",5,accounts[11].getValue());
         assertEquals("Account Q differs",37,accounts[16].getValue());
     }
     */
     
     
     /*
     @Test
     public void testLHSReference() throws IOException {
    	 // initialize accounts
    	 accounts = new Account[numLetters];
         accounts[0] = new Account(47);  // A = 47
         accounts[1] = new Account(0);   // B = 0
         accounts[3] = new Account(14);  // D = 14
         accounts[7] = new Account(29);  // H = 29
         accounts[12] = new Account(7);  // M = 7
         accounts[14] = new Account(9);  // O = 9
         accounts[21] = new Account(12); // V = 13
         
         MultithreadedServer.runServer("src/hw09/data/lhsReference", accounts);
         
         // assert correct account values
         assertEquals("Account A differs",47,accounts[0].getValue());
         assertEquals("Account B differs",0,accounts[1].getValue());
         assertEquals("Account D differs",14,accounts[3].getValue());
         assertEquals("Account H differs",29,accounts[7].getValue());
         assertEquals("Account M differs",29,accounts[12].getValue());
         assertEquals("Account O differs",22,accounts[14].getValue());
         assertEquals("Account V differs",13,accounts[21].getValue());
    	 
     }

     */
     
     /*
     @Test
     public void testVerifyTransactionAbortException() throws IOException {
 	 // initialize accounts
 	 accounts = new Account[numLetters];
 	 accounts[0] = new Account(34);  // A = 34
 	 accounts[1] = new Account(17);  // B = 17
 	 accounts[2] = new Account(9);   // C = 9
 	 accounts[3] = new Account(7);   // D = 7
 	 accounts[4] = new Account(12);  // E = 12
 	
 	 MultithreadedServer.runServer("src/hw09/data/verifyTranscationAbortError", accounts);
     
      // assert correct account values
      assertEquals("Account A differs",34,accounts[0].getValue());
      assertEquals("Account B differs",43,accounts[1].getValue());
      assertEquals("Account C differs",9,accounts[2].getValue());
      assertEquals("Account D differs",62,accounts[3].getValue());
      assertEquals("Account E differs",12,accounts[4].getValue());

     }
     */
     /*
     @Test
     public void testSubtraction() throws IOException {
		 // initialize accounts
		 accounts = new Account[numLetters];
	  	 accounts[0] = new Account(12);  // A = 12
	  	 accounts[1] = new Account(17);  // B = 37
	  	 accounts[2] = new Account(5);   // C = 5
	  	 accounts[3] = new Account(7);   // D = 4
	  	 accounts[4] = new Account(12);  // E = 28
	  	 accounts[5] = new Account(7);   // F = 7
	 	 accounts[6] = new Account(27);  // G = 27
	 	 accounts[7] = new Account(6);   // H = 6 
	 	 accounts[8] = new Account(3);   // I = 3
	  	 
	  	
	  	 MultithreadedServer.runServer("src/hw09/data/subtraction", accounts);
	      
	     // assert correct account values
	     assertEquals("Account A differs",11,accounts[0].getValue());
	     assertEquals("Account B differs",36,accounts[1].getValue());
	     assertEquals("Account C differs",4,accounts[2].getValue());
	     assertEquals("Account D differs",21,accounts[3].getValue());
	     assertEquals("Account E differs",7,accounts[4].getValue());
	     assertEquals("Account F differs",7,accounts[5].getValue());
	     assertEquals("Account G differs",15,accounts[6].getValue());
	     assertEquals("Account H differs",6,accounts[7].getValue());
	     assertEquals("Account I differs",3,accounts[8].getValue());
     }
     */

}