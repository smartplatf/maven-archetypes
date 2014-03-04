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
 * File:                org.smart.plugin.TestDescription
 * Author:              rsankar
 * Revision:            1.0
 * Date:                27-10-2013
 *
 * ************************************************************
 * REVISIONS
 * ************************************************************
 * A set of descriptions for tests
 *
 * ************************************************************
 * */

package org.smart.plugin;

import java.util.Map;
import java.util.Collection;

import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

public class TestDescription
{
    public class keydata
    {
        String type;
        String value;
        //Map keydata;

        String getAction()
        {
            if ((value != null) && (value.indexOf("*") >= 0))
                return "search";

            return "lookup";
        }

        Object getValue()
        {
            if (value != null)
                return value;

            //if (keydata != null)
            {
                //TODO
            }

            return "notgiven";
        }

        public String toString()
        {
            return type + ":" + value;
        }
    }

    private String event;
    private keydata postto;
    private String postdatafromfile;
    private String dataType;
    private Map<String, Object> postdata;
    private Map<String, Object> test;

    public TestDescription()
    {
    }

    public String getEvent() { return event; }
    public String getPostDataFromFile() { return postdatafromfile; }
    public String getDataType() { return dataType; }
    public void addKey(JSONObject json)
    {
        JSONObject jkey = new JSONObject();
        String action = postto.getAction();
        jkey.put("___smart_action___", action);
        jkey.put("___smart_value___", postto.getValue());
        json.put(postto.type, jkey);
    }

    private void addCollection(JSONArray json, Collection data)
    {
        for (Object obj : data)
        {
            Object add = getObjectData(obj);
            if (add != null)
                json.add(add);
        }
    }

    private Object getObjectData(Object val)
    {
        if (val == null)
            return val;
        if (val instanceof Map)
        {
            JSONObject jobj = new JSONObject();
            addData(jobj, (Map)val);
            return jobj;
        }
        else if (val instanceof Collection)
        {
            JSONArray arr = new JSONArray();
            addCollection(arr, (Collection)val);
            return arr;
        }

        return val;
    }

    public void addData(JSONObject json, Map data)
    {
        if (data == null)
            return;

        for (Object key : data.keySet())
        {
            Object val = data.get(key);
            Object add = getObjectData(val);
            if (add != null)
                json.put(key, add);
        }
    }

    public void addData(JSONObject json)
    {
        addData(json, postdata);
    }

    public Map getTest() { return test; }

    public String toString()
    {
        return "Event: " + event + " keydata: " + postto
            + " postdata: " + 
                    postdata + " test: " + test;
    }
}

