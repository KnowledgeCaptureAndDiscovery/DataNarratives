# \<feedback-app\>

The single page web app of the feedback system

## Install the Polymer-CLI

First, make sure you have the [Polymer CLI](https://www.npmjs.com/package/polymer-cli) installed. Then run `polymer serve` to serve your application locally.

## Viewing Your Application

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

## Running a Server with node.js

As prerequisites, install node.js on your machine (find installers [here](https://nodejs.org/en/download/), or use a package manager). The Node Package Manager (npm) is also installed with node.

Install some modules necessary 

```
$ npm install express
$ npm install body-parser
```

