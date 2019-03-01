// 
// A simple server for testing HTTP POST requests
// This program for node.js. To run,
//   % node http.js

const http = require('http');
const port = 1337;
const querystring = require('querystring');

const server = http.createServer(function (request, response) {
    request.setEncoding("utf-8");

    if (request.method === 'GET') {
        response.statusCode = 200;
        response.end('This is a ChangeMacroRecorder HTTP server!\n');

    } else if (request.method === 'POST') {

        request.on("data", function(chunk) {
            console.log(chunk);
            response.statusCode = 200;
            response.end('Ok');
        });
    
    } else {
        respond.statusCode = 404;
        response.end('Not Found!');
    }
});
server.listen(1337, '127.0.0.1');
console.log('Server running');
