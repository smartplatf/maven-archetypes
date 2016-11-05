package org.smart.plugin;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;

import org.anon.utilities.config.Format;
import org.anon.utilities.crosslink.CrossLinkAny;

import static org.anon.utilities.objservices.ObjectServiceLocator.*;
import static org.anon.utilities.services.ServiceLocator.*;

@Mojo( name = "test" )
@Execute( phase = LifecyclePhase.TEST )
public class SmartTestMojo
    extends AbstractMojo
{
    @Parameter( property = "maven.smart.port", defaultValue = "9081", required = true )
    private int port;

    @Parameter( property = "maven.smart.server", defaultValue = "localhost", required = true )
    private String server;

    @Parameter( property = "maven.smart.tenant", defaultValue = "", required = true )
    private String tenant;

    @Parameter( property = "maven.smart.flow", required = true, defaultValue="" )
    private String flow;

    @Parameter( property = "maven.smart.features", required = true, defaultValue="'all'" )
    private String features;

    @Parameter( property = "maven.smart.tests", required = true) 
    private List<TestConfig> tests;

    @Parameter( defaultValue = "${project.build.directory}", property = "maven.smart.testLocation", required = true )
    private File testLocation;

    public void execute()
        throws MojoExecutionException
    {
        SecureSmartClient clnt = null;

        try
        {
            clnt = new SecureSmartClient(port, server, tenant, flow, "");
            for (int i = 0; i < tests.size(); i++)
            {
                TestConfig cfg = tests.get(i); //execute in order
                File[] files = cfg.getFiles(testLocation + "/test-classes/");

                String tsttenant = cfg.getTenant();
                String tstflow = cfg.getFlow();

                SecureSmartClient useclnt = clnt;
                boolean admin = false;

                if ((tsttenant != null) || (tstflow != null))
                {
                    if (tsttenant == null)
                        tsttenant = tenant;

                    if (tstflow == null)
                        tstflow = flow;

                    useclnt = new SecureSmartClient(port, server, tsttenant, tstflow, "");
                    admin = cfg.runAsAdmin();
                }

                if (cfg.getTestUser() != null)
                    useclnt.authenticate(cfg.getTestUser(), cfg.getTestPassword(), admin);

                for (int j = 0; (files != null) && (j < files.length); j++)
                {
                    Tests tsts = readTests(files[j]);
                    System.out.println("Need to execute: " + files[j].getPath() + ":" + tsts.getTests());
                    List<TestDescription> lst = tsts.getTests();
                    for (TestDescription desc : lst)
                    {
                        if ((desc.getPostDataFromFile() == null) || (desc.getPostDataFromFile().length() <= 0))
                        {
                            JSONObject post = new JSONObject();
                            desc.addKey(post);
                            desc.addData(post);
                            test(post, desc, useclnt);
                        }
                        else
                        {
                            String path = files[j].getAbsolutePath();
                            String nm = files[j].getName();

                            int ind = path.indexOf(nm);
                            path = path.substring(0, ind);
                            path = path + desc.getPostDataFromFile();
                            InputStream str = new FileInputStream(path);
                            assertion().assertTrue((str != null), "Cannot find the file: " + path);
                            BufferedReader in = new BufferedReader(new InputStreamReader(str));
                            String line = in.readLine();
                            while(line != null)
                            {
                                String[] vals = line.split("\\|\\|\\|");
                                Map data = new HashMap();
                                for (int l = 0; l < vals.length; l++)
                                {
                                    if (vals[l].trim().length() > 0)
                                    {
                                        String[] one = vals[l].split(":");
                                        data.put(one[0], one[1]);
                                    }
                                }
                                if (desc.getDataType() != null)
                                {
                                    URL tsturl = new URL("file://" + testLocation + "/test-classes/");
                                    URLClassLoader ldr = new URLClassLoader(new URL[] { tsturl });
                                    CrossLinkAny cl = new CrossLinkAny(desc.getDataType(), ldr);
                                    data = (Map)cl.invoke("convert", new Class[] { Map.class }, new Object[] { data });
                                    System.out.println("Data is: " + data);
                                }
                                JSONObject post = new JSONObject();
                                desc.addKey(post);
                                desc.addData(post, data);
                                test(post, desc, useclnt);
                                line = in.readLine();
                            }

                            in.close();
                            str.close();
                        }
                    }
                }

                if (cfg.getWait() > 0)
                    Thread.currentThread().sleep(cfg.getWait());
            }
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Error executing tests " + flow + ":" + tenant, e );
        }
    }

    private void test(JSONObject post, TestDescription desc, SecureSmartClient useclnt)
        throws Exception
    {
        String poststr = post.toString();
        System.out.println("Posting: " + poststr + ":" + desc.getEvent());
        AssertJSONResponse resp = useclnt.post(desc.getEvent(), poststr);
        JSONArray arr = resp.getAllResponses();
        assertion().assertNotNull(arr, "The test failed. Got an error response.");
        assertion().assertTrue(arr.size() > 0, "The test failed. No responses got.");
        JSONObject jobj = (JSONObject)arr.get(0);
        Map<String, Object> test = desc.getTest();
        testagainst(jobj, test);
    }

    private void testagainst(JSONArray arr, List coll, Object key)
        throws Exception
    {
        assertion().assertTrue(arr.size() >= coll.size(), "The test failed. There are not enough elements to compare." + key);
        for (int i = 0; i < coll.size(); i++)
        {
            Object check = coll.get(i);
            boolean got = false;
            for (int j = 0; j < arr.size(); j++)
            {
                try
                {
                    Object val = arr.get(j);
                    checkagainst(val, check, key);
                    got = true;
                    break;
                }
                catch (Exception e)
                {

                }
            }

            assertion().assertTrue(got, "Cannot find a list value. The test failed. " + key + ":" + check);
        }
    }

    private void checkagainst(Object val, Object check, Object key)
        throws Exception
    {
        assertion().assertNotNull(val, "The test failed. No value found for: " + key);
        System.out.println("Checking: " + val + ":" + check + ":" + key + ":" + (check instanceof Map) + ":" + (check instanceof List));
        if (check instanceof List)
        {
            assertion().assertTrue((val instanceof JSONArray), "The test failed. The value is not an array." + key);
            List coll = (List)check;
            testagainst((JSONArray)val, coll, key);
        }
        else if (check instanceof Map)
        {
            if (val instanceof JSONArray)
                val = ((JSONArray)val).get(0);
            assertion().assertTrue((val instanceof JSONObject), "The test failed. The value is not an object." + key);
            testagainst((JSONObject)val, (Map)check);
        }
        else
        {
            //this is a direct check
            assertion().assertTrue(val.equals(check), "The test failed. The values are not equal." + key);
        }
    }

    private void testagainst(JSONObject jobj, Map test)
        throws Exception
    {
        if (test != null)
        {
            for (Object key : test.keySet())
            {
                Object val = jobj.get(key);
                Object check = test.get(key);
                checkagainst(val, check, key);
            }
        }
    }

    private Tests readTests(File file)
        throws Exception
    {
        InputStream str = new FileInputStream(file);
        Format fmt = config().readYMLConfig(str);
        Map values = fmt.allValues();
        Tests tests = convert().mapToObject(Tests.class, values);
        return tests;
    }
}
