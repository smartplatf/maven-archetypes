package org.smart.plugin;

import java.io.InputStream;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

import org.anon.smart.channels.data.PData;
import org.anon.smart.channels.http.HTTPRequestPData;

import static org.anon.utilities.services.ServiceLocator.*;

public class SecureSmartClient extends SmartClient
{
    private String _sessionId;
    private String _adminSessionId;

    public SecureSmartClient()
    {
        super();
    }

    public SecureSmartClient(int port, String server, String tenant, String flow, String soa)
    {
        super(port, server, tenant, flow, soa);
    }

    @Override
    protected PData createPData(InputStream istr, String uri)
        throws Exception
    {
        HTTPRequestPData data = (HTTPRequestPData)super.createPData(istr, uri);
        boolean useAdmin = (uri.contains("AdminSmartFlow") && uri.contains("SmartOwner")); //all smart admin flows are on owner
        if ((useAdmin) && (_adminSessionId != null) && (_adminSessionId.length() > 0))
            data.addHeader("Session-Id", _adminSessionId);

        if ((!useAdmin) && (_sessionId != null) && (_sessionId.length() > 0))
            data.addHeader("Session-Id", _sessionId);
        return data;
    }

    public AssertJSONResponse createUserWithIdentity(String username, String user, String identity, String password)
        throws Exception
    {
        AssertJSONResponse resp = post("CreateUser", "{'FlowAdmin':{'___smart_action___':'lookup', '___smart_value___':'Security'},'id':'" + user + "','name':'" + username + "'}");
        assertion().assertTrue(resp != null, "No Response");
        JSONArray arr = resp.getAllResponses();
        assertion().assertTrue(arr.size() > 0, "No Response");
        JSONObject o = (JSONObject)arr.get(0);
        String v = (String)o.get("message");
        assertion().assertTrue(v != null, "Value null");
        assertion().assertTrue(v.startsWith("Created a user for") || v.startsWith("A user already exists for:"), "Error create user");

        if (v.startsWith("Created a user for"))
        {
            resp = post("AddIdentity", "{'SmartUser':{'___smart_action___':'lookup','___smart_value___':'rsankarx'},'identity':'" + identity + "','credentialKey':'" + password + "','type':'custom'}");
            assertion().assertTrue(resp != null, "Response null");
            resp.assertStringValue("message", "Added identity: " + identity);
        }

        return resp;
    }

    public AssertJSONResponse authenticate(String identity, String password)
        throws Exception
    {
        return authenticate(identity, password, false);
    }

    public AssertJSONResponse authenticate(String identity, String password, boolean admin)
        throws Exception
    {
        AssertJSONResponse resp = postTo(_port, _server, "/" + _tenant + "/Security/Authenticate", "{'FlowAdmin':{'___smart_action___':'lookup', '___smart_value___':'Security'},'identity':'" +identity + "', 'password':'" + password + "', 'type':'custom'}", true);
        assertion().assertTrue(resp != null, "No Response");

        JSONArray resparr = resp.getAllResponses();
        assertion().assertTrue(resparr != null, "No Response");
        assertion().assertTrue(resparr.size() > 0, "No Response");
        JSONObject o = (JSONObject)resparr.get(0);
        String v = (String)o.get("sessionId");
        assertion().assertTrue(v != null, "Value Null");

        if (admin)
            _adminSessionId = v;
        else
            _sessionId = v;

        System.out.println("Set the session Id as: " + _sessionId);
        return resp;
    }

    public AssertJSONResponse authenticateAdmin(String identity, String password)
        throws Exception
    {
        AssertJSONResponse resp = postTo(_port, _server, "/SmartOwner/Security/Authenticate", "{'FlowAdmin':{'___smart_action___':'lookup', '___smart_value___':'Security'},'identity':'" + identity + "', 'password':'" + password + "', 'type':'custom'}", true);
        assertion().assertTrue(resp != null, "Response Null");

        JSONArray resparr = resp.getAllResponses();
        assertion().assertTrue(resparr != null, "Response Null");
        assertion().assertTrue(resparr.size() > 0, "No Response");
        JSONObject o = (JSONObject)resparr.get(0);
        String v = (String)o.get("sessionId");
        assertion().assertTrue(v != null, "Value null");
        _adminSessionId = v;
        return resp;
    }

    public AssertJSONResponse logout()
        throws Exception
    {
        AssertJSONResponse resp = post("Logout", "{'Session':{'___smart_action___':'lookup', '___smart_value___':'" + _sessionId + "','___key_type___':'java.util.UUID'}}");
        assertion().assertTrue(resp != null, "Response null");
        return resp;
    }
}

