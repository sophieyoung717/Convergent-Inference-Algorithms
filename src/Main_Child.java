import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Hashtable;
import java.util.Random;

public class Main_Child {
	public static void main(String[] args) {
		Main_Base test = new Main_Base(19, 19, "child.dsc", "veInputChild.txt", "Child_VE_1.txt", "Child_Leaky_2.txt",
				"Child_gibbs_2.txt", "Child_CyclicSampling_2.txt", "Child_RandomSampling_2.txt", 10, // Sampling
																										// interval (in
																										// ms) --
																										// Sampling
																										// processes
																										// emit samples
																										// once every
																										// period
				1000 // Cutoff interval (in ms) -- Abort sampling after this amount of time
		);

		test.doVE();
		test.doLeakyJoins();
		test.doGibbsSampling();
		test.doCyclicSampling();
		test.doRandomSampling();
	}
}
