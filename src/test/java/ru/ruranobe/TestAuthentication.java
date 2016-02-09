package ru.ruranobe;

import org.junit.Assert;
import org.junit.Test;

import ru.ruranobe.misc.Authentication;
import ru.ruranobe.misc.MD5;

public class TestAuthentication {

	private static final String TEST_PASSWORD = "abc123";

	@Test
	public void testMD5() {
		String md5Hash = MD5.crypt(TEST_PASSWORD);
		String currentPassword = MD5.crypt(TEST_PASSWORD);
		Assert.assertEquals(currentPassword, Authentication.getPassHash(Authentication.HASH_MD5, md5Hash, currentPassword));
	}

	@Test
	public void testMD5SaltMD5() {
		String md5Hash = MD5.crypt(TEST_PASSWORD);
		String currentPassword = "abc12345:d1c307ac690f17febd40732296df0c7f";
		Assert.assertEquals(currentPassword,
		                    Authentication.getPassHash(Authentication.HASH_MD5_SALT_MD5, md5Hash, currentPassword));
	}

	@Test
	public void testMD5SaltMD5Generate() {
		String md5Hash = MD5.crypt(TEST_PASSWORD);
		String newPassword = Authentication.getPassHash(Authentication.HASH_MD5_SALT_MD5, md5Hash, null);
		Assert.assertEquals(41, newPassword.length());
		Assert.assertEquals(8, newPassword.indexOf(":"));
		String[] passwordParts = newPassword.split(":");
		String password = passwordParts[0] + ":" + MD5.crypt(passwordParts[0] + '-' + md5Hash);
		Assert.assertEquals(password, newPassword);
	}
}
