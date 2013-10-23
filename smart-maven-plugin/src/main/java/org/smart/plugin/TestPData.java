package org.smart.plugin;

import java.util.UUID;

import org.anon.smart.channels.data.PData;
import org.anon.smart.channels.data.CData;
import org.anon.smart.channels.data.DScope;
import org.anon.smart.channels.http.HTTPRequestPData;

import static org.anon.utilities.services.ServiceLocator.*;
import org.anon.utilities.exception.CtxException;

public class TestPData extends HTTPRequestPData
{
    private String _tenant;
    private String _flowType;
    private String _eventType;
    private UUID _sessionID;
    private String _posted;

    public TestPData(DScope scope, CData data)
        throws CtxException
    {
        super(scope, data);
        _posted = io().readStream(data.data()).toString();
        try
        {
            data.data().reset();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void setTenant(String tenant) { _tenant = tenant; }
    public void setFlowType(String flow) { _flowType = flow; }
    public void setEventType(String evt) { _eventType = evt; }
    public void setSessionID(UUID sess) { _sessionID = sess; }

    public String tenant() { return _tenant; }
    public String flowType() { return _flowType; }
    public String eventType() { return _eventType; }
    public UUID session() { return _sessionID; }

    public String getPosted() { return _posted; }

    public String toString()
    {
        return ":Tenant: " + _tenant +
            ":Flow:" + _flowType +
            ":EventType:" + _eventType +
            ":Session:" + _sessionID + 
            ":data:" + _posted;
    }
}

