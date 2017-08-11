/*
 * Copyright (C) 2016 Twilight Sparkle <your.name at your.org>
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

package CVR;

import CVR.fileformat.CorruptedFileException;
import CVR.fileformat.SectionNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Twilight Sparkle <your.name at your.org>
 */
@Ignore public class TestCVR {
    static Logger log = LoggerFactory.getLogger(TestCVR.class);
    private final Path location;
    private final double version;
    private final String db_name; 
    CVR cvr;
    public TestCVR(Builder build){
        location = build.location;
        version = build.version;
        db_name = build.dbname;
        
    }
    
    @Before
    public void setUp() throws IOException, SectionNotFoundException, CorruptedFileException {
        cvr = new CVR.Builder(location).build();
    }
    
    @Test
    public void check_version(){
        assertEquals("CVR version", version, cvr.getCVRChunkVersion(),0.01);
    }
    
    @Test
    public void check_db_name(){
        assertEquals("CVR DB Name", db_name, cvr.getChuckDatabaseName());
    }
    
    @Test
    public void check_color_palette(){
        for(int ii = 0; ii < cvr.getPalette().getPhysicalPalette().size(); ii++) {
            log.debug("{} {} ", ii, cvr.getPalette().getPhysicalPalette().get(ii).toString());
        }
        
    
}
    
    public static class Builder{
    private final Path location;
    private final double version;
    private final String dbname; 

        public Builder(String loc, double version, String DB_NAME) {
            location = Paths.get("src/test/resources").resolve(Paths.get(loc));
            this.version = version;
            this.dbname = DB_NAME;
        }
        
     public TestCVR build(){
         return new TestCVR(this);
     }
    }
}
