/* Project: LispLib
 * File:    Symbol.java
 * Created: May 8, 2011
 */
package com.lthorup.lisp;

/** This class represents a Symbol
 *
 * @author Layne
 */
public class Symbol extends Exp {

    private String name;
    private Exp value;
    private boolean trace;

    public Symbol(String name) {
        super(Type.SYMBOL);
        this.name = name;
        this.value = null;
        this.trace = false;
    }

    public String name() {
        return name;
    }

    public Exp value() {
        return value;
    }

    public void setValue(Exp value) {
        this.value = value;
    }

    public boolean trace() {
        return trace;
    }

    public void setTrace(boolean trace) {
        this.trace = trace;
    }

    @Override
    public void print(PrintHandler p, int level) {
        p.print(name);
    }

    @Override
    public boolean equal(Exp e) {
        return this == e;
    }

    @Override
    public String toString() {
        return name;
    }

    public static void addPrims(SymbolTable s) {
        s.addPrim("symbolp", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primSymbolp(interp, args, env, level);
            }
        });
        s.addPrim("setq", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primSetq(interp, args, env, level);
            }
        });
        s.addPrim("tosym", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primToSym(interp, args, env, level);
            }
        });
    }

    private static Exp primSymbolp(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Exp e = argList.next(Exp.Type.ANY, env, true, level);
        if (e.type() == Exp.Type.SYMBOL)
            return interp.symTable().T;
        return List.Nil;
    }

    private static Exp primSetq(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Symbol sym = (Symbol) argList.next(Exp.Type.SYMBOL, null, false, level);
        Exp value = argList.next(Exp.Type.ANY, env, true, level);
        while (env != List.Nil) {
            List binding = (List) env.head();
            if (sym == binding.head()) {
                binding.setTail(value);
                return value;
            }
            env = env.tail();
        }
        sym.setValue(value);
        return value;
    }

    private static Exp primToSym(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Str s = (Str) argList.next(Exp.Type.STRING, env, true, level);
        return interp.symTable().add(s.value());
    }
}
