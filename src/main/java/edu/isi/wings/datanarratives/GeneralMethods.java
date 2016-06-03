/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.isi.wings.datanarratives;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.util.FileManager;
import java.io.InputStream;

/**
 *
 * @author dgarijo
 */
public class GeneralMethods {
    /**
     * Method for querying a SPARQL repository
     * @param endpointURL repository URI
     * @param queryIn query to perforn
     * @return result of the query
     */
    public static ResultSet queryOnlineRepository(String endpointURL, String queryIn){
        Query query = QueryFactory.create(queryIn);
        //System.out.println(queryIn);
        QueryExecution qe = QueryExecutionFactory.sparqlService(endpointURL, query);
        ResultSet rs = qe.execSelect();        
        return rs;
    }
    
    public static void constructWithOnlineRepository(String endpointURL, String queryIn, OntModel m){
        Query query = QueryFactory.create(queryIn);
        //System.out.println(queryIn);
        QueryExecution qe = QueryExecutionFactory.sparqlService(endpointURL, query);
        qe.execConstruct(m);        
    }
    
    /**
     * Method to perform a SELECT query to a local repository
     * @param model which we want to query
     * @param queryIn query to be launched at the model
     * @return results
     */
    public static ResultSet queryLocalRepository(OntModel model, String queryIn){
        Query query = QueryFactory.create(queryIn);
        //System.out.println(queryIn);
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        ResultSet rs = qe.execSelect(); 
        return rs;
    }
    
    public static void readFileIntoKnowledgeBase (OntModel model, String file){
        InputStream in = FileManager.get().open(file);
        if (in == null) {
            throw new IllegalArgumentException("DOI File: " + file + " not found");
        }
        try{
            model.read(in, null, "TTL");
        }catch(Exception e){
            model.read(in, null);//rdf/xml by default
        }
    }
}
