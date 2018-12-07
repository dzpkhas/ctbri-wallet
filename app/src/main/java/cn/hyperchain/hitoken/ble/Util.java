package cn.hyperchain.hitoken.ble;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Util {
	//
	// this file is just for debug
	//
	private static String sExternalLogFilePath = null;
	public final static String DATA_DEVICE_ADDR = "data_device_addr";
	public final static String DATA_RESULT = "data_result";
	public final static String DATA_TITLE = "data_title";
	public final static String DATA_BOOL_LABEL = "data_bool_label";
	public final static String DATA_BOOL = "data_bool";
	public final static String DATA_BYTE1_LABEL = "data_byte1_label";
	public final static String DATA_BYTE1 = "data_byte1";
	public final static String DATA_BYTE2_LABEL = "data_byte2_label";
	public final static String DATA_BYTE2 = "data_byte2";
	public final static String DATA_BYTES1_LABEL = "data_bytes1_label";
	public final static String DATA_BYTES1 = "data_bytes1";
	public final static String DATA_CODE = "data_code";

	public static String hexstr(byte value) {
		return String.format("%02X", value);
	}

	public static String hexstr(byte data[], boolean spaceSplit) {
		StringBuilder stringBuilder;
		String format;
		if (null == data) {
			return "";
		}
		stringBuilder = new StringBuilder(data.length);
		if (spaceSplit) {
			format = "%02X ";
		} else {
			format = "%02X";
		}
		for (byte byteChar : data) {
			stringBuilder.append(String.format(format, (int) byteChar & 0xff));
		}
		return stringBuilder.toString().trim();
	}

	public static byte ch2hex(char ch) {
		if (ch >= '0' && ch <= '9') {
			return (byte) (ch - '0');
		}
		if (ch >= 'a' && ch <= 'f') {
			return (byte) (ch - 'a' + 10);
		}
		if (ch >= 'A' && ch <= 'F') {
			return (byte) (ch - 'A' + 10);
		}
		return (byte) 0;
	}

	public static byte hex2byte(String str) {
		char ch0, ch1;
		if (str.length() == 0) {
			return (byte) 0;
		}
		if (str.length() == 1) {
			str = "0" + str;
		}
		ch0 = str.charAt(0);
		ch1 = str.charAt(1);
		return (byte) ((ch2hex(ch0) << 4) | ch2hex(ch1));
	}

	public static byte[] hex2bytes(String str) {
		byte[] ret = null;
		int index, count;
		str = str.replaceAll(" ", "").replace("\r", "").replaceAll("\n", "");
		if (str.length() % 2 == 1) {
			str = str.substring(0, str.length() - 1) + "0"
					+ str.charAt(str.length() - 1);
		}
		count = str.length();
		ret = new byte[count / 2];
		for (index = 0; index < count; index += 2) {
			ret[index / 2] = (byte) ((byte) (ch2hex(str.charAt(index)) << 4) | (byte) ch2hex(str
					.charAt(index + 1)));
		}
		return ret;
	}

	public static void setLogFile(String logFilePath) {
		sExternalLogFilePath = logFilePath;
	}

	/**
	 * 
	 * * ��16�����ַ�ת�����ֽ����� * @param hex * @return
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

	public static String AddSpace(String msg) {

		StringBuilder str = new StringBuilder(msg.replace(" ", ""));

		int i = str.length() / 2;
		int j = str.length() % 2;

		for (int x = (j == 0 ? i - 1 : i); x > 0; x--) {
			str = str.insert(x * 2, " ");
		}
		return str.toString();
	}

	public static void log(String str) {
		java.text.SimpleDateFormat fmt;
		fmt = new java.text.SimpleDateFormat("MM-dd HH:mm:ss",
				java.util.Locale.CHINA);
		FileOutputStream os = null;
		if (null != sExternalLogFilePath) {
			try {
				os = new FileOutputStream(sExternalLogFilePath, true);
				os.write(fmt.format(new java.util.Date()).getBytes());
				os.write((" " + str).getBytes());
				os.write("\r\n".getBytes());
				os.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
				if (null != os) {
					try {
						os.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}
}
