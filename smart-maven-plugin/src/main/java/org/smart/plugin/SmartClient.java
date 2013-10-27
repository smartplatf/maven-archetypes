package org.smart.plugin;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import net.sf.json.JSONSerializer;
import net.sf.json.JSON;

import org.anon.smart.channels.shell.SCShell;
import org.anon.smart.channels.distill.Rectifier;
import org.anon.smart.channels.http.HTTPConfig;
import org.anon.smart.channels.http.HTTPClientChannel;
import org.anon.smart.channels.data.PData;
import org.anon.smart.channels.data.ContentData;

import static org.anon.utilities.services.ServiceLocator.*;

public class SmartClient
{
    private SCShell _shell;
    protected String _server;
    protected String _tenant;
    private String _flow;
    private String _soa;
    protected int _port;

    public SmartClient(int port, String server, String tenant, String flow, String soa)
    {
        _port = port;
        _tenant = tenant;
        _flow = flow;
        _server = server;
        _soa = soa;
        _shell = new SCShell();
    }

    public SmartClient()
    {
        _shell = new SCShell();
    }

    public AssertJSONResponse post(String event, String post)
        throws Exception
    {
        return postTo(_port, _tenant, _flow, event, post);
    }

    public AssertJSONResponse deployJar(String jar, String flow)
        throws Exception
    {
        AssertJSONResponse resp = postTo(_port, _server, 
                "/SmartOwner/AdminSmartFlow/DeployEvent", 
                "{'TenantAdmin':{'___smart_action___':'lookup', '___smart_value___':'SmartOwner'}, 'deployJar':'" + jar + "','flowsoa':'" + flow + "'}", true);
        assertion().assertTrue(resp != null, "Response Null");
        resp.assertStringStartsWith("success", "Done");
        return resp;
    }

    public AssertJSONResponse createTenant(String tenant, String enable, String features)
        throws Exception
    {
        AssertJSONResponse resp = postTo(_port, _server, "/SmartOwner/AdminSmartFlow/NewTenant", "{'TenantAdmin':{'___smart_action___':'lookup', '___smart_value___':'SmartOwner'}, 'tenant':'" + tenant + "','enableFlow':'" + enable + "','enableFeatures':[" + features + "]}", true);
        assertion().assertTrue(resp != null, "Response Null");
        resp.assertStringStartsWith("success", "Done");
        return resp;
    }


    public AssertJSONResponse postTo(int port, String event, String post)
        throws Exception
    {
        return postTo(port, _tenant, _flow, event, post);
    }

    public AssertJSONResponse postTo(int port, String tenant, String flow, String event, String post)
        throws Exception
    {
        String uri = "/" + tenant + "/" + flow + "/" + event;
        return postTo(port, _server, uri, post, true);
    }

    protected PData createPData(InputStream istr, String uri)
        throws Exception
    {
        return new TestPData(null, new ContentData(istr));
    }

    public AssertJSONResponse postTo(int port, String server, String uri, String post, boolean wait)
        throws Exception
    {
        String ret = "";
        JSON jret = null;
        ResponseCollector collect = new ResponseCollector(wait);
        Rectifier rr = new Rectifier();
        rr.addStep(collect);
        HTTPConfig ccfg = new HTTPConfig(port, false);
        ccfg.setClient();
        ccfg.setServer(server);
        ccfg.setRectifierInstinct(rr, new TestDataFactory());
        HTTPClientChannel cchnl = (HTTPClientChannel)_shell.addChannel(ccfg);
        cchnl.connect();
        ByteArrayInputStream istr = new ByteArrayInputStream(post.getBytes());
        //PData d = new TestPData(null, new ContentData(istr));
        PData d = createPData(istr, uri);
        cchnl.post(uri, new PData[] { d });
        if (wait)
        {
            collect.waitForResponse();
            ret = collect.getResponse();
            cchnl.disconnect();
            jret = JSONSerializer.toJSON(ret);
            return new AssertJSONResponse(jret);
        }

        return null;
    }
}

