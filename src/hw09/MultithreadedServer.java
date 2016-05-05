package hw09;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.*;

class Cache {
	Account acc;
	private int initValue = 0;
	private boolean isItRead;
	private boolean isItWritten;
	private int current;
	
	public Cache(Account a) {
		acc = a;
		initValue = a.peek();
		isItRead = false;
		isItWritten = false;
		current = initValue;
	}
	
	public void open_if_needed() throws TransactionAbortException {
		acc.open(false);	
	}
	
	public void close_acc() {
		acc.close();
	}
}

// TO DO: Task is currently an ordinary class.
// You will need to modify it to make it a task,
// so it can be given to an Executor thread pool.
//
class Task implements Runnable {
    private static final int A = constants.A;
    private static final int Z = constants.Z;
    private static final int numLetters = constants.numLetters;

    private Account[] accounts;
    private String transaction;

    // TO DO: The sequential version of Task peeks at accounts
    // whenever it needs to get a value, and opens, updates, and closes
    // an account whenever it needs to set a value.  This won't work in
    // the parallel version.  Instead, you'll need to cache values
    // you've read and written, and then, after figuring out everything
    // you want to do, (1) open all accounts you need, for reading,
    // writing, or both, (2) verify all previously peeked-at values,
    // (3) perform all updates, and (4) close all opened accounts.
    
    /*
     * Cache = copy of account object
     * CACHE IS SEPARATE CLASS!!!!
     * One field is pointer to account to account
     * int readvalue
     * boolean isitread
     * boolean isitwritten
     * int value
     * 
     * Functions:
     * 
     * open_if_necessary():
     * If account is open for writing, ope
     * Create cache objects in run!!!!
     * Code stays the same, we just manipulate cache instead of account
     * 
     * 
     * 
     * Two phase commit
     * Phase 1: open all accounts, "climbing phase"
     * within try block
     * for (i = A; i <= Zl; i++) {
     * 		C[i].open_if_needed();
     * }
     * catch exception
     * if exception is caught, conflict at letter L, 
     * close all accounts at L, clean_up() method, continue
     * 
     * Phase 2: Verify phase
     * Releasing all the locks
     * If verification fails, cleanup, continue, do phase 1 all over again
     * 
     * Phase 3: Write and close, cannot fail!!!
     * Then break out of loop
     * 
     * EACH THREAD HAS ITS OWN CACHE
     * 
     * 
     * Thread pool creates thread for us
     * schedlue task by calling execute
     */

    public Task(Account[] allAccounts, String trans) {
        accounts = allAccounts;
        transaction = trans;
    }
    
    // TO DO: parseAccount currently returns a reference to an account.
    // You probably want to change it to return a reference to an
    // account *cache* instead.
    // parseAccount returns account cache
    //
    private Account parseAccount(String name) {
        int accountNum = (int) (name.charAt(0)) - (int) 'A';
        if (accountNum < A || accountNum > Z)
            throw new InvalidTransactionError();
        Account a = accounts[accountNum];
        for (int i = 1; i < name.length(); i++) {
            if (name.charAt(i) != '*')
                throw new InvalidTransactionError();
            accountNum = (accounts[accountNum].peek() % numLetters);
            a = accounts[accountNum];
        }
        return a;
    }

    private int parseAccountOrNum(String name) {
        int rtn;
        if (name.charAt(0) >= '0' && name.charAt(0) <= '9') {
            rtn = new Integer(name).intValue();
        } else {
            rtn = parseAccount(name).peek();
        }
        return rtn;
    }

    public void run() {
        // tokenize transaction
    	
    	// do a while true loop for everything in run
    	// then do same as before but write on cache
    	
    
    
        String[] commands = transaction.split(";");

        for (int i = 0; i < commands.length; i++) {
            String[] words = commands[i].trim().split("\\s");
            if (words.length < 3)
                throw new InvalidTransactionError();
            Account lhs = parseAccount(words[0]);
            if (!words[1].equals("="))
                throw new InvalidTransactionError();
            int rhs = parseAccountOrNum(words[2]);
            for (int j = 3; j < words.length; j+=2) {
                if (words[j].equals("+"))
                    rhs += parseAccountOrNum(words[j+1]);
                else if (words[j].equals("-"))
                    rhs -= parseAccountOrNum(words[j+1]);
                else
                    throw new InvalidTransactionError();
            }
            try {
                
            } catch (TransactionAbortException e) {
                // won't happen in sequential version
            }
            lhs.update(rhs);
            lhs.close();
        }
        System.out.println("commit: " + transaction);
    }
}

public class MultithreadedServer {
	
	
	// requires: accounts != null && accounts[i] != null (i.e., accounts are properly initialized)
	// modifies: accounts
	// effects: accounts change according to transactions in inputFile
    public static void runServer(String inputFile, Account accounts[])
        throws IOException {
    	
    	
        // read transactions from input file
        String line;
        BufferedReader input =
            new BufferedReader(new FileReader(inputFile));

        // TO DO: you will need to create an Executor and then modify the
        // following loop to feed tasks to the executor instead of running them
        // directly. 

        while ((line = input.readLine()) != null) {
        	
        	Executor e = Executors.newFixedThreadPool(constants.numLetters);
        	Task t = new Task(accounts, line);
        	e.execute(t);
        }
        
        input.close();

    }
}
