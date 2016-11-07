/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CVR;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author Gregory Jordan
 */
public class Vector {
    private final int x;
    private final int y;
    private final int z;

    public Vector(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Vector add(Vector vv){
        return new Vector(x + vv.getX(), y + vv.getY(), z + vv.getZ());
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }
    
    @Override
    public String toString(){
        return "(X: "+x+" ,Y: " + "y: " + y + ", Z: "+ z +")";
    }
    
    public static Vector readVector(ByteBuffer byteBuff){
        int z = byteBuff.order(ByteOrder.LITTLE_ENDIAN).getShort() & 0xff;
        int x = byteBuff.order(ByteOrder.LITTLE_ENDIAN).getShort() & 0xff;
        int y = byteBuff.order(ByteOrder.LITTLE_ENDIAN).getShort() & 0xff;
        
        return new Vector(x,y,z);
        
    }
}
