
/**
 * Program that search for a specific text inside a the content of all files into a
 * small directory.
 * This program isn't multithreaded application
 * 
 * Supported formats : {txt ,md ,gradle ,docx ,doc ,xml ,json ,rtf , properties,}
 * also read information inside archives {zip , jar}
 * Readers will return suitably message if they cannot read the current file
 * @Note Very slow for large data
 * @Note Cannot read docx , doc inside zip or jar file
 * @Warning text to search is case sensitive
 * 
 * @Return List with all files in the folder, containing the specified text, sorted by the file size. 
 * 
 * @author Dinko Filev
 *
 */
package demo;

import filesearch.TextSearch;

public class Main {

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		
		String path = args[0];
		String textToSearch = args[1];
		TextSearch search = new TextSearch(path,textToSearch);
	

		System.out.println("Processing time : " + (System.currentTimeMillis() - start) + "ms");
		
	}

}

