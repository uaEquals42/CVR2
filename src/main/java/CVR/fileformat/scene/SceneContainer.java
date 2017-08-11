/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CVR.fileformat.scene;

import CVR.fileformat.CorruptedFileException;
import CVR.fileformat.FileHelper;
import CVR.fileformat.SectionNotFoundException;
import CVR.fileformat.Tags;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author grjordan
 */
public class SceneContainer {

    static Logger log = LoggerFactory.getLogger(SceneContainer.class);
    private final String SCENE_NAME;
    private final List<MeshContainer> mesh;
    private final List<AnimationContainer> animations;


    private SceneContainer(Builder build) {
        SCENE_NAME = build.sceneName;
        mesh = build.mesh;
        animations = build.animations;
    }

    public static class Builder {

        private final String sceneName;
    private final List<MeshContainer> mesh;
    private final List<AnimationContainer> animations;
   

        public SceneContainer build(){
            return new SceneContainer(this);
        }
        
        public Builder(FileHelper file) throws SectionNotFoundException, CorruptedFileException {

            long numObjects;
            long numbOfFrames;
            file.moveToTag(Tags.CVRCHUNK_SCENE_CONTAINER);

            sceneName = file.readStringAtTag(Tags.CVRCHUNK_SCENE_NAME);
            log.info("Scene Name: {}", sceneName);

            file.moveToTag(Tags.CVRCHUNK_OBJECT_COUNTER);
            long tmpsize = file.readUInteger();
            if (tmpsize != 12) {
                throw new CorruptedFileException("Size should be 12, not" + tmpsize);
            }

            numObjects = file.readUInteger();
            log.info("Number of Objects: {}", numObjects);
            mesh = new ArrayList<>((int) numObjects);
            
            file.moveToTag(Tags.CVRCHUNK_FRAME_COUNTER);
            tmpsize = file.readUInteger();
            if (tmpsize != 12) {
                throw new CorruptedFileException("Size should be 12, not" + tmpsize);
            }
            numbOfFrames = file.readUInteger();
            log.info("Number of Frames: {} ", numbOfFrames);
            animations = new ArrayList<>((int) numbOfFrames);
            
            // Time to read in each object.  Which can contain submeshes.
            for (int ii = 0; ii < numObjects; ii++) {


            }

            
        }

        
        
    }
}
