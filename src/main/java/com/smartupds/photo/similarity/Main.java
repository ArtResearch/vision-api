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
//            args = new String[]{  "-image_ids","{\"image_ids\":[],\"type\":\"INDEX_IMAGE_IDS\"}",
//                                  "-m","Pastec"};
//            args = new String[]{    "-image_ids","{\"status\": \"ok\", \"error\": [], \"method\": \"list\", \"result\": [\"3\", \"1\", \"4\", \"\", \"2\"]}",
//                                    "-m","match"};
//            args = new String[]{    "-p","http://localhost:9999/blazegraph/sparql",
//                                    "-e","https://vision.artresearch.net/sparql",
//                                    "-visionModel","C:\\Users\\mafragias\\Documents\\WORKSPACE\\NetBeansProjects\\PhotoSimilarity\\PhotoSimilarity-Workspace\\IDs\\2020-11-24T12-17-41_matchIDs.json",
//                                    "-m","Match"};
//            args =  new String[]{   "-image_url","https://ids.lib.harvard.edu/ids/iiif/406553072/full/full/0/native.jpg"};
//
//            args = new String[] {   "-m","pastec",
//                                    "-e","https://artresearch.net/sparql",
//                                    "-pharos_user","user",
//                                    "-pharos_password","pharosadmin",
//                                    "-json_file","./PhotoSimilarity-Workspace/IDs/2021-01-15T17-52-46_pastecIDs.json",
//                                    "-pharosModel","./PhotoSimilarity-Workspace/IDs/2021-01-15T17-52-46_pastecIDs.ttl"
//            };
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
        Option json_file = new Option("json_file",true,"Update Pharos model : -json_file [json_file.json]");
        Option pharos_model = new Option("pharosModel",true,"Update Pharos model : -pharosModel [index_graph.ttl]");
        Option pharos_user = new Option("pharos_user",true,"Pharos username : -pharos_user [username]");
        Option pharos_password = new Option("pharos_password",true,"Pharos password : -pharos_password [password]");
        Option vision_model = new Option("visionModel",true,"Update Vision model : -visionModel [similarity_file.json]");
        Option vision_user = new Option("vision_user",true,"Vision username : -vision_user [username]");
        Option vision_password = new Option("vision_password",true,"Vision password : -vision_password [password]");
        Option image_url = new Option("image_url",true,"Resize image : -image_url [url]");
        
        options.addOption(query);
        options.addOption(endpoint);
        options.addOption(pharos_endpoint);
        options.addOption(method);
        options.addOption(image_ids);
        options.addOption(json_file);
        options.addOption(pharos_model);
        options.addOption(pharos_user);
        options.addOption(pharos_password);
        options.addOption(vision_model);
        options.addOption(vision_user);
        options.addOption(vision_password);
        options.addOption(image_url);
    }

    private static void handleCommandLine(CommandLine line) {
        if (line.hasOption("m")){
            Resources.setMethod(line.getOptionValue("m"));
            Logger.getLogger(Main.class.getName()).log(Level.INFO,"Selected Method : ".concat(Resources.SIMILARITY_METHOD));
        }
        if (line.hasOption("pharos_user") && line.hasOption("pharos_password")){
            Resources.setPharosUsernameAndPassword(line.getOptionValue("pharos_user"),line.getOptionValue("pharos_password"));
            Logger.getLogger(Main.class.getName()).log(Level.INFO,"Setting Pharos Configuration.");
        } else 
            Logger.getLogger(Main.class.getName()).log(Level.INFO,"Setting Pharos Default Configurations.");
        if (line.hasOption("vision_user") && line.hasOption("vision_password")){
            Resources.setVisionUsernameAndPassword(line.getOptionValue("vision_user"),line.getOptionValue("vision_password"));
            Logger.getLogger(Main.class.getName()).log(Level.INFO,"Setting Vision Configuration.");
        } else 
            Logger.getLogger(Main.class.getName()).log(Level.INFO,"Setting Vision Default Configurations.");
        if (line.hasOption("q") && line.hasOption("e")){
            QueryHandler q = new QueryHandler(line.getOptionValue("q"));
            q.setRepository(line.getOptionValue("e")); 
            q.createGraph();
        } else if (line.hasOption("image_ids")){
            IndexHandler ih = new IndexHandler(line.getOptionValue("image_ids"));
            ih.handle();
        } else if (line.hasOption("pharosModel") && line.hasOption("e")){
            ModelGenerator model = new ModelGenerator(line.getOptionValue("pharosModel"));
            model.generateIndexModel(line.getOptionValue("json_file"));
            model.setRepository(line.getOptionValue("e"));
            model.updateIndexes();
        } else if (line.hasOption("visionModel")){
            ModelGenerator model = new ModelGenerator(Utils.listJSONFilesForFolder(new File(line.getOptionValue("visionModel"))));
            model.setRepository(line.getOptionValue("e"));
            model.getIndexes(line.getOptionValue("p"));
            model.generate();
        } else if (line.hasOption("image_url")){
            Utils.resizeImage(line.getOptionValue("image_url"));
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
