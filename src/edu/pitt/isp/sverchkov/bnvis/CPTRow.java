/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pitt.isp.sverchkov.bnvis;

import java.util.Map;

/**
 *
 * @author YUS24
 */
public interface CPTRow extends Iterable<Double>{
    Map<String, String> parentAssignment();
}
