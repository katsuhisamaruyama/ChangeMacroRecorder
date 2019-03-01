// 
// A simple server for testing HTTP POST requests
// This program for node.js. To run,
//   % node httpJson.js

const http = require('http');
const port = 1337;
const querystring = require('querystring');

const server = http.createServer(function (request, response) {
    request.setEncoding("utf-8");

    if (request.method === 'GET') {
        response.writeHead(200, {'Content-Type': 'text/plain; charset=utf-8'});
        response.end('This is a ChangeMacroRecorder HTTP server!\n');

    } else if (request.method === 'POST') {
        request.on("data", function(chunk) {
            const data = JSON.parse(chunk);            
            console.log(data);            
            response.statusCode = 200;
            response.end(JSON.stringify({ 'result': 'Ok' }));
        });
    
    } else {
        respond.statusCode = 404;
        response.writeHead(404, {'Content-Type': 'text/plain; charset=utf-8'});
        response.end('Not Found\n');
    }
});
server.listen(1337, '127.0.0.1');
console.log('Server running');
