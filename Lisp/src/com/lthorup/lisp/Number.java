/* Project: LispLib
 * File:    Number.java
 * Created: May 8, 2011
 */
package com.lthorup.lisp;

/** This class represents a Number
 *
 * @author Layne
 */
public class Number extends Exp {

    private double value;

    public Number(double value) {
        super(Type.NUMBER);
        this.value = value;
    }

    public double value() {
        return value;
    }

    @Override
    public void print(PrintHandler p, int level) {
        p.print(toString());
    }

    @Override
    public boolean equal(Exp e) {
        if (e == this)
            return true;
        if (e.type != Type.NUMBER)
            return false;
        Number a = (Number) e;
        return a.value == value;
    }

    @Override
    public String toString() {
        return String.format("%g", value);
    }

    public static void addPrims(SymbolTable s) {
        Symbol pi = s.add("PI");
        pi.setValue(new Number(Math.PI));

        s.addPrim("numberp", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primNumberp(interp, args, env, level);
            }
        });
        s.addPrim("=", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primEQ(interp, args, env, level);
            }
        });
        s.addPrim("<", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primLT(interp, args, env, level);
            }
        });
        s.addPrim("<=", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primLTE(interp, args, env, level);
            }
        });
        s.addPrim(">", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primGT(interp, args, env, level);
            }
        });
        s.addPrim(">=", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primGTE(interp, args, env, level);
            }
        });
        s.addPrim("+", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primPlus(interp, args, env, level);
            }
        });
        s.addPrim("-", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primMinus(interp, args, env, level);
            }
        });
        s.addPrim("*", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primMult(interp, args, env, level);
            }
        });
        s.addPrim("/", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primDiv(interp, args, env, level);
            }
        });
        s.addPrim("int", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primInt(interp, args, env, level);
            }
        });
        s.addPrim("frac", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primFrac(interp, args, env, level);
            }
        });
        s.addPrim("sqrt", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primSqrt(interp, args, env, level);
            }
        });
        s.addPrim("rand", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primRand(interp, args, env, level);
            }
        });
        s.addPrim("sin", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primSin(interp, args, env, level);
            }
        });
        s.addPrim("cos", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primCos(interp, args, env, level);
            }
        });
        s.addPrim("tan", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primTan(interp, args, env, level);
            }
        });
    }

    private static Exp primNumberp(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Exp e = argList.next(Exp.Type.ANY, env, true, level);
        if (e.type() == Exp.Type.NUMBER)
            return interp.symTable().T;
        return List.Nil;
    }

    private static Exp primEQ(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Number a = (Number) argList.next(Exp.Type.NUMBER, env, false, level);
        Number b = (Number) argList.next(Exp.Type.NUMBER, env, true, level);
        if (a.value() == b.value())
            return interp.symTable().T;
        return List.Nil;
    }

    private static Exp primLT(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Number a = (Number) argList.next(Exp.Type.NUMBER, env, false, level);
        Number b = (Number) argList.next(Exp.Type.NUMBER, env, true, level);
        if (a.value() < b.value())
            return interp.symTable().T;
        return List.Nil;
    }

    private static Exp primLTE(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Number a = (Number) argList.next(Exp.Type.NUMBER, env, false, level);
        Number b = (Number) argList.next(Exp.Type.NUMBER, env, true, level);
        if (a.value() <= b.value())
            return interp.symTable().T;
        return List.Nil;
    }

    private static Exp primGT(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Number a = (Number) argList.next(Exp.Type.NUMBER, env, false, level);
        Number b = (Number) argList.next(Exp.Type.NUMBER, env, true, level);
        if (a.value() > b.value())
            return interp.symTable().T;
        return List.Nil;
    }

    private static Exp primGTE(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Number a = (Number) argList.next(Exp.Type.NUMBER, env, false, level);
        Number b = (Number) argList.next(Exp.Type.NUMBER, env, true, level);
        if (a.value() >= b.value())
            return interp.symTable().T;
        return List.Nil;
    }

    private static Exp primPlus(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Number a = (Number) argList.next(Exp.Type.NUMBER, env, false, level);
        Number b = (Number) argList.next(Exp.Type.NUMBER, env, false, level);
        double sum = a.value() + b.value();
        while (!argList.empty()) {
            a = (Number) argList.next(Exp.Type.NUMBER, env, false, level);
            sum += a.value();
        }
        return new Number(sum);
    }

    private static Exp primMinus(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Number a = (Number) argList.next(Exp.Type.NUMBER, env, false, level);
        if (args == List.Nil)
            return new Number(-a.value());
        Number b = (Number) argList.next(Exp.Type.NUMBER, env, false, level);
        double dif = a.value() - b.value();
        while (!argList.empty()) {
            a = (Number) argList.next(Exp.Type.NUMBER, env, false, level);
            dif -= a.value();
        }
        return new Number(dif);
    }

    private static Exp primMult(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Number a = (Number) argList.next(Exp.Type.NUMBER, env, false, level);
        Number b = (Number) argList.next(Exp.Type.NUMBER, env, false, level);
        double prod = a.value() * b.value();
        while (!argList.empty()) {
            a = (Number) argList.next(Exp.Type.NUMBER, env, false, level);
            prod *= a.value();
        }
        return new Number(prod);
    }

    private static Exp primDiv(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Number a = (Number) argList.next(Exp.Type.NUMBER, env, false, level);
        Number b = (Number) argList.next(Exp.Type.NUMBER, env, false, level);
        double quo = a.value() / b.value();
        while (!argList.empty()) {
            a = (Number) argList.next(Exp.Type.NUMBER, env, false, level);
            quo /= a.value;
        }
        return new Number(quo);
    }

    private static Exp primInt(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Number a = (Number) argList.next(Exp.Type.NUMBER, env, true, level);
        return new Number((double) (long) a.value());
    }

    private static Exp primFrac(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Number a = (Number) argList.next(Exp.Type.NUMBER, env, true, level);
        return new Number(a.value() - (long) a.value());
    }

    private static Exp primSqrt(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Number a = (Number) argList.next(Exp.Type.NUMBER, env, true, level);
        return new Number(Math.sqrt(a.value()));
    }

    private static Exp primRand(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        if (args == List.Nil)
            return new Number(Math.random());
        else {
            Number s = (Number) argList.next(Exp.Type.NUMBER, env, false, level);
            Number e = (Number) argList.next(Exp.Type.NUMBER, env, true, level);
            int start = (int) s.value();
            int end = (int) e.value();
            if (start < 0 || end <= start)
                throw new Exception("bad arguments");
            return new Number(Math.random() * (end - start + 1));
        }
    }

    private static Exp primSin(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Number a = (Number) argList.next(Exp.Type.NUMBER, env, true, level);
        return new Number(Math.sin(a.value() * Math.PI / 180.0));
    }

    private static Exp primCos(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Number a = (Number) argList.next(Exp.Type.NUMBER, env, true, level);
        return new Number(Math.cos(a.value() * Math.PI / 180.0));
    }

    private static Exp primTan(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Number a = (Number) argList.next(Exp.Type.NUMBER, env, true, level);
        return new Number(Math.tan(a.value() * Math.PI / 180.0));
    }
}
