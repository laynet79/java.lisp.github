/* Project: LispLib
 * File:    Function.java
 * Created: May 8, 2011
 */
package com.lthorup.lisp;

/** This class represents a Function
 *
 * @author Layne
 */
public class Function extends Exp {

    private List vars;
    private List body;
    private boolean macro;

    public Function(List vars, List body, boolean macro) {
        super(Type.FUNCTION);
        this.vars = vars;
        this.body = body;
        this.macro = macro;
    }

    public List vars() {
        return vars;
    }

    public List body() {
        return body;
    }

    public boolean macro() {
        return macro;
    }

    @Override
    public void print(PrintHandler p, int level) {
        p.print("<FUNCTION>");
    }

    @Override
    public boolean equal(Exp e) {
        return this == e;
    }

    public static void addPrims(SymbolTable s) {
        s.addPrim("defun", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primDefun(interp, args, env, level);
            }
        });
        s.addPrim("demacro", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primDefmacro(interp, args, env, level);
            }
        });
        s.addPrim("lambda", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primLambda(interp, args, env, level);
            }
        });
    }

    private static Exp makeFunction(ArgList argList, boolean macro, List env, int level) throws Exception {
        List vars = (List) argList.next(Exp.Type.LIST, null, false, level);
        List body = argList.rest();
        List v = vars;
        while (v != List.Nil) {
            if (v.head().type() != Exp.Type.SYMBOL)
                throw new Exception("bad argument list");
            v = v.tail();
        }
        return new Function(vars, body, macro);
    }

    public static Exp primDefun(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Symbol sym = (Symbol) argList.next(Exp.Type.SYMBOL, null, false, level);
        sym.setValue(makeFunction(argList, false, env, level));
        return sym;
    }

    private static Exp primDefmacro(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Symbol sym = (Symbol) argList.next(Exp.Type.SYMBOL, null, false, level);
        sym.setValue(makeFunction(argList, true, env, level));
        return sym;
    }

    private static Exp primLambda(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        return makeFunction(argList, false, env, level);
    }
}
