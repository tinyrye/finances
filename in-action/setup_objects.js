var http = require("http");
var fs = require("fs");

function EntityResponseHandler(response, afterEntityCompleted)
{
    var me = this;
    this.responseBodyText = "";
    this.entityObject = null;
    response.on("data", function(chunk) {
        me.responseBodyText += chunk;
    });
    response.on("end", function() {
        if (response.statusCode == 200) {
            me.entityObject = JSON.parse(me.responseBodyText);
            afterEntityCompleted(me);
        }
    });
}

var referenceEntities = null;

var holderId = null;
var holder = null;
var activeBudget = null;
if (process.argv.length > 2) {
    holderId = parseInt(process.argv[2]);
}

fs.readFile('main-objects.json', function(err, data) {
    referenceEntities = JSON.parse(data);
    referenceEntities.primaryAccount.holder = referenceEntities.holder;
});

var holderByIdGet = {
    hostname: "localhost", port: 8088,
    method: "GET", path: "/account/holder/" + holderId
};

var holderPost = {
    hostname: 'localhost', port: 8088,
    method: "POST", path: "/account/holder",
    headers: {
        'Content-Type': 'application/json'
    }
};

var primaryAccountPost = {
    hostname: 'localhost', port: 8088,
    method: 'POST', path: '/account?primary=true',
    headers: { 'Content-Type': 'application/json' }
};

var activeBudgetGet = {
    hostname: 'localhost', port: 8088,
    method: 'GET', path: '/account/holder/' + holderId + '/budget'
}

var activeBudgetPost = {
    hostname: 'localhost', port: 8088,
    method: 'POST', path: '/account/holder/' + holderId + '/budget',
    headers: { 'Content-Type': 'application/json' }
}

var activeBudgetItemPost = {
    hostname: 'localhost', port: 8088,
    method: 'POST', path: '/account/holder/' + holderId + '/budget/item',
    headers: { 'Content-Type': 'application/json' }
}

function addActiveBudgetItems()
{
    referenceEntities.activeBudget.items.forEach(function(item)
    {
        console.info("Adding budget item: " + JSON.stringify(item));
        var request = http.request(activeBudgetItemPost, function(response) {
            var responseHandler = new EntityResponseHandler(response, function(h) {
                activeBudget.items.push(h.entityObject);
            });
        });
        request.write(JSON.stringify(item));
        request.end();
    });
}

function createActiveBudget()
{
    var request = http.request(activeBudgetPost, function(response) {
        var responseHandler = new EntityResponseHandler(response, function(h) {
            activeBudget = h.entityObject;
            activeBudget.items = [];
            addActiveBudgetItems();
        });
        switch (response.statusCode) {
            case 200: console.info("Created Active Budget"); break;
        }
    });
    console.info("Budget: " + JSON.stringify(referenceEntities.activeBudget));
    request.write(JSON.stringify(referenceEntities.activeBudget));
    request.end();
}

function ensureActiveBudget()
{
    var request = http.request(activeBudgetGet, function(response) {
        var responseHandler = new EntityResponseHandler(response, function(h) {
            budget = h.entityObject;
            
        });
        switch (response.statusCode) {
            case 200: console.info("Has active budget"); break;
            case 404: console.info("Missing active budget");
                createActiveBudget(); break;
        }
    });
    request.end();
}

function ensurePrimaryAccount()
{
    referenceEntities.primaryAccount.holder = holder;
    if (holder.primaryAccount == null) {
        var request = http.request(primaryAccountPost, function(response) {
            var responseHandler = new EntityResponseHandler(response, function(h) {
                holder.primaryAccount = h.entityObject;
                ensureActiveBudget();
            });
        });
        request.write(JSON.stringify(referenceEntities.primaryAccount));
        request.end();
    }
    else {
        ensureActiveBudget();
    }
}

var createHolder = function()
{
    console.info("Creating Holder");
    var request = http.request(holderPost, function(response) {
        var responseHandler = new EntityResponseHandler(response, function(h) {
            holder = h.entityObject;
            console.info("Created Holder: id=" + holder.id);
            ensurePrimaryAccount();
        });
        console.info("Create Holder Response: " + response.statusCode);
        switch (response.statusCode) {
            case 302: console.warn("Already exists:");
        }
    });
    request.write(JSON.stringify(referenceEntities.holder));
    request.end();
}

var getHolder = function()
{
    console.info("Lookup Holder #" + holderId);
    var request = http.request(holderByIdGet, function(response)
    {
        var responseHandler = new EntityResponseHandler(response, function(handler) {
            holder = handler.entityObject;
            console.log("Get Holder Response: " + handler.responseBodyText);
            ensurePrimaryAccount();
        });
        switch (response.statusCode) {
            case 404:
                console.error("Does not exist by id: " + holderByIdGet.path);
                createHolder(referenceEntities.holder);
                break;
            case 500: console.error("Failed response"); process.exit();
        }
    });
    request.end();
}

if (holderId == null) createHolder(referenceEntities.holder);
else getHolder(holderId);
