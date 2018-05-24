var express = require('express');
var app = express();
var bodyParser = require('body-parser');
var fs = require('fs');

app.use(bodyParser.json()); // for parsing application/json
app.use(bodyParser.urlencoded({ extended: true })); // for parsing application/x-www-form-urlencoded

app.use(function (req, res, next) {

    // Website to be allowed to connect
    res.setHeader('Access-Control-Allow-Origin', 'http://127.0.0.1:8081');

    // Request methods to be allowed
    res.setHeader('Access-Control-Allow-Methods', 'GET, POST');

    // Request headers to be allowed
    res.setHeader('Access-Control-Allow-Headers', 'X-Requested-With,content-type');

    // // Set to true if you need the website to include cookies in the requests sent
    // // to the API (e.g. in case you use sessions)
    // res.setHeader('Access-Control-Allow-Credentials', true);

    // Pass to next layer of middleware
    next();
});


app.get('/', function(req, res) {
    // console.log(req.query.fileName);
    if(typeof req.query.fileName !== "undefined") {
        fs.readFile('data_narratives.json', function(err, data) {
            res.contentType('json');
            res.send(data);
        });
    }
    else if(typeof req.query.originNarrativeIndex !== "undefined") {
        fs.readFile('data_store.json', function(err, data) {
            data = JSON.parse(data);
            var filteredData = data.filter(function(currentValue) {
                return currentValue.originNarrativeIndex == req.query.originNarrativeIndex;
            });
            filteredData = filteredData.sort(function(current, next) {
                return (next.timeStamp - current.timeStamp); 
            });
            filteredData = JSON.stringify(filteredData, null, 4);
            res.contentType('json');
            res.send(filteredData);
        });
    }
});

app.post('/', function(req, res) {
    fs.readFile('data_store.json', function(err, data) {
        data = JSON.parse(data);
        data.push(req.body);
        data = JSON.stringify(data, null, 4);
        fs.writeFile('data_store.json', data, function(err){
            if(err)
                throw err;
        });
        res.contentType('json');
        res.send(JSON.stringify({status: "success"}));
    });
});

app.listen(8080);