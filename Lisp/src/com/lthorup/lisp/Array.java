/* Project: LispLib
 * File:    Array.java
 * Created: May 8, 2011
 */
package com.lthorup.lisp;

/** This class represents a Array
 *
 * @author Layne
 */
public class Array extends Exp {

    private Exp[] data;
    private static final int MAX_ARRAY = 65536;

    public Array(int size) throws Exception {
        super(Type.ARRAY);
        if (size < 0 || size > MAX_ARRAY)
            throw new Exception("illegal array size");
        data = new Exp[size];
    }

    public int size() {
        return data.length;
    }

    public Exp get(int i) throws Exception {
        if (i < 0 || i >= data.length)
            throw new Exception("index out of range");
        if (data[i] == null)
            return List.Nil;
        return data[i];
    }

    public void set(int i, Exp value) throws Exception {
        if (i < 0 || i >= data.length)
            throw new Exception("index out of range");
        data[i] = value;
    }

    @Override
    public void print(PrintHandler p, int level) {
        if (data.length > 9)
            p.print(String.format("<ARRAY %d>", data.length));
        else {
            p.print("[");
            for (int i = 0; i < data.length; i++) {
                if (data[i] == null)
                    p.print("NIL");
                else
                    data[i].print(p, 0);
                if (i < (data.length - 1))
                    p.print(" ");
            }
            p.print("]");
        }
    }

    @Override
    public boolean equal(Exp e) {
        if (e == this)
            return true;
        if (e.type != Type.ARRAY)
            return false;
        Array a = (Array) e;
        if (a.data.length != data.length)
            return false;
        for (int i = 0; i < data.length; i++) {
            if (!a.data[i].equals(data[i]))
                return false;
        }
        return true;
    }

    public static void addPrims(SymbolTable s) {
        s.addPrim("arrayp", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primArrayp(interp, args, env, level);
            }
        });
        s.addPrim("array", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primArray(interp, args, env, level);
            }
        });
        s.addPrim("arrset", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primArrSet(interp, args, env, level);
            }
        });
    }

    private static Exp primArrayp(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Exp e = argList.next(Exp.Type.ANY, env, true, level);
        if (e.type() == Exp.Type.ARRAY)
            return interp.symTable().T;
        return List.Nil;
    }

    private static Exp primArray(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Exp first = argList.next(Exp.Type.ANY, env, false, level);
        if (argList.empty() && first.type() == Exp.Type.NUMBER) {
            Number size = (Number) first;
            return new Array((int) size.value());
        }
        Array a = new Array(args.length());
        a.set(0, first);
        int i = 1;
        while (!argList.empty())
            a.set(i++, argList.next(Exp.Type.ANY, env, false, level));
        return a;
    }

    private static Exp primArrSet(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Number i = (Number) argList.next(Exp.Type.NUMBER, env, false, level);
        Array a = (Array) argList.next(Exp.Type.ARRAY, env, false, level);
        Exp e = argList.next(Exp.Type.ANY, env, true, level);
        a.set((int) (i.value()), e);
        return e;
    }
}
