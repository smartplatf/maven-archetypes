##set( $symbol_pound = '#' )
##set( $symbol_dollar = '$' )
##set( $symbol_escape = '\' )
function ${data}() {
    this.prime = '${data}';
    this.callback = '';

    this.create = function(form, cb) {
        console.log("create data");
        var json = $(form).serializeObject();
        this.callback = cb;
        this.submitSuccess = this.createcallback;
        this.createPrime(this.prime, JSON.stringify(json));
    };

    this.createcallback = function(resp, smartobj) {
        smartobj.callback(resp);
    };

    this.listMessages = function(sz, cb) {
        this.callback = cb;
        this.submitSuccess = this.listcallback;
        this.list(this.prime, sz);
    };

    this.listcallback = function(resp, smartobj) {
        var cnt = 0;
        if (resp instanceof smartresponse) {
            var lresp = resp.response;
            cnt = lresp.list.length;
            console.log(lresp.list.length);
            for (var i = 0; i < lresp.list.length; i++)
            {
                smartobj.callback(lresp.list[i].values['${artifactId}']);
            }
        }
    };

    this.${event} = function(data, cb) {
        console.log("Calling event for " + data);
        this.callback = cb;
        this.submitSuccess = this.createcallback;

        //Add your event data here.
        //send['yourfld'] = 'yourvalue'
        var send = {};

        this.postDataTo(send, ${event}, this.prime, data);
    };
};

${data}.prototype = new smart();


function create${data}conn()
{
    var fsmart = new ${data}();
    fsmart.sessionId = smartconfig.userSession;
    fsmart.submitSuccess = function(response) {
    	console.log(JSON.stringify(response));
    };

    fsmart.submitFailure = function(error) {
        console.log(JSON.stringify(error));
    };

    fsmart.submitStart = function() {
    };

    fsmart.submitEnd = function() {
    };

    fsmart.submitProgress = function(pct) {
    };

    fsmart.networkFailure = function(error) {
        console.log(JSON.stringify(error));
    };

    fsmart.flowName = '${data}Flow';
    return fsmart;
}


