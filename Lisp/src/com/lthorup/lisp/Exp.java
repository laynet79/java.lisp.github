/* Project: LispLib
 * File:    Exp.java
 * Created: May 8, 2011
 */
package com.lthorup.lisp;

/** This class represents a Exp
 *
 * @author Layne
 */
public abstract class Exp {

    public enum Type {

        ANY, SYMBOL, NUMBER, STRING, LIST, ARRAY, FUNCTION, PRIMATIVE
    }
    protected Type type;

    public Exp(Type type) {
        this.type = type;
    }

    public Type type() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
    private static String[] typeName = {"any", "symbol", "number", "string", "list", "function", "primative"};

    public String typeName() {
        return typeName[type.ordinal()];
    }

    public static String typeName(Type type) {
        return typeName[type.ordinal()];
    }

    public abstract void print(PrintHandler p, int level);

    public abstract boolean equal(Exp e);
}
