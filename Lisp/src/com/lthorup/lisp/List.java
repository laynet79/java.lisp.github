/* Project: LispLib
 * File:    List.java
 * Created: May 8, 2011
 */
package com.lthorup.lisp;

/** This class represents a List
 *
 * @author Layne
 */
public class List extends Exp {

    private Exp head;
    private Exp tail;
    public static List Nil = new List(null, null);

    public List(Exp head, Exp tail) {
        super(Type.LIST);
        this.head = head;
        this.tail = tail;
    }

    public Exp head() {
        return head;
    }

    public List tail() {
        return (List) tail;
    }
    
    public Exp tailExp() {
        return tail;
    }

    public void setTail(Exp tail) {
        this.tail = tail;
    }

    public int length() {
        int len = 0;
        List n = this;
        while (n != List.Nil) {
            len++;
            n = n.tail();
        }
        return len;
    }

    public Exp get(int index) {
        List n = this;
        int i = 0;
        while (n != List.Nil) {
            if (i == index)
                return n.head;
            i++;
            n = n.tail();
        }
        return null;
    }

    public static Exp append(List a, List b) {
        if (a == List.Nil)
            return b;
        List front = new List(a.head, List.Nil);
        List back = front;
        a = a.tail();
        while (a != List.Nil) {
            back.setTail(new List(a.head, List.Nil));
            back = back.tail();
            a = a.tail();
        }
        back.setTail(b);
        return front;
    }

    @Override
    public void print(PrintHandler p, int level) {
        if (this == List.Nil) {
            if (level == 0)
                p.print("NIL");
            else
                p.print(")");
        }
        else {
            if (level == 0)
                p.print("(");
            else
                p.print(" ");

            if (this.tail.type != Type.LIST) {
                this.head.print(p, 0);
                p.print(" . ");
                this.tail.print(p, 0);
                p.print(")");
            }
            else {
                this.head.print(p, 0);
                this.tail.print(p, level + 1);
            }
        }
    }

    @Override
    public boolean equal(Exp e) {
        if (e == this)
            return true;
        if (e.type != Type.LIST)
            return false;
        List a = (List) e;
        List b = this;
        while (a != List.Nil && b != List.Nil) {
            if (! a.head.equal(b.head))
                return false;
            if (a.tail.type != Exp.Type.LIST || b.tail.type != Exp.Type.LIST)
                return a.tail.equals(b.tail);
            a = a.tail();
            b = b.tail();
        }
        return a == List.Nil && b == List.Nil;
    }

    public static void addPrims(SymbolTable s) {
        s.addPrim("listp", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primListp(interp, args, env, level);
            }
        });
        s.addPrim("cons", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primCons(interp, args, env, level);
            }
        });
        s.addPrim("list", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primList(interp, args, env, level);
            }
        });
        s.addPrim("car", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primCar(interp, args, env, level);
            }
        });
        s.addPrim("cdr", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primCdr(interp, args, env, level);
            }
        });
        s.addPrim("length", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primLength(interp, args, env, level);
            }
        });
        s.addPrim("append", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primAppend(interp, args, env, level);
            }
        });
        s.addPrim("nth", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primNth(interp, args, env, level);
            }
        });
        s.addPrim("assoc", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primAssoc(interp, args, env, level);
            }
        });
    }

    private static Exp primListp(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Exp e = argList.next(Exp.Type.ANY, env, true, level);
        if (e.type() == Exp.Type.LIST)
            return interp.symTable().T;
        return List.Nil;
    }

    private static Exp primCons(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Exp head = argList.next(Exp.Type.ANY, env, false, level);
        Exp tail = argList.next(Exp.Type.ANY, env, true, level);
        return new List(head, tail);
    }

    private static Exp primList(Lisp interp, List args, List env, int level) throws Exception {
        if (args == List.Nil)
            return List.Nil;
        Exp head = interp.eval(args.head(), env, level);
        List tail = (List) primList(interp, args.tail(), env, level);
        return new List(head, tail);
    }

    private static Exp primCar(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        List n = (List) argList.next(Exp.Type.LIST, env, true, level);
        if (n == List.Nil)
            throw new Exception("empty list");
        return n.head();
    }

    private static Exp primCdr(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        List n = (List) argList.next(Exp.Type.LIST, env, true, level);
        if (n == List.Nil)
            throw new Exception("empty list");
        return n.tail();
    }

    private static Exp primLength(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Exp n = argList.next(Exp.Type.ANY, env, true, level);
        if (n.type() == Exp.Type.LIST)
            return new Number(((List) n).length());
        if (n.type() == Exp.Type.ARRAY)
            return new Number(((Array) n).size());
        throw new Exception("list or array expected");
    }

    private static Exp primAppend(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        List a = (List) argList.next(Exp.Type.LIST, env, false, level);
        List b = (List) argList.next(Exp.Type.LIST, env, false, level);
        return List.append(a, b);
    }

    private static Exp primNth(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Number i = (Number) argList.next(Exp.Type.NUMBER, env, false, level);
        Exp n = argList.next(Exp.Type.ANY, env, true, level);
        if (n.type() == Exp.Type.LIST)
            return ((List) n).get((int) (i.value()));
        if (n.type() == Exp.Type.ARRAY)
            return ((Array) n).get((int) (i.value()));
        throw new Exception("list or array expected");
    }

    private static Exp primAssoc(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Exp key = argList.next(Exp.Type.ANY, env, false, level);
        List data = (List) argList.next(Exp.Type.LIST, env, true, level);
        while (data != List.Nil) {
            if (data.head().type() != Exp.Type.LIST || data == List.Nil)
                throw new Exception("bad assoc record");
            List record = (List) data.head();
            if (key.equal(record.head()))
                return record.tail();
            data = data.tail();
        }
        return List.Nil;
    }
}
