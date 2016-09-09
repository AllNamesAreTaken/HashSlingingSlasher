package com.forfun.wdh.passwordapp.service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class PasswordService {
	// PRECODE
	// Salt
	// Password
	// Character Choice
	// Should make profiles for different characters
	// (*) Allowed Characters
	// [v] Special Characters
	// [v] Numbers
	// [v] Upper Characters
	// [v] Lower Characters
	// ( ) Custom [abcdefghijklmnopqrstuvwxyz...]
	// CODE
	// Input word

	public static String special2 = "\\/'`"; // Possibly illegal for most
	// password systems? (guessing)
	public static String special = "@%+!#$^?:.(){}[]~-_";
	public static String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static String lowercase = "abcdefghijklmnopqrstuvwxyz";
	public static String number = "0123456789";
	private byte[] salt;
	private String password;
	private String charProfile;
	private String saltLocation;
	private boolean hasSalt;

	public PasswordService() {
		hasSalt = false;
		salt = new byte[64];
		charProfile = special + number + lowercase + uppercase + special2;
	}

	public PasswordService(byte[] salt, String password) {
		hasSalt = true;
		this.salt = salt;
		this.password = password;
		charProfile = special + number + lowercase + uppercase + special2;
	}

	public PasswordService(byte[] salt, String password, String charProfile) {
		hasSalt = true;
		this.salt = salt;
		this.password = password;
		this.charProfile = charProfile;
	}

	public String generatePassblock(String passphrase, int rounds, String fileLocation, String fileName) throws MissingSaltException, MissingPasswordException
	{
		if(fileLocation == null || fileName == null || fileLocation.equals("") || fileName.equals(""))
		{
			throw new MissingSaltException();
		}
		else
		{
			setSalt(fileLocation, fileName);
		}
		return generatePassblock(passphrase, rounds);
	}

	public String generatePassblock(String passphrase, int rounds) throws MissingSaltException, MissingPasswordException {
		String output = "";
		if(password == null || password.equals("")){
			throw new MissingPasswordException();
		}
		if(salt == null || hasSalt == false) {
			throw new MissingSaltException();
		}
		String compiled = password + passphrase;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			md.update(salt);
			byte[] bytes = md.digest(compiled.getBytes("UTF-8"));
			output = compileString512(bytes);
			for(int i = 1; i < rounds && i < 200; i++) {
				bytes = md.digest(output.getBytes("UTF-8"));
				output += compileString512(bytes);
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return output;
	}

	private String compileString512(byte[] bytes) {
		String output = "";
		for (int i = 0; i < bytes.length; i++) {
			output += charProfile.charAt(((0xFF & bytes[i]) % charProfile.length()));
		}
		return output;
	}

	public String generateSalt(String fileLocation, String fileName) {
		File saltDir = new File(fileLocation);
		if(!saltDir.exists()) {
			return "No Directory";
		}
		Date curDate = new Date();
		DateFormat dateForm = new SimpleDateFormat("yyyyMMddHHmmss");
		if(fileName.equals("")) {
			fileName = "salt" + dateForm.format(curDate) + ".salt";
		}
		File saltLoc = new File(fileLocation + '/' + fileName);
		String saltFileLoc = "";
		if(!saltLoc.exists()) {
			saltFileLoc = fileLocation + '/' + fileName;
		}
		File saltFile = new File(saltFileLoc);
		try {
			saltFile.createNewFile();
			byte[] newSalt = new byte[8 * 16];
			Random ran = new Random();
			ran.nextBytes(newSalt);
			DataOutputStream dos = new DataOutputStream(new FileOutputStream(saltFile));
			dos.write(newSalt);

		} catch (IOException e) {
			e.printStackTrace();
		}

		hasSalt = true;
		return fileName;
	}

	public byte[] getSalt() {
		return salt;
	}

	public void setSalt(byte[] salt) {
		this.salt = salt;
		hasSalt = true;
	}

	public void setSalt(String saltLocation, String saltName) {
		File saltLoc = new File(saltLocation + '/' + saltName);
		if (saltLoc.exists() && saltLoc.isFile()) {
			try {
				DataInputStream dis = new DataInputStream(new FileInputStream(saltLoc));
				dis.readFully(salt);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		hasSalt = true;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCharProfile() {
		return charProfile;
	}

	public void setCharProfile(String charProfile) {
		this.charProfile = charProfile;
	}
}