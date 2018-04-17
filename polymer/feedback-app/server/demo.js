var express = require('express');
var app = express();
var bodyParser = require('body-parser');
var fs = require('fs');

app.use(bodyParser.json()); // for parsing application/json
app.use(bodyParser.urlencoded({ extended: true })); // for parsing application/x-www-form-urlencoded

app.use(function (req, res, next) {

    // Website you wish to allow to connect
    res.setHeader('Access-Control-Allow-Origin', 'http://localhost:8081');

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
    fs.readFile('data_narratives.json', function(err, data) {
        res.contentType('json');
        res.send(data);
    });
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