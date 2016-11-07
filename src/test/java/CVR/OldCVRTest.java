/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CVR;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gregory Jordan
 */
public class OldCVRTest {
    static Logger log = LoggerFactory.getLogger(OldCVRTest.class);
    public OldCVRTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        
    }

    @Test
    public void loadAcvr(){
        CVR cvr = loadCVR(Paths.get("A.cvr"));
        assertEquals("CVR version", 10.1, cvr.getCVRChunkVersion(),0.01);
        assertEquals("CVR DB Name", "BASIC.MAX", cvr.getChuckDatabaseName());
    }
    @Test
    public void loadAcolpodCvr(){
        CVR cvr = loadCVR(Paths.get("ACOLPOD.cvr"));
        assertEquals("CVR version", 10.0, cvr.getCVRChunkVersion(),0.01);
        assertEquals("CVR DB Name", "MB_Alien_Pod2.max", cvr.getChuckDatabaseName());
    }
    @Test
    public void loadVwntuCvr(){
        
        CVR cvr = loadCVR(Paths.get("Vwntu.cvr"));
        assertEquals("CVR version", 10.1, cvr.getCVRChunkVersion(),0.01);
        assertEquals("CVR DB Name", "TERRAF.MAX", cvr.getChuckDatabaseName());
    }
    
    
    private CVR loadCVR(Path location){
        log.info("test loading {}", location);
        CVR cvr=null;
        // TODO review the generated test code and remove the default call to fail.
        location = Paths.get("src/test/resources").resolve(location);
        log.debug("Load from ", location.toString());
        try {
            cvr = new CVR.Builder(location).build();
        } catch (IOException ex) {
            log.error("IOException");
            log.error(ex.getMessage());
            log.error(ex.toString());
            fail(ex.getMessage());
            
        } catch (CorruptedFileException ex) {
            log.error("Corrupted File Exception");
            log.error(ex.getMessage());
            log.error(ex.toString());
            fail(ex.getMessage());
        } catch (SectionNotFoundException ex) {
            log.error("Section not Found");
            log.error(ex.getMessage());
            log.error(ex.toString());
            fail(ex.getMessage());
        }
        return cvr;
    }
    
   
    
}
