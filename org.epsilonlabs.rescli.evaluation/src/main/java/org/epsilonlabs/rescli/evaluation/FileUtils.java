package org.epsilonlabs.rescli.evaluation;


import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.io.Files;

public class FileUtils {
	private static final Logger logger = LogManager.getLogger(FileUtils.class);


	private static String reportFile;
	
	public FileUtils(String reportFile) {
		FileUtils.reportFile = reportFile;
	}
	
	public String getProperFileNameFromUrl(String filePath) {
		String fileName = null;
		try {
			fileName = java.net.URLDecoder.decode(new File(filePath).getName(), "UTF-8");
			fileName = fileName.replaceAll(" ", "-");
			String regex = "[\\(|\\[|\\{|\\)|\\}|\\]]";
			fileName = fileName.replaceAll(regex, "");
		} catch (UnsupportedEncodingException e) {
			logger.error("Failed to get a proper file name from string file path.");
			e.printStackTrace();
		}
		return fileName;
	}
	
	private static Collection<File> listFileTree(File dir) {
	    Set<File> fileTree = new HashSet<File>();
	    if(dir==null||dir.listFiles()==null){
	        return fileTree;
	    }
	    for (File entry : dir.listFiles()) {
	        if (entry.isFile()) fileTree.add(entry);
	        else fileTree.addAll(listFileTree(entry));
	    }
	    return fileTree;
	}


	/**
	 * 
	 * @param fileLocation file location where to look for 
	 * @param fileExtension file extension to look for
	 */
	public static File findFirstFileByExtension(File fileLocation, String fileExtension) {

	        for ( File f : listFileTree(fileLocation) ) {
	            if (f.getName().endsWith(fileExtension)) {
	            		return f;
	            }
	        }
	        return null;
	}
	
	/**
	 * 
	 * @param fileLocation file location where to look for 
	 * @param fileName file name to look for
	 */
	public static File findFirstFileByName(File fileLocation, String fileName) {

	        for ( File f : listFileTree(fileLocation) ) {
	            if (f.getName().equals(fileName)) {
	            		return f;
	            }
	        }
	        return null;
	}
	
	public static void save(CharSequence charSequence, File targetFile, Charset encoding) throws Exception {
		try {
			Files.createParentDirs(targetFile);
			Files.write(charSequence, targetFile, encoding);
		} catch (IOException e) {
			throw new Exception("Unable to save file " + targetFile.getAbsolutePath() + " --- " + e.getMessage());
		}
	}
	
	public static File[] getListOfAcceptedFiles(File sourceFilePath, String acceptedFileExtension) {
		File[] files = sourceFilePath.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.toLowerCase().endsWith(acceptedFileExtension);
		    }
		});
		return files;
	}

}