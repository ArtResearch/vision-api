/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartupds.photo.similarity.impl;

import com.google.common.io.Files;
import com.smartupds.photo.similarity.common.Resources;
import com.smartupds.photo.similarity.common.Utils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.query.GraphQuery;
import org.eclipse.rdf4j.query.GraphQueryResult;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;

/**
 *
 * @author mafragias
 */
public class QueryHandler {
    
    private String constructQuery = "";
    private Repository repo;
    
    public QueryHandler(String query){
        Matcher m = Pattern.compile("CONSTRUCT[\\s]*\\{[\\s?a-zA-Z_:\\.]*\\}[\\s]*WHERE[\\s]*\\{[\\s\\w?{}.:\\W]*\\}").matcher(query);
        File f = new File(query);
        if (m.find()){
            this.constructQuery = query;
        } else if (f.isFile()){
            try {
                BufferedReader reader = new BufferedReader( new InputStreamReader(new FileInputStream(query),"UTF8"));
                String row;
                while((row = reader.readLine())!=null)
                    this.constructQuery = this.constructQuery + row + "\n";
            } catch (FileNotFoundException | UnsupportedEncodingException ex) {
                Logger.getLogger(QueryHandler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(QueryHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            throw new UnsupportedOperationException("This query format is not supported.");
        }
        Logger.getLogger(QueryHandler.class.getName()).log(Level.INFO, "Processing CONSTRUCT Query : \n\n".concat(constructQuery));
    }
    
    public String createGraph(){
        String graphPath = "";
        try {
            Logger.getLogger(QueryHandler.class.getName()).log(Level.INFO, "Initializing Repository.");
            repo.initialize();
            RepositoryConnection conn = repo.getConnection();
            GraphQuery graph = conn.prepareGraphQuery(QueryLanguage.SPARQL, constructQuery);
            GraphQueryResult result = graph.evaluate();
            graphPath = Resources.GRAPHS +"/"+LocalDateTime.now().toString().replace(":", "-")+"_graph.ttl";
//            graphPath = Resources.GRAPHS +"/graph.ttl";
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(graphPath), "UTF-8");
            HashSet<String> previous_ids = listImageIds();
            File file1 = new File(Resources.GRAPHS +"/image_uris");
            File file2 = new File(Resources.GRAPHS +"/"+LocalDateTime.now().toString().replace(":", "-")+"_image_uris");
            if (file1.exists()){
                Files.copy(file1, file2);
            }
            OutputStreamWriter writer2 = new OutputStreamWriter(new FileOutputStream(Resources.GRAPHS +"/image_uris"), "UTF-8");
            HashSet<String> image_uris_distinct = new HashSet<>();
            while(result.hasNext()){
                Statement stmt = result.next();
                if (!previous_ids.contains(stmt.getObject().toString().trim())){
                    writer.append("<"+stmt.getSubject().toString()+"> <"+ stmt.getPredicate()+"> <"+stmt.getObject()+">.\n");
                    image_uris_distinct.add(stmt.getObject().toString().trim());
                }
            }
            writer.close();
            Logger.getLogger(QueryHandler.class.getName()).log(Level.INFO, "File with graph created at :".concat(graphPath));
            writer2.append(String.join("\n", image_uris_distinct));
            writer2.close();
            Logger.getLogger(QueryHandler.class.getName()).log(Level.INFO, "File with image URIS created at :".concat(Resources.GRAPHS +"/image_uris"));
            Logger.getLogger(QueryHandler.class.getName()).log(Level.INFO, "Repository Shutting Down.");
            repo.shutDown();
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(QueryHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(QueryHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return graphPath;
    }
    
    private HashSet<String> listImageIds(){
        HashSet<String> ids =  new HashSet<>();
        Utils.listFilesForFolder(new File(Resources.GRAPHS)).forEach(file -> {
           if (!file.endsWith(".ttl")){
               try {
                   BufferedReader reader = new BufferedReader( new InputStreamReader(new FileInputStream(file), "UTF8"));
                   String row = "";
                   while((row = reader.readLine())!=null){
                       ids.add(row.trim());
                   }
                   reader.close();
               } catch (FileNotFoundException | UnsupportedEncodingException ex) {
                   Logger.getLogger(QueryHandler.class.getName()).log(Level.SEVERE, null, ex);
               } catch (IOException ex) {
                   Logger.getLogger(QueryHandler.class.getName()).log(Level.SEVERE, null, ex);
               }
           } 
        });
        return ids;
    }
    
    // Setters & Getters
    public void setRepository(String endpoint) {
        repo = new SPARQLRepository(endpoint.trim());
    }
}
