<<<<<<< HEAD
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartupds.photo.similarity.common;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author mafragias
 */
public class Utils {
    /**
     * Checks the contents of a directory an returns a lists of files inside it.
     * @param folder a File
     * @return list of files
     */
    public static ArrayList<String> listFilesForFolder(final File folder) {
        ArrayList<String> filePaths = new ArrayList<>(); 
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                filePaths.add(folder.getAbsoluteFile() + "\\" + fileEntry.getName());
            }
        }
        return filePaths;
    }
    
    /**
     *
     * @param folder
     * @return
     */
    public static ArrayList<String> listJSONFilesForFolder(final File folder){
        ArrayList<String> filePaths = new ArrayList<>();
        if (folder.isDirectory()){
            for(String file: listFilesForFolder(folder)) 
                if (file.endsWith(Resources.JSON) && file.contains(Resources.SIMILARITY_METHOD))
                        filePaths.add(file);
        } else if (folder.isFile() && folder.getAbsolutePath().endsWith(Resources.JSON))
            filePaths.add(folder.getAbsolutePath());
        else
            throw new UnsupportedOperationException("\nFile Error.\n");
        return filePaths;
    }
    
    /**
     *
     * @param strNum
     * @return
     */
    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static void resizeImage(String URL) {
        if (URL.contains("iif")){
            String newURL = "";
            Matcher m = Pattern.compile("(^.*\\/)[^\\/]*(\\/[^\\/]*\\/[^\\/]*$)").matcher(URL);
            if (m.find()){
                newURL = m.group(1) + "!1000,1000" + m.group(2);
            }
            System.out.println(newURL);
        } else {
            System.out.println(URL);
        }
    }
}
=======
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartupds.photo.similarity.common;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author mafragias
 */
public class Utils {
    /**
     * Checks the contents of a directory an returns a lists of files inside it.
     * @param folder a File
     * @return list of files
     */
    public static ArrayList<String> listFilesForFolder(final File folder) {
        ArrayList<String> filePaths = new ArrayList<>(); 
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                filePaths.add(folder.getAbsoluteFile() + "\\" + fileEntry.getName());
            }
        }
        return filePaths;
    }
    
    /**
     *
     * @param folder
     * @return
     */
    public static ArrayList<String> listJSONFilesForFolder(final File folder){
        ArrayList<String> filePaths = new ArrayList<>();
        if (folder.isDirectory()){
            for(String file: listFilesForFolder(folder)) 
                if (file.endsWith(Resources.JSON) && file.contains(Resources.SIMILARITY_METHOD))
                        filePaths.add(file);
        } else if (folder.isFile() && folder.getAbsolutePath().endsWith(Resources.JSON))
            filePaths.add(folder.getAbsolutePath());
        else
            throw new UnsupportedOperationException("\nFile Error.\n");
        return filePaths;
    }
    
    /**
     *
     * @param strNum
     * @return
     */
    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static void resizeImage(String URL) {
        if (URL.contains("iif")){
            String newURL = "";
            Matcher m = Pattern.compile("(^.*\\/)[^\\/]*(\\/[^\\/]*\\/[^\\/]*$)").matcher(URL);
            if (m.find()){
                newURL = m.group(1) + "!1000,1000" + m.group(2);
            }
            System.out.println(newURL);
        } else {
            System.out.println(URL);
        }
    }
}
>>>>>>> 930f331e0324085a7cdd19add594a70a90657571
