package org.smart.plugin;

import org.anon.smart.channels.Route;
import org.anon.smart.channels.http.HTTPMessageReader;
import org.anon.smart.channels.http.HTTPDataFactory;
import org.anon.smart.channels.http.HTTPMessageDScope;

import org.anon.utilities.exception.CtxException;

public class TestDScope extends HTTPMessageDScope
{
    public TestDScope(Route r, Object msg, HTTPMessageReader rdr, HTTPDataFactory fact)
        throws CtxException
    {
        super(r, msg, rdr, fact);
    }

    protected void handlePath(String path)
        throws CtxException
    {
    }

	@Override
	public Object eventLegend(ClassLoader ldr) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String flow() {
		// TODO Auto-generated method stub
		return null;
	}
}

