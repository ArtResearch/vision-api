/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartupds.photo.similarity.impl;

import com.smartupds.photo.similarity.common.Resources;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
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
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(graphPath), "UTF-8");
            while(result.hasNext()){
                Statement stmt = result.next();
                writer.append("<"+stmt.getSubject().toString()+"> <"+ stmt.getPredicate()+"> <"+stmt.getObject()+">.\n");
            }
            writer.close();
            Logger.getLogger(QueryHandler.class.getName()).log(Level.INFO, "File with graph created at :".concat(graphPath));
            Logger.getLogger(QueryHandler.class.getName()).log(Level.INFO, "Repository Shutting Down.");
            repo.shutDown();
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(QueryHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(QueryHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return graphPath;
    }
    
    // Setters & Getters
    public void setRepository(String endpoint) {
        repo = new SPARQLRepository(endpoint);
    }
    
}
