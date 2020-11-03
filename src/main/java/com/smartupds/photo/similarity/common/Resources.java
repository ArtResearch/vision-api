/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartupds.photo.similarity.common;

/**
 *
 * @author mafragias
 */
public class Resources {
    // METHOD 
    public static String SIMILARITY_METHOD;
    // Directories
    public static final String WORKSPACE = "./PhotoSimilarity-Workspace";
    public static final String GRAPHS = WORKSPACE + "/Graphs";
    public static final String IDS = WORKSPACE + "/IDs";
    public static final String MODEL = WORKSPACE + "/Model";
    
    // PREFIXES
    public static final String PHAROS_CUSTOM = "https://pharos.artresearch.net/custom/";
    public static final String VISION = "https://pharos.artresearch.net/resource/";
    public static final String SIM = "http://purl.org/ontology/similarity/";
    
    // PROPERTIES & ENTITIES
    public static final String ASSOCIATION = "Association";
    public static final String ASSOCIATION_METHOD = "AssociationMethod";
    public static final String METHOD = "method";
    public static final String ELEMENT = "element";
    public static final String WEIGHT = "weight";
    public static final String GRAPH = "graph";
    public static final String SIMILARITY = "similarity";
    
    // FILE EXTENTION
    public static final String JSON = "json";
    
    // Setters
    public static void setMethod(String method){
        Resources.SIMILARITY_METHOD = method.trim().toLowerCase();
    }
    
}
