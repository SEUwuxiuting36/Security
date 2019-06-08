package processing;

/**
 * @author: Wu Xiuting
 */
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
	/**
	 * ����char����,16���ƶ�Ӧ�Ļ����ַ�
	 */
	private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
			'e', 'f' };

	/**
	 * md5����
     * @param str ��Ҫ���ܵ�����
     * @return ���ܽ��128λ
     * @author sucb
     * @date 2017��7��26������5:12:16
	 */
	public static String getMD5Hash(String str) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(str.getBytes());
			StringBuilder sb = new StringBuilder();
			byte[] bytes = messageDigest.digest();
			for (byte b : bytes) {
				sb.append(HEX_DIGITS[(b & 0xf0) >> 4]).append(HEX_DIGITS[(b & 0x0f)]);
					}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
}
