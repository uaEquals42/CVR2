/*
 * Copyright (C) 2017 Twilight Sparkle
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package CVR.fileformat;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Twilight Sparkle
 */
public class FileHelper {

    static Logger log = LoggerFactory.getLogger(FileHelper.class);
    final ByteBuffer byteBuff;

    public FileHelper(ByteBuffer byteBuff) {
        this.byteBuff = byteBuff;
    }

    public boolean isCvrFile() {
        int tmp = byteBuff.order(ByteOrder.LITTLE_ENDIAN).getInt();

        return tmp == Tags.CVRCHUNK_FILE_CONTAINER.getValue();

    }

    public float getCvrVersion() throws SectionNotFoundException {

        moveToTag(Tags.CVRCHUNK_VERSION);
        log.trace("Position: {}", byteBuff.position());

        int lengthOfThisSection = getSize();
        log.trace("Section Length: {}", lengthOfThisSection);

        log.trace("Position: {}", byteBuff.position());
        float version = byteBuff.order(ByteOrder.LITTLE_ENDIAN).getFloat();
        log.trace("Position: {}", byteBuff.position());

        return version;

    }

    public byte get() {
        return byteBuff.get();
    }

    public int position() {
        return byteBuff.position();
    }

    /**
     * Throws an error if the length of the file isn't the same as the length
     * the file thinks it has. Increments byteBuff by 4.
     *
     * @param byteBuff
     * @param sourceFile
     * @throws CorruptedFileException
     */
    public int checkFileSize(byte[] sourceFile) throws CorruptedFileException {
        int filesize = byteBuff.getInt();
        if (filesize != sourceFile.length) {
            log.error("Filesize doesn't match the actuall filesize");
            throw new CorruptedFileException("Filesize doesn't match the actuall filesize");
        }
        return filesize;
    }

    /**
     * Read an unsigned Int, store it in a long.
     *
     * @return
     */
    public long readUInteger() {
        int a = byteBuff.getInt();
        long result = a & 0xffffffffL;
        return result;
    }

    public int readUnByte() {
        return byteBuff.get() & 0xFF;
    }

    public int getInt() {
        return byteBuff.getInt();
    }

    public float getFloat() {
        return byteBuff.getFloat();
    }

    public String readStringAtTag(Tags tag) throws SectionNotFoundException {
        log.trace("readStringAtTag({})", tag);
        moveToTag(tag);
        log.trace("Position: {}", byteBuff.position());
        int string_size = getSize();
        log.trace("String size: {}", string_size);
        String result = "";
        for (int ii = 0; ii < string_size; ii++) {
            result = result + (char) byteBuff.get();
        }
        log.trace(result);
        return result;

    }

    public int getSize() {
        int size = byteBuff.getInt() - 8;  // Need to subtract 8 bytes because the size includes the size of the tag and this value.
        return size;
    }

    /**
     * Moves to the point right after the tag. Aka, should point to the size
     * info for the next stuff.
     *
     * @param byteBuff
     * @param tag
     * @throws SectionNotFoundException
     */
    public void moveToTag(Tags tag) throws SectionNotFoundException {
        moveToTag(tag, byteBuff.position());
    }

    public void moveToTag(Tags tag, int pos) throws SectionNotFoundException {
        pos = findTag(tag, pos);
        byteBuff.position(pos);
    }

    public int findTag(Tags tag) throws SectionNotFoundException {
        return findTag(tag, byteBuff.position());
    }

    public int findTag(Tags tag, int start) throws SectionNotFoundException {

        for (int ii = start; ii < byteBuff.capacity() - 4; ii++) {
            if (byteBuff.order(ByteOrder.LITTLE_ENDIAN).getInt(ii) == tag.getValue()) {
                log.trace("Found tag at {}", ii);
                return ii + 4;
            }
        }
        log.error("Tag not found: {}", tag);
        throw new SectionNotFoundException("Tag not found" + tag);
    }

}
