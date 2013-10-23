package org.smart.plugin;

import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

import static org.anon.utilities.services.ServiceLocator.*;

public class AssertJSONResponse
{
    private JSONObject _jobject;
    private JSONArray _responses;
    private JSONArray _errors;

    public AssertJSONResponse(JSON j)
        throws Exception
    {
        assertion().assertTrue(j != null, "JSON Null");
        assertion().assertTrue(j instanceof JSONObject, "Not a JSON");
        _jobject = (JSONObject)j;
        _responses = (JSONArray)_jobject.get("responses");
        _errors = (JSONArray)_jobject.get("errors");
    }

    public JSONObject getjson() { return _jobject; }
    public JSONArray getAllResponses() { return _responses; }

    public void assertStringValue(String fld, String val)
        throws Exception
    {
        assertStringValue(0, fld, val);
    }

    public void assertStringValue(int ind, String fld, String val)
        throws Exception
    {
        assertion().assertTrue(_responses != null, "No Response");
        assertion().assertTrue(_responses.size() > ind, "Exception Response");
        JSONObject o = (JSONObject)_responses.get(ind);
        String v = (String)o.get(fld);
        assertion().assertTrue(v != null, "Value null");
        assertion().assertTrue(v.equals(val), "Value not equals");
    }

    public void assertStringStartsWith(String fld, String val)
        throws Exception
    {
        assertStringStartsWith(0, fld, val);
    }

    public void assertStringStartsWith(int ind, String fld, String val)
        throws Exception
    {
        assertion().assertTrue(_responses != null, "Response null");
        assertion().assertTrue(_responses.size() > ind, "No Response");
        JSONObject o = (JSONObject)_responses.get(ind);
        String v = (String)o.get(fld);
        assertion().assertTrue(v != null, "Value null");
        assertion().assertTrue(v.startsWith(val), "value not equals");
    }

    public void assertHasResponses()
        throws Exception
    {
        assertion().assertTrue(_responses != null, "Response null");
        assertion().assertTrue(_responses.size() > 0, "No Responses");
    }

    public void assertHasErrors()
        throws Exception
    {
        assertion().assertTrue(_errors != null, "No Errors");
        assertion().assertTrue(_errors.size() > 0, "No Errors");
    }
}

