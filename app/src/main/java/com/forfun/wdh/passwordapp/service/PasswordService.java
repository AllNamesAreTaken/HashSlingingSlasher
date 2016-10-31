package com.forfun.wdh.passwordapp.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

import com.forfun.wdh.passwordapp.exceptions.MissingCharProfileException;
import com.forfun.wdh.passwordapp.exceptions.MissingDirectoryException;
import com.forfun.wdh.passwordapp.exceptions.MissingPasswordException;
import com.forfun.wdh.passwordapp.exceptions.MissingSaltException;

public class PasswordService implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public static final String special = "@%+!#$^?:.(){}[]~-_\\/'`";
	public static final String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String lowercase = "abcdefghijklmnopqrstuvwxyz";
	public static final String number = "0123456789";
	public static final String defaultCharProfile = special + number + uppercase + lowercase;
	public static final String defaultHashAlgorithm = "SHA-512";
	public static final int saltByteArraySize = 4096;

	private byte[] salt = new byte[saltByteArraySize];
	private String charProfile = defaultCharProfile;
	private String hashAlgorithm = defaultHashAlgorithm;
	private String baseDirectory;
	private String saltFileName;
	private String password;
	private int rounds = 4;

	private boolean saveSaltFileName = false;
	private boolean saveCharProfile = false;
	private boolean saveHashAlgorithm = false;
	private boolean savePassword = false;
	private boolean saveRounds = false;
//	private boolean saveBaseDirectory = false;

	// Constructors
	public PasswordService(String baseDirectory) {
		this.baseDirectory = baseDirectory;
	}

	// Getters&Setters

	public boolean isSaveSaltFileName() {
		return saveSaltFileName;
	}

	public boolean isSaveCharProfile() {
		return saveCharProfile;
	}

	public boolean isSaveHashAlgorithm() {
		return saveHashAlgorithm;
	}

	public boolean isSavePassword() {
		return savePassword;
	}

	public boolean isSaveRounds() {
		return saveRounds;
	}

	public void setSaveSaltFile(boolean saveSaltFileName) {
		this.saveSaltFileName = saveSaltFileName;
	}

	public void setSaveCharProfile(boolean saveCharProfile) {
		this.saveCharProfile = saveCharProfile;
	}

	public void setSaveHashAlgorithm(boolean saveHashAlgorithm) {
		this.saveHashAlgorithm = saveHashAlgorithm;
	}

	public void setSavePassword(boolean savePassword) {
		this.savePassword = savePassword;
	}

	public void setSaveRounds(boolean saveRounds) {
		this.saveRounds = saveRounds;
	}


	public byte[] getSalt() {
		return salt;
	}

	public String getPassword() {
		return password;
	}

	public String getCharProfile() {
		return charProfile;
	}

	public String getBaseDirectory() {
		return baseDirectory;
	}

	public String getSaltFileName() {
		return saltFileName;
	}

	public String getHashAlgorithm() {
		return hashAlgorithm;
	}

	public int getRounds() {
		return rounds;
	}

	public void setSalt(byte[] salt) {
		this.salt = salt;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setCharProfile(String charProfile) throws Exception {
		if(charProfile.length() == 0) {
			throw new Exception("Char profile cannot be 0 in length");
		}
		if(charProfile.length() > 256) {
			throw new Exception("Char profile cannot be longer than 256");
		}
		this.charProfile = charProfile;
	}

	public void setBaseDirectory(String saltFileLocation) {
		this.baseDirectory = saltFileLocation;
	}

	public void setSaltFileName(String saltFileName) {
		this.saltFileName = saltFileName;
	}

	public void setHashAlgorithm(String hashAlgorithm) {
		this.hashAlgorithm = hashAlgorithm;
	}

	public void setRounds(int rounds) {
		this.rounds = rounds;
	}

	// Create HashSlingingSlasher Directories
	public void createMissingHSSDirectories() throws MissingDirectoryException {
		File baseLocFile = new File(getBaseDirectory());
		if (!baseLocFile.isDirectory()) {
			throw new MissingDirectoryException();
		}
		// Create HashSlingingSlasher dir
		File hssd = new File(getBaseDirectory() + "/HashSlingingSlasher");
		if (!hssd.exists()) {
			hssd.mkdir();
		}
		// Create salt and settings dir
		File saltd = new File(getBaseDirectory() + "/HashSlingingSlasher/salt");
		File settd = new File(getBaseDirectory() + "/HashSlingingSlasher/settings");
		if (!saltd.exists()) {
			saltd.mkdir();
		}

		if (!settd.exists()) {
			settd.mkdir();
		}
	}

	// WriteNotes
	public void writeNotes(String notes) throws IOException {
		File notesFile = new File(getBaseDirectory() + "/HashSlingingSlasher/settings/notes.txt");
		notesFile.createNewFile();
		FileWriter nfw = new FileWriter(getBaseDirectory() + "/HashSlingingSlasher/settings/notes.txt", false);
		BufferedWriter writeStream = new BufferedWriter(nfw);
		writeStream.write(notes);
		writeStream.close();
	}

	// ReadNotes
	public String readNotes() throws IOException {
		File notesFile = new File(getBaseDirectory() + "/HashSlingingSlasher/settings/notes.txt");
		notesFile.createNewFile();
		FileReader nfr = new FileReader(getBaseDirectory() + "/HashSlingingSlasher/settings/notes.txt");
		BufferedReader readStream = new BufferedReader(nfr);
		StringBuilder sb = new StringBuilder();
		while (readStream.ready()) {
			sb.append(readStream.readLine() + "\n");
		}
		readStream.close();
		return sb.toString();
	}

	// SaveSettings
	public void saveSettings() throws IOException {
		File settings = new File(getBaseDirectory() + "/HashSlingingSlasher/settings/settings.dat");
		settings.delete();
		settings.createNewFile();
		FileWriter nfw = new FileWriter(getBaseDirectory() + "/HashSlingingSlasher/settings/settings.dat", false);
		BufferedWriter writeStream = new BufferedWriter(nfw);
		writeStream.write(1);
		writeStream.write(getBaseDirectory());
		writeStream.newLine();
		writeStream.write(2);
		if(isSaveSaltFileName())
		{
			writeStream.write(1);
		}
		else {
			writeStream.write(2);
		}
		writeStream.newLine();
		writeStream.write(3);
		if(isSaveCharProfile())
		{
			writeStream.write(1);
		}
		else {
			writeStream.write(2);
		}
		writeStream.newLine();
		writeStream.write(4);
		if(isSaveHashAlgorithm())
		{
			writeStream.write(1);
		}
		else {
			writeStream.write(2);
		}
		writeStream.newLine();
		writeStream.write(5);
		if(isSavePassword())
		{
			writeStream.write(1);
		}
		else {
			writeStream.write(2);
		}
		writeStream.newLine();
		writeStream.write(6);
		if(isSaveRounds())
		{
			writeStream.write(1);
		}
		else {
			writeStream.write(2);
		}
		writeStream.newLine();
		writeStream.close();
	}

	// LoadSettings
	public void loadSettings() throws IOException {
		FileReader nfr = new FileReader(getBaseDirectory() + "/HashSlingingSlasher/settings/settings.dat");
		BufferedReader readStream = new BufferedReader(nfr);
		while (readStream.ready()) {
			switch (readStream.read()) {
				case 1:
						setBaseDirectory(readStream.readLine());
					break;
				case 2:
					if(readStream.read() == 1) {
						setSaveSaltFile(true);
					}else {
						setSaveSaltFile(false);
					}
					break;
				case 3:
					if(readStream.read() == 1) {
						setSaveCharProfile(true);
					}else {
						setSaveCharProfile(false);
					}
					break;
				case 4:
					if(readStream.read() == 1) {
						setSaveHashAlgorithm(true);
					}else {
						setSaveHashAlgorithm(false);
					}
					break;
				case 5:
					if(readStream.read() == 1) {
						setSavePassword(true);
					}else {
						setSavePassword(false);
					}
				case 6:
					if(readStream.read() == 1) {
						setSaveRounds(true);
					}else {
						setSaveRounds(false);
					}
					break;
				default:
					break;
			}
		}
		readStream.close();
	}

	// SaveCache
	public void saveCache() throws IOException {
		File settings = new File(getBaseDirectory() + "/HashSlingingSlasher/settings/cache.dat");
		settings.delete();
		settings.createNewFile();
		FileWriter nfw = new FileWriter(getBaseDirectory() + "/HashSlingingSlasher/settings/cache.dat", false);
		BufferedWriter writeStream = new BufferedWriter(nfw);

		if(isSavePassword()) {
			writeStream.write(1);
			writeStream.write(getPassword());
			writeStream.newLine();
		}

		if(isSaveSaltFileName()) {
			writeStream.write(2);
			writeStream.write(getSaltFileName());
			writeStream.newLine();
		}

		if(isSaveCharProfile()) {
			writeStream.write(3);
			writeStream.write(getCharProfile());
			writeStream.newLine();
		}

		if(isSaveHashAlgorithm()) {
			writeStream.write(4);
			writeStream.write(getHashAlgorithm());
			writeStream.newLine();
		}

		if(isSaveRounds()) {
			writeStream.write(5);
			writeStream.write(getPassword());
			writeStream.newLine();
		}

		if(isSaveRounds()) {
			writeStream.write(6);
			writeStream.write(getRounds());
			writeStream.newLine();
		}

		writeStream.close();
	}

	// LoadCache
	public void loadCache() throws IOException {
		FileReader nfr = new FileReader(getBaseDirectory() + "/HashSlingingSlasher/settings/cache.dat");
		BufferedReader readStream = new BufferedReader(nfr);
		while (readStream.ready()) {
			switch (readStream.read()) {
				case 1:
					setPassword(readStream.readLine());
					break;
				case 2:
					setSaltFileName(readStream.readLine());
					break;
				case 3:
					try {
						setCharProfile(readStream.readLine());
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case 4:
					setHashAlgorithm(readStream.readLine());
					break;
				case 5:
					setPassword(readStream.readLine());
					break;
				case 6:
					setRounds(readStream.read());
					break;
				default:
					break;
			}
		}
		readStream.close();
	}

	// Load Salt File
	public void loadSaltFile() throws IOException {
		if (getBaseDirectory() == null) {
			throw new IOException("File Location is null");
		}
		if (getSaltFileName() == null || getSaltFileName().equals("")) {
			throw new IOException("You must give a filename");
		}
		File saltLoc = new File(getBaseDirectory() + "/HashSlingingSlasher/salt/" + getSaltFileName());
		if (saltLoc.exists() && saltLoc.isFile()) {
			FileInputStream fis = new FileInputStream(saltLoc);
			DataInputStream dis = new DataInputStream(fis);
			dis.readFully(getSalt());
			dis.close();
			fis.close();
		}
	}

	// Generate Password Method
	public String generatePassblock(String passphrase) throws MissingSaltException, MissingPasswordException,
			NoSuchAlgorithmException, MissingCharProfileException, IOException {
		loadSaltFile();
		byte[] genSalt = getSalt();

		// Checks
		if (getCharProfile() == null || getCharProfile().equals("")) {
			throw new MissingCharProfileException();
		}
		if (getPassword() == null || getPassword().equals("")) {
			throw new MissingPasswordException();
		}
		if (genSalt == null || Arrays.equals(genSalt, new byte[saltByteArraySize])) {
			throw new MissingSaltException();
		}

		// Initialize
		StringBuilder output = new StringBuilder();
		String seed = getPassword() + passphrase;
		MessageDigest md = MessageDigest.getInstance(getHashAlgorithm());
		md.update(Arrays.copyOfRange(genSalt, 0, 128));
		byte[] bytes = md.digest(seed.getBytes("UTF-8"));

		// Create
		output.append(convertByteArrayToString(bytes));
		for (int i = 1; i < getRounds() && i > 0; i++) {
			md.update(xorByteArrays(bytes, Arrays.copyOfRange(genSalt, (i * 128) % genSalt.length, ((i+1) * 128) % genSalt.length)));
			bytes = md.digest(seed.getBytes("UTF-8"));
			output.append(convertByteArrayToString(bytes));
		}
		return output.toString();
	}

	// Generate Salt Method
	public void createNewSaltFile(String filename) throws IOException {
		createNewSaltFile(filename, false);
	}

	public void createNewSaltFile(String fileName, boolean overwrite) throws IOException {
		// Check requirements
		if (getBaseDirectory() == null) {
			throw new IOException("File Location is null");
		}
		if (fileName == null || fileName.equals("")) {
			throw new IOException("You must give a filename");
		}
		if (!fileName.endsWith(".salt")) {
			fileName += ".salt";
		}

		// Create file
		File saltFile = new File(getBaseDirectory() + "/HashSlingingSlasher/salt/" + fileName);
		if (saltFile.exists()) {
			if (!overwrite) {
				throw new IOException("Overwrite is false but file exists");
			}
			saltFile.delete();
			saltFile.createNewFile();
		}

		byte[] newSalt = new byte[saltByteArraySize];
		Random ran = new Random();
		ran.nextBytes(newSalt);

		// Save to file
		FileOutputStream fos = new FileOutputStream(saltFile);
		DataOutputStream dos = new DataOutputStream(fos);
		dos.write(newSalt);
		dos.close();
		fos.close();

		// Replace previous filename
		setSaltFileName(fileName);
	}

	// Private
	private String convertByteArrayToString(byte[] bytes) {
		StringBuilder output = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			output.append(getCharProfile().charAt(((0xFF & bytes[i]) % getCharProfile().length())));
		}
		return output.toString();
	}

	private byte[] xorByteArrays(byte[] a, byte[] b) {
		int max = a.length;
		int min = b.length;
		boolean amax = true;
		if(max < b.length) {
			max = b.length;
			min = a.length;
			amax = false;
		}
		byte[] toReturn = new byte[max];
		for(int i = 0; i < min; i++) {
			toReturn[i] = (byte) ((byte)a[i] ^ (byte)b[i]);
		}
		if(amax) {
			for(int i = min; i < max; i++) {
				toReturn[i] = (byte) ((byte)a[i] ^ (byte)0x00);
			}
		}
		else {
			for(int i = min; i < max; i++) {
				toReturn[i] = (byte) ((byte)b[i] ^ (byte)0x00);
			}
		}
		return toReturn;
	}
}