package filesearch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.poifs.filesystem.NotOLE2FileException;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

/**
 * Class contains many tools for reading different file formats supported
 * formats : {txt ,md ,gradle ,docx ,doc ,xml ,json ,rtf , properties,} also
 * read information inside archives {zip , jar}
 * 
 * @author Dinko Filev
 *
 */
public class Reader {
 
	/**
	 * Search for specific text in Zip and Jar archives
	 * 
	 * @return true if any file inside archives contains the searching text
	 */
	private boolean zipJarReader(File file, String textToSearch) {
		ZipFile zipFile = null;
		Scanner sc = null;
		InputStream stream = null;
		boolean containsText = false;
		try {
			zipFile = new ZipFile(file,Charset.forName("Cp437"));
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				if (containsText) {
					break;
				}
				ZipEntry zipEntry = entries.nextElement();
				stream = zipFile.getInputStream(zipEntry);
				sc = new Scanner(stream);
				if (zipEntry.getName().endsWith(".zip")) {
					containsText = readInnerZipFile(file, zipEntry.getName(), textToSearch);
					continue;
				}
				while (sc.hasNextLine()) {
					if (sc.nextLine().contains(textToSearch)) { // check for "textToSearch" at every line
						
						containsText = true;
						break;
					}
				}
			}
		} catch (ZipException e) {
			System.err.println("ERROR OPENING archive FILE : "+file.getName() );
			//e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Error occurred during the work with "+ file.getName());
			//e.printStackTrace();
		} finally {
			// clean up I/O streams
			if (zipFile != null || sc != null) {
				try {
					zipFile.close();
					sc.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return containsText;
	}

	/**
	 * Search for specific text inside inner zip archive Supported Format :
	 * {.zip}
	 * 
	 * @param zipFile
	 *            - outer zip file
	 * @param innerZipFileEntryName
	 *            - inner zip file name
	 * @param textToSearch
	 *            - text to search
	 * 
	 * @return true if any file inside archives contains the searching text
	 */
	private boolean readInnerZipFile(File zipFile, String innerZipFileEntryName, String textToSearch) {

		ZipFile outerZipFile = null;
		File tempFile = null;
		FileOutputStream tempOut = null;
		ZipFile innerZipFile = null;
		Scanner sc = null;
		boolean containsText = false;
		try {
			outerZipFile = new ZipFile(zipFile);
			tempFile = File.createTempFile("tempFile", "zip");
			tempOut = new FileOutputStream(tempFile);
			IOUtils.copy( //
					outerZipFile.getInputStream(new ZipEntry(innerZipFileEntryName)), //
					tempOut);
			innerZipFile = new ZipFile(tempFile);
			Enumeration<? extends ZipEntry> entries = innerZipFile.entries();
			while (entries.hasMoreElements()) {
				if (containsText) {
					break;
				}
				ZipEntry zipEntry = entries.nextElement();
				if (zipEntry.getName().endsWith(".zip")) {
					containsText = readInnerZipFile(tempFile, zipEntry.getName(), textToSearch);
					continue;
				}
				InputStream stream = innerZipFile.getInputStream(zipEntry);
				sc = new Scanner(stream);
				while (sc.hasNextLine()) {
					if (sc.nextLine().contains(textToSearch)) { // check for "textToSearch" at every line
						containsText = true;
						break;
					}
				}
				if (containsText) {
					break;
				}
			}

		} catch (IOException e) {
			System.err.println("ERROR OPENING inner ZIP FILE :"+ innerZipFileEntryName);
		//	e.printStackTrace();
		} finally {
			// clean up I/O streams
			try {
				if (sc != null) {
					sc.close();
				}
				if (outerZipFile != null)

					outerZipFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			IOUtils.closeQuietly(tempOut);
			if (tempFile != null && !tempFile.delete()) {
				// System.out.println("Could not delete " + tempFile);
			}
			try {
				if (innerZipFile != null)
					innerZipFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return containsText;
	}

	/**
	 * Search for the specific text in various text file formats Supported
	 * Formats : {txt ,md ,gradle ,xml ,json ,rtf , properties}
	 * 
	 * @param file
	 *            - text file
	 * @param textToSearch
	 *            - text to search
	 * @return true if text in file contains the searched text
	 */
	private boolean txtReader(File file, String textToSearch) {
		BufferedReader br = null;
		FileReader fr = null;
		boolean containsText = false;
		try {

			fr = new FileReader(file);
			br = new BufferedReader(fr);

			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				if (sCurrentLine.contains(textToSearch)) { // check for "textToSearch" at every line
					containsText = true;
					break;
				}
			}

		} catch (IOException e) {
			System.err.println("Error occurred during the work with "+ file.getName());
			//e.printStackTrace();

		} finally {
			// clean up I/O streams
			try {

				if (br != null)
					br.close();

				if (fr != null)
					fr.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}

		}
		return containsText;
	}

	/**
	 * Search for the specific text in the file Supported Formats : {.doc}
	 * Using Apache POI tools
	 * @param file
	 *            - file.doc
	 * @param searchedText
	 *            - text to search
	 * @return true if text in file contains the searched text
	 */
	private boolean readDocFile(File file, String searchedText) {
		boolean containsText = false;
		FileInputStream fis = null;
		HWPFDocument doc = null;
		WordExtractor we = null;
		try {
			fis = new FileInputStream(file.getAbsolutePath());

			doc = new HWPFDocument(fis);

			we = new WordExtractor(doc);

			String[] paragraphs = we.getParagraphText();

			for (String para : paragraphs) {  
				if (para.contains(searchedText)) { // check for "textToSearch" at every line
					containsText = true;
					break;
				}
			}
			fis.close();
		} catch (Exception e) {
			System.err.println("Error occurred during the work with "+ file.getName());
			//e.printStackTrace();
		} finally {
			// clean up I/O streams
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (doc != null) {
				try {
					doc.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (we != null) {
				try {
					we.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return containsText;

	}

	/**
	 * Search for the specific text in the file Supported Formats : {.docx}
	 * Using Apache POI tools
	 * @param file
	 *            - file.docx
	 * @param searchedText
	 *            - text to search
	 * @return true if text in file contains the searched text
	 */
	private boolean readDocxFile(File file, String searchedText) {
		boolean containsText = false;
		FileInputStream fis = null;
		XWPFDocument document = null;
		try {
			fis = new FileInputStream(file.getAbsolutePath());
			document = new XWPFDocument(fis);
			List<XWPFParagraph> paragraphs = document.getParagraphs();
			for (XWPFParagraph para : paragraphs) {
				if (para.getText().contains(searchedText)) { // check for "textToSearch" at every line
					containsText = true;
					break;
				}
			}
		} catch (Exception e) {
			System.err.println("Error occurred during the work with "+ file.getName());
			//e.printStackTrace();
		} finally {
			// clean up I/O streams
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (document != null) {
				try {
					document.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		return containsText;
	}

	/**
	 * Check the file format and choose the read method
	 * 
	 * @param entry
	 *            - file
	 * @param textToSearch
	 *            - text to search
	 * @return true if text in file contains the searched text
	 */
	boolean fileFormatCheck(File entry, String textToSearch) {
		String extension = "";
		if (entry.getName().lastIndexOf(".") != -1) {
			extension = entry.getName().toLowerCase().substring(entry.getName().lastIndexOf("."));
		}

		/*
		 * available file extensions
		 */

		boolean containsText = false;
		switch (extension) {

		case ".zip":
			containsText = zipJarReader(entry, textToSearch);
			break;
		case ".jar":
			containsText = zipJarReader(entry, textToSearch);
			break;
		case ".txt":
			containsText = txtReader(entry, textToSearch);
			break;
		case ".md":
			containsText = txtReader(entry, textToSearch);
			break;
		case ".gradle":
			containsText = txtReader(entry, textToSearch);
			break;
		case ".java":
			containsText = txtReader(entry, textToSearch);
			break;
		case ".docx":
			containsText = readDocxFile(entry, textToSearch);
			break;
		case ".doc":
			containsText = readDocFile(entry, textToSearch);
			break;
		case ".xml":
			containsText = txtReader(entry, textToSearch);
			break;
		case ".json":
			containsText = txtReader(entry, textToSearch);
			break;
		case ".rtf":
			containsText = txtReader(entry, textToSearch);
			break;
		case ".properties":
			containsText = txtReader(entry, textToSearch);
			break;

		}

		return containsText;
	}
	

}
