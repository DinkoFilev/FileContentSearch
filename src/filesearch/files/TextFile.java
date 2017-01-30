package filesearch.files;


public class TextFile extends AbstractFile{

	public TextFile(String fileName, String filePath, long fileSize) {
		super(fileName, filePath, fileSize,AbstractFile.FileType.TEXTFILE);
		
	}

	
	 
}
