/* Project: LispLib
 * File:    ArgIter.java
 * Created: May 8, 2011
 */
package com.lthorup.lisp;

/** This class represents a ArgIter
 *
 * @author Layne
 */
public class ArgList {

    private Lisp interp;
    private List args;

    public ArgList(Lisp interp, List args) {
        this.interp = interp;
        this.args = args;
    }

    public boolean empty() {
        return args == List.Nil;
    }

    public Exp next(Exp.Type type, List env, boolean last, int level) throws Exception {
        if (args == List.Nil || (last && args.tail() != List.Nil))
            throw new Exception("wrong number of arguments");
        Exp arg = args.head();
        if (env != null) {
            arg = interp.eval(arg, env, level);
        }
        if (type != Exp.Type.ANY && arg.type() != type) {
            throw new Exception(String.format("%s expected", Exp.typeName(type)));
        }
        args = args.tail();
        return arg;
    }

    public List rest() {
        List r = args;
        args = List.Nil;
        return r;
    }
}
