/* Project: LispLib
 * File:    SymbolTable.java
 * Created: May 8, 2011
 */
package com.lthorup.lisp;

import java.util.*;

/** This class represents a SymbolTable
 *
 * @author Layne
 */
public class SymbolTable {

    public Symbol T, NIL, REST, QUOTE;
    private ArrayList<Symbol> table = new ArrayList<Symbol>();

    public SymbolTable() {
        T = add("T");
        T.setValue(T);
        Symbol nil = add("NIL");
        nil.setValue(List.Nil);
        REST = add("REST");
        QUOTE = add("QUOTE");
    }

    public final Symbol add(String name) {
        String uname = name.toUpperCase();
        for (Symbol s : table) {
            if (s.name().equals(uname))
                return s;
        }
        Symbol t = new Symbol(uname);
        table.add(t);
        return t;
    }

    public void addPrim(String name, Prim.Handler handler) {
        Symbol s = add(name);
        s.setValue(new Prim(handler));
    }

    public List lookupTraced() {
        List t = List.Nil;
        for (Symbol s : table) {
            if (s.trace())
                t = new List(s, t);
        }
        return t;
    }
}
