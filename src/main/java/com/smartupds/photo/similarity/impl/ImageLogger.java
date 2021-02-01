/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartupds.photo.similarity.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author mafragias
 */
public class ImageLogger {
    
    private String json;
    private String log;
    
    public ImageLogger(String json, String log){
        this.json = json;
        this.log = log;
    }
    
    public void generate(){
        try {
            Gson GSON = new GsonBuilder().setPrettyPrinting().create();
            JSONObject errorsJsonObject = new JSONObject();
            OutputStreamWriter writer =new OutputStreamWriter(new FileOutputStream(this.log), "UTF-8");
            errorsJsonObject.put("errors", parseJSON());
            writer.append(GSON.toJson(errorsJsonObject));
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(ImageLogger.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ImageLogger.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private JSONArray parseJSON(){
        JSONArray errorJsonObject = new JSONArray();
        try {
            // IMAGE_DOWNLOADER_HTTP_ERROR
            JSONParser parserJson = new JSONParser();
            Object obj = parserJson.parse(new InputStreamReader(new FileInputStream(json), "UTF8"));
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray imageList = (JSONArray) jsonObject.get("results");
            Iterator<JSONObject> iterator = imageList.iterator();
            while(iterator.hasNext()){
                JSONObject result = iterator.next();
                if (result.get("image_id")==null || ((JSONObject)result.get("search_results")).get("type").equals("IMAGE_DOWNLOADER_HTTP_ERROR")){
                    errorJsonObject.add(result);
                }
            }
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(ImageLogger.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ParseException ex) {
            Logger.getLogger(ImageLogger.class.getName()).log(Level.SEVERE, null, ex);
        }
        return errorJsonObject;
    }
}
