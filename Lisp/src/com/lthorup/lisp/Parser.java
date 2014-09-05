/* Project: LispLib
 * File:    Parser.java
 * Created: May 9, 2011
 */
package com.lthorup.lisp;

import java.util.*;

/** This class represents a Parser
 *
 * @author Layne
 */
public class Parser {

    private String input;
    private int inputNext;
    private SymbolTable symTable;
    private Symbol QUOTE;
    private Exp tokenValue = null;

    private enum Token {

        END, LP, RP, LB, RB, DOT, QUOTE, SYMBOL, NUMBER, STRING
    }

    public Parser(SymbolTable symTable) {
        this.symTable = symTable;
        QUOTE = symTable.add("QUOTE");
    }

    public ArrayList parse(String input) throws Exception {
        this.input = input;
        inputNext = 0;
        ArrayList<Exp> expList = new ArrayList<Exp>();
        Exp e = parseExp();
        while (e != null) {
            expList.add(e);
            e = parseExp();
        }
        return expList;
    }

    private Exp parseExp() throws Exception {
        Token t = nextToken(true);
        if (t == Token.END) {
            return null;
        }
        if (t == Token.SYMBOL || t == Token.NUMBER || t == Token.STRING) {
            return tokenValue;
        }
        if (t == Token.LP) {
            return parseList();
        }
        if (t == Token.LB) {
            List n = (List) parseArray();
            //if (n == List.Nil)
            //	throw new Exception("empty array");
            Array a = new Array(n.length());
            int i = 0;
            while (n != List.Nil) {
                a.set(i++, n.head());
                n = n.tail();
            }
            return a;
        }
        if (t == Token.QUOTE) {
            Exp body = parseExp();
            if (body == null)
                throw new Exception("bad quote expression");
            return new List(QUOTE, new List(body, List.Nil));
        }
        throw new Exception("bad expression");
    }

    private Exp parseList() throws Exception {
        Token t = nextToken(false);
        if (t == Token.RP) {
            nextToken(true);
            return List.Nil;
        }
        else if (t == Token.DOT) {
            nextToken(true);
            Exp tail = parseExp();
            if (tail == null)
                throw new Exception("bad list");
            t = nextToken(true);
            if (t != Token.RP)
                throw new Exception("bad list");
            return tail;
        }
        Exp head = parseExp();
        if (head == null)
            throw new Exception("bad list");
        return new List(head, parseList());
    }

    private Exp parseArray() throws Exception {
        Token t = nextToken(false);
        if (t == Token.RB) {
            nextToken(true);
            return List.Nil;
        }
        Exp head = parseExp();
        if (head == null)
            throw new Exception("bad array");
        return new List(head, parseArray());
    }

    private Token nextToken(boolean remove) throws Exception {
        // find start of next token
        String s = input;
        int i = inputNext;
        while (true) {
            while (i < s.length() && Character.isWhitespace(s.charAt(i)))
                i++;
            if (i >= s.length() || s.charAt(i) != ';')
                break;
            while (i < s.length() && s.charAt(i) != '\n')
                i++;
        }

        int e = i + 1;
        Token t;
        if (i == s.length())
            t = Token.END;
        else if (s.charAt(i) == '(')
            t = Token.LP;
        else if (s.charAt(i) == ')')
            t = Token.RP;
        else if (s.charAt(i) == '[')
            t = Token.LB;
        else if (s.charAt(i) == ']')
            t = Token.RB;
        else if (s.charAt(i) == '.')
            t = Token.DOT;
        else if (s.charAt(i) == '\'')
            t = Token.QUOTE;
        else if (s.charAt(i) == '"') {
            while (e < s.length() && s.charAt(e) != '\n' && s.charAt(e) != '"')
                e++;
            if (s.charAt(e) != '"')
                throw new Exception("bad string");
            e++;
            t = Token.STRING;
            tokenValue = new Str(s.substring(i + 1, e - 1));
        }
        else {
            while (e < s.length()
                    && !Character.isWhitespace(s.charAt(e))
                    && s.charAt(e) != '(' && s.charAt(e) != ')'
                    && s.charAt(e) != '[' && s.charAt(e) != ']'
                    && s.charAt(e) != ';') {
                e++;
            }
            String v = s.substring(i, e);
            if (Character.isDigit(v.charAt(0)) || (v.length() > 1 && v.charAt(0) == '-' && Character.isDigit(v.charAt(1)))) {
                try {
                    t = Token.NUMBER;
                    tokenValue = new Number(Double.parseDouble(v));
                }
                catch (Exception ex) {
                    throw new Exception("bad integer");
                }
            }
            else {
                t = Token.SYMBOL;
                tokenValue = symTable.add(v);
            }
        }
        if (remove)
            inputNext = e;
        return t;
    }
}
