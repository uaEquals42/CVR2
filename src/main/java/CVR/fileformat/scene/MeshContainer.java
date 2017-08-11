/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CVR.fileformat.scene;

import CVR.fileformat.FileHelper;
import CVR.fileformat.Stop;
import CVR.fileformat.Tags;
import static CVR.fileformat.scene.SceneContainer.log;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Gregory Jordan
 */
public class MeshContainer {
    private final String name;
    
    public MeshContainer(FileHelper file) {
        Vector meshStart;
        file.moveToTag(Tags.CVRCHUNK_OBJECT_CONTAINER);
        long numberOfVoxelsInJumpGroup;
        long objectSize = file.readUInteger();
        String objectName = file.readStringAtTag(Tags.CVRCHUNK_OBJECT_NAME);
        log.debug("Object Name: {}", objectName);

        file.moveToTag(Tags.CVRCHUNK_GEOMETRY_CONTAINER);
        file.moveToTag(Tags.CVRCHUNK_VOXEL_OBJECT);

        long voxlength = file.readUInteger();
        log.debug("length: {} bytes", voxlength);

        // VOXEL_HEADER
        int flags = file.getInt();
        log.debug("flag: {}", Integer.toBinaryString(flags));

        /**
         * 0000 uint32 Flags. 1 = Next voxel group has its own VOXEL_HEADER 2 =
         * Next voxel group has its own JUMP_HEADER 4 = Unused 8 = Used but
         * unknown what it means
         */
        boolean flagVoxelHeader = (flags & 1) == 1;  //Next mesh has a voxel header.
        boolean flagJumpHeader = (flags & 2) == 2;  //Next mesh has a jump header.
        boolean flag4 = (flags & 4) == 4;  // Never used.
        boolean flag8 = (flags & 8) == 8;  // Unknown.

        long sizeOfVoxelObject = file.readUInteger();
        float unitsPer3dPixel = file.getFloat();
        log.trace("Units Per 3d Pixel: {}", unitsPer3dPixel);

        long positionOfNextHeader = file.position() + file.readUInteger();
        long positionOfEndOfVoxelGroup = file.position() + file.readUInteger(); //bytes to last byte of this voxel group
        Vector scale = Vector.readVector(byteBuff);
        log.trace("Scale: {}", scale);
        Vector position = Vector.readVector(byteBuff);
        meshStart = position;
        log.trace("Position: {}", position);
        Vector rotationCenter = Vector.readVector(byteBuff);
        log.trace("Rotation Center: {}", rotationCenter);
        //log.debug("Position: {}", byteBuff.position());
        long numberOfVoxels = file.readUInteger();
        numberOfVoxelsInJumpGroup = numberOfVoxels;
        log.trace("Number of Voxels: {}", numberOfVoxels);

        if (flagJumpHeader) {
            // JUMP_HEADER(0x20 or 0x24)
            /**
             *
             * 0000 SVECTOR3 Position 0006 SVECTOR3 v2--unknown vector, used
             * determining max size 000C SVECTOR3 v3--unknown vector, used
             * determining max size 0012 SVECTOR3 PositionOffset--start drawing
             * at Position+PositionOffset 0018 uint32 # of voxels in this jump
             * group 001C uint32 JumpFlags. 1 = Has Offset
             * < 020    uint32      Offset to next JUMP_HEADER.  Present only if JumpFlags & 1>
             */
            log.debug("Position A {}", file.position());
            Vector position2 = Vector.readVector(byteBuff);
            log.debug("{} {}", position, position2);
            Vector maxSize1 = Vector.readVector(byteBuff);
            Vector maxSize2 = Vector.readVector(byteBuff);
            Vector positionOffset = Vector.readVector(byteBuff);  //PositionOffset--start drawing at Position+PositionOffset
            log.debug("Position B {}", file.position());
            numberOfVoxelsInJumpGroup = file.readUInteger();
            log.debug("Voxel number {} {}", numberOfVoxels, numberOfVoxelsInJumpGroup);
            boolean hasoffset = (file.getInt() & 1) == 1;
            if (hasoffset) {
                long offset = file.readUInteger();
                log.debug("Offset: {}", offset);
            }
            meshStart = position2.add(positionOffset);
        }

        List<Voxel> voxels = new LinkedList();

        Vector curentPosition = meshStart;
        for (long vv = 0; vv < numberOfVoxelsInJumpGroup; vv++) {
            byte b1 = file.get();
            byte b2 = file.get();
            byte b3 = file.get();
            try {
                curentPosition = getPos(b1, curentPosition);
            } catch (Stop ex) {
                log.debug("Position {}", file.position());
                log.info("End of voxel group: {} = {}", vv, numberOfVoxelsInJumpGroup);

            }

        }

    }

}
