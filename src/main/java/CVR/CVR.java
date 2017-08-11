/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CVR;

import CVR.fileformat.CorruptedFileException;
import CVR.fileformat.FileHelper;
import CVR.fileformat.PaletteContainer;
import CVR.fileformat.scene.SceneContainer;
import CVR.fileformat.SectionNotFoundException;
import CVR.fileformat.Tags;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author grjordan
 */
public class CVR {

    static Logger log = LoggerFactory.getLogger(CVR.class);
    private final float CVRChunkVersion;
    private final String ChuckDatabaseName;
    private final PaletteContainer palette;
    private final SceneContainer scene;
    private final byte[] sourceFile;
    private final String cvrName;

    public CVR(Builder build) {
        this.CVRChunkVersion = build.CVRChunkVersion;
        this.ChuckDatabaseName = build.ChuckDatabaseName;
        this.palette = build.palette;
        this.scene = build.scene;
        this.sourceFile = build.sourceFile;
        this.cvrName = build.cvrName;
    }

    public float getCVRChunkVersion() {
        return CVRChunkVersion;
    }

    public String getChuckDatabaseName() {
        return ChuckDatabaseName;
    }

    public PaletteContainer getPalette(){
        return palette;
    }

    public SceneContainer getScene() {
        return scene;
    }
    
    
    
    
    
    
    
    public static class Builder {

            final float CVRChunkVersion;
            final String ChuckDatabaseName;
            final PaletteContainer palette;
            final SceneContainer scene;
            final byte[] sourceFile;
            String cvrName;
            final FileHelper file;
        
        public Builder(Path location) throws CorruptedFileException, SectionNotFoundException, IOException {
            byte[] cvrFile = Files.readAllBytes(location);
            sourceFile = cvrFile;

            ByteBuffer byteBuff = ByteBuffer.wrap(cvrFile).order(ByteOrder.LITTLE_ENDIAN).asReadOnlyBuffer();
            file = new FileHelper(byteBuff);
            
            if (!file.isCvrFile()) {
                throw new IllegalArgumentException("Not a cvr file");
            }

            int fileSize = file.checkFileSize(sourceFile);
            log.info("Filesize: {} bytes", fileSize);

            CVRChunkVersion = file.getCvrVersion();
            log.info("CVR Version: {}", CVRChunkVersion);

            ChuckDatabaseName = file.readStringAtTag(Tags.CVRCHUNK_DB_NAME);
            log.info("Internal File name: {}", ChuckDatabaseName);
       
            palette = new PaletteContainer.Builder(file).build();
            log.info("Palette Name: {}", palette.getName());
            
            
            scene = new SceneContainer.Builder(file).build();
            
           
        }
        
        public CVR build(){
            return new CVR(this);
        }
    }
}
