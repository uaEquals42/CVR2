/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CVR;

import java.awt.Color;
import java.util.List;

/**
 *
 * @author grjordan
 */
public class PaletteContainer {

    private final String name;
    private final List<Color> physicalPalette;
    private final List<List<Integer>> shadeArray;
    private final List<List<Color>> color16Bit;
    final int start;
    final int end;

    public PaletteContainer(String name, List<Color> physicalPalette, List<List<Integer>> shadeArray, List<List<Color>> color16Bit, int start, int end) {
        this.name = name;
        this.physicalPalette = physicalPalette;
        this.shadeArray = shadeArray;
        this.start = start;
        this.end = end;
        this.color16Bit = color16Bit;
    }

    public String getName() {
        return name;
    }

    public List<Color> getPhysicalPalette() {
        return physicalPalette;
    }

    public List<List<Integer>> getShadeArray() {
        return shadeArray;
    }


        
}
