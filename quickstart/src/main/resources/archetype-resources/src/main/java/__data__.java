#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

public class ${data} implements java.io.Serializable
{
    //This is an automatically generated key for referencing
    //your object. If you change this, be sure to change the
    //corresponding .soa file also.
    
    private String ${artifactId};

    //Add your data fields here. This will be stored and
    //retrieved back when this data is created and updated.
    //Creating a member as transient will not save the data.

    public ${data}()
    {
    }

    public String get${artifactId}() { return ${artifactId}; }
}

