var express = require('express');
var app = express();
var bodyParser = require('body-parser');
var fs = require('fs');

app.use(bodyParser.json()); // for parsing application/json
app.use(bodyParser.urlencoded({ extended: true })); // for parsing application/x-www-form-urlencoded

app.use(function (req, res, next) {

    // Website you wish to allow to connect
    res.setHeader('Access-Control-Allow-Origin', 'http://127.0.0.1:8081');

    // Request methods you wish to allow
    res.setHeader('Access-Control-Allow-Methods', 'GET, POST');

    // Request headers you wish to allow
    res.setHeader('Access-Control-Allow-Headers', 'X-Requested-With,content-type');

    // Set to true if you need the website to include cookies in the requests sent
    // to the API (e.g. in case you use sessions)
    res.setHeader('Access-Control-Allow-Credentials', true);

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
        console.log(req.query.originNarrativeIndex);
        fs.readFile('data_store.json', function(err, data) {
            data = JSON.parse(data);
            // console.log(data);
            var filteredData = data.filter(function(currentValue) {
                console.log(currentValue.originNarrativeIndex);
                return currentValue.originNarrativeIndex == req.query.originNarrativeIndex;
            });
            console.log(filteredData);
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
    // console.log(req.body);
    fs.readFile('data_store.json', function(err, data) {
        data = JSON.parse(data);
        data.push(req.body);
        data = JSON.stringify(data, null, 4);
        fs.writeFile('data_store.json', data, function(err){
            if(err)
                throw err;
        });
        res.contentType('json');
        res.send(data);
    });
//  res.contentType('json');
//  res.send({ status: "successful" });
});

app.listen(8080);