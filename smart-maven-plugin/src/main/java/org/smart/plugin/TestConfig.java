/**
 * SMART - State Machine ARchiTecture
 *
 * Copyright (C) 2012 Individual contributors as indicated by
 * the @authors tag
 *
 * This file is a part of SMART.
 *
 * SMART is a free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SMART is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 * */
 
/**
 * ************************************************************
 * HEADERS
 * ************************************************************
 * File:                org.smart.plugin.TestConfig
 * Author:              rsankar
 * Revision:            1.0
 * Date:                27-10-2013
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A configuration for tests
 *
 * ************************************************************
 * */

package org.smart.plugin;

import java.io.File;

public class TestConfig
{

    private String testuser;
    private String testpassword;
    private String files;

    public TestConfig()
    {
        files = "**.json";
    }

    public String getTestUser() { return testuser; }
    public String getTestPassword() { return testpassword; }

    public String toString()
    {
        return testuser + ":" + testpassword + ":" + files;
    }

    public File[] getFiles(String dir)
    {
        File file = new File(dir);
        return file.listFiles(new TestFileFilter(files));
    }
}

