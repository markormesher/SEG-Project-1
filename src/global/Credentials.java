package global;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Credentials {

	private String username;
	private String password;

	public Credentials(String username, char[] password) {
		this.username = username;
		this.password = encryptPassword(password);
	}

	public Credentials(String usernameAndPassword) {
		String[] info = usernameAndPassword.split(Settings.USER_INFO_SEPARATOR);
		this.username = info[0];
		this.password = info[1];
	}

	public String toString() {
		return username + Settings.USER_INFO_SEPARATOR + password;
	}

	public String getUsername() {
		return username;
	}

	public boolean isValidLogin() {
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(Settings.PATH_TO_USERS_FILE));
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String[] info = line.split(Settings.USER_INFO_SEPARATOR);
				if (info[0].equals(username) && info[1].equals(password)) {
					return true;
				}
			}
		} catch (FileNotFoundException e) {
			return false;
		} finally {
			// good practise
			if (scanner != null) {
				scanner.close();
			}
		}
		return false;
	}

	private static String encryptPassword(char[] password) {
		String stringPassword = "";
		for (int i = 0; i < password.length; ++i) {
			stringPassword += password[i];
		}
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(stringPassword.getBytes());
			byte[] digest = md.digest();
			StringBuilder sb = new StringBuilder();
			for (byte b : digest) {
				sb.append(String.format("%02x", b & 0xff));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			// this pretty much will never happen
			System.out.println("MD5 missing");
		}
		return "";
	}

	public static boolean isUsernameValid(String username) {
		String pattern = "^[a-zA-Z0-9]*$";
		return username.matches(pattern);
	}

	public static boolean isUsernameTaken(String username) {
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(Settings.PATH_TO_USERS_FILE));
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String[] info = line.split(Settings.USER_INFO_SEPARATOR);
				if (info[0].equals(username)) {
					return true;
				}
			}
		} catch (FileNotFoundException e) {
			return false;
		} finally {
			// good practise
			if (scanner != null) {
				scanner.close();
			}
		}
		return false;
	}

	public static void signUp(String username, char[] password) {
		String encryptedPassword = encryptPassword(password);
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new FileOutputStream(new File(Settings.PATH_TO_USERS_FILE), true));
			writer.println(username + Settings.USER_INFO_SEPARATOR + encryptedPassword);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (writer != null) writer.close();
		}
	}
}
