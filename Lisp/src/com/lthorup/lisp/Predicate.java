/* Project: LispLib
 * File:    Predicate.java
 * Created: May 11, 2011
 */
package com.lthorup.lisp;

/** This class represents a Predicate
 *
 * @author Layne
 */
public class Predicate {

    public static void addPrims(SymbolTable s) {
        s.addPrim("equal", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primEqual(interp, args, env, level);
            }
        });
        s.addPrim("not", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primNot(interp, args, env, level);
            }
        });
        s.addPrim("and", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primAnd(interp, args, env, level);
            }
        });
        s.addPrim("null", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primNull(interp, args, env, level);
            }
        });
        s.addPrim("atom", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primAtom(interp, args, env, level);
            }
        });
    }

    private static Exp primEqual(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Exp a = argList.next(Exp.Type.ANY, env, false, level);
        Exp b = argList.next(Exp.Type.ANY, env, true, level);
        if (a.equal(b))
            return interp.symTable().T;
        return List.Nil;
    }

    private static Exp primNot(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Exp e = argList.next(Exp.Type.ANY, env, true, level);
        if (e == interp.symTable().T)
            return List.Nil;
        return interp.symTable().T;
    }

    private static Exp primAnd(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Exp a = argList.next(Exp.Type.ANY, env, false, level);
        Exp b = argList.next(Exp.Type.ANY, env, false, level);
        boolean result = (a == interp.symTable().T && b == interp.symTable().T);
        while (result && !argList.empty()) {
            a = argList.next(Exp.Type.ANY, env, false, level);
            result = result && (a == interp.symTable().T);
        }
        if (result)
            return interp.symTable().T;
        return List.Nil;
    }

    private static Exp primNull(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Exp e = argList.next(Exp.Type.ANY, env, true, level);
        if (e == List.Nil)
            return interp.symTable().T;
        return List.Nil;
    }

    private static Exp primAtom(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Exp e = argList.next(Exp.Type.ANY, env, true, level);
        if (e == List.Nil || e.type() == Exp.Type.NUMBER || e.type() == Exp.Type.SYMBOL)
            return interp.symTable().T;
        return List.Nil;
    }
}
