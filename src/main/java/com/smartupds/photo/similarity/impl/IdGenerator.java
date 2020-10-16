/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartupds.photo.similarity.impl;

import com.smartupds.photo.similarity.common.Resources;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RDFDataMgr;

/**
 *
 * @author mafragias
 */
public class IdGenerator {
    ArrayList<String> images =  new ArrayList<>();
    public IdGenerator(String graphPath) {
        this.images = parseFile(graphPath);
    }
    private ArrayList<String> parseFile(String graphPath){
        Logger.getLogger(IdGenerator.class.getName()).log(Level.INFO, "Parsing graph file.");
        ArrayList<String> objects =  new ArrayList<>();
        Model model = RDFDataMgr.loadModel(graphPath);
        StmtIterator stmtIterator = model.listStatements();
        while(stmtIterator.hasNext()){
            Statement stmt = stmtIterator.next();
            objects.add(stmt.getObject().toString());
        }
        Logger.getLogger(IdGenerator.class.getName()).log(Level.INFO, "Objects from graph file are parsed.");
        return objects;
    }
    
    public void generate() {
        try {
            Logger.getLogger(IdGenerator.class.getName()).log(Level.INFO, "Generating Pastec IDs.");
            long imageID = 1;
            String idsPath = Resources.PASTEC_IDS +"/pastecIDs";
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(idsPath), "UTF-8");
            for (String image: images){
                    String command = "curl -X PUT -d '{\"url\":\""+image+"\"}' http://localhost:4212/index/images/"+imageID;
//                    ProcessBuilder pb = new ProcessBuilder(command);
                    Process p = Runtime.getRuntime().exec(command);
                    Logger.getLogger(IdGenerator.class.getName()).log(Level.INFO, "Excecuting curl command : ".concat(command));
                    
                    BufferedReader reader =  new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String result = "";
                    while((result = reader.readLine())!=null){
                        System.out.println(result);
                        
                    }
                    writer.append("http://localhost:4212/index/images/"+imageID+"\n");
                    imageID++;
//                p.wait();
//                p.destroy();

//                break;
            }
            writer.close();
            Logger.getLogger(IdGenerator.class.getName()).log(Level.INFO, "Pastec IDs generated and saved in :.".concat(idsPath));
        } catch (IOException ex) {
            Logger.getLogger(IdGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
            

    }
    
}
