/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CVR.fileformat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author grjordan
 */
public class PaletteContainer {
    static Logger log = LoggerFactory.getLogger(PaletteContainer.class);
    
    private final String name;
    private final List<Color> physicalPalette;
    private final List<List<Integer>> shadeArray;
    private final List<List<Color>> color16Bit;
    final int start;
    final int end;


    private PaletteContainer(Builder build){
        this.name = build.name;
        this.physicalPalette = build.physicalPalette;
        this.shadeArray = build.shadeArray;
        this.start = build.start;
        this.end = build.end;
        this.color16Bit = build.color16Bit;
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


    public static class Builder{
        private String name;
        private List<Color> physicalPalette;
        private List<List<Integer>> shadeArray;
        private List<List<Color>> color16Bit;
        private int start;
        private int end;
        
        
        public PaletteContainer build(){
            return new PaletteContainer(this);
        }
        
        public Builder(FileHelper file) throws SectionNotFoundException {
            
            physicalPalette=new ArrayList<>();
            shadeArray= new ArrayList<>();
            color16Bit = new ArrayList<>();
            
            file.moveToTag(Tags.CVRCHUNK_PALETTE_CONTAINER);
            
            int endLocation = file.getSize() + file.position();
            
            log.debug("Palette End location: {}", endLocation);
            
            
            name = file.readStringAtTag(Tags.CVRCHUNK_PALETTE_NAME);
            
            
            file.moveToTag(Tags.CVRCHUNK_PALETTE_DATA);
            log.debug("Position: {}", file.position());
            
            start = file.readUnByte();
            end = file.readUnByte();
            log.debug("Start: {}  End: {}", start, end);
            for(int ii = 0;ii<256;ii++){
                physicalPalette.add(new Color(file.readUnByte(),file.readUnByte(),file.readUnByte() ));
            }
            log.debug("Physical Palette size: {}", physicalPalette.size());
            log.debug("End of Physical Palette: {}", file.position());
            
            for(int colorIndex = 0; colorIndex<256; colorIndex++){
                shadeArray.add(new ArrayList<Integer>());
                log.debug("Color: {}", colorIndex);
                for(int shade = 0; shade < 24; shade++){
                    int paletteCode = file.readUnByte();
                    log.debug("   Shade {}: {} ", shade, physicalPalette.get(paletteCode));
                    shadeArray.get(colorIndex).add(paletteCode);
                }
            }
            log.debug("End of Shades: {}", file.position());

            try {
                // Material Section is optional.  Most have it.  Some don't.
                file.moveToTag(Tags.CVRCHUNK_MATERIAL);

                log.debug("Material Position: {}", file.position());
                int materialSize = file.getSize();
                log.debug("Material Size: {}", materialSize);
                for (int colorIndex = 0; colorIndex < 256; colorIndex++) {
                    color16Bit.add(new ArrayList<Color>());
                    for (int shade = 0; shade < 24; shade++) {
                        file.get();
                        file.get();  // TODO:  Get this converted into color.
                        log.warn("Material Section Exists, no code for converting into color");
                    }
                }
            } catch (SectionNotFoundException ex) {
                log.warn("No 16 bit color section.  Will have to use 256 color table.");
            }

            
            
        }
    
    
}

    
}
