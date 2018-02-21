package CyclicSampling;

public interface adder<Key> {
    Key zero(); // Adding zero items
    Key add(Key lhs, Key rhs); // Adding two items
}