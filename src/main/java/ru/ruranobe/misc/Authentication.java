package ru.ruranobe.misc;

import org.apache.wicket.util.string.Strings;

import java.security.SecureRandom;

public class Authentication {

	private static final SecureRandom RANDOM = new SecureRandom();

	public static final int HASH_MD5 = 1;
	public static final int HASH_MD5_SALT_MD5 = 2;
	public static final int HASH_PBKDF2 = 3;

	public static final int ACTUAL_HASH_TYPE = HASH_MD5_SALT_MD5;

	/**
	 * Получает хеш пароля в соответствии с текущей версией пароля
	 * @param version версия хеша пароля
	 * @param password md5 хеш пароля для построения хеша
	 * @param currentPassword текущий пароль для построения хеша
	 * @return
	 */
	public static String getPassHash(Integer version, String password, String currentPassword) {
		String result;
		switch (version) {
			case HASH_MD5:
				result = password;
				break;
			case HASH_MD5_SALT_MD5:
				String salt;
				if (!Strings.isEmpty(currentPassword)) {
					String[] passwordParts = currentPassword.split(":");
					if (passwordParts.length == 2) {
						salt = passwordParts[0];
					} else {
						throw new UnsupportedOperationException("Inconsistent password type.");
					}
				} else {
					salt = Integer.toHexString(RANDOM.nextInt());
				}
				StringBuilder sb = new StringBuilder();
				sb.append(salt);
				sb.append('-');
				sb.append(password);
				result = salt + ":" + MD5.crypt(sb.toString());
				break;
			case HASH_PBKDF2:
				throw new UnsupportedOperationException("Not yet implemented.");
			default:
				throw new UnsupportedOperationException("Unsupported password hash.");
		}
		return result;
	}
}
