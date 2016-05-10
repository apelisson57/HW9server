package hw09;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.*;

class Cache {
	Account acc;
	private int initialValue;
	private int currentValue;
	private boolean isItRead;
	private boolean isItWritten;
	
	public Cache(Account a) {
		acc = a;
		initialValue = a.peek();
		isItRead = false;
		isItWritten = false;
		currentValue = initialValue;
	}
	
	// If a Cache is marked for reading or writing, its Account is opened appropriately.
	public void openIfNeeded() throws TransactionAbortException {
		if (isItRead) {
			acc.open(false);
		}
		if (isItWritten) {
			acc.open(true);
		}
	}
	
	public void closeAccount() {
		acc.close();
	}

	
	public int peekAccount() {
		return acc.peek();
	}
	
	public void write(int value) {
		currentValue = value;
	}
	
	public void markForReading() {
		isItRead = true;
	}
	
	public void markForWriting() {
		isItWritten = true;
	}
	
	public int getCurrentValue() {
		return currentValue;
	}
	
	public int getInitialValue() {
		return initialValue;
	}
	
	public boolean isRead() {
		return isItRead;
	}
	
	public boolean isWritten() {
		return isItWritten;
	}
}

class Task implements Runnable {
    private static final int A = constants.A;
    private static final int Z = constants.Z;
    private static final int numLetters = constants.numLetters;

    private Account[] accounts;
    private Cache[] caches;
    private String transaction;

    // TO DO: The sequential version of Task peeks at accounts
    // whenever it needs to get a value, and opens, updates, and closes
    // an account whenever it needs to set a value.  This won't work in
    // the parallel version.  Instead, you'll need to cache values
    // you've read and written, and then, after figuring out everything
    // you want to do, (1) open all accounts you need, for reading,
    // writing, or both, (2) verify all previously peeked-at values,
    // (3) perform all updates, and (4) close all opened accounts.

    public Task(Account[] allAccounts, String trans) {
        accounts = allAccounts;
        // Create a cache to wrap each account.
        for (int accountNum = 0; accountNum < allAccounts.length; accountNum++) {
        	caches[accountNum] = new Cache(allAccounts[accountNum]);
        }
        transaction = trans;
    }
    
    // Return a reference to an account *cache* instead of an account.
    //
    private Cache parseAccount(String name) {
        int accountNum = (int) (name.charAt(0)) - (int) 'A';
        if (accountNum < A || accountNum > Z)
            throw new InvalidTransactionError();
        for (int i = 1; i < name.length(); i++) {
            if (name.charAt(i) != '*')
                throw new InvalidTransactionError();
            accountNum = (accounts[accountNum].peek() % numLetters);
        }
        return caches[accountNum];
    }

    private int parseAccountOrNum(String name) {
        int rtn;
        if (name.charAt(0) >= '0' && name.charAt(0) <= '9') {
            rtn = new Integer(name).intValue();
        } else {
            rtn = parseAccount(name).peekAccount();
        }
        return rtn;
    }
    
    //	Cleanup method. All Accounts read from or written to in the cache are closed.  
    //
    public void closeOpenAccounts() {
    	for (int accountNum = A; accountNum < numLetters; accountNum++) {
    		Cache accountCache = caches[accountNum];
    		if (accountCache.isRead() || accountCache.isWritten()) {
    			accounts[accountNum].close();
    		}
    	}
    }

    public void run() {
    	// tokenize transaction
        String[] commands = transaction.split(";");

        // Parse each command in the transaction.
        for (int i = 0; i < commands.length; i++) {
            String[] words = commands[i].trim().split("\\s");
            if (words.length < 3)
                throw new InvalidTransactionError();
            Cache lhs = parseAccount(words[0]);
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
            // Carry out each command in the local cache.
            lhs.markForWriting();
            lhs.write(rhs);
            
            // Mark each read Cache for reading.
            for (int wordNum = 2; wordNum < words.length; wordNum++) {
            	String word = words[wordNum];
            	// word is not an operator
                if ((!(word.equals("=") || word.equals("+") || word.equals("-"))) &&
                		// word does not begin with a digit
                		(!(word.charAt(0) >= '0' && word.charAt(0) <= '9'))) {
                	Cache cacheRead = parseAccount(word);
                	cacheRead.markForReading();
                }
            }
        }
        
        while (true) {
        	// TODO Phase 1: Open all read/written accounts in global accounts array.
        	try {
        		for (int cacheNum = 0; cacheNum < numLetters; cacheNum++) {
        			caches[cacheNum].openIfNeeded(); // should open accounts[...]
        		}
        	} catch (TransactionAbortException e) {
        		closeOpenAccounts();
        		continue;
        	}
        	// Phase 2: Verify that all opened Accounts have the correct values.
        	try {
        		for (int cacheNum = 0; cacheNum < numLetters; cacheNum++) {
        			accounts[cacheNum].verify(caches[cacheNum].getInitialValue());
        		}
        	} catch (TransactionAbortException e) {
        		closeOpenAccounts();
        		continue;
        	}
        	// Write to all accounts written to.
        	for (int cacheNum = 0; cacheNum < numLetters; cacheNum++) {
        		Cache someCache = caches[cacheNum];
        		if (someCache.isWritten()) {
        			accounts[cacheNum].update(someCache.getCurrentValue());
        		}
        	}
        	closeOpenAccounts();
        	
        	break;	// Success! Output successful-write message.
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

        // Create an Executor and then feed tasks to the executor instead of running them directly. 
        
        Executor e = Executors.newCachedThreadPool();

        while ((line = input.readLine()) != null) {
        	Task t = new Task(accounts, line);
        	e.execute(t);
        }
        
        input.close();
    }
}
