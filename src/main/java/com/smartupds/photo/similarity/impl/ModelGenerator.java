package com.smartupds.photo.similarity.impl;


import com.smartupds.photo.similarity.common.Resources;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.Update;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mafragias
 */
public class ModelGenerator {
    private ArrayList<String> jsonfiles;
    private HashMap<BigInteger ,String> image_index = new HashMap<>();
    private HashMap<BigInteger ,String[][]> image_maps = new HashMap<>();
    
    private String filename;
    private SPARQLRepository repo;
    
    public ModelGenerator(ArrayList<String> jsonfiles) {
        this.jsonfiles = jsonfiles;
    }
    public ModelGenerator(String filename){
        this.filename = filename;
    }
    
    /**
     *
     */
    public void generate() {
        Logger.getLogger(ModelGenerator.class.getName()).log(Level.INFO, "Model generation started.");
        parseJSON();
        Logger.getLogger(ModelGenerator.class.getName()).log(Level.INFO, "Initializing Repository.");
        repo.setUsernameAndPassword("vision","vision");
        repo.initialize();
        String modelPath = Resources.MODEL +"/"+LocalDateTime.now().toString().replace(":", "-")+"_model.ttl";
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(modelPath), "UTF-8")) {
            image_maps.forEach((index,maps) -> {
                try {
                    String image_1 = image_index.get(index);
                    BigInteger hash_1 = new BigInteger(DigestUtils.sha1Hex(image_1).replaceAll("[a-zA-Z]+", "").trim());
                    for (String[] map : maps) {
                        String image_2 = image_index.get(new BigInteger(map[0]));
                        BigInteger hash_2 = new BigInteger(DigestUtils.sha1Hex(image_2).replaceAll("[a-zA-Z]+", "").trim());
                        BigInteger hash = hash_1.add(hash_2);
                        String graph = "";
                        graph = graph.concat("<"+Resources.VISION+Resources.GRAPH+"/visual_similarity/"+hash+"> {\n");
                        // image_uri sim:element https://vision.artresearch.net/resource/similarity/association/hash
                        graph = graph.concat("\t<"+image_1+"> <"+Resources.SIM+Resources.ELEMENT+"> <"+Resources.VISION+StringUtils.capitalize(Resources.SIMILARITY)+"/"+hash+">.\n");
                        graph = graph.concat("\t<"+image_2+"> <"+Resources.SIM+Resources.ELEMENT+"> <"+Resources.VISION+StringUtils.capitalize(Resources.SIMILARITY)+"/"+hash+">.\n");
                        // https://vision.artresearch.net/resource/Similarity/Association/hash a sim:Similarity
                        graph = graph.concat("\t<"+Resources.VISION+StringUtils.capitalize(Resources.SIMILARITY)+"/"+hash+"> a <"+Resources.SIM+StringUtils.capitalize(Resources.SIMILARITY)+">.\n");
                        //https://vision.artresearch.net/resource/Similarity/Association/hash sim:method https://vision.artresearch.net/resource/similarity/association/hash/Association/Pastec
                        graph = graph.concat("\t<"+Resources.VISION+StringUtils.capitalize(Resources.SIMILARITY)+"/"+hash+"> <"+Resources.SIM+Resources.METHOD+"> <"+Resources.VISION+StringUtils.capitalize(Resources.SIMILARITY)+"/"+hash+"/"+Resources.ASSOCIATION+"/"+StringUtils.capitalize(Resources.SIMILARITY_METHOD)+">.\n");
                        // https://vision.artresearch.net/resource/similarity/association/hash/Association/Pastec a sim:Association
                        graph = graph.concat("\t<"+Resources.VISION+StringUtils.capitalize(Resources.SIMILARITY)+"/"+hash+"/"+Resources.ASSOCIATION+"/"+StringUtils.capitalize(Resources.SIMILARITY_METHOD)+"> a <"+Resources.SIM+Resources.ASSOCIATION+">.\n");
                        // https://vision.artresearch.net/resource/similarity/association/hash/Association/Pastec sim:method https://vision.artresearch.net/resource/Similarity/AssociationMethod/Pastec
                        graph = graph.concat("\t<"+Resources.VISION+StringUtils.capitalize(Resources.SIMILARITY)+"/"+hash+"/"+Resources.ASSOCIATION+"/"+StringUtils.capitalize(Resources.SIMILARITY_METHOD)+"> <"+Resources.SIM+Resources.METHOD+"> <"+Resources.VISION+StringUtils.capitalize(Resources.SIMILARITY)+"/"+Resources.ASSOCIATION_METHOD+"/"+StringUtils.capitalize(Resources.SIMILARITY_METHOD)+">.\n");
                        // https://vision.artresearch.net/resource/Similarity/AssociationMethod/Pastec a sim:AssociationMethod
                        graph = graph.concat("\t<"+Resources.VISION+StringUtils.capitalize(Resources.SIMILARITY)+"/"+Resources.ASSOCIATION_METHOD+"/"+StringUtils.capitalize(Resources.SIMILARITY_METHOD)+"> a <"+Resources.SIM+Resources.ASSOCIATION_METHOD+">.\n");
                        // https://vision.artresearch.net/resource/similarity/association/hash/Association/Pastec sim:weight "90"
                        graph = graph.concat("\t<"+Resources.VISION+StringUtils.capitalize(Resources.SIMILARITY)+"/"+hash+"/"+Resources.ASSOCIATION+"/"+StringUtils.capitalize(Resources.SIMILARITY_METHOD)+"> <"+Resources.SIM+Resources.WEIGHT+"> \"" + map[1] + "\".\n");
                        graph = graph.concat("}\n");
                        writer.append(graph);
                        updateModel(graph);
                    }   
                } catch (IOException ex) {
                    Logger.getLogger(ModelGenerator.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            Logger.getLogger(ModelGenerator.class.getName()).log(Level.INFO, "File with model created at :".concat(modelPath));
            Logger.getLogger(ModelGenerator.class.getName()).log(Level.INFO, "Model generated.");
            Logger.getLogger(ModelGenerator.class.getName()).log(Level.INFO, "Repository Shutting Down.");
            repo.shutDown();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ModelGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ModelGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void updateModel(String graph){
        Logger.getLogger(ModelGenerator.class.getName()).log(Level.INFO, "Adding graph to the model.");
        String graph_query = "INSERT DATA { GRAPH " +graph+"}";
        RepositoryConnection conn = repo.getConnection();
        Update update = conn.prepareUpdate(graph_query);
        Logger.getLogger(ModelGenerator.class.getName()).log(Level.INFO, "Executing graph insert query.");
        update.execute();
        Logger.getLogger(ModelGenerator.class.getName()).log(Level.INFO, "Graph insert successfully executed.");
    }
    
    public void updateIndexes(){
        try {
            String updateQuery = "INSERT DATA { GRAPH ";
            Logger.getLogger(ModelGenerator.class.getName()).log(Level.INFO, "Initializing Repository.");
//            repo.setUsernameAndPassword("admin", "admin");
            repo.initialize();
            Logger.getLogger(ModelGenerator.class.getName()).log(Level.INFO, "Generating update query.");
            BufferedReader reader = new BufferedReader( new InputStreamReader(new FileInputStream(filename), "UTF8"));
            String row;
            while ((row = reader.readLine())!=null)
                updateQuery += row +"\n";
            updateQuery += "}";
            RepositoryConnection conn = repo.getConnection();
            Update update = conn.prepareUpdate(updateQuery);
            Logger.getLogger(ModelGenerator.class.getName()).log(Level.INFO, "Executing update query.");
            update.execute();
            Logger.getLogger(ModelGenerator.class.getName()).log(Level.INFO, "Update query successfully executed.");
            Logger.getLogger(ModelGenerator.class.getName()).log(Level.INFO, "Repository Shutting Down.");
            repo.shutDown();
        } catch (UnsupportedEncodingException | FileNotFoundException ex) {
            Logger.getLogger(ModelGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ModelGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void parseJSON(){
        try {
            for(String jsonfile: jsonfiles){
                JSONParser parserJson = new JSONParser();
                Object obj = parserJson.parse(new InputStreamReader(new FileInputStream(jsonfile), "UTF8"));
                JSONObject jsonObject = (JSONObject) obj;
                JSONArray companyList = (JSONArray) jsonObject.get("results");
                Iterator<JSONObject> iterator = companyList.iterator();
                while (iterator.hasNext()) {
                    JSONObject result = iterator.next();
                    if (result.get("image_id")!=null){
                        BigInteger image_id = new BigInteger(result.get("image_id").toString());
//                        String image_url = result.get("image_url").toString();
//                        image_index.put(image_id, image_url);
                        JSONObject search_results = (JSONObject)result.get("search_results");
                        JSONArray image_ids = (JSONArray) search_results.get("image_ids");
                        JSONArray scores = (JSONArray) search_results.get("scores");
                        String[][] image_scores = new String[image_ids.size()][2];
                        if (!image_ids.isEmpty()){
                            for(int i=0;i<image_ids.size();i++){
                                image_scores[i][0] = image_ids.get(i).toString();
                                image_scores[i][1] = scores.get(i).toString();
                            }
                            image_maps.put(image_id, image_scores);
                        }
                    }
                }
            }
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(ModelGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ParseException ex) {
            Logger.getLogger(ModelGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void getIndexes(String pharos_endpoint) {
        SPARQLRepository pharos_repo = new SPARQLRepository(pharos_endpoint.trim());
        Logger.getLogger(ModelGenerator.class.getName()).log(Level.INFO, "Initializing Pharos Repository.");
//            repo.setUsernameAndPassword("admin", "admin");
        pharos_repo.initialize();
        Logger.getLogger(ModelGenerator.class.getName()).log(Level.INFO, "Retrieving "+StringUtils.capitalize(Resources.SIMILARITY_METHOD)+" indexes.");
        String select = "SELECT ?image ?index WHERE { ?image <https://pharos.artresearch.net/custom/"+Resources.SIMILARITY_METHOD+"/has_index> ?index}";
        RepositoryConnection conn = pharos_repo.getConnection();
        TupleQuery tupleQuery = conn.prepareTupleQuery(select);
        TupleQueryResult tqr = tupleQuery.evaluate();
        while(tqr.hasNext()){
            BindingSet result = tqr.next();
            String image = result.getValue("image").stringValue();
            String index = result.getValue("index").stringValue();
            index = index.substring(index.lastIndexOf("/")+1);
            image_index.put(new BigInteger(index), image);
        }
        Logger.getLogger(ModelGenerator.class.getName()).log(Level.INFO, "Query successfully evaluated.");
        Logger.getLogger(ModelGenerator.class.getName()).log(Level.INFO, "Pharos Repository Shutting Down.");
        pharos_repo.shutDown();
    }
    /**
     *
     * @param endpoint
     */
    public void setRepository(String endpoint) {
        repo = new SPARQLRepository(endpoint.trim());
    }



}
