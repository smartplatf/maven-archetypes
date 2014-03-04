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
    private String tenant;
    private String flow;
    private String testuser;
    private String testpassword;
    private String files;
    private boolean runasadmin;
    private int wait;

    public TestConfig()
    {
        files = "**.json";
        wait = -1;
    }

    public String getTestUser() { return testuser; }
    public String getTestPassword() { return testpassword; }
    public String getTenant() { return tenant; }
    public String getFlow() { return flow; }
    public boolean runAsAdmin() { return runasadmin; }
    public int getWait() { return wait; }

    public String toString()
    {
        return testuser + ":" + testpassword + ":" + files + ":" + tenant + ":" + flow;
    }

    public File[] getFiles(String dir)
    {
        int index = files.lastIndexOf("/");
        String f = files;
        String lookin = dir;
        if (index > 0)
        {
            String addPath = files.substring(0, index);
            lookin = lookin + "/" + addPath;
            f = files.substring(index + 1);
            System.out.println("looking in directory: " + lookin + ":" + f);
        }
        File file = new File(lookin);
        return file.listFiles(new TestFileFilter(f));
    }
}

