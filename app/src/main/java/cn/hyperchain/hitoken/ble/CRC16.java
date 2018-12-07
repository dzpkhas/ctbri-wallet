package cn.hyperchain.hitoken.ble;

public class CRC16 {
	/**
	 * CRC-16/CCITT-FALSE x16+x12+x5+1 
	 * 
	 * info Name:CRC-16/CCITT-FAI Width:16 Poly:0x1021 Init:0xFFFF RefIn:False
	 * RefOut:False XorOut:0x0000
	 * 
	 * @param bytes
	 * @param length
	 * @return
	 */
	public static int crc_16_CCITT_False(byte[] bytes, int length) {
		int crc = 0xffff; // initial value
		int polynomial = 0x1021; // poly value
		for (int index = 0; index < bytes.length; index++) {
			byte b = bytes[index];
			for (int i = 0; i < 8; i++) {
				boolean bit = ((b >> (7 - i) & 1) == 1);
				boolean c15 = ((crc >> 15 & 1) == 1);
				crc <<= 1;
				if (c15 ^ bit)
					crc ^= polynomial;
			}
		}
		crc &= 0xffff;
		return crc;
	}

	/**
	 * 
	 * 
	 * @param str
	 * @return
	 */
	public static byte[] toBytes(String str) {
		if (str == null || str.trim().equals("")) {
			return new byte[0];
		}

		byte[] bytes = new byte[str.length() / 2];
		for (int i = 0; i < str.length() / 2; i++) {
			String subStr = str.substring(i * 2, i * 2 + 2);
			bytes[i] = (byte) Integer.parseInt(subStr, 16);
		}
		return bytes;
	}

	public static String getCrc16(String Key) {
		byte[] test = CRC16.toBytes(Key);
		return Integer.toHexString(CRC16.crc_16_CCITT_False(test, 0))
				.toUpperCase();
	}

	public static void main(String[] args) {
		System.out.println(getCrc16("123456780000"));
	}

}