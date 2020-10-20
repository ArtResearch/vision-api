package com.smartupds.photo.similarity.impl;


import com.smartupds.photo.similarity.common.Resources;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.digest.DigestUtils;
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
    
    private String jsonfile;
    private HashMap<BigInteger ,String> image_index = new HashMap<>();
    private HashMap<BigInteger ,String[][]> image_maps = new HashMap<>();
//    private HashSet<BigInteger > hash_set =  new HashSet<>();
    public ModelGenerator(String jsonfile) {
        this.jsonfile = jsonfile;
    }

    public void generate() {
        try {
            Logger.getLogger(ModelGenerator.class.getName()).log(Level.INFO, "Model generation started.");
            parseJSON();
            String modelPath = Resources.MODEL +"/"+LocalDateTime.now().toString().replace(":", "-")+"_model.ttl";
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(modelPath), "UTF-8");
            image_maps.forEach((index,maps) -> {
                try {
                    String image_1 = image_index.get(index);
                    BigInteger hash_1 = new BigInteger(DigestUtils.sha1Hex(image_1).replaceAll("[a-zA-Z]+", "").trim());
                    for(int i=0; i<maps.length;i++){
                        String image_2 = image_index.get(new BigInteger(maps[i][0]));
                        BigInteger hash_2 = new BigInteger(DigestUtils.sha1Hex(image_2).replaceAll("[a-zA-Z]+", "").trim());
                        BigInteger hash = hash_1.add(hash_2);
                        writer.append("<"+image_1+"> <"+Resources.SIM+"element> <"+Resources.SIM+Resources.ASSOCIATION+"/"+hash+">.\n");
                        writer.append("<"+image_2+"> <"+Resources.SIM+"element> <"+Resources.SIM+Resources.ASSOCIATION+"/"+hash+">.\n");
                        writer.append("<"+Resources.SIM+Resources.ASSOCIATION+"/"+hash+"> a <"+Resources.SIM+Resources.ASSOCIATION+">.\n");
                        writer.append("<"+Resources.SIM+Resources.ASSOCIATION+"/"+hash+"> <"+Resources.SIM+Resources.METHOD+"> <"+Resources.SIM+Resources.ASSOCIATION_METHOD+"/Pastec/"+hash+">.\n");
                        writer.append("<"+Resources.SIM+Resources.ASSOCIATION_METHOD+"/Pastec/"+hash+"> a <"+Resources.SIM+Resources.ASSOCIATION_METHOD+">.\n");
                        writer.append("<"+Resources.SIM+Resources.ASSOCIATION_METHOD+"/Pastec/"+hash+"> a <"+Resources.SIM+Resources.ASSOCIATION_METHOD+"/Pastec>.\n");
                        writer.append("<"+Resources.SIM+Resources.ASSOCIATION_METHOD+"/Pastec/"+hash+"> <"+Resources.SIM+Resources.WEIGHT+"> \""+maps[i][1]+"\".\n");
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ModelGenerator.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            Logger.getLogger(ModelGenerator.class.getName()).log(Level.INFO, "File with model created at :".concat(modelPath));
            Logger.getLogger(ModelGenerator.class.getName()).log(Level.INFO, "Model generated.");
            writer.close();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ModelGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ModelGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void parseJSON(){
        try {
            JSONParser parserJson = new JSONParser();
            Object obj = parserJson.parse(new InputStreamReader(new FileInputStream(jsonfile), "UTF8"));
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray companyList = (JSONArray) jsonObject.get("results");
            Iterator<JSONObject> iterator = companyList.iterator();
            while (iterator.hasNext()) {
                JSONObject result = iterator.next();
                if (result.get("image_id")!=null){
                    BigInteger image_id = new BigInteger(result.get("image_id").toString());
                    String image_url = result.get("image_url").toString();
                    image_index.put(image_id, image_url);
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
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(ModelGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ParseException ex) {
            Logger.getLogger(ModelGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
