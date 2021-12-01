/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartupds.photo.similarity.impl;

import com.smartupds.photo.similarity.common.Resources;
import com.smartupds.photo.similarity.common.Utils;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.Update;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;

/**
 *
 * @author mafragias
 */
public class IndexHandler {
    // public void handle() {
    //     String indexes = "";
    //     if (Resources.SIMILARITY_METHOD.equals(Resources.PASTEC_METHOD))
    //         indexes = ((JSONArray) jsonIndexes.get("image_ids")).toString();
    //     else if (Resources.SIMILARITY_METHOD.equals(Resources.MATCH_METHOD))
    //         indexes = ((JSONArray) jsonIndexes.get("result")).toString();
        
    //     String[] temp = indexes.substring(indexes.indexOf("[")+1,indexes.indexOf("]")).split(",");
    //     HashSet<BigInteger> indexesHashed = new HashSet<>();
    //     for(String index:temp){
    //         if (Utils.isNumeric(index.replace("\"", ""))){
    //             indexesHashed.add(new BigInteger(index.replace("\"", "")));
    //         }
    //     }

    //     Logger.getLogger(IndexHandler.class.getName()).log(Level.INFO, "Handling indexes.");
    //     if (indexesHashed.size()>0){
    //         if (Resources.SIMILARITY_METHOD.equals(Resources.MATCH_METHOD)) {
    //             System.out.print(Collections.max(indexesHashed).add(new BigInteger("1000000")));
    //             Logger.getLogger(IndexHandler.class.getName()).log(Level.INFO, "Current max index returned : ".concat(""+Collections.max(indexesHashed).add(new BigInteger("1000000"))));
    //         } else {
    //             System.out.print(Collections.max(indexesHashed));
    //             Logger.getLogger(IndexHandler.class.getName()).log(Level.INFO, "Current max index returned : ".concat(""+Collections.max(indexesHashed)));
    //         }
    //     } else {
    //         System.out.print(1000000);
    //         Logger.getLogger(IndexHandler.class.getName()).log(Level.INFO, "Current max index returned : 1000000");
    //     }
            
    // }
    public String query = "";
    private SPARQLRepository repo;

    public IndexHandler() {
        if(Resources.SIMILARITY_METHOD.equals(Resources.MATCH_METHOD)) {
            this.query = "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
            "select (MAX(xsd:integer(?in)) AS ?max) WHERE {\n" +
            "  graph <https://pharos.artresearch.net/resource/graph/visual_similarity/match> {\n" +
            "  	?x <https://pharos.artresearch.net/resource/vocab/vision/match/has_index> ?in.\n" +
            "  }\n" +
            "}";
        } else {
            this.query = "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
            "select (MAX(xsd:integer(?index)) AS ?max) WHERE {\n" +
            "  graph <https://pharos.artresearch.net/resource/graph/visual_similarity/pastec> {\n" +
            "  	?x <https://pharos.artresearch.net/resource/vocab/vision/pastec/has_index> ?in.\n" +
            "  }\n" + 
            "  BIND(REPLACE(STR(?in),\"https://vision.artresearch.net:4212/index/images/\",\"\") AS ?index)\n" +
            "}";
        }
    }

    public void handle() {
        repo.setUsernameAndPassword(Resources.PHAROS_USER,Resources.PHAROS_PASSWORD);
        repo.initialize();
        RepositoryConnection conn = repo.getConnection();
        TupleQuery tupleQuery = conn.prepareTupleQuery(query);
        TupleQueryResult tqr = tupleQuery.evaluate();
        String max = "";
        while(tqr.hasNext()){
            BindingSet result = tqr.next();
            max = result.getValue("max").stringValue();
        }
        if (!max.equals("0") && !max.equals("")){
            System.out.print(max);
            Logger.getLogger(IndexHandler.class.getName()).log(Level.INFO, "Current max index returned : ".concat(""+max));
        } else {
            System.out.print(1000000);
            Logger.getLogger(IndexHandler.class.getName()).log(Level.INFO, "Current max index returned : 1000000");
        }
        repo.shutDown();
    }
    
    // Setter
    public void setRepository(String endpoint) {
        repo = new SPARQLRepository(endpoint.trim());
    }

}