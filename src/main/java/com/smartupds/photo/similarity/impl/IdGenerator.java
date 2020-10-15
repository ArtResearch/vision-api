/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartupds.photo.similarity.impl;

import com.smartupds.photo.similarity.common.Resources;
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
        Logger.getLogger(IdGenerator.class.getName()).log(Level.INFO, "Generating Pastec IDs.");
        Logger.getLogger(IdGenerator.class.getName()).log(Level.INFO, "Pastec IDs generated and saved in :.".concat(Resources.PASTEC_IDS));
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
