package kmaru.jchord.test;

import java.io.PrintStream;
import java.net.InetAddress;
import java.net.URL;

import kmaru.jchord.Chord;
import kmaru.jchord.ChordException;
import kmaru.jchord.ChordNode;
import kmaru.jchord.Hash;

public class Main1 {

	public static final String HASH_FUNCTION = "SHA-1";

	public static final int KEY_LENGTH = 160;

	public static final int NUM_OF_NODES = 1000;

	public static void main(String[] args) throws Exception {

		PrintStream out = System.out;

		out = new PrintStream("result.log");

		long start = System.currentTimeMillis();

		String host = InetAddress.getLocalHost().getHostAddress();
		int port = 9000;

		Hash.setFunction(HASH_FUNCTION);
		Hash.setKeyLength(KEY_LENGTH);

		Chord chord = new Chord();
		for (int i = 0; i < NUM_OF_NODES; i++) {
			URL url = new URL("http", host, port + i, "");
			try {
				chord.createNode(url.toString());
			} catch (ChordException e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
		out.println(NUM_OF_NODES + " nodes are created.");

		for (int i = 0; i < NUM_OF_NODES; i++) {
			ChordNode node = chord.getSortedNode(i);
			out.println(node);
		}

		for (int i = 1; i < NUM_OF_NODES; i++) {
			ChordNode node = chord.getNode(i);
			node.join(chord.getNode(0));
			ChordNode preceding = node.getSuccessor().getPredecessor();
			node.stabilize();
			if (preceding == null) {
				node.getSuccessor().stabilize();
			} else {
				preceding.stabilize();
			}
		}
		out.println("Chord ring is established.");

		for (int i = 0; i < NUM_OF_NODES; i++) {
			ChordNode node = chord.getNode(i);
			node.fixFingers();
		}
		out.println("Finger Tables are fixed.");

		for (int i = 0; i < NUM_OF_NODES; i++) {
			ChordNode node = chord.getSortedNode(i);
			node.printFingerTable(out);
		}

		long end = System.currentTimeMillis();

		int interval = (int) (end - start);
		System.out.printf("Elapsed Time : %d.%d", interval / 1000,
				interval % 1000);
	}
}
