/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.bnvis;

import java.util.Collection;

/**
 *
 * @author YUS24
 */
public interface BNNodeModel {
    
    String name();
    Collection<String> values();
    Collection<? extends CPTRow> activeCPTS();
    
}
