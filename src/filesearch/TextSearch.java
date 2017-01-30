package filesearch;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import filesearch.directories.Directory;
import filesearch.files.AbstractFile;
import filesearch.files.TextFile;

/**
 * This class represents the searching a specific text into a small folder
 * 
 * @author Dinko Filev
 *
 */
public class TextSearch {

	private Directory rootDirectory;
	private int folderNumbers;
 
	public TextSearch(String dir, String textToSearch) {
		folderNumbers = 0;
		prepareToSearch(dir, textToSearch);

	}

	/**
	 * Prepare to search in directories Check if directory not exist or it is
	 * file. return warning message Initialize the root directory (root
	 * directory is the starting directory) e.g /opt/files/ - files is the root
	 * of the search
	 * 
	 * @param dir
	 *            - the root directory
	 * @param textToSearch
	 *            - text to search
	 */
	private void prepareToSearch(String dir, String textToSearch) {

		// make path separator work on all systems
		if (dir.contains("/") || dir.contains("\\")) {
			dir = dir.replaceAll("/", Matcher.quoteReplacement(File.separator));
			dir = dir.replaceAll("\\\\", Matcher.quoteReplacement(File.separator));
		}
		File file = new File(dir);
		if (!file.exists() || file.isFile()) {
			System.err.println("Directory does not exist , or you are trying to enter a file");
			return;
		}
		/*
		 * Initialize root directory
		 */

		try {
			rootDirectory = new Directory(file.getName(), file.getAbsolutePath(), getSizeOfFolder(file.toPath()));

		} catch (IOException e) {
			System.err.println("Cannot initialize root directory : " + file.getName());
			System.err.println("Try with another directory");
			// e.printStackTrace();
			return;
		}
		searchInFiles(file, textToSearch, rootDirectory);
		
		for (int i = 0; i < folderNumbers; i++) {
			clearUnusedFolders(rootDirectory);
		}

		printResults(rootDirectory, 1);

	}

	/**
	 * Clear empty folders before print the result
	 * @param rootDirectory
	 */
	private void clearUnusedFolders(Directory rootDirectory) {

		Directory root = rootDirectory;
		TreeSet<AbstractFile> files = root.getDirectoryFiles();
		for (Iterator<AbstractFile> iterator = files.iterator(); iterator.hasNext();) {
			AbstractFile abstractFile = (AbstractFile) iterator.next();

			if (abstractFile.getFileType().equals(AbstractFile.FileType.DIRECTORY)) {
				if (((Directory) abstractFile).getDirectoryFiles().size() == 0) {
					iterator.remove();
					
					continue;
				}
				root = (Directory) abstractFile;
				clearUnusedFolders(root);

			}

		}

	}

	/**
	 * Search recursively into the filesystem tree
	 * 
	 * @param dir
	 *            - root directory
	 * @param textToSearch
	 *            - text to search
	 * @param rootDirectory
	 *            - object that represents the root directory
	 */
	private void searchInFiles(File dir, String textToSearch, Directory rootDirectory) {

		Reader reader = new Reader();
		//System.out.println(dir);  // print directories
		try {
			if (dir != null && (!dir.isFile())) {
				for (File entry : dir.listFiles()) {
					if (entry.isDirectory()) {
						Directory newRoot = null;
						try {
							newRoot = new Directory(entry.getName(), entry.getAbsolutePath(),
									getSizeOfFolder(dir.toPath()));
						} catch (IOException e) {
							System.err.print("Error occurred with initialize a inner root folder");
							// e.printStackTrace();

						}

						if (newRoot != null) {
							folderNumbers++;
							rootDirectory.addFile(newRoot);
						}

						searchInFiles(new File(entry.getAbsolutePath()), textToSearch, newRoot);

					} else {
						if (reader.fileFormatCheck(entry, textToSearch)) {
							AbstractFile abstractfile = new TextFile(entry.getName(), entry.getAbsolutePath(),
									entry.length());
							rootDirectory.addFile(abstractfile);
						}

					}
				}
			}
		} catch (NullPointerException e) {
			System.err.println(
					"This error should not appear , the reason for it is that the files in the directory are totally unreadable");
			// listFiles() If this abstract pathname does not denote a
			// directory, then this method returns null
		}

	}

	/**
	 * By given Directory returns the directory size in bytes
	 * 
	 * @param file
	 *            - given file
	 * @return long - size in bytes
	 * @throws I/O
	 *             -exception if directory not found
	 * 
	 */
	private long getSizeOfFolder(Path startPath) throws IOException {

		final AtomicLong size = new AtomicLong(0);

		Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				size.addAndGet(attrs.size());
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
				// Skip folders that can't be traversed
				System.out.println("skipped: " + file + "e=" + exc);
				return FileVisitResult.CONTINUE;
			}
		});

		return size.get();
	}

	/**
	 * Print a list with all files in the folder, containing the specified text,
	 * sorted by the file size.
	 * 
	 * @param dir
	 *            - root directory
	 * @param indent
	 *            - number of minus symbol , that is used for printing
	 * @example -root -----folder1 ---------folder inside ---------file.zip
	 *          -----folder2 -----folder3
	 * 
	 */
	private void printResults(Directory dir, int indent) {
		for (int i = 0; i < indent; i++) {
			System.out.print('-');
		}
		System.out.println(dir.getName());
		for (Iterator<AbstractFile> iterator = dir.getDirectoryFiles().iterator(); iterator.hasNext();) {
			AbstractFile abstractFile = (AbstractFile) iterator.next();

			if (abstractFile.getFileType().equals(AbstractFile.FileType.DIRECTORY)) {

				printResults((Directory) abstractFile, indent + 4);
				continue;
			}
			for (int i = 0; i < indent + 4; i++) {
				System.out.print('-');
			}
			System.out.println(abstractFile.getName());
		}

	}

}
