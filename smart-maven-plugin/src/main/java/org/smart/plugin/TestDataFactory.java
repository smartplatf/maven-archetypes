package org.smart.plugin;

import java.io.InputStream;

import org.anon.smart.channels.Route;
import org.anon.smart.channels.MessageReader;
import org.anon.smart.channels.data.CData;
import org.anon.smart.channels.data.PData;
import org.anon.smart.channels.data.DScope;
import org.anon.smart.channels.http.HTTPMessageDScope;
import org.anon.smart.channels.http.HTTPMessageReader;
import org.anon.smart.channels.http.HTTPDataFactory;
import org.anon.smart.channels.distill.Rectifier;

import org.anon.utilities.exception.CtxException;

public class TestDataFactory implements HTTPDataFactory
{
    public TestDataFactory()
    {
    }

    public HTTPMessageDScope createDScope(Route chnl, Object msg, MessageReader rdr)
        throws CtxException
    {
        return new TestDScope(chnl, msg, (HTTPMessageReader)rdr, this);
    }

    public PData createPrimal(DScope scope, CData data)
        throws CtxException
    {
        return new TestPData(scope, data);
    }
}

