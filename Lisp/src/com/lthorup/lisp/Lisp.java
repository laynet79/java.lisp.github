/* Project: LispLib
 * File:    LispInterpreter.java
 * Created: May 8, 2011
 */
package com.lthorup.lisp;

import java.util.*;

/** This class represents a LispInterpreter
 *
 * @author Layne
 */
public class Lisp {

    private SymbolTable symTable;
    private Parser parser;
    private volatile boolean stopping;
    private volatile boolean breaking;
    private PrintHandler printer = new PrintHandler() {

        @Override
        public void print(String s) {
            System.out.print(s);
        }
    };

    public Lisp() {
        symTable = new SymbolTable();
        parser = new Parser(symTable);

        addPrims();
        Symbol.addPrims(symTable);
        List.addPrims(symTable);
        Array.addPrims(symTable);
        Number.addPrims(symTable);
        Str.addPrims(symTable);
        Predicate.addPrims(symTable);
        Flow.addPrims(symTable);
        Function.addPrims(symTable);
    }

    public SymbolTable symTable() {
        return symTable;
    }

    public void setPrinter(PrintHandler p) {
        printer = p;
    }

    public void Interpret(String input) {
        try {
            ArrayList<Exp> expList = parser.parse(input);
            stopping = false;
            breaking = false;
            for (Exp e : expList) {
                Exp value = eval(e, List.Nil, 0);
                value.print(printer, 0);
                printer.print("\r\n");
            }
        }
        catch (Exception err) {
            if (err.getMessage().length() != 0)
                printer.print(String.format("ERROR: %s\r\n", err.getMessage()));
        }
    }

    public void stop() {
        stopping = true;
    }

    public void go() {
        breaking = false;
    }

    public Exp eval(Exp e, List env, int level) throws Exception {
        try {
            if (stopping)
                throw new Exception("terminated");

            switch (e.type()) {
                case NUMBER:
                    return e;
                case STRING:
                    return e;
                case ARRAY:
                    return e;
                case SYMBOL:
                    return lookup((Symbol) e, env);
                default: // list
                {
                    if (e == List.Nil)
                        return e;
                    List exp = (List) e;
                    Exp op = eval(exp.head(), env, level + 1);
                    if (op.type() == Exp.Type.FUNCTION) {
                        Function f = (Function) op;
                        List newEnv;
                        if (f.macro())
                            newEnv = evalArgs(f.vars(), exp.tail(), null, level + 1);
                        else
                            newEnv = evalArgs(f.vars(), exp.tail(), env, level + 1);
                        boolean tracing = exp.head().type() == Exp.Type.SYMBOL && ((Symbol) exp.head()).trace();
                        if (tracing) {
                            for (int i = 0; i < level; i++) {
                                printer.print(" ");
                            }
                            printer.print(String.format("enter %s %d:\r\n", exp.head().toString(), level));
                            printEnv(newEnv, level);
                        }
                        Exp result = evalBody(f.body(), newEnv, level + 1);
                        if (tracing) {
                            for (int i = 0; i < level; i++)
                                printer.print(" ");
                            printer.print(String.format("exit %s %d: ", exp.head().toString(), level));
                            result.print(printer, 0);
                            printer.print("\r\n");
                        }
                        return result;
                    }
                    else
                        if (op.type() == Exp.Type.PRIMATIVE) {
                            Prim p = (Prim) op;
                            List args = exp.tail();
                            return p.handler.eval(this, args, env, level);
                        }
                        else
                            throw new Exception("bad function");
                }
            }
        }
        catch (Exception err) {
            if (err.getMessage().length() != 0)
                printer.print(String.format("ERROR: %s\r\n", err.getMessage()));
            e.print(printer, 0);
            printer.print("\r\n");
            printEnv(env, level);
            throw new Exception("");
        }
    }

    private void printEnv(List env, int level) {
        while (env != List.Nil) {
            for (int i = 0; i < level; i++)
                printer.print(" ");
            List binding = (List) env.head();
            Symbol name = (Symbol) binding.head();
            Exp value = binding.tailExp();
            printer.print(String.format(" %s: ", name.name()));
            value.print(printer, 0);
            printer.print("\r\n");
            env = env.tail();
        }
    }

    private List evalArgs(List vars, List args, List env, int level) throws Exception {
        if (vars == List.Nil) {
            if (args == List.Nil)
                return List.Nil;
            throw new Exception("wrong number of arguments");
        }

        Symbol name = (Symbol) vars.head();
        if (name == symTable.REST) {
            Exp value = evalRest(args, env, level);
            return new List(new List(symTable.REST, value), List.Nil);
        }
        else {
            Exp value = args.head();
            if (env != null) {
                value = eval(value, env, level);
            }
            List rest = evalArgs(vars.tail(), args.tail(), env, level);
            return new List(new List(name, value), rest);
        }
    }

    private List evalRest(List args, List env, int level) throws Exception {
        if (args == List.Nil)
            return List.Nil;
        Exp value = args.head();
        if (env != null)
            value = eval(value, env, level);
        List rest = evalRest(args.tail(), env, level);
        return new List(value, rest);
    }

    public Exp evalBody(List body, List env, int level) throws Exception {
        Exp value = List.Nil;
        while (body != List.Nil) {
            value = eval(body.head(), env, level);
            body = body.tail();
        }
        return value;
    }

    private Exp lookup(Symbol sym, List env) throws Exception {
        while (env != List.Nil) {
            List binding = (List) env.head();
            if (sym == binding.head()) {
                return binding.tailExp();
            }
            env = env.tail();
        }
        if (sym.value() == null)
            throw new Exception("unbound symbol");
        return sym.value();
    }

    private void addPrims() {
        symTable.addPrim("eval", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primEval(interp, args, env, level);
            }
        });
        symTable.addPrim("quote", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primQuote(interp, args, env, level);
            }
        });
        symTable.addPrim("trace", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primTrace(interp, args, env, level);
            }
        });
        symTable.addPrim("untrace", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primUntrace(interp, args, env, level);
            }
        });
        symTable.addPrim("break", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primBreak(interp, args, env, level);
            }
        });
        symTable.addPrim("write", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primWrite(interp, args, env, level);
            }
        });
        symTable.addPrim("writeln", new Prim.Handler() {

            @Override
            public Exp eval(Lisp interp, List args, List env, int level) throws Exception {
                return primWriteLn(interp, args, env, level);
            }
        });
    }

    private Exp primEval(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Exp exp = argList.next(Exp.Type.ANY, env, true, level);
        return eval(exp, env, level);
    }

    private Exp primQuote(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Exp value = argList.next(Exp.Type.ANY, null, true, level);
        return value;
    }

    private Exp primTrace(Lisp interp, List args, List env, int level) throws Exception {
        if (args == List.Nil)
            return symTable.lookupTraced();
        ArgList argList = new ArgList(interp, args);
        while (!argList.empty()) {
            Symbol s = (Symbol) argList.next(Exp.Type.SYMBOL, null, false, level);
            s.setTrace(true);
        }
        return symTable.T;
    }

    private Exp primUntrace(Lisp interp, List args, List env, int level) throws Exception {
        if (args == List.Nil)
            args = symTable.lookupTraced();
        ArgList argList = new ArgList(interp, args);
        while (!argList.empty()) {
            Symbol s = (Symbol) argList.next(Exp.Type.SYMBOL, null, false, level);
            s.setTrace(false);
        }
        return symTable.T;
    }

    private Exp primBreak(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        Str s = (Str) argList.next(Exp.Type.STRING, env, true, level);
        printer.print("break: " + s.toString() + "\r\n");
        breaking = true;
        while (breaking && !stopping)
            Thread.sleep(10);
        return symTable.T;
    }

    private Exp primWrite(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        while (!argList.empty()) {
            Exp s = argList.next(Exp.Type.ANY, env, false, level);
            if (s.type() == Exp.Type.STRING)
                printer.print(s.toString());
            else
                s.print(printer, 0);
        }
        return symTable.T;
    }

    private Exp primWriteLn(Lisp interp, List args, List env, int level) throws Exception {
        ArgList argList = new ArgList(interp, args);
        while (!argList.empty()) {
            Exp s = argList.next(Exp.Type.ANY, env, false, level);
            if (s.type() == Exp.Type.STRING)
                printer.print(s.toString());
            else
                s.print(printer, 0);
        }
        printer.print("\r\n");
        return symTable.T;
    }
}
