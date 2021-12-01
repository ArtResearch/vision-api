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

/**
 *
 * @author mafragias
 */
public class IndexHandler {
    
    private JSONObject jsonIndexes = new JSONObject();
    public IndexHandler(String indexes) {
        try {
            JSONParser parser = new JSONParser();
            jsonIndexes = (JSONObject) parser.parse(new InputStreamReader(new FileInputStream(indexes), "UTF8"));
        } catch (ParseException | FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(IndexHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(IndexHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     *
     */
    public void handle() {
        String indexes = "";
        if (Resources.SIMILARITY_METHOD.equals(Resources.PASTEC_METHOD))
            indexes = ((JSONArray) jsonIndexes.get("image_ids")).toString();
        else if (Resources.SIMILARITY_METHOD.equals(Resources.MATCH_METHOD))
            indexes = ((JSONArray) jsonIndexes.get("result")).toString();
        
        String[] temp = indexes.substring(indexes.indexOf("[")+1,indexes.indexOf("]")).split(",");
        HashSet<BigInteger> indexesHashed = new HashSet<>();
        for(String index:temp){
            if (Utils.isNumeric(index.replace("\"", ""))){
                indexesHashed.add(new BigInteger(index.replace("\"", "")));
            }
        }

        Logger.getLogger(IndexHandler.class.getName()).log(Level.INFO, "Handling indexes.");
        if (indexesHashed.size()>0){
            if (Resources.SIMILARITY_METHOD.equals(Resources.MATCH_METHOD)) {
                System.out.print(Collections.max(indexesHashed).add(new BigInteger("1000000")));
                Logger.getLogger(IndexHandler.class.getName()).log(Level.INFO, "Current max index returned : ".concat(""+Collections.max(indexesHashed).add(new BigInteger("1000000"))));
            } else {
                System.out.print(Collections.max(indexesHashed));
                Logger.getLogger(IndexHandler.class.getName()).log(Level.INFO, "Current max index returned : ".concat(""+Collections.max(indexesHashed)));
            }
        } else {
            System.out.print(1000000);
            Logger.getLogger(IndexHandler.class.getName()).log(Level.INFO, "Current max index returned : 1000000");
        }
            
    }
    
}