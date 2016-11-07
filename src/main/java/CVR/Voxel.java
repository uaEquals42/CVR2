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
public class Voxel {
    private final int x;
    private final int y;
    private final int z;
    
    private final int colorCode;
    
    private final float normalx;
    private final float normaly;
    private final float normalz;

    public Voxel(int x, int y, int z, int colorCode, float normalx, float normaly, float normalz) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.colorCode = colorCode;
        this.normalx = normalx;
        this.normaly = normaly;
        this.normalz = normalz;
    }
    
    
}
