package filesearch.directories;


import java.util.TreeSet;
import filesearch.files.AbstractFile;


/**
 * File directory that contains both directories and files
 * 
 * @author Dinko Filev
 *
 */
public class Directory extends AbstractFile {

	private TreeSet<AbstractFile> filesAndFolders;
 
	public Directory(String name, String path, long size) {
		super(name, path, size, AbstractFile.FileType.DIRECTORY);
		filesAndFolders = new TreeSet<>();

	}

	public void addFile(AbstractFile abstractfile) {
		filesAndFolders.add(abstractfile);
	}

	public TreeSet<AbstractFile> getDirectoryFiles() {

		return filesAndFolders;
	}

}
