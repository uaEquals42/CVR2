/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CVR;

import java.awt.Color;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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
    

    
    
    
    
    
    public static class Builder {

            final float CVRChunkVersion;
            final String ChuckDatabaseName;
            final PaletteContainer palette;
            final SceneContainer scene;
            final byte[] sourceFile;
            String cvrName;
            final ByteBuffer byteBuff;
        
        public Builder(Path location) throws CorruptedFileException, SectionNotFoundException, IOException {
            byte[] cvrFile = Files.readAllBytes(location);
            sourceFile = cvrFile;

            byteBuff = ByteBuffer.wrap(cvrFile).order(ByteOrder.LITTLE_ENDIAN).asReadOnlyBuffer();

            if (!isCvrFile()) {
                throw new IllegalArgumentException("Not a cvr file");
            }

            int fileSize = checkFileSize(sourceFile);
            log.info("Filesize: {} bytes", fileSize);

            CVRChunkVersion = getCvrVersion();
            log.info("CVR Version: {}", CVRChunkVersion);

            ChuckDatabaseName = readStringAtTag(Tags.CVRCHUNK_DB_NAME);
            log.info("Internal File name: {}", ChuckDatabaseName);
       
            palette = readColors();
            log.info("Palette Name: {}", palette.getName());
            
            
            scene = readScene();

           
        }
        
        public CVR build(){
            return new CVR(this);
        }
        
        private SceneContainer readScene() throws SectionNotFoundException, CorruptedFileException {
            String sceneName;
            long numObjects;
            long numbOfFrames;
            moveToTag(Tags.CVRCHUNK_SCENE_CONTAINER);
            
            sceneName = readStringAtTag(Tags.CVRCHUNK_SCENE_NAME);
            log.debug("Scene Name: {}", sceneName);
            
            moveToTag(Tags.CVRCHUNK_OBJECT_COUNTER);
            int tmpsize = byteBuff.getInt();
            if(tmpsize!=12){      
                throw new CorruptedFileException("Size should be 12, not" + tmpsize);
            }
            
            numObjects = readUInteger();
            log.debug("Number of Objects: {}", numObjects);
        
            moveToTag(Tags.CVRCHUNK_FRAME_COUNTER);
            tmpsize = byteBuff.getInt();
            if (tmpsize != 12) {
                throw new CorruptedFileException("Size should be 12, not" + tmpsize);
            }
            numbOfFrames = readUInteger();
            log.debug("Number of Frames: {} ", numbOfFrames);
            
            
            // Time to read in each object.  Which can contain submeshes.
            for(int ii = 0; ii < numObjects; ii++){
                Vector meshStart;
                moveToTag(Tags.CVRCHUNK_OBJECT_CONTAINER);
                long numberOfVoxelsInJumpGroup;
                long objectSize = readUInteger();
                String objectName = readStringAtTag(Tags.CVRCHUNK_OBJECT_NAME);
                log.debug("Object Name: {}", objectName);

                
                moveToTag(Tags.CVRCHUNK_GEOMETRY_CONTAINER);
                moveToTag(Tags.CVRCHUNK_VOXEL_OBJECT);
                
                
                long voxlength = readUInteger();
                log.trace("length: {} bytes", voxlength);
                
                
                // VOXEL_HEADER
                int flags = byteBuff.getInt();                
                log.debug("flag: {}", Integer.toBinaryString(flags));
                
                /**
                 * 0000    uint32      
                 *  Flags.  1 = Next voxel group has its own VOXEL_HEADER
                            2 = Next voxel group has its own JUMP_HEADER
                            4 = Unused
                            8 = Used but unknown what it means
                 */
                boolean flagVoxelHeader = (flags & 1) == 1;  //Next mesh has a voxel header.
                boolean flagJumpHeader = (flags & 2) == 2;  //Next mesh has a jump header.
                boolean flag4 = (flags & 4) == 4;  // Never used.
                boolean flag8 = (flags & 8) == 8;  // Unknown.
                
                long sizeOfVoxelObject = readUInteger();
                float unitsPer3dPixel = byteBuff.getFloat();
                log.trace("Units Per 3d Pixel: {}", unitsPer3dPixel);  
                
                long positionOfNextHeader = byteBuff.position() + readUInteger();
                long positionOfEndOfVoxelGroup = byteBuff.position() + readUInteger(); //bytes to last byte of this voxel group
                Vector scale = Vector.readVector(byteBuff);
                log.trace("Scale: {}", scale);
                Vector position = Vector.readVector(byteBuff);
                meshStart = position;
                log.trace("Position: {}", position);
                Vector rotationCenter = Vector.readVector(byteBuff);
                log.trace("Rotation Center: {}", rotationCenter);
                //log.debug("Position: {}", byteBuff.position());
                long numberOfVoxels = readUInteger();
                numberOfVoxelsInJumpGroup = numberOfVoxels;
                log.trace("Number of Voxels: {}", numberOfVoxels);
                
                if (flagJumpHeader) {
                    // JUMP_HEADER(0x20 or 0x24)
                    /**
                     *
                     * 0000 SVECTOR3 Position 0006 SVECTOR3 v2--unknown vector,
                     * used determining max size 000C SVECTOR3 v3--unknown
                     * vector, used determining max size 0012 SVECTOR3
                     * PositionOffset--start drawing at Position+PositionOffset
                     * 0018 uint32 # of voxels in this jump group 001C uint32
                     * JumpFlags. 1 = Has Offset
                     * < 020    uint32      Offset to next JUMP_HEADER.  Present only if JumpFlags & 1>
                     */
                    log.debug("Position A {}", byteBuff.position());
                    Vector position2 = Vector.readVector(byteBuff);
                    log.debug("{} {}", position, position2);
                    Vector maxSize1 = Vector.readVector(byteBuff);
                    Vector maxSize2 = Vector.readVector(byteBuff);
                    Vector positionOffset = Vector.readVector(byteBuff);  //PositionOffset--start drawing at Position+PositionOffset
                    log.debug("Position B {}", byteBuff.position());
                    numberOfVoxelsInJumpGroup = readUInteger();
                    log.debug("Voxel number {} {}", numberOfVoxels, numberOfVoxelsInJumpGroup);
                    boolean hasoffset = (byteBuff.getInt() & 1) == 1;
                    if (hasoffset) {
                        long offset = readUInteger();
                        log.debug("Offset: {}", offset);
                    }
                    meshStart = position2.add(positionOffset);
                }
                
                
                
                List<Voxel> voxels = new LinkedList();
                
                Vector curentPosition = meshStart;
                for(long vv = 0; vv < numberOfVoxelsInJumpGroup;  vv++){
                    byte b1 = byteBuff.get();
                    byte b2 = byteBuff.get();
                    byte b3 = byteBuff.get();
                    try {
                        curentPosition = getPos(b1, curentPosition);
                    } catch (Stop ex) {
                        log.debug("Position {}", byteBuff.position());
                        log.info("End of voxel group: {} = {}", vv, numberOfVoxelsInJumpGroup);
                        
                    }
                    
                }
                
            }
            
            
            return null;
        }

        private Vector getPos(byte bb, Vector cc) throws Stop, CorruptedFileException {
            switch ((bb >> 3)&0b00011111) {
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
                    throw new CorruptedFileException(""+((bb >> 3)&0b00011111));
            }
        }

        /**
         * Read an unsigned Int, store it in a long.
         * @return 
         */
        private long readUInteger(){
            int a = byteBuff.getInt();
            long result = a & 0xffffffffL;
            return result;
        }
        
        
        private PaletteContainer readColors() throws SectionNotFoundException {
            String name;
            List<Color> physicalPalette=new ArrayList<>();
            List<List<Integer>> shadeArray=new ArrayList<>();
            List<List<Color>> color16Bit =new ArrayList<>();
            
            moveToTag(Tags.CVRCHUNK_PALETTE_CONTAINER);
            
            int endLocation = getSize() + byteBuff.position();
            
            log.debug("Palette End location: {}", endLocation);
            
            
            name = readStringAtTag(Tags.CVRCHUNK_PALETTE_NAME);
            
            
            moveToTag(Tags.CVRCHUNK_PALETTE_DATA);
            log.debug("Position: {}", byteBuff.position());
            
            int start = readUnByte();
            int end = readUnByte();
            log.trace("Start: {}  End: {}", start, end);
            for(int ii = 0;ii<256;ii++){
                physicalPalette.add(new Color(readUnByte(),readUnByte(),readUnByte() ));
            }
            log.debug("Physical Palette size: {}", physicalPalette.size());
            log.debug("End of Physical Palette: {}", byteBuff.position());
            
            for(int colorIndex = 0; colorIndex<256; colorIndex++){
                shadeArray.add(new ArrayList<Integer>());
                log.trace("Color: {}", colorIndex);
                for(int shade = 0; shade < 24; shade++){
                    int paletteCode = readUnByte();
                    log.trace("   Shade {}: {} ", shade, physicalPalette.get(paletteCode));
                    shadeArray.get(colorIndex).add(paletteCode);
                }
            }
            log.debug("End of Shades: {}", byteBuff.position());

            try {
                // Material Section is optional.  Most have it.  Some don't.
                moveToTag(Tags.CVRCHUNK_MATERIAL);

                log.debug("Material Position: {}", byteBuff.position());
                int materialSize = getSize();
                log.debug("Material Size: {}", materialSize);
                for (int colorIndex = 0; colorIndex < 256; colorIndex++) {
                    color16Bit.add(new ArrayList<Color>());
                    for (int shade = 0; shade < 24; shade++) {
                        byteBuff.get();
                        byteBuff.get();  // TODO:  Get this converted into color.
                        log.warn("Material Section Exists, no code for converting into color");
                    }
                }
            } catch (SectionNotFoundException ex) {
                log.warn("No 16 bit color section.  Will have to use 256 color table.");
            }

            
            return new PaletteContainer(name, physicalPalette, shadeArray, color16Bit, start, end);
        }
        
        private int readUnByte(){
             return byteBuff.get() & 0xFF;
        }
        
        private String readStringAtTag(Tags tag) throws SectionNotFoundException{
                    log.trace("readStringAtTag({})", tag);
                    moveToTag(tag);
                    log.trace("Position: {}", byteBuff.position());
                    int string_size = getSize();
                    log.trace("String size: {}", string_size);
                    String result = "";
                    for(int ii = 0; ii < string_size; ii++){
                        result = result + (char) byteBuff.get();
                    }
                    log.trace(result);
                    return result;
                
        }
        
        private int getSize(){
            int size = byteBuff.getInt()-8;  // Need to subtract 8 bytes because the size includes the size of the tag and this value.
            return size;
        }

        private boolean isCvrFile() {
            int tmp = byteBuff.order(ByteOrder.LITTLE_ENDIAN).getInt();
            return tmp == Tags.CVRCHUNK_FILE_CONTAINER.getValue();
        }

        private float getCvrVersion() throws SectionNotFoundException {
            
                moveToTag(Tags.CVRCHUNK_VERSION);
                log.trace("Position: {}", byteBuff.position());

                int lengthOfThisSection = getSize();
                log.trace("Section Length: {}", lengthOfThisSection);

                log.trace("Position: {}", byteBuff.position());
                float version = byteBuff.order(ByteOrder.LITTLE_ENDIAN).getFloat();
                log.trace("Position: {}", byteBuff.position());

                return version;

            
        }

        /**
         * Throws an error if the length of the file isn't the same as the length the file thinks it has.
         * Increments byteBuff by 4.
         * @param byteBuff
         * @param sourceFile
         * @throws CorruptedFileException 
         */
        private int checkFileSize(byte[] sourceFile) throws CorruptedFileException{
            int filesize = byteBuff.getInt();
            if (filesize != sourceFile.length) {
                log.error("Filesize doesn't match the actuall filesize");
                throw new CorruptedFileException("Filesize doesn't match the actuall filesize");
            }
            return filesize;
        }
        
        /**
         * Moves to the point right after the tag.  Aka, should point to the size info for the next stuff.
         * @param byteBuff
         * @param tag
         * @throws SectionNotFoundException 
         */
        private void moveToTag(Tags tag) throws SectionNotFoundException {
            moveToTag(tag, byteBuff.position());
        }

        private void moveToTag(Tags tag, int pos) throws SectionNotFoundException {
            pos = findTag(tag, pos);
            byteBuff.position(pos);
        }

        
        
        private int findTag(Tags tag) throws SectionNotFoundException {
            return findTag(tag, byteBuff.position());
        }

        private int findTag(Tags tag, int start) throws SectionNotFoundException {

            for (int ii = start; ii < byteBuff.capacity()-4; ii++) {
                if (byteBuff.order(ByteOrder.LITTLE_ENDIAN).getInt(ii) == tag.getValue()) {
                    log.trace("Found tag at {}", ii);
                    return ii + 4;
                }
            }
            log.error("Tag not found: {}", tag);
            throw new SectionNotFoundException("Tag not found" + tag);
        }

    }

}
