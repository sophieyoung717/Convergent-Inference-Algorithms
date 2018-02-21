package CyclicSampling;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

public class Tools {

	public static Set<BigInteger> getPrimes(long[] arr) {
		Set<BigInteger> sets = new HashSet<BigInteger>();
		for (int i = 0; i < arr.length; i++) {
			Set<BigInteger> set = new HashSet<BigInteger>();
			getPrimes(new BigInteger("" + arr[i]), set);
			sets.addAll(set);
		}
		return sets;
	}

	public static BigInteger sqrt(BigInteger n) {
		BigInteger a = BigInteger.ONE;
		BigInteger b = new BigInteger(n.shiftRight(5).add(new BigInteger("8")).toString());
		while (b.compareTo(a) >= 0) {
			BigInteger mid = new BigInteger(a.add(b).shiftRight(1).toString());
			if (mid.multiply(mid).compareTo(n) > 0)
				b = mid.subtract(BigInteger.ONE);
			else
				a = mid.add(BigInteger.ONE);
		}
		return a.subtract(BigInteger.ONE);
	}

	public static void getPrimes(BigInteger value, Set<BigInteger> set) {
		boolean isFirst = true;
		BigInteger two = new BigInteger("2");
		while (value.mod(two).equals(BigInteger.ZERO)) {
			if (isFirst)
				set.add(two);
			value = value.divide(two);
		}
		for (int i = 3; sqrt(value).compareTo(new BigInteger("" + i)) >= 0; i = i + 2) {
			BigInteger I = new BigInteger("" + i);
			while (value.mod(I).equals(BigInteger.ZERO)) {
				set.add(I);
				value = value.divide(I);
			}
		}
		if (value.compareTo(two) > 0)
			set.add(value);
	}

	public static boolean isPrime(BigInteger value) {
		BigInteger two = new BigInteger("2");
		if (value.mod(two).equals(BigInteger.ZERO)) {
			return false;
		}
		for (int i = 3; sqrt(value).compareTo(new BigInteger("" + i)) >= 0; i = i + 2) {
			BigInteger I = new BigInteger("" + i);
			if (value.mod(I).equals(BigInteger.ZERO)) {
				return false;
			}
		}
		return true;
	}

	public static BigInteger getCoPrime(BigInteger value) {
		BigInteger result = value.multiply(value);
		Set<BigInteger> set = new HashSet<BigInteger>();
		getPrimes(value, set);
		// Long max=Long.MIN_VALUE;
		// for(BigInteger v:set){
		// if(max<v)max=v;
		// }
		// for(Long i=(Long)(max+1);i<value;i++){
		// if(isPrime(i)){
		// result=i;
		// break;
		// }
		// }
		BigInteger two = new BigInteger("2");
		BigInteger i = two;
		while (true) {
			if (isPrime(i) && !set.contains(i)) {
				result = i;
				break;
			}
			i = i.add(BigInteger.ONE);
		}
		return result;
	}

	public static BigInteger getA(long[] arr, BigInteger totalSamples) {
		BigInteger result = BigInteger.ONE;
		Set<BigInteger> set = getPrimes(arr);
		for (BigInteger s : set) {
			result = result.multiply(s);
		}
		BigInteger four = new BigInteger("4");
		if (totalSamples.mod(four).equals(BigInteger.ZERO) && !(result.mod(four).equals(BigInteger.ZERO))) {
			result = result.multiply(four);
		}
		return result.add(BigInteger.ONE);
	}

	public static void exchange(int[] arr, int i, int j) {
		int tmp = arr[i];
		arr[i] = arr[j];
		arr[j] = tmp;
	}

}
