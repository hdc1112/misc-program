/**
 *
 * @author Nathan Magnus, under the supervision of Howard Hamilton
 * Copyright: University of Regina, Nathan Magnus and Su Yibin, June 2009.
 * No reproduction in whole or part without maintaining this copyright notice
 * and imposing this condition on any subsequent users.
 *
 * File:
 * Input files needed:
 *      1. config.txt - three lines, each line is an integer
 *          line 1 - number of items per transaction
 *          line 2 - number of transactions
 *          line 3 - minsup
 *      2. transa.txt - transaction file, each line is a transaction, items are separated by a space
 */

//package apriori;
import java.io.*;
import java.util.*;

public class Apriori {

  public static void main(String[] args) {
    // hdc: program starts at here
    AprioriCalculation ap = new AprioriCalculation();

    ap.aprioriProcess();
  }
}
/******************************************************************************
 * Class Name   : AprioriCalculation
 * Purpose      : generate Apriori itemsets
 *****************************************************************************/
class AprioriCalculation
{
  // hdc: remember that in Apriori algorithm, candidate set,
  // hdc: strictly speaking, is a set of set.
  // hdc: and here, "String" is the "set", and Vector<String> is the "set of set"
  // hdc: the "set" format is 345, this means, the item 2 3 4 are inside this set
  // hdc: this implementation didn't say explicity about Ci and Li
  // hdc: they are all stored inside ths candidates data structure
  Vector<String> candidates=new Vector<String>(); //the current candidates
  String configFile="config.txt"; //configuration file
  String transaFile="transa.txt"; //transaction file
  String outputFile="apriori-output.txt";//output file
  int numItems; //number of items per transaction
  int numTransactions; //number of transactions
  double minSup; //minimum support for a frequent itemset
  String oneVal[]; //array of value per column that will be treated as a '1'
  String itemSep = " "; //the separator value for items in the database

  /************************************************************************
   * Method Name  : aprioriProcess
   * Purpose      : Generate the apriori itemsets
   * Parameters   : None
   * Return       : None
   *************************************************************************/
  public void aprioriProcess()
  {
    Date d; //date object for timing purposes
    long start, end; //start and end time
    int itemsetNumber=0; //the current itemset being looked at
    //get config
    // hdc: read #transactions, #items, minimum support
    getConfig();

    System.out.println("Apriori algorithm has started.\n");

    //start timer
    d = new Date();
    start = d.getTime();

    //while not complete
    do
    {
      //increase the itemset that is being looked at
      itemsetNumber++;

      //generate the candidates
      // hdc: generate candidate sets based on previous round's candidate set
      // and the current itemsetNumber. there's no pruning in this implementation.
      generateCandidates(itemsetNumber);
      // hdc: now we have the Ci

      //determine and display frequent itemsets
      // hdc: for each candidate itemset, decide whether its appearance
      // has exceeded minimum support.
      calculateFrequentItemsets(itemsetNumber);

      // hdc: now we have the Li

      if(candidates.size()!=0)
      {
        // hdc: at this level, the candidates set print (Li)
        // hdc: this if stmt is only for printing purpose
        System.out.println("Frequent " + itemsetNumber + "-itemsets");
        System.out.println(candidates);
      }
      //if there are <=1 frequent items, then its the end. This prevents reading through the database again. When there is only one frequent itemset.
      // hdc: if Li's size is 1 or 0, then Ci+1 must be empty, so no need to proceed
    }while(candidates.size()>1);

    //end timer
    d = new Date();
    end = d.getTime();

    //display the execution time
    System.out.println("Execution time is: "+((double)(end-start)/1000) + " seconds.");
  }

  /************************************************************************
   * Method Name  : getInput
   * Purpose      : get user input from System.in
   * Parameters   : None
   * Return       : String value of the users input
   *************************************************************************/
  public static String getInput()
  {
    String input="";
    //read from System.in
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    //try to get users input, if there is an error print the message
    try
    {
      input = reader.readLine();
    }
    catch (Exception e)
    {
      System.out.println(e);
    }
    return input;
  }

  /************************************************************************
   * Method Name  : getConfig
   * Purpose      : get the configuration information (config filename, transaction filename)
   *              : configFile and transaFile will be change appropriately
   * Parameters   : None
   * Return       : None
   *************************************************************************/
  private void getConfig()
  {
    FileWriter fw;
    BufferedWriter file_out;

    String input="";
    //ask if want to change the config
    System.out.println("Default Configuration: ");
    System.out.println("\tRegular transaction file with '" + itemSep + "' item separator.");
    System.out.println("\tConfig File: " + configFile);
    System.out.println("\tTransa File: " + transaFile);
    System.out.println("\tOutput File: " + outputFile);
    System.out.println("\nPress 'C' to change the item separator, configuration file and transaction files");
    System.out.print("or any other key to continue.  ");
    input=getInput();

    if(input.compareToIgnoreCase("c")==0)
    {
      System.out.print("Enter new transaction filename (return for '"+transaFile+"'): ");
      input=getInput();
      if(input.compareToIgnoreCase("")!=0)
        transaFile=input;

      System.out.print("Enter new configuration filename (return for '"+configFile+"'): ");
      input=getInput();
      if(input.compareToIgnoreCase("")!=0)
        configFile=input;

      System.out.print("Enter new output filename (return for '"+outputFile+"'): ");
      input=getInput();
      if(input.compareToIgnoreCase("")!=0)
        outputFile=input;

      System.out.println("Filenames changed");

      System.out.print("Enter the separating character(s) for items (return for '"+itemSep+"'): ");
      input=getInput();
      if(input.compareToIgnoreCase("")!=0)
        itemSep=input;


    }

    try
    {
      FileInputStream file_in = new FileInputStream(configFile);
      BufferedReader data_in = new BufferedReader(new InputStreamReader(file_in));
      //number of items
      numItems=Integer.valueOf(data_in.readLine()).intValue();

      //number of transactions
      numTransactions=Integer.valueOf(data_in.readLine()).intValue();

      //minsup
      minSup=(Double.valueOf(data_in.readLine()).doubleValue());

      //output config info to the user
      System.out.print("\nInput configuration: "+numItems+" items, "+numTransactions+" transactions, ");
      System.out.println("minsup = "+minSup+"%");
      System.out.println();
      minSup/=100.0;

      // hdc: I don't know what oneval is used for
      // hdc: but by default its element are all "1"
      oneVal = new String[numItems];
      System.out.print("Enter 'y' to change the value each row recognizes as a '1':");
      if(getInput().compareToIgnoreCase("y")==0)
      {
        for(int i=0; i<oneVal.length; i++)
        {
          System.out.print("Enter value for column #" + (i+1) + ": ");
          oneVal[i] = getInput();
        }
      }
      else
        for(int i=0; i<oneVal.length; i++)
          oneVal[i]="1";

      //create the output file
      fw= new FileWriter(outputFile);
      file_out = new BufferedWriter(fw);
      //put the number of transactions into the output file
      file_out.write(numTransactions + "\n");
      file_out.write(numItems + "\n******\n");
      file_out.close();
    }
    //if there is an error, print the message
    catch(IOException e)
    {
      System.out.println(e);
    }
  }

  /************************************************************************
   * Method Name  : generateCandidates
   * Purpose      : Generate all possible candidates for the n-th itemsets
   *              : these candidates are stored in the candidates class vector
   * Parameters   : n - integer value representing the current itemsets to be created
   * Return       : None
   *************************************************************************/
  private void generateCandidates(int n)
  {
    // hdc: this function is used to calculate Ci+1 from Li
    Vector<String> tempCandidates = new Vector<String>(); //temporary candidate string vector
    String str1, str2; //strings that will be used for comparisons
    StringTokenizer st1, st2; //string tokenizers for the two itemsets being compared

    //if its the first set, candidates are just the numbers
    if(n==1)
    {
      for(int i=1; i<=numItems; i++)
      {
        tempCandidates.add(Integer.toString(i));
      }
    }
    else if(n==2) //second itemset is just all combinations of itemset 1
    {
      //add each itemset from the previous frequent itemsets together
      for(int i=0; i<candidates.size(); i++)
      {
        st1 = new StringTokenizer(candidates.get(i));
        str1 = st1.nextToken();
        for(int j=i+1; j<candidates.size(); j++)
        {
          st2 = new StringTokenizer(candidates.elementAt(j));
          str2 = st2.nextToken();
          tempCandidates.add(str1 + " " + str2);
        }
      }
    }
    else
    {
      //for each itemset
      for(int i=0; i<candidates.size(); i++)
      {
        //compare to the next itemset
        for(int j=i+1; j<candidates.size(); j++)
        {
          //create the strigns
          str1 = new String();
          str2 = new String();
          //create the tokenizers
          st1 = new StringTokenizer(candidates.get(i));
          st2 = new StringTokenizer(candidates.get(j));

          //make a string of the first n-2 tokens of the strings
          for(int s=0; s<n-2; s++)
          {
            str1 = str1 + " " + st1.nextToken();
            str2 = str2 + " " + st2.nextToken();
          }

          //if they have the same n-2 tokens, add them together
          if(str2.compareToIgnoreCase(str1)==0)
            tempCandidates.add((str1 + " " + st1.nextToken() + " " + st2.nextToken()).trim());
        }
      }
    }
    //clear the old candidates
    candidates.clear();
    //set the new ones
    candidates = new Vector<String>(tempCandidates);
    tempCandidates.clear();
  }

  /************************************************************************
   * Method Name  : calculateFrequentItemsets
   * Purpose      : Determine which candidates are frequent in the n-th itemsets
   *              : from all possible candidates
   * Parameters   : n - iteger representing the current itemsets being evaluated
   * Return       : None
   *************************************************************************/
  private void calculateFrequentItemsets(int n)
  {
    // hdc: the input to this function is
    // hdc: n, the iteration counter
    // hdc: candidates, or Cn, frequentCandidates will be a subset of Cn
    // hdc: and frequentCandidates will be assigned to candidates
    Vector<String> frequentCandidates = new Vector<String>(); //the frequent candidates for the current itemset
    FileInputStream file_in; //file input stream
    BufferedReader data_in; //data input stream
    FileWriter fw;
    BufferedWriter file_out;

    StringTokenizer st, stFile; //tokenizer for candidate and transaction
    boolean match; //whether the transaction has all the items in an itemset
    // hdc: trans[] array stores the row-wise item information
    boolean trans[] = new boolean[numItems]; //array to hold a transaction so that can be checked
    // hdc: this records every "set" 's occurrences
    // hdc: count[] is used to filter candidates
    int count[] = new int[candidates.size()]; //the number of successful matches

    try
    {
      //output file
      fw= new FileWriter(outputFile, true);
      file_out = new BufferedWriter(fw);
      //load the transaction file
      file_in = new FileInputStream(transaFile);
      data_in = new BufferedReader(new InputStreamReader(file_in));

      //for each transaction
      // hdc: i.e., for each row
      // hdc: the whole logic of the following code is:
      // hdc: for each row
      // hdc:     for each candidate in candidates
      // hdc:         if this candidate appears in this row
      // hdc:             count[this candidate]++
      // hdc: now filter the candidates
      for(int i=0; i<numTransactions; i++)
      {
        //System.out.println("Got here " + i + " times"); //useful to debug files that you are unsure of the number of line
        stFile = new StringTokenizer(data_in.readLine(), itemSep); //read a line from the file to the tokenizer
        //put the contents of that line into the transaction array
        for(int j=0; j<numItems; j++)
        {
          // hdc: oneVal[] array is used to tell that in this row
          // hdc: whether 0 should be treated as true, or 1 should be treated as true
          // hdc: if it matches oneVal[i], then compareToIgnoreCase() would return 0
          // hdc: and the whole right expression would return true
          trans[j]=(stFile.nextToken().compareToIgnoreCase(oneVal[j])==0); //if it is not a 0, assign the value to true
        }

        // hdc: now we have a trans[] array

        //check each candidate
        // hdc: i.e., check each "set" in the "set of set"
        for(int c=0; c<candidates.size(); c++)
        {
          match = false; //reset match to false
          //tokenize the candidate so that we know what items need to be present for a match
          // hdc: for each set in the candidates set, check its existence
          st = new StringTokenizer(candidates.get(c));
          // hdc: now we have a set, check whether this set belongs to this row's transaction
          //check each item in the itemset to see if it is present in the transaction
          while(st.hasMoreTokens())
          {
            match = (trans[Integer.valueOf(st.nextToken())-1]);
            // hdc: if one item doesn't show up in this row, then it's not a match
            if(!match) //if it is not present in the transaction stop checking
              break;
          }
          if(match) //if at this point it is a match, increase the count
            count[c]++;
        }

      }
      for(int i=0; i<candidates.size(); i++)
      {
        //  System.out.println("Candidate: " + candidates.get(c) + " with count: " + count + " % is: " + (count/(double)numItems));
        //if the count% is larger than the minSup%, add to the candidate to the frequent candidates
        // hdc: filter each candidate out
        if((count[i]/(double)numTransactions)>=minSup)
        {
          frequentCandidates.add(candidates.get(i));
          //put the frequent itemset into the output file
          file_out.write(candidates.get(i) + "," + count[i]/(double)numTransactions + "\n");
        }
      }
      file_out.write("-\n");
      file_out.close();
    }
    //if error at all in this process, catch it and print the error messate
    catch(IOException e)
    {
      System.out.println(e);
    }
    //clear old candidates
    candidates.clear();
    //new candidates are the old frequent candidates
    candidates = new Vector<String>(frequentCandidates);
    frequentCandidates.clear();
  }
}
