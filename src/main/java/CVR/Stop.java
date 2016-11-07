/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CVR;

/**
 *
 * @author Gregory Jordan
 */
public class Stop extends Exception {

    /**
     * Creates a new instance of <code>Stop</code> without detail message.
     */
    public Stop() {
    }

    /**
     * Constructs an instance of <code>Stop</code> with the specified detail
     * message.
     *
     * @param msg the detail message.
     */
    public Stop(String msg) {
        super(msg);
    }
}
