/* Project: LispLib
 * File:    Primitive.java
 * Created: May 8, 2011
 */
package com.lthorup.lisp;

/** This class represents a Primitive
 *
 * @author Layne
 */
public class Prim extends Exp {

    public interface Handler {

        public Exp eval(Lisp interp, List args, List env, int level) throws Exception;
    }
    public Handler handler;

    public Prim(Handler handler) {
        super(Type.PRIMATIVE);
        this.handler = handler;
    }

    @Override
    public void print(PrintHandler p, int level) {
        p.print("<PRIMATIVE>");
    }

    @Override
    public boolean equal(Exp e) {
        return this == e;
    }
}
