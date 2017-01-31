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
 * /


HOW TO RUN:

java -jar FileContentSearch.jar /home/anywhere "I WANT TO SEARCH THIS TEXT"

first argument is directory
second is the text that we are looking for

Folder with test files is available
