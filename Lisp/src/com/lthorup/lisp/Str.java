/* Project: LispLib
 * File:    Str.java
 * Created: May 8, 2011
 */
package com.lthorup.lisp;

/** This class represents a Str
 *
 * @author Layne
 */
public class Str extends Exp {

    private String value;

    public Str(String value) {
        super(Type.STRING);
        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    public void print(PrintHandler p, int level) {
        p.print(String.format("\"%s\"", value));
    }

    @Override
    public boolean equal(Exp e) {
        if (e == this)
            return true;
        if (e.type != Type.STRING)
            return false;
        Str s = (Str) e;
        return value.equals(s.value);
    }

    @Override
    public String toString() {
        return value;
    }

    public static void addPrims(SymbolTable s) {
        s.addPrim("stringp", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primStringp(interp, args, env, level);
            }
        });
        s.addPrim("str", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primStr(interp, args, env, level);
            }
        });
        s.addPrim("strlen", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primStrLen(interp, args, env, level);
            }
        });
        s.addPrim("substr", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primStrSub(interp, args, env, level);
            }
        });
        s.addPrim("indexof", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primStrIndexOf(interp, args, env, level);
            }
        });
    }

    private static Exp primStringp(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Exp e = argList.next(Exp.Type.ANY, env, true, level);
        if (e.type() == Exp.Type.STRING)
            return interp.symTable().T;
        return List.Nil;
    }

    private static Exp primStr(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Exp e = argList.next(Exp.Type.ANY, env, false, level);
        String str = e.toString();
        while (!argList.empty()) {
            e = argList.next(Exp.Type.ANY, env, false, level);
            str = str + e.toString();
        }
        return new Str(str);
    }

    private static Exp primStrLen(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Str s = (Str) argList.next(Exp.Type.STRING, env, true, level);
        return new Number(s.value().length());
    }

    private static Exp primStrSub(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Str s = (Str) argList.next(Exp.Type.STRING, env, false, level);
        Number start = (Number) argList.next(Exp.Type.NUMBER, env, false, level);
        Number len = (Number) argList.next(Exp.Type.NUMBER, env, true, level);
        int startIndex = (int) start.value();
        int length = (int) len.value();
        return new Str(s.value().substring(startIndex, startIndex + length));
    }

    private static Exp primStrIndexOf(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Str s = (Str) argList.next(Exp.Type.STRING, env, false, level);
        Str t = (Str) argList.next(Exp.Type.STRING, env, true, level);
        return new Number(s.value().indexOf(t.value()));
    }
}
