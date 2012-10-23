
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


public class BitOps {

	public BitOps() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Set<Byte> bytes = new LinkedHashSet<Byte>();
		
		bytes.add((byte)  1);	// 0000 0001 => 00000 00001 =    1		 1
		bytes.add((byte)  4);	// 0000 0100 => 00000 00001 =    1		 2
		bytes.add((byte) 16);	// 0001 0000 => 00000 00001 =    1		 3
		bytes.add((byte) 64);	// 0100 0000 => 00000 00001 =    1		 4
		bytes.add((byte)  0);	// 0000 0000 ^

		bytes.add((byte)  3);	// 0000 0011 => 11000 00011 =  771		 5
		bytes.add((byte) 31);	// 0001 1111 => 11100 00111 =  903		 6
		bytes.add((byte)238);	// 1110 1110 => 01110 01110 =  462		 7
		bytes.add((byte) 28);	// 0001 1100 => 00111 11100 =  252		 8
		bytes.add((byte) 63);	// 0011 1111 ^

		bytes.add((byte) 48);	// 0011 0000 => 10001 10000 =  560		 9
		bytes.add((byte)  2);	// 0000 0010 => 10000 00000 =  512		10
		bytes.add((byte)  8);	// 0000 1000 => 10000 00000 =  512		11
		bytes.add((byte) 32);	// 0010 0000 => 10000 00000 =  512		12
		bytes.add((byte)128);	// 1000 0000 ^

		bytes.add((byte)255);	// 1111 1111 => 11111 11111 = 1023		13
		bytes.add((byte)195);	// 1100 0011 => 00001 10000 =   48		14
		bytes.add((byte)240);	// 1111 0000 => 11111 01111 = 1007		15
		bytes.add((byte) 62);	// 0011 1110 => 00111 10100 =  244		16
		bytes.add((byte) 61);	// 0011 1101 ^

		bytes.add((byte) 85);	// 0101 0101 => 01010 10101 = 341		17
		bytes.add((byte)169);	// 1010 1001 => 10101 01010 = 682		18 samples in 22.5 bytes
		bytes.add((byte)106);	// 0110 1010 ^	(junk 0110)

//		for(int i=0; i<8; ++i){
//			System.out.println("\ni=" + i + " => " + (0x1 << i) + "\t 2^ => " + 
//					                         BigInteger.valueOf( 1 << i     ).toString(2) );
//			System.out.print(    "& 1 => " + BigInteger.valueOf((1 << i) & 1).toString(2) );
//			System.out.print(  "\t| 1 => " + BigInteger.valueOf((1 << i) | 1).toString(10));
//			System.out.println("\t| 1 => " + BigInteger.valueOf((1 << i) | 1).toString(2) );
//		}
		
		Iterator<Byte> itr = bytes.iterator();
		int i=1;
		while(itr.hasNext()){
			int it = (itr.next() & 255);
			System.out.print((i++) + "> " + it + "\t: " + it );
			
//			System.out.print("\t" + BigInteger.valueOf( (it & 2047) & 1023 ).toString(10) );
			System.out.print("\t" + BigInteger.valueOf( it ).toString(10) );
			
			String lead = "00000000".substring(0, (8-BigInteger.valueOf(it).toString(2).length()));
			
			System.out.println("\t" + lead + BigInteger.valueOf(it).toString(2) );

//			System.out.print("\t" + BigInteger.valueOf( (it & 2047) & 1023 ).toString(2) );
//			System.out.println("\t"  + BigInteger.valueOf( it ).toString(2) );
		}
		
		List<Long> list = zephyParse(bytes);
		
		for(int j=0; j<list.size(); ++j){
			System.out.print((j+1) + "\t");
			System.out.print(
				BigInteger.valueOf(
					list.get(j)
				).toString(10) +
				"\t"
			);
			System.out.print(
				"0000000000".substring(0, (10-BigInteger.valueOf(list.get(j)).toString(2).length())) +
				BigInteger.valueOf(
						list.get(j)
					).toString(2)
			);
			System.out.print("\n");
		}
	}

	// Turn a Set of 8-bit Bytes into a List of 10-bit Longs
	public static List<Long> zephyParse(Set<Byte> samples) {
		List<Long> decoded = new ArrayList<Long>();
		Byte[] array = (Byte[])samples.toArray(new Byte[samples.size()]);

		int shifter = 1;
		for(int i=1; i<samples.size(); ++i){
			if(shifter != 5){
				long rightMask = ((long)255 << ((shifter-1) * 2)) & 255;
				long right = (((long)array[i-1]) & rightMask) >> ((shifter-1)*2);
				long leftMask = ((long)1 << (shifter * 2)) - 1;
				long left = ((((long)array[i]) & leftMask) << (8-((shifter-1)*2)));
				
				decoded.add(
					(left | right) & 1023
				);
			}

			shifter++;
			if(shifter > 5){
				shifter = 1;
			}
		}
		
		return decoded;
	}

}
