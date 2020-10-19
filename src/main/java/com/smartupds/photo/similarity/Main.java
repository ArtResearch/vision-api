/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartupds.photo.similarity;

import com.smartupds.photo.similarity.common.Resources;
import com.smartupds.photo.similarity.impl.IdHandler;
import com.smartupds.photo.similarity.impl.ModelGenerator;
import com.smartupds.photo.similarity.impl.QueryHandler;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Photo Similarity Project
 * @author mafragias
 */
public class Main {
    static CommandLineParser parser = new DefaultParser();
    static Options options = new Options();
    
    public static void main(String[] args){
//        BasicConfigurator.configure();
        createWorkSpace();
        createOptions();
        try {
//            String[] fake_args = {  "-q", "query",
//                                    "-e", "http://localhost:9999/blazegraph/sparql"};
//            String[] fake_args = {  "-image_ids","{\"image_ids\":[15,14,13,12,11,10,9,8,7,6,5,4,3,2,1],\"type\":\"INDEX_IMAGE_IDS\"}"};
//            String[] fake_args = {  "-image_ids","{\"image_ids\":[],\"type\":\"INDEX_IMAGE_IDS\"}"};
            CommandLine line = parser.parse( options, args );
            handleCommandLine(line);
        } catch( ParseException exp ) {
            printOptions();
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Reason : ".concat(exp.getMessage()));
        }
    }

    private static void createOptions() {
        Option query = new Option("q",true,"Construct Query: -q [query]");
        query.setArgName("query");
        Option endpoint = new Option("e",true,"Endpoint: -q [endpoint]");
        endpoint.setArgName("endpoint");
        Option image_ids = new Option("image_ids",true,"Image IDs: -image_ids [image_ids in json format]");
        endpoint.setArgName("image_ids");
        Option model = new Option("m",true,"Generate model : -model");
        model.setArgName("model");
        
        options.addOption(query);
        options.addOption(endpoint);
        options.addOption(model);
        options.addOption(image_ids);
    }

    private static void handleCommandLine(CommandLine line) {
        if (line.hasOption("q") && line.hasOption("e")){
            QueryHandler q = new QueryHandler(line.getOptionValue("q"));
            q.setRepository(line.getOptionValue("e"));
            String graphPath = q.createGraph();
        } else if (line.hasOption("m")){
//            ModelGenerator model = new ModelGenerator(Resources.PASTEC_IDS + "/pastecIDs.json");
            ModelGenerator model = new ModelGenerator(line.getOptionValue("m"));
            model.generate();
        } else if (line.hasOption("image_ids")){
            IdHandler ih = new IdHandler(line.getOptionValue("image_ids"));
            ih.handle();
        }else {
            printOptions();
            throw new UnsupportedOperationException("\nWrong arguments.\n");
        }
    }

    private static void printOptions(){
        String header = "\nChoose from options below:\n\n";
        String footer = "\nParsing failed.";
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar PhotoSimilarity-1.0-assembly.jar", header, options, footer, true);
    }

    private static void createWorkSpace() {
        Logger.getLogger(Main.class.getName()).log(Level.INFO,"Creating PhotoSimilarity-Workspace folder.");
        new File(Resources.WORKSPACE).mkdir();
        new File(Resources.GRAPHS).mkdir();
        new File(Resources.PASTEC_IDS).mkdir();
        new File(Resources.MODEL).mkdir();
    }
}
