// 
// A simple server for testing HTTP POST requests
// This program for node.js. To run,
//   % node http.js

var http = require('http');
var querystring = require('querystring');

http.createServer(function (req, res) {
    
    if (req.url === '/' && req.method === 'GET') {
        res.writeHead(200, {'Content-Type': 'text/plain; charset=utf-8'});
    Res.end('This is a test HTTP server!\n');
    
    } else if (req.url === '/post' && req.method === 'POST') {
    var data = '';
    req.on('readable', function(chunk) {
        data += req.read();
    });
    req.on('end', function() {
        querystring.parse(data);
            res.end(data);
            console.log(data);
        });
    
    } else {
        res.statusCode = 404;
        res.end('Not Found!\n');
    }
}).listen(1337, '127.0.0.1');
console.log('Server running');
