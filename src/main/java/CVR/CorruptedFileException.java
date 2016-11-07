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
public class CorruptedFileException extends Exception {

    /**
     * Creates a new instance of <code>CorruptedFileException</code> without
     * detail message.
     */
    public CorruptedFileException() {
    }

    /**
     * Constructs an instance of <code>CorruptedFileException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public CorruptedFileException(String msg) {
        super(msg);
    }
}
