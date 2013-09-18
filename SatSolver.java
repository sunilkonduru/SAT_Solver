import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;
import java.util.Vector;
public class SatSolver {

	private  HashMap<Integer,Integer> clauseTruthValues= new HashMap<Integer,Integer>();
	private  int _totalNumberOfVariables;
	private  int _totalNumberOfClauses;
	ArrayList<ArrayList<Integer>> clause=new ArrayList<ArrayList<Integer>>();
	ArrayList<ArrayList<Integer>> copyClause=new ArrayList<ArrayList<Integer>>();
	ArrayList<Integer> outputLiterals=new ArrayList<Integer>();
	public void scanInput(String fileName) throws FileNotFoundException
	{
		File file= new File(fileName);
		Scanner scan= new Scanner(file);
		ArrayList<Integer> aList;
		
		int clauseNumber=0;
		while(scan.hasNextLine())
		{
			String line=scan.nextLine();
			if(line.startsWith("c"))
			{
				continue;
				
			}
			else if(line.startsWith("p"))
			{
				String delimiter="[ ]+";
				String[] countVariables=line.split(delimiter);
				if(countVariables[1].equals("cnf"))
				{
					_totalNumberOfVariables=Integer.parseInt(countVariables[2]);
					_totalNumberOfClauses=Integer.parseInt(countVariables[3]);
				}
		        
			}
			else
			{
			   String delimiter="[ ]+";
			   String[] variables=line.split(delimiter);
			   
			  int selectedVariable;
			  int countOfVariables=0;
			  aList=new ArrayList<Integer>();
			   while(true)
			   {
				  selectedVariable=Integer.parseInt(variables[countOfVariables]);
				  countOfVariables++;
				  if(selectedVariable!=0)
				  {
				  aList.add(selectedVariable);
				  }
				  else
				  {
					  clause.add(aList);
					  copyClause.add(aList);
					  break;
				  }
				  
			   }
			  
			   
			}
		}
		
	}
	public int checkUnitPropogation(ArrayList<ArrayList<Integer>> anyClause,HashMap<Integer,Integer> truthValues)
	{
		
		ArrayList<Integer> arrayList=new ArrayList<Integer>();
		int returnUnit=-1;
		for(int i=0;i<anyClause.size();i++)
		{
			
			if(anyClause.get(i).size()==1)
			{
				returnUnit=anyClause.get(i).get(0);
				for(int k=i;k<anyClause.size();k++)
				{
				for(int j=0;j<anyClause.get(i).size();j++)
				{
					if(anyClause.get(k).size()==1&&(anyClause.get(k).get(j)==-returnUnit))
					{
						return -600000;
						
					}
				}
				}
				truthValues.put(returnUnit, 1);
				anyClause.remove(i);
				return returnUnit;
			}
		}
		
		return 0;
	}
	public boolean computeUnitClause(ArrayList<ArrayList<Integer>> anyClause,int unit)
	{
		boolean testVariableClause=false;
		boolean testVariable=false;
		if(anyClause.size()!=0)
		{
			for(int i=0;i<anyClause.size();i++)
			{
				if(anyClause.get(i).size()>1)
				{
				for(int j=0;j<anyClause.get(i).size();j++)
				{
					
					if(unit==anyClause.get(i).get(j))
					{
						testVariableClause=true;
						anyClause.remove(i);
						if(testVariableClause&&i==0)
						{
							i=-1;
							testVariableClause=false;
						}
						if(testVariableClause&&i>0)
						{
							i=i-1;
							testVariableClause=false;
						}
						
						break;
						
						
					}
					else if(unit==(-1)*(anyClause.get(i).get(j)))
					{  
						testVariable=true;
						anyClause.get(i).remove(j);
						if(anyClause.get(i).size()==0)
						{
							return false;
						}
						if(testVariable&&j==0)
						{
							j=-1;
							testVariable=false;
						}
						
						if(testVariable&&j>0)
						{
							j=j-1;
							testVariable=false;
						}
						
						
					}
				}
			}	
		}
		}
		return true;
	}
	public boolean unitPropogation(ArrayList<ArrayList<Integer>> anyClause,HashMap<Integer,Integer> truthValues)
	{
		boolean satisfiabilityCheck=true;
		int unit=checkUnitPropogation(anyClause,truthValues);
		while(unit!=0)
		{
			if(unit==-600000)
	    	{
			 
			 satisfiabilityCheck=false;
			 return satisfiabilityCheck; 
		    }
		 satisfiabilityCheck=computeUnitClause(anyClause,unit);
		 if(satisfiabilityCheck)
		 {
			 unit=checkUnitPropogation(anyClause,truthValues);
		 }
		 else
		 {
			 break;
		 }
		}
		return satisfiabilityCheck;
	}

	public boolean pureLiteral(ArrayList<ArrayList<Integer>> anyClause,HashMap<Integer,Integer> truthValues)
	{
		int key=0;
		HashMap<Integer,Integer> positive=new HashMap<Integer,Integer>();
		HashMap<Integer,Integer> negative=new HashMap<Integer,Integer>();
		boolean returnbool=false;
		
		for(int i=0;i<anyClause.size();i++)
		{
			for(int j=0;j<anyClause.get(i).size();j++)
			{
				key=anyClause.get(i).get(j);
				if(key>0)
				{
					positive.put(key, 1);
				}
				else if(key<0)
				{
					negative.put(Math.abs(key), 1);
				}
				
			}
		}
		for(int i=0;i<anyClause.size();i++)
		{
			for(int j=0;j<anyClause.get(i).size();j++)
			{
				key=anyClause.get(i).get(j);
				if(positive.containsKey(Math.abs(key)))
				{
					if(!negative.containsKey(Math.abs(key)))
					{
						returnbool=true;
						positive.remove(key);
						truthValues.put(key,1);
						for(int k=0;k<anyClause.size();k++)
						{
							if(anyClause.get(k).contains(key))
							{
								anyClause.remove(k);
								k=k-1;
							}
						}
						return returnbool;
					}
				}
				if(!positive.containsKey(Math.abs(key)))
				{
					if(negative.containsKey(Math.abs(key)))
					{
						returnbool=true;
						negative.remove(Math.abs(key));
						truthValues.put(key,1);
						for(int k=0;k<anyClause.size();k++)
						{
							if(anyClause.get(k).contains(key))
							{
								anyClause.remove(k);
								k=k-1;
							}
						}
						return returnbool;
					}
					
				}
				
			}
		}
		return returnbool;
	}
	public boolean computeForSatisfiability(ArrayList<ArrayList<Integer>> anyClause,int value,HashMap<Integer,Integer> truthValues)
	{
		boolean testVariableClause=false;
		boolean testVariable=false;
		boolean checkSatisfiability=false;
		for(int i=0;i<anyClause.size();i++)
		{
			for(int j=0;j<anyClause.get(i).size();j++)
			{
				
				if(value==anyClause.get(i).get(j))
				{
					testVariableClause=true;
					anyClause.remove(i);
					if(testVariableClause&&i==0)
					{
						i=-1;
						testVariableClause=false;
					}
					if(testVariableClause&&i>0)
					{
						i=i-1;
						testVariableClause=false;
					}
					
					break;
					
					
				}
				else if(value==(-1)*anyClause.get(i).get(j))
				{  
					testVariable=true;
					anyClause.get(i).remove(j);
					if(testVariable&&j==0)
					{
						j=-1;
						testVariable=false;
					}
					
					if(testVariable&&j>0)
					{
						j=j-1;
						testVariable=false;
					}
					
					
				}
			}
		}
		checkSatisfiability=unitPropogation(anyClause,truthValues);
		if(checkSatisfiability==false)
			return checkSatisfiability;
		boolean pureSatisfy=true;
		while(pureSatisfy)
		{
		 pureSatisfy=pureLiteral(anyClause,truthValues);
		}
		clauseTruthValues=truthValues;
		int totalElements=0;
		int key1;
		int value1;
		HashMap<Integer,Integer> tempTruthValues=new HashMap<Integer,Integer>();
		for(Map.Entry<Integer, Integer> entry:truthValues.entrySet())
	    {
	    	key1=entry.getKey();
	    	value1=entry.getValue();
	    	tempTruthValues.put(key1, value1);
	    }

		for(int i=0;i<anyClause.size();i++)
		{
			totalElements+=anyClause.get(i).size();
		}
		ArrayList<ArrayList<Integer>> tempArrayList=new ArrayList<ArrayList<Integer>>();
		if(totalElements>anyClause.size())
		{
			ArrayList<Integer> aList;
			for(int i=0;i<anyClause.size();i++)
			{
				aList=new ArrayList<Integer>();
				int alValue;
				for(int j=0;j<anyClause.get(i).size();j++)
				{
					alValue=anyClause.get(i).get(j);
					aList.add(alValue);
				}
				tempArrayList.add(aList);
			}
		}
		if(anyClause.size()>0)
		{	
		checkSatisfiability=splitAndBackTrack(anyClause,tempArrayList,truthValues,tempTruthValues);
		}
		return checkSatisfiability;
		
	}
	public boolean splitAndBackTrack(ArrayList<ArrayList<Integer>> anyClause,ArrayList<ArrayList<Integer>> tempArrayList,HashMap<Integer,Integer> truthValues,HashMap<Integer,Integer> tempTruthValues)
	{
		boolean checkSatisfiability;
		int value=0;
		for(int i=0;i<anyClause.size();i++)
		{
			if(anyClause.get(i).size()>1)
			{
				value=momHeuristic(anyClause);
				truthValues.put(value, 1);
				break;
			}
			
		}
		checkSatisfiability=computeForSatisfiability(anyClause,value,truthValues);
		if(checkSatisfiability)
		{
			return true;
		}
		else
		{
			value=-value;
			tempTruthValues.put(value, 1);
			checkSatisfiability=computeForSatisfiability(tempArrayList, value,tempTruthValues);
			if(checkSatisfiability)
			{
			  return true;
			}
			else
			{
				return false;
			}
		}
	}
	public int momHeuristic(ArrayList<ArrayList<Integer>> anyClause)
	{
		//find the maximum repetitive element in the minimum sized clause
		int min=10000;
	    int minIndex=0;
	    for(int i=0;i<anyClause.size();i++)
	    {
	    	if(min>anyClause.get(i).size())
	    	{
	    		min=anyClause.get(i).size();
	    		minIndex=i;
	    	}
	    }
	    //now you know the minimum size, find the most repetitive or maximum element in that minimum size\
	    //for doing that put all the elements of that minimum size in hashmap and associated count of variables
	    int maxVariable=0;
	    HashMap<Integer,Vector<Integer>> map=new HashMap<Integer,Vector<Integer>>();
	    
	    int key=0;
	    int incrementValue=0;
	    int largestValue=0;
	    int value1;
	    int value2;
	    Vector<Integer> signInformation;
	    for(int i=0;i<anyClause.size();i++)
	    {
	    	if(anyClause.get(i).size()==min)
	    	{
	    	    for(int j=0;j<anyClause.get(i).size();j++)
	    	    {
	    	    	key=anyClause.get(i).get(j);
	    	    	if(map.containsKey(Math.abs(key)))
	    	    	{
	    	    		signInformation=map.get(Math.abs(key));
	    	    		if(key>0)
	    	    		{
	    	    			value1=signInformation.get(0);
	    	    			value1=value1+1;
	    	    			value2=signInformation.get(1);
	    	    			incrementValue=value1+value2;
	    	    			if(largestValue<incrementValue)
	    	    			{
	    	    				largestValue=incrementValue;
	    	    				if(value1>value2)
	    	    				{
	    	    				 maxVariable=Math.abs(key);
	    	  
	    	    				}
	    	    				else
	    	    				{
	    	    					maxVariable=(-1)*Math.abs(key);
	    	    				}
	    	    			}
	    	    			signInformation.set(0, value1);
	    	    			map.put(Math.abs(key),signInformation);
	    	    		}
	    	    		else if(key<0)
	    	    		{
	    	    			value1=signInformation.get(0);
	    	    			value2=signInformation.get(1);
	    	    			value2+=1;
	    	    			incrementValue=value1+value2;
	    	    			if(largestValue<incrementValue)
	    	    			{
	    	    				largestValue=incrementValue;
	    	    				if(value1>value2)
	    	    				{
	    	    				 maxVariable=Math.abs(key);
	    	    				}
	    	    				else
	    	    				{
	    	    					maxVariable=(-1)*Math.abs(key);
	    	    				}
	    	    			}
	    	    			signInformation.set(1, value2);
	    	    			map.put(Math.abs(key),signInformation);
	    	    		}
	    	    	}
	    	    	else
	    	    	{
	    	    		signInformation=new Vector<Integer>();
	    	    		if(key>0)
	    	    		{
	    	    			signInformation.add(1);
	    	    			signInformation.add(0);
	    	    			if(largestValue==0)
	    	    			{
	    	    				largestValue=1;
	    	    				maxVariable=Math.abs(key);
	    	    			}
	    	    			map.put(Math.abs(key),signInformation);
	    	    		}
	    	    		else if(key<0)
	    	    		{
	    	    			signInformation.add(0);
	    	    			signInformation.add(1);
	    	    			if(largestValue==0)
	    	    			{
	    	    				largestValue=1;
	    	    				maxVariable=(-1)*Math.abs(key);
	    	    			}
	    	    			map.put(Math.abs(key), signInformation);
	    	    		}
	    	
	    	    	}
	    	    }
	    	}
	    }
	    	    		
		return maxVariable;
	}
	public static void main(String args[])throws FileNotFoundException
	{
		SatSolver sat=new SatSolver();
		sat.scanInput(args[0]);
        boolean checkSatisfiability=false;
		Integer variable;
		Integer falseValue=0;
		
		for(int i=0;i<sat.clause.size();i++)
		{
			for(int j=0;j<sat.clause.get(i).size();j++)
			{
				variable=sat.clause.get(i).get(j);
				if(!sat.clauseTruthValues.containsKey(variable))
				{
					sat.clauseTruthValues.put(variable, falseValue);
				}
			}
		}
		sat.unitPropogation(sat.clause,sat.clauseTruthValues);
		if(sat.clause.size()==0)
		{
			checkSatisfiability=true;
		}
        boolean pureSatisfy=true;
        while(pureSatisfy)
            pureSatisfy=sat.pureLiteral(sat.clause,sat.clauseTruthValues);
        if(sat.clause.size()==0)
        {
        	checkSatisfiability=true;
        }
		HashMap<Integer,Integer> tempTruthValues=new HashMap<Integer, Integer>();
		int key;
		int value;
		if(sat.clause.size()>0)
		{
			ArrayList<ArrayList<Integer>> tempArrayList=new ArrayList<ArrayList<Integer>>();
			ArrayList<Integer> aList;
			
			for(Map.Entry<Integer, Integer> entry:sat.clauseTruthValues.entrySet())
		    {
		    	key=entry.getKey();
		    	value=entry.getValue();
		    	tempTruthValues.put(key, value);
		    }
			
			for(int i=0;i<sat.clause.size();i++)
			{
				aList=new ArrayList<Integer>();
				
				int alValue;
				for(int j=0;j<sat.clause.get(i).size();j++)
				{
					alValue=sat.clause.get(i).get(j);
					aList.add(alValue);
				}
				tempArrayList.add(aList);
			}
			checkSatisfiability=sat.splitAndBackTrack(sat.clause, tempArrayList,sat.clauseTruthValues,tempTruthValues);
		}
		System.out.println("c solution for previous problem");
		if(checkSatisfiability)
		{
			System.out.println("s SATISFIABLE");
			System.out.print("v ");
			for(Map.Entry<Integer, Integer> entry:sat.clauseTruthValues.entrySet())
		    {
		    	key=entry.getKey();
		    	value=entry.getValue();
		    	if(value==1)
		    	{
		    		System.out.print(key+" ");
		    	}
		    }
			System.out.print(0);
			System.out.println();
		}
		
		
		if(!checkSatisfiability)
		{
			System.out.println("s UNSATISFIABLE");
		}
		
	}
}