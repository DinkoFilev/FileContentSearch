package filesearch.files;

/**
 * File / Directory POJO
 * @author CaveMan
 *
 */
public abstract class AbstractFile implements Comparable<AbstractFile>{
	public enum FileType{DIRECTORY,TEXTFILE}
		 
	
	private String name;
	private String path;
	private long size;
	private FileType fileType;
	
	protected AbstractFile(String name,String path,long size,FileType fileType){
		this.name = name;
		this.path = path;
		this.size = size;
		this.fileType = fileType;
		
	}
	public String getName() {
		return name;
	}


	public String getPath() {
		return path;
	}


	public double getSize() {
		return size;
	}
	
	
	public FileType getFileType() {
		return fileType;
	}
	@Override
	public String toString() {
		return "AbstractFile [name=" + name + ", path=" + path + ", size=" + size + ", fileType=" + fileType + "]";
	}
	@Override
	public int compareTo(AbstractFile o) {
		if((int)(this.size - o.size) == 0){
			return this.getName().compareTo(o.getName());
		}
		return (int) (o.size - this.size  );
	}

	
	
}
