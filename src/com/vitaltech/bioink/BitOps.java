import java.math.BigInteger;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public class BitOps {

	public BitOps() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Set<Byte> bytes = new LinkedHashSet<Byte>();

		bytes.add((byte)  1);	// 0000 0001 => 00000 00001
		bytes.add((byte)  4);	// 0000 0100 => 00000 00001
		bytes.add((byte) 16);	// 0001 0000 => 00000 00001
		bytes.add((byte) 64);	// 0100 0000 => 00000 00001
		bytes.add((byte)  0);	// 0000 0000

		bytes.add((byte)  3);	// 0000 0011 => 00000 00011
		bytes.add((byte) 12);	// 0000 1100 => 00000 00011
		bytes.add((byte) 48);	// 0011 0000 => 00000 00011
		bytes.add((byte)192);	// 1100 0000 => 00000 00011
		bytes.add((byte)  0);	// 0000 0000

		bytes.add((byte)  0);	// 0000 0000 => 10000 00000
		bytes.add((byte)  2);	// 0000 0010 => 10000 00000
		bytes.add((byte)  8);	// 0000 1000 => 10000 00000
		bytes.add((byte) 32);	// 0010 0000 => 10000 00000
		bytes.add((byte)128);	// 1000 0000

		bytes.add((byte)  1);	// 0000 0001 => 10000 00001
		bytes.add((byte)  4);	// 0000 0110 => 10000 00001
		bytes.add((byte) 16);	// 0000 1000

//		for(int i=0; i<8; ++i){
//			System.out.println("\ni=" + i + " => " + (0x1 << i) + "\t 2^ => " + 
//					                         BigInteger.valueOf( 1 << i     ).toString(2) );
//			System.out.print(    "& 1 => " + BigInteger.valueOf((1 << i) & 1).toString(2) );
//			System.out.print(  "\t| 1 => " + BigInteger.valueOf((1 << i) | 1).toString(10));
//			System.out.println("\t| 1 => " + BigInteger.valueOf((1 << i) | 1).toString(2) );
//		}
		
		Iterator<Byte> itr = bytes.iterator();
		while(itr.hasNext()){
			byte it = itr.next();
			
			System.out.print( BigInteger.valueOf( (it & 2047) & 1023 ).toString(10) );
			
//			String lead = "0000000000".substring(0, (10-BigInteger.valueOf(it).toString(2).length()));
//			System.out.println("\t" + lead + BigInteger.valueOf(it).toString(2) );

			System.out.println("\t"  + BigInteger.valueOf( (it & 2047) & 1023 ).toString(2) );
		}
	}
	
	public static List<Integer> zephyParse(Set<Byte> samples) {
		List<Integer> decoded = new ArrayList<Integer>();
//								//       128,64,32,16  8,4,2,1
//		decoded.add(3); 		// debug 0000 0011
//		decoded.add(6); 		// debug 0000 0110
//		decoded.add(0x1 << 6);
		
		decoded.add(1);
		
		return decoded;
	}

}
