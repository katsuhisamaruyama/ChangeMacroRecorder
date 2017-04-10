/*
 *  Copyright 2017
 *  Software Science and Technology Lab.
 *  Department of Computer Science, Ritsumeikan University
 */

package org.jtool.macrorecorder.internal.diff;

import org.jtool.macrorecorder.internal.diff.diff_match_patch.Diff;
import org.jtool.macrorecorder.internal.diff.diff_match_patch.Operation;
import org.jtool.macrorecorder.macro.DocumentMacro;
import org.jtool.macrorecorder.macro.MacroPath;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Generates diff macros from differences between the contents of two the source files.
 * @author Katsuhisa Maruyama
 */
public class DiffMacroGenerator {
    
    /**
     * The cost of an empty edit operation in terms of edit characters.
     */
    private static short editCost = 4;
    
    /**
     * Sets the edit costs for finding difference.
     * @param cost the edit cost to be set
     */
    public static void setEditCost(short cost) {
        editCost = cost;
    }
    
    /**
     * Generates diff macros from the differences between two textual contents.
     * @param path the path of a file on which the macros were performed
     * @param branch the branch name of a file on which the macros were performed
     * @param otext the contents of the source code file to be diffed
     * @param ntext the contents of the source code file to be diffed
     * @return the collection of the code deltas
     */
    public static List<DiffMacro> generate(String path, String branch, String otext, String ntext) {
        assert otext != null;
        assert ntext != null;
        
        diff_match_patch dmp = new diff_match_patch();
        dmp.Diff_EditCost = editCost;
        
        LinkedList<Diff> diffs = dmp.diff_main(otext, ntext);
        dmp.diff_cleanupEfficiency(diffs);
        
        List<DiffMacro> macros = getDeltas(path, branch, diffs);
        List<DiffMacro> aggregatedMacros = aggregate(macros);
        
        return aggregate(aggregatedMacros);
    }
    
    /**
     * Obtains the deltas from a given difference information.
     * @param path the path of a file on which the macros were performed
     * @param branch the branch name of a file on which the macros were performed
     * @param diffs the collection of difference information of the diff utility.
     * @return the collection of the code deltas
     */
    private static List<DiffMacro> getDeltas(String path, String branch, LinkedList<Diff> diffs) {
        List<DiffMacro> macros = new ArrayList<DiffMacro>();
        int offset = 0;
        int offsetGap = 0;
        
        for (ListIterator<Diff> pointer = diffs.listIterator(); pointer.hasNext(); ) { 
            Diff diff = pointer.next();
            DiffMacro.Action action = null;
            String itext = "";
            String dtext = "";
            
            if (diff.operation == Operation.INSERT) {
                action = DocumentMacro.Action.AUTO_DIFF;
                itext = diff.text;
                
            } else if (diff.operation == Operation.DELETE) {
                action = DocumentMacro.Action.AUTO_DIFF;
                dtext = diff.text;
            }
            
            if (action != null) {
                int start = offset + offsetGap;
                DiffMacro macro = new DiffMacro(action, new MacroPath(path), branch, start, itext, dtext);
                macros.add(macro);
                
                offsetGap = offsetGap - dtext.length();
            }
            
            offset = offset + diff.text.length();
        }
        
        return macros;
    }
    
    /**
     * Aggregates the insertion and deletion diff macros that are performed consecutively at the same position.
     * @param macros the collection of diff macros that represent either of insertion or deletion
     * @return the collection of the aggregated diff macros
     */
    private static List<DiffMacro> aggregate(List<DiffMacro> macros) {
        if (macros.size() < 2) {
            return macros;
        }
        
        List<DiffMacro> aggregatedMacros = new ArrayList<DiffMacro>();
        aggregatedMacros.add(macros.get(0));
        for (int idx = 1; idx < macros.size(); idx++) {
            DiffMacro prevMacro = macros.get(idx - 1);
            DiffMacro curMacro = macros.get(idx);
            
            if (prevMacro.deleted() && curMacro.inserted() &&
                prevMacro.getStart() == curMacro.getStart()) {
                prevMacro.setInsertedText(curMacro.getInsertedText());
            } else {
                aggregatedMacros.add(curMacro);
            }
        }
        
        return aggregatedMacros;
    }
}
