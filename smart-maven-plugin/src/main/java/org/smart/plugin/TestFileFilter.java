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
 * File:                org.smart.plugin.TestFileFilter
 * Author:              rsankar
 * Revision:            1.0
 * Date:                27-10-2013
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A set filter for test files
 *
 * ************************************************************
 * */

package org.smart.plugin;

import java.io.File;
import java.io.FileFilter;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class TestFileFilter implements FileFilter
{
    private String namefilter;
    private Pattern compiled;

    public TestFileFilter(String filter)
    {
        namefilter = filter;
        namefilter = namefilter.replaceAll("\\.", "\\\\.");
        namefilter = namefilter.replaceAll("\\*\\*", ".*");
        namefilter = namefilter.replaceAll("\\?\\?", ".");
        compiled = Pattern.compile(namefilter);
    }

    public boolean accept(File pathname)
    {
        String name = pathname.getName(); //matching only the filename??
        if (compiled.matcher(name).matches())
            return true;

        return false;
    }
}

