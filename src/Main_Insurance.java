import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Hashtable;
import java.util.Random;




public class Main_Insurance {
	public static void main(String[] args){
		Main_Base test = new Main_Base(		
			26,
			19,
			"insurance.dsc",
			"veInputInsurance.txt",
			"Insurance_VE_1.txt",
			"Insurance_Leaky_2.txt",
			"Insurance_gibbs_2.txt",
			"Insurance_CyclicSampling_2.txt",
			"Insurance_RandomSampling_2.txt",
			600,                         // Sampling interval (in ms) -- Sampling processes emit samples once every period
			1000000                       // Cutoff interval (in ms) -- Abort sampling after this amount of time
		);
		
		// test.doVE();
		test.doLeakyJoins();
		// test.doGibbsSampling();
		// test.doCyclicSampling();
		// test.doRandomSampling();
	}
}
