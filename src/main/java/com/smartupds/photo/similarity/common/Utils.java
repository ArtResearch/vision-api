/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartupds.photo.similarity.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.rdf4j.query.Update;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;

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
    
    
    public static void uploadFile(String file,String graph,String endpoint, String username, String password){
        SPARQLRepository repo = new SPARQLRepository(endpoint);
        if( username!=null && password!=null)
            repo.setUsernameAndPassword(username, password);
        try {
            BufferedReader reader = new BufferedReader( new InputStreamReader(new FileInputStream(file), "UTF8"));
            String row = "";
            String ttl = "";
            int rowCounter = 1;
            while((row = reader.readLine())!=null){
                ttl = ttl + row + "\n";
                if(rowCounter%1000==0){
                    materialize(repo,graph,ttl);
                    ttl="";
                }
                rowCounter++;
            }
            if(!ttl.equals("")){
                materialize(repo,graph,ttl);
            }
            reader.close();
            
        } catch (UnsupportedEncodingException | FileNotFoundException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void materialize(SPARQLRepository repo, String graph,String ttl){
        Logger.getLogger(Utils.class.getName()).log(Level.INFO,"Initializing repository");
        repo.initialize();
        Logger.getLogger(Utils.class.getName()).log(Level.INFO,"Getting repository connection");
        RepositoryConnection conn = repo.getConnection();
        String insertQ = "INSERT {"
                    + " GRAPH <"+graph+"> {\n\t"
                    + "     "+ttl+"}} WHERE {}";
//        Logger.getLogger(Utils.class.getName()).log(Level.INFO, "Preparing Update query:\n".concat(insertQ));
        Logger.getLogger(Utils.class.getName()).log(Level.INFO, "Preparing Update query...\n");
        Update insertQuery = conn.prepareUpdate(insertQ);
        insertQuery.execute();
        Logger.getLogger(Utils.class.getName()).log(Level.INFO,"Successful query execution");
        Logger.getLogger(Utils.class.getName()).log(Level.INFO,"Shutting down repository");
        conn.close();
        repo.shutDown();
    }
}