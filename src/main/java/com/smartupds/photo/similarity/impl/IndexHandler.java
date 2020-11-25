/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartupds.photo.similarity.impl;

import com.smartupds.photo.similarity.common.Resources;
import com.smartupds.photo.similarity.common.Utils;
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
            jsonIndexes = (JSONObject) parser.parse(indexes);
        } catch (ParseException ex) {
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
        
        String[] index = indexes.substring(indexes.indexOf("[")+1,indexes.indexOf("]")).split(",");
        HashSet<BigInteger> indexesHashed = new HashSet<>();
        if (Utils.isNumeric(index[0].replace("\"", ""))){
            indexesHashed = Arrays.stream(index).map(i ->new BigInteger(i.replace("\"", ""))).collect(Collectors.toCollection(HashSet::new));
        }else
            indexesHashed = new HashSet<>();
        Logger.getLogger(IndexHandler.class.getName()).log(Level.INFO, "Handling indexes.");
        if (indexesHashed.size()>0)
            System.out.print(Collections.max(indexesHashed));
        else
            System.out.print(0);
        Logger.getLogger(IndexHandler.class.getName()).log(Level.INFO, "Current max index returned.");
    }
    
}
