package cn.hyperchain.hitoken.ble;

public class HexToAscii {

	public String convertStringToHex(String str) {

		char[] chars = str.toCharArray();

		StringBuffer hex = new StringBuffer();
		for (int i = 0; i < chars.length; i++) {
			hex.append(Integer.toHexString((int) chars[i]));
		}

		return hex.toString();
	}

	public String convertHexToString(String hex) {

		StringBuilder sb = new StringBuilder();
		StringBuilder temp = new StringBuilder();

		for (int i = 0; i < hex.length() - 1; i += 2) {

			// grab the hex in pairs
			String output = hex.substring(i, (i + 2));
			// convert hex to decimal
			int decimal = Integer.parseInt(output, 16);
			// convert the decimal to character
			sb.append((char) decimal);

			temp.append(decimal);
		}

		return sb.toString();
	}

	//測試
	public static void main(String[] args) {

		HexToAscii strToHex = new HexToAscii();
		System.out.println("\n-----ASCII码转换为16进制 -----");
		String str = "0";
		System.out.println("字符串: " + str);
		String hex = strToHex.convertStringToHex(str);
		System.out.println("转换为16进制 : " + hex);

		System.out.println("\n***** 16进制转换为ASCII *****");
		System.out.println("Hex : " + hex);
		System.out.println("ASCII : " + strToHex.convertHexToString(hex));

	}
}