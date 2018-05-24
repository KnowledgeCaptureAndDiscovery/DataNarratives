# \<feedback-app\>

The single page web app of the feedback system.

## Install Node.js and npm

If you don't have Node.js or npm installed, follow the instructions [here](https://www.npmjs.com/get-npm). 

## Install the Polymer-CLI

First, make sure you have the [Polymer CLI](https://www.npmjs.com/package/polymer-cli) installed. Then run `polymer serve` to serve your application locally.

## Installing dependencies

There are two types of dependencies that need to be installed.

Install the Polymer front-end related dependencies with

```
$ bower install
```
It will install all the dependencies in bower.json.


Next, install the Node.js back-end related dependencies with

```
$ npm install
```

It will install all the dependencies in package.json.

## Viewing Your Application

First, set the server up. Navigate to feedback-app/server directory, and run

```
$ node server.js
```

With the server running, in the directory feedback-app, run

```
$ polymer serve
```

## Building Your Application

```
$ polymer build
```

This will create builds of your application in the `build/` directory, optimized to be served in production. You can then serve the built versions by giving `polymer serve` a folder to serve from:

```
$ polymer serve build/default
```

## Running Tests

```
$ polymer test
```

Your application is already set up to be tested via [web-component-tester](https://github.com/Polymer/web-component-tester). Run `polymer test` to run your application's test suite locally.