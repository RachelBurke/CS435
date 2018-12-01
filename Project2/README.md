# Project 2 - HTTP Web Server

## Instructions

Write a single-threaded web server which:

Handles GET requests
Handles html, css, png, jpg and jpeg files
returns one of 200, 404 or 500 statuses for each request
may use non-persistent or persistent HTTP (your choice which one)
emits a log message to stdout describing each request handled
runs on thomas.butler.edu

## Requirements

The program should be organized using classes and functions.  Minimally it should likely include:
A class for HTTP requests
A class for HTTP responses
A function for handling each client connection
A function for (trying) to read files for each request
When finished, your webserver should be able to successfully serve a simple web page to a web browser like firefox, chrome or safari.

## Helpful Links

The typed structure we created together on the whiteboard can be found here as a gist:
https://github.butler.edu/gist/npartenh/5f25530c5cd5139c5508031b92bf66e7

The code we started in class can also be found here as a gist:
https://github.butler.edu/gist/npartenh/292f504e85c7ea230912687892daada3
