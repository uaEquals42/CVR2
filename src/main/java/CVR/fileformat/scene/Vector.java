/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CVR.fileformat.scene;

import CVR.fileformat.CorruptedFileException;
import CVR.fileformat.Stop;
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
    
    private static Vector getPos(byte bb, Vector cc) throws Stop, CorruptedFileException {
            switch ((bb >> 3) & 0b00011111) {
                case 0x00:
                    return new Vector(cc.getX() - 1, cc.getY() - 1, cc.getZ() - 1);
                case 0x01:
                    return new Vector(cc.getX() - 1, cc.getY(), cc.getZ() - 1);
                case 0x02:
                    return new Vector(cc.getX() - 1, cc.getY() + 1, cc.getZ() - 1);
                case 0x03:
                    return new Vector(cc.getX(), cc.getY() - 1, cc.getZ() - 1);
                case 0x04:
                    return new Vector(cc.getX(), cc.getY(), cc.getZ() - 1);
                case 0x05:
                    return new Vector(cc.getX(), cc.getY() + 1, cc.getZ() - 1);
                case 0x06:
                    return new Vector(cc.getX() + 1, cc.getY() - 1, cc.getZ() - 1);
                case 0x07:
                    return new Vector(cc.getX() + 1, cc.getY(), cc.getZ() - 1);
                case 0x08:
                    return new Vector(cc.getX() + 1, cc.getY() + 1, cc.getZ() - 1);
                case 0x09:
                    return new Vector(cc.getX() - 1, cc.getY() - 1, cc.getZ());
                case 0x0A:
                    return new Vector(cc.getX() - 1, cc.getY(), cc.getZ());
                case 0x0B:
                    return new Vector(cc.getX() - 1, cc.getY() + 1, cc.getZ());
                case 0x0C:
                    return new Vector(cc.getX(), cc.getY() - 1, cc.getZ());
                case 0x0D:
                    return new Vector(cc.getX(), cc.getY() + 1, cc.getZ());
                case 0x0E:
                    return new Vector(cc.getX() + 1, cc.getY() - 1, cc.getZ());
                case 0x0F:
                    return new Vector(cc.getX() + 1, cc.getY(), cc.getZ());
                case 0x10:
                    return new Vector(cc.getX() + 1, cc.getY() + 1, cc.getZ());
                case 0x11:
                    return new Vector(cc.getX() - 1, cc.getY() - 1, cc.getZ() + 1);
                case 0x12:
                    return new Vector(cc.getX() - 1, cc.getY(), cc.getZ() + 1);
                case 0x13:
                    return new Vector(cc.getX() - 1, cc.getY() + 1, cc.getZ() + 1);
                case 0x14:
                    return new Vector(cc.getX(), cc.getY() - 1, cc.getZ() + 1);
                case 0x15:
                    return new Vector(cc.getX(), cc.getY(), cc.getZ() + 1);
                case 0x16:
                    return new Vector(cc.getX(), cc.getY() + 1, cc.getZ() + 1);
                case 0x17:
                    return new Vector(cc.getX() + 1, cc.getY() - 1, cc.getZ() + 1);
                case 0x18:
                    return new Vector(cc.getX() + 1, cc.getY(), cc.getZ() + 1);
                case 0x19:
                    return new Vector(cc.getX() + 1, cc.getY() + 1, cc.getZ() + 1);
                case 0x1A:
                    throw new Stop();
                default:
                    throw new CorruptedFileException("" + ((bb >> 3) & 0b00011111));
            }
    
}
}
