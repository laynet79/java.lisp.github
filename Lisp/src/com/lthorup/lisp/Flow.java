/* Project: LispLib
 * File:    Flow.java
 * Created: May 11, 2011
 */
package com.lthorup.lisp;

/** This class represents a Flow
 *
 * @author Layne
 */
public class Flow {

    public static void addPrims(SymbolTable s) {
        s.addPrim("let", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primLet(interp, args, env, level);
            }
        });
        s.addPrim("if", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primIf(interp, args, env, level);
            }
        });
        s.addPrim("cond", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primCond(interp, args, env, level);
            }
        });
        s.addPrim("rep", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primRep(interp, args, env, level);
            }
        });
        s.addPrim("while", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primWhile(interp, args, env, level);
            }
        });
        s.addPrim("for", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primFor(interp, args, env, level);
            }
        });
    }

    private static Exp primLet(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        List locals = (List) argList.next(Exp.Type.LIST, null, false, level);
        List body = argList.rest();
        List newEnv = env;
        while (locals != List.Nil) {
            if (locals.head().type() == Exp.Type.SYMBOL) {
                List newVar = new List(locals.head(), List.Nil);
                newEnv = new List(newVar, newEnv);
            }
            else
                if (locals.head().type() == Exp.Type.LIST) {
                    List var = (List) locals.head();
                    if (var.length() != 2 || var.head().type() != Exp.Type.SYMBOL) {
                        throw new Exception("bad local var def");
                    }
                    List newVar = new List(var.head(), interp.eval(var.tail().head(), newEnv, level));
                    newEnv = new List(newVar, newEnv);
                }
                else {
                    throw new Exception("bad local var def");
                }
            locals = locals.tail();
        }
        return interp.evalBody(body, newEnv, level);
    }

    private static Exp primIf(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Exp c = argList.next(Exp.Type.ANY, env, false, level);
        Exp i = argList.next(Exp.Type.ANY, null, false, level);
        Exp t = argList.next(Exp.Type.ANY, null, true, level);
        if (c == interp.symTable().T)
            return interp.eval(i, env, level);
        return interp.eval(t, env, level);
    }

    private static Exp primCond(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        while (!argList.empty()) {
            List n = (List) argList.next(Exp.Type.LIST, null, false, level);
            if (n == List.Nil)
                throw new Exception("bad condition");
            Exp c = interp.eval(n.head(), env, level);
            if (c == interp.symTable().T) {
                return interp.evalBody(n.tail(), env, level);
            }
        }
        return List.Nil;
    }

    private static Exp primRep(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Number c = (Number) argList.next(Exp.Type.NUMBER, env, false, level);
        List body = args;
        Exp value = List.Nil;
        long cnt = (long) c.value();
        while (cnt > 0) {
            value = interp.evalBody(body, env, level);
            cnt--;
        }
        return value;
    }

    private static Exp primWhile(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Exp e = argList.next(Exp.Type.ANY, null, false, level);
        List body = args;
        Exp value = List.Nil;
        Exp test = interp.eval(e, env, level);
        while (test == interp.symTable().T) {
            value = interp.evalBody(body, env, level);
            test = interp.eval(e, env, level);
        }
        return value;
    }

    private static Exp primFor(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Symbol s = (Symbol) argList.next(Exp.Type.SYMBOL, null, false, level);
        Number from = (Number) argList.next(Exp.Type.NUMBER, env, false, level);
        Number to = (Number) argList.next(Exp.Type.NUMBER, env, false, level);
        List body = args;
        int f = (int) from.value();
        int t = (int) to.value();
        List b = new List(s, List.Nil);
        List newEnv = new List(b, env);
        Exp value = List.Nil;
        for (int i = f; i <= t; i++) {
            b.setTail(new Number(i));
            value = interp.evalBody(body, newEnv, level);
        }
        return value;
    }

    private static Exp primSleep(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Number ms = (Number) argList.next(Exp.Type.NUMBER, env, true, level);
        Thread.sleep((int) (ms.value() * 1000.0));
        return interp.symTable().T;
    }
}
