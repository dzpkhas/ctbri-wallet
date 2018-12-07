package cn.hyperchain.hitoken.ble;

import java.security.MessageDigest;

/**
 * @author weixin E-mail:weixin@rthitech.com.cn
 * @version 1.0
 */

public class Utils {


	/**
	 *
	 * @param src
	 * @return
	 */
	static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("0x");
		if (src == null || src.length <= 0) {
			return null;
		}
		char[] buffer = new char[2];
		for (int i = 0; i < src.length; i++) {
			buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
			buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
			System.out.println(buffer);
			stringBuilder.append(buffer);
		}
		return stringBuilder.toString();
	}

	/**
	 * 
	 * */
	public static byte[] hexStringToByte(String hex) {
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
		}
		return result;
	}

	private static byte toByte(char c) {
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}

	public static String str2HexStr(String str) {

		char[] chars = "0123456789ABCDEF".toCharArray();
		StringBuilder sb = new StringBuilder("");
		byte[] bs = str.getBytes();
		int bit;

		for (int i = 0; i < bs.length; i++) {
			bit = (bs[i] & 0x0f0) >> 4;
			sb.append(chars[bit]);
			bit = bs[i] & 0x0f;
			sb.append(chars[bit]);
			sb.append(' ');
		}
		return sb.toString().trim();
	}

	/**
	 */
	public static String hexStr2Str(String hexStr) {
		String str = "0123456789ABCDEF";
		char[] hexs = hexStr.toCharArray();
		byte[] bytes = new byte[hexStr.length() / 2];
		int n;

		for (int i = 0; i < bytes.length; i++) {
			n = str.indexOf(hexs[2 * i]) * 16;
			n += str.indexOf(hexs[2 * i + 1]);
			bytes[i] = (byte) (n & 0xff);
		}
		return new String(bytes);
	}

	public static String addZeroForNum(String str, int strLength) {
		int strLen = str.length();
		StringBuffer sb = null;
		while (strLen < strLength) {
			sb = new StringBuffer();
			sb.append("0").append(str);// ?ù×ó??0
			// sb.append(str).append("0"); //?ù????0
			str = sb.toString();
			strLen = str.length();
		}
		return str;
	}

	public static String hexAddSum(String number) {
		int m = 0;
		String sum = "";
		for (int i = 0; i < number.length() / 2; i++) {
			m = m + Integer.parseInt(number.substring(i * 2, i * 2 + 2), 16);
		}
		sum = addZeroForNum(Integer.toHexString(m), 2);
		if (sum.length() > 2) {
			sum = sum.substring(sum.length() - 2, sum.length());
		}
		return sum;
	}
	
	
	/*
	 * CR
	 */
	public static String crc16(String gprsstr) {
		try {
			int crc;
			int strlength, r;
			byte sbit;
			int tc;
			strlength = gprsstr.length();
			byte[] data = gprsstr.getBytes();
			crc = 0x0000FFFF;
			for (int i = 0; i < strlength; i++) {
				tc = (int) (crc >>> 8);
				crc = (int) (tc ^ data[i]);
				for (r = 0; r < 8; r++) {
					sbit = (byte) (crc & 0x00000001);
					crc >>>= 1;
					if (sbit != 0)
						crc ^= 0x0000A001;
				}
			}
			return Integer.toHexString(crc);
		} catch (Exception ex) {
			return "";
		}
	}

	public static String Make_CRC(byte[] data) {
	        byte[] buf = new byte[data.length];//
	        for (int i = 0; i < data.length; i++) {
	            buf[i] = data[i];
	        }
	        int len = buf.length;
	        int crc = 0xFFFF;//16??
	        for (int pos = 0; pos < len; pos++) {
	            if (buf[pos] < 0) {
	                crc ^= (int) buf[pos] + 256; // XOR byte into least sig. byte of
	                                                // crc
	            } else {
	                crc ^= (int) buf[pos]; // XOR byte into least sig. byte of crc
	            }
	            for (int i = 8; i != 0; i--) { // Loop over each bit
	                if ((crc & 0x0001) != 0) { // If the LSB is set
	                    crc >>= 1; // Shift right and XOR 0xA001
	                    crc ^= 0xA001;
	                } else
	                    // Else LSB is not set
	                    crc >>= 1; // Just shift right
	            }
	        }
	        String c = Integer.toHexString(crc);
	        if (c.length() == 4) {
	            c = c.substring(2, 4) + c.substring(0, 2);
	        } else if (c.length() == 3) {
	            c = "0" + c;
	            c = c.substring(2, 4) + c.substring(0, 2);
	        } else if (c.length() == 2) {
	            c = "0" + c.substring(1, 2) + "0" + c.substring(0, 1);
	        }
	        return c;
	    }

		
	public static String getSha1(String str) {
        
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',  
                'a', 'b', 'c', 'd', 'e', 'f' };  
        try {  
				 MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
				 mdTemp.update(str.getBytes("UTF-8"));  
				 byte[] md = mdTemp.digest();  
				 int j = md.length;  
				 char buf[] = new char[j * 2];  
                int k = 0;  
                for (int i = 0; i < j; i++) {  
                byte byte0 = md[i];  
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];  
                buf[k++] = hexDigits[byte0 & 0xf];  
        }  
            return new String(buf);
        } catch (Exception e) {
        return null;  
   }  
} 
	
	public static String convertHexToString(String hex) {

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
		// System.out.println(sb.toString());
		return sb.toString();
	}
}

