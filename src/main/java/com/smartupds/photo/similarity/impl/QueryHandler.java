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
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.query.GraphQuery;
import org.eclipse.rdf4j.query.GraphQueryResult;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;

/**
 * To process queries in order to retrieve a list of images.
 * @author mafragias
 */
public class QueryHandler {
    
    private String constructQuery = "";
    private SPARQLRepository repo;
    private String graphPath = "";
    
    
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
    }
    
    /**
     * Processes the construct query and returns the path the graph is saved to.
     * @return
     */
    public String createGraph(){
        try {
            Logger.getLogger(QueryHandler.class.getName()).log(Level.INFO, "Initializing Pharos Repository.");
            repo.setUsernameAndPassword(Resources.PHAROS_USER, Resources.PHAROS_PASSWORD);
            repo.initialize();
            RepositoryConnection conn = repo.getConnection();
            // Filter out images with a particular method index
            constructQuery = constructQuery.substring(0,constructQuery.lastIndexOf("}"))
                                + "\t?image <https://pharos.artresearch.net/resource/vocab/vision/"+Resources.MATCH_METHOD+"/has_index>|"
                                + "<https://pharos.artresearch.net/resource/vocab/vision/"+Resources.PASTEC_METHOD+"/has_index> ?index.\n"
                                + constructQuery.substring(constructQuery.lastIndexOf("}"));
//            constructQuery = constructQuery.substring(0,constructQuery.lastIndexOf("}"))
//                                + "\tFILTER NOT EXISTS { ?image <https://pharos.artresearch.net/resource/vocab/vision/"+Resources.MATCH_METHOD+"/has_index>|"
//                                + "<https://pharos.artresearch.net/resource/vocab/vision/"+Resources.PASTEC_METHOD+"/has_index> ?index}\n"
//                                + constructQuery.substring(constructQuery.lastIndexOf("}"));
            Logger.getLogger(QueryHandler.class.getName()).log(Level.INFO, "Processing CONSTRUCT Query : \n\n".concat(constructQuery));
            GraphQuery graph = conn.prepareGraphQuery(QueryLanguage.SPARQL, constructQuery);
            GraphQueryResult result = graph.evaluate();
            this.graphPath = Resources.GRAPHS +"/"+LocalDateTime.now().toString().replace(":", "-")+"_graph.ttl";
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(this.graphPath), "UTF-8");
            File file1 = new File(Resources.GRAPHS +"/image_uris");
            File file2 = new File(Resources.GRAPHS +"/"+LocalDateTime.now().toString().replace(":", "-")+"_image_uris");
            if (file1.exists()){
                Files.copy(file1, file2);
            }
            OutputStreamWriter writer2 = new OutputStreamWriter(new FileOutputStream(Resources.GRAPHS +"/image_uris"), "UTF-8");
            HashSet<String> image_uris_distinct = new HashSet<>();
            while(result.hasNext()){
                Statement stmt = result.next();
                if(stmt.getObject() instanceof IRI)
                    writer.append("<"+stmt.getSubject()+"> <"+stmt.getPredicate()+"> <" + stmt.getObject() +">. \n");
                else
                    writer.append("<"+stmt.getSubject()+"> <"+stmt.getPredicate()+"> " + stmt.getObject() +". \n");
                if(stmt.getPredicate().stringValue().equals(Resources.PHAROS_CUSTOM.concat(Resources.HAS_IMAGE)))
                    image_uris_distinct.add(stmt.getObject().toString().trim());
            }
            writer.close();
            Logger.getLogger(QueryHandler.class.getName()).log(Level.INFO, "Construct query result saved at :".concat(this.graphPath));
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
        return this.graphPath;
    }
    
    // Setters & Getters
    /**
     * Setting Repository
     * @param endpoint
     */
    public void setRepository(String endpoint) {
        repo = new SPARQLRepository(endpoint.trim());
    }
}