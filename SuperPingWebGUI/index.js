var express = require('express');
const fs = require('fs');
const path = require('path');

var app = express();

app.engine('html', require('ejs').renderFile);

app.use('/js', express.static(__dirname + '/node_modules/bootstrap/dist/js')); // redirect bootstrap JS
app.use('/js', express.static(__dirname + '/node_modules/jquery/dist')); // redirect JS jQuery
app.use('/css', express.static(__dirname + '/node_modules/bootstrap/dist/css')); // redirect CSS bootstrap


app.get('/', function (req, res) {
  res.render('index.html');
});

var bodyParser = require('body-parser')
app.use(bodyParser.json()); // support json encoded bodies
app.use(bodyParser.urlencoded({ extended: true })); // support encoded bodies

app.get('/getcountry', function (req, res) {
  res.setHeader('Content-Type', 'application/json');

  var xd = req.query['country'];
  var p = "./countries/" + xd;

  let dirCont = fs.readdirSync( p );
  let files = dirCont.filter( function( elm ) {return elm.match(/.*\.(json)/ig);});

  var jsonarray = [];
  for (var i = 0; i < files.length; i++) {
    var parsedJSON = require(p+"/"+files[i]);

    jsonarray.push(parsedJSON);
  }
  
  var json = JSON.stringify(xd);
  res.send(JSON.stringify(jsonarray));

});

app.get('/listcountries', function (req, res) {
  res.setHeader('Content-Type', 'application/json');
  
  var p = "./countries/";


  var countriesList = [];
  var countriesJSON;

  fs.readdir(p, function (err, files) {
    if (err) {
      throw err;
    }
    var lol;
    files.map(function (file) {
      return path.join(p, file);
    }).filter(function (file) {
      return fs.statSync(file).isDirectory();
    }).forEach(function (file) {
      lol = file.substr(10);
      countriesList.push(lol);
    });
    countriesJSON = JSON.stringify(countriesList);
    res.send(countriesJSON);
    console.log("Countries loaded!");
  });


});





/*app.get('/', function (req, res) {
  res.render('index')
  //res.send('Hello World!');
});*/

app.listen(3000, function () {
  console.log('App listening on port 3000!');
});
