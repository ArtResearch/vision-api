/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartupds.photo.similarity;

import com.smartupds.photo.similarity.common.Resources;
import com.smartupds.photo.similarity.common.Utils;
import com.smartupds.photo.similarity.impl.IndexHandler;
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
//            String[] fake_args = {  "-e", "http://localhost:9999/blazegraph/sparql",
//                                    "-q", "query",
//                                    "-m", "Pastec"};
//            String[] fake_args = {  "-p","http://localhost:9999/blazegraph/sparql",
//                                    "-e","https://vision.artresearch.net/sparql",
//                                    "-m","Pastec",
//                                    "-visionModel","C:\\Users\\mafragias\\Documents\\WORKSPACE\\NetBeansProjects\\PhotoSimilarity\\PhotoSimilarity-Workspace\\IDs\\2020-11-09T14-14-46_pastecIDs.json"};
//            String[] fake_args = {  "-image_ids","{\"image_ids\":[\"3\",\"1\",\"2\"],\"type\":\"INDEX_IMAGE_IDS\"}"};
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
        Option endpoint = new Option("e",true,"Endpoint: -e [endpoint]");
        endpoint.setArgName("endpoint");
        Option pharos_endpoint = new Option("p",true,"Pharos endpoint: -e [pharos_endpoint]");
        pharos_endpoint.setArgName("pharos_endpoint");
        Option method = new Option("m",true,"Method name : -m [method_name]");
        method.setArgName("method");
        Option image_ids = new Option("image_ids",true,"Image IDs: -image_ids [image_ids in json format]");
        endpoint.setArgName("image_ids");
        Option pharos_model = new Option("pharosModel",true,"Update Pharos model : -pharosModel [index_graph.ttl]");
        Option vision_model = new Option("visionModel",true,"Update Vision model : -visionModel [similarity_file.json]");
        
        options.addOption(query);
        options.addOption(endpoint);
        options.addOption(pharos_endpoint);
        options.addOption(method);
        options.addOption(image_ids);
        options.addOption(pharos_model);
        options.addOption(vision_model);
    }

    private static void handleCommandLine(CommandLine line) {
        if (line.hasOption("m")){
            Resources.setMethod(line.getOptionValue("m"));
            Logger.getLogger(Main.class.getName()).log(Level.INFO,"Selected Method : ".concat(Resources.SIMILARITY_METHOD));
        }
        if (line.hasOption("q") && line.hasOption("e")){
            QueryHandler q = new QueryHandler(line.getOptionValue("q"));
            q.setRepository(line.getOptionValue("e")); 
            q.createGraph();
        } else if (line.hasOption("image_ids")){
            IndexHandler ih = new IndexHandler(line.getOptionValue("image_ids"));
            ih.handle();
        } else if (line.hasOption("pharosModel") && line.hasOption("e")){
            ModelGenerator model = new ModelGenerator(line.getOptionValue("pharosModel"));
            model.setRepository(line.getOptionValue("e"));
            model.updateIndexes();
        } else if (line.hasOption("visionModel")){
            ModelGenerator model = new ModelGenerator(Utils.listJSONFilesForFolder(new File(line.getOptionValue("visionModel"))));
            model.setRepository(line.getOptionValue("e"));
            model.getIndexes(line.getOptionValue("p"));
//            System.out.println(line.getOptionValue("visionModel"));
//            System.out.println(line.getOptionValue("p"));
//            System.out.println(line.getOptionValue("e"));
            model.generate();
        } else {
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
        new File(Resources.IDS).mkdir();
        new File(Resources.MODEL).mkdir();
    }
}
