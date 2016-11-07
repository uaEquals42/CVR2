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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Twilight Sparkle <your.name at your.org>
 */
public class Test_A_cvr extends TestCVR{
    static Logger log = LoggerFactory.getLogger(Test_A_cvr.class);
public Test_A_cvr() {
    super(new TestCVR.Builder("A.cvr",10.1,"BASIC.MAX"));
}
}
