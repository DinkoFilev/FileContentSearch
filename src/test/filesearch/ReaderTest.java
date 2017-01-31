package filesearch;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ReaderTest {
	static Reader reader;
	static File textFile;
	static File zipTmpFile;
	static String textToSearch;

	@BeforeClass
	public static void setUp() {
		textToSearch = "example";
		// create text file and write some text
		reader = new Reader();
		textFile = new File("TestFile.txt");
		zipTmpFile = new File("tmp.zip");
		
		if (!textFile.exists()) {
			try {
				textFile.createNewFile();
				writeTextToFile(textFile, textToSearch);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/*
	 * Test Zip and Inner zip reader with simple zip file
	 */
	@Test
	public void testZipJarReader() throws IOException {
		FileInputStream in = new FileInputStream(textFile);
		// out put file
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipTmpFile));
		// name the file inside the zip file
		out.putNextEntry(new ZipEntry(textFile.getName()));

		// buffer size
		byte[] b = new byte[1024];
		int count;

		while ((count = in.read(b)) > 0) {
			out.write(b, 0, count);
		}
		out.close();
		in.close();

		// TEST

		assertTrue(reader.zipJarReader(zipTmpFile, textToSearch));
	}

	/*
	 * Test txt reader with simple text file
	 */
	@Test
	public void testTxtReader() {

		//TEST
		assertTrue(reader.txtReader(textFile, textToSearch));

	}

	/*
	 * Clean all files used under tests
	 */
	@AfterClass
	public static void clearFiles() {
		if (textFile.exists()) {
			textFile.delete();
		}
		if (zipTmpFile.exists()) {
			zipTmpFile.delete();
		}
	}

	private static void writeTextToFile(File file, String text) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(textFile, "UTF-8");
			writer.println("Write to text file");
			writer.println(text);

		} catch (IOException e) {
			// do something
		} finally {
			if (writer != null) {
				writer.close();
			}
		}

	}

}
