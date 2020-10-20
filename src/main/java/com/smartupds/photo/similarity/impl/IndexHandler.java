/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartupds.photo.similarity.impl;

import com.smartupds.photo.similarity.common.Utils;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author mafragias
 */
public class IndexHandler {

    private final HashSet<BigInteger> indexes;

    public IndexHandler(String indexes) {
        String[] index = indexes.substring(indexes.indexOf("[")+1,indexes.indexOf("]")).split(",");
        if (Utils.isNumeric(index[0]))
            this.indexes = Arrays.stream(index).map(i ->new BigInteger(i)).collect(Collectors.toCollection(HashSet::new));
        else
            this.indexes = new HashSet<>();
    }
    
    public void handle() {
        Logger.getLogger(IndexHandler.class.getName()).log(Level.INFO, "Handling Pastec indexes.");
        if (this.indexes.size()>0)
          System.out.print(Collections.max(indexes));
        else
          System.out.print(0);
        Logger.getLogger(IndexHandler.class.getName()).log(Level.INFO, "Current max Pastec index returned.");
    }
    
}
