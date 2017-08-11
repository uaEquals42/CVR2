/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CVR.fileformat;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author grjordan
 */
public enum Tags {
    
    
    CVRCHUNK_FILE_CONTAINER(0x00525643),
    CVRCHUNK_VERSION(0x01000000),
    CVRCHUNK_DB_NAME(0x02000000),

    CVRCHUNK_PALETTE_CONTAINER(0x03000000),
    CVRCHUNK_PALETTE_NAME(0x01010000),
    CVRCHUNK_PALETTE_DATA(0x01020000),
    CVRCHUNK_MATERIAL(0x01030000),

    CVRCHUNK_SCENE_CONTAINER(0x04000000),
    CVRCHUNK_SCENE_NAME(0x04010000),
    CVRCHUNK_OBJECT_COUNTER(0x04020000),
    CVRCHUNK_FRAME_COUNTER(0x04030000),

    CVRCHUNK_OBJECT_CONTAINER(0x04040000),
    CVRCHUNK_OBJECT_NAME(0x04040100),

    CVRCHUNK_GEOMETRY_CONTAINER(0x04040200),
    CVRCHUNK_VOXEL_OBJECT(0x04040201),

    CVRCHUNK_ANIMATION_CONTAINER(0x04040300),
    CVRCHUNK_OBJECT_FLAG (0x04040301),
    CVRCHUNK_OBJECT_LOCATION(0x04040302),
    CVRCHUNK_OBJECT_MATRIX(0x04040303);
   
	
	private final int value;
        
	private Tags(int intt) {
            value = intt;
           	
	}

    public int getValue() {
  
        return value;
    }

        

	
	
			
}
