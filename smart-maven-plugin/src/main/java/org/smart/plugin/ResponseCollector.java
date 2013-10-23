package org.smart.plugin;

import org.anon.smart.channels.distill.Distillation;
import org.anon.smart.channels.distill.Rectifier;
import org.anon.smart.channels.distill.Distillate;

import org.anon.utilities.exception.CtxException;

public class ResponseCollector implements Distillation
{
    private Rectifier _myRectifier;
    private String _response;
    private Object _waitForResponse;
    private boolean _wait;

    public ResponseCollector(boolean waitresp)
    {
        _wait = waitresp;
        if (_wait)
            _waitForResponse = new Object();
    }

    public void setRectifier(Rectifier rectifier)
    {
        _myRectifier = rectifier;
    }

    public Distillate distill(Distillate prev)
        throws CtxException
    {
        TestPData data = (TestPData)prev.current();
        _response = data.getPosted();
        System.out.println(prev.current());
        if (_waitForResponse != null)
        {
            _wait = false;
            synchronized(_waitForResponse)
            {
                _waitForResponse.notifyAll();
            }
        }
        return prev;
    }

    public Distillate condense(Distillate prev)
        throws CtxException
    {
        System.out.println(prev.current());
        return prev;
    }

    public boolean distillFrom(Distillate prev)
        throws CtxException
    {
        return true;
    }

    public boolean condenseFrom(Distillate prev)
        throws CtxException
    {
        return true;
    }

    public String getResponse()
    {
        return _response;
    }

    public void waitForResponse()
        throws Exception
    {
        if (_wait)
        {
            synchronized(_waitForResponse)
            {
                if (_wait)
                    _waitForResponse.wait();
            }
        }
    }
}

