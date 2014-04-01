import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;


public class Discount {
	
	protected ArrayList<Character> vowels = new ArrayList<Character>();
	private String inputFile = null;
	private static int VOWELS = 0;
	private static int CONSONANTS = 1;
	
	boolean debugMode = false;

	public Discount(String inputFile) {
		this.inputFile = inputFile;
	}

	public static void main(String[] args) {
		if(args == null || args.length != 1) {
			throw new IllegalArgumentException("You should use a file path as the only argument!");
		}
		
		Discount discount = new Discount(args[0]);
		String[] customersPerLine;
		String[] productsPerLine;
		
		try {
			BufferedReader reader = discount.prepareWork();
			String currentLine;
			
			while((currentLine = reader.readLine()) != null) {
				String[] tmp = currentLine.split(";");
				
				customersPerLine = tmp[0].split(",");
				productsPerLine = tmp[1].split(",");
				
				double biggestSSForCurrentLine = discount.calculateBiggestSS(customersPerLine, productsPerLine);
				if(biggestSSForCurrentLine == -1)
					System.out.println("Bad input line detected!");
				else
					System.out.println(biggestSSForCurrentLine);
			}
		} catch (FileNotFoundException e) {
			System.out.println("Input file not found!");
			e.printStackTrace();
			return;
		} catch (IOException e) {
			System.out.println("Failed to read a line from input file!");
			e.printStackTrace();
			return;
		}
		
		System.out.print(discount.calculateSS("Jack Abraham", "iPad 2 - 4-pack"));
		
		
	}
	
	public BufferedReader prepareWork() throws FileNotFoundException {
		vowels.add('a');
		vowels.add('e');
		vowels.add('i');
		vowels.add('o');
		vowels.add('u');
		vowels.add('A');
		vowels.add('E');
		vowels.add('I');
		vowels.add('O');
		vowels.add('U');
		
		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		return reader;
	}
	
	protected double calculateBiggestSS(String[] customers, String[] products) {
		double biggestSSSoFar = 0;
		double[][] ssMatrix;
		boolean[] postionFlag = new boolean[products.length];
		ArrayList<Double> allPossibleSS = new ArrayList<Double>();
		int loopTime, m, n;
		
		if(customers.length < 1 || products.length < 1)
			return -1;
		
		ssMatrix = new double[customers.length][products.length];
		
		for(int i = 0; i < customers.length; i++) {
			for(int j = 0; j < products.length; j++) {
				ssMatrix[i][j] = calculateSS(customers[i], products[j]);
			}
		}
		
		if(customers.length == 1 || products.length == 1)
			return getMaximum(ssMatrix);
		
		if(customers.length > products.length) {
			m = products.length;
			n = customers.length;
		}
		else {
			m = customers.length;
			n = products.length;
		}
		
		
		
		return biggestSSSoFar;
	}
	
	protected double findBiggestSS(double[][] matrix, int m, int n) {
		double res = 0;
		boolean[] positionFlag = new boolean[n];
		ArrayList<Integer> tmp = new ArrayList<Integer>();
		ArrayList<ArrayList<Integer>> sss = new ArrayList<ArrayList<Integer>>();
		
		permute(positionFlag, sss, m, n, tmp);
		findBiggestSSFromPermutations(sss, matrix);

		return res;
	}
	
	protected double findBiggestSSFromPermutations(ArrayList<ArrayList<Integer>> sss, double[][] matrix) {
		double res = 0;
		
		for(ArrayList<Integer> permutation : sss) {
			double ssFromThisPermutation = 0;
			for(int i = 0; i < permutation.size(); i++) {
				ssFromThisPermutation += matrix[i][permutation.get(i)];
			}
			if(ssFromThisPermutation > res)
				res = ssFromThisPermutation;
		}
				
		return res;
	}
	
	protected void permute(boolean[] positionFlag, ArrayList<ArrayList<Integer>> sss, int m, int n, ArrayList<Integer> tmp) {
		if(m == 0) {
			sss.add(tmp);
			return;
		}
		
		while(positionFlag[n--] == false) {}
		tmp.add(n);
		positionFlag[n] = true;
		permute(positionFlag, sss, m - 1, n, tmp);
		tmp.remove(tmp.size() - 1);
		positionFlag[n] = false;		
	}
	
	protected double getMaximum(double[][] matrix) {
		double max = 0;
		
		for(int i = 0; i < matrix.length; i++) {
			for(int j = 0; j < matrix[0].length; j++) {
				if(matrix[i][j] > max)
					max = matrix[i][j];
			}
		}
		
		return max;
	}
	
	public double calculateSS(String customer, String product) {
		double ss = 0;
		
		if((getLettersAmount(product) % 2) == 0) {
			ss = counterForVowelsAndConsonants(customer, VOWELS) * 1.5;
		}
		else {
			ss = counterForVowelsAndConsonants(customer, CONSONANTS) * 1.5;
		}
		
		if(CheckForShareFactor(customer, product))
			ss = ss * 1.5;
		
		return ss;
	}
	
	protected int counterForVowelsAndConsonants(String name, int option) {
		int vowelsCounter = 0;
		int consonantsCounter = 0;
		
		for(int i = 0; i < name.length(); i++) {
			if(vowels.contains(name.charAt(i))) {
				vowelsCounter++;
			}
			else if(Character.isLetter(name.charAt(i)))
				consonantsCounter++;
		}
		
		if(option == VOWELS) {
			if(debugMode)
				System.out.println("DEBUG: There are " + vowelsCounter + " vowels in " + name);
			return vowelsCounter;
		}

		else if(option == CONSONANTS) {
			if(debugMode)
				System.out.println("DEBUG: There are " + consonantsCounter + " consonants in " + name);
			return consonantsCounter;
		}		
		else
			return 0;
	}
	
	protected int getLettersAmount(String name) {
		int res = 0;
		
		for(int i = 0; i < name.length(); i++) {
			if(Character.isLetter(name.charAt(i)))
				res++;
		}
		
		return res;
	}
	
	protected boolean CheckForShareFactor(String customer, String product) {
		boolean res = false;
		
		int lettersInCustomerNmae = getLettersAmount(customer);
		int lettersInProductNmae = getLettersAmount(product);
		
		for(int i = 2; i < ((lettersInCustomerNmae > lettersInProductNmae) ? lettersInProductNmae/2 : lettersInCustomerNmae/2); i++) {
			if(lettersInCustomerNmae % i == 0 && lettersInProductNmae % i == 0) {
				if(debugMode)
					System.out.println("DEBUG: The common factor of " + customer + " and " + product + " is " + i);
				res = true;
				return res;
			}
		}
		
		return res;
	}
}
