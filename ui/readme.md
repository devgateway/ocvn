# DG Toolkit
## Prerequisites
In order to be able to work efficiently you should familiarize yourself with the following technologies:

1. EcmaScript6 aka ES6 aka JS2015 aka Harmony.
The new version of JavaScript. Any valid JS is valid ES6, but ES6 brings to the table some new features
 so it won't hurt to [familiarize](http://es6-features.org/) yourself with it.
 _Note: some of these features(ex. constants) might require a polyfill in order to work in IE9._
 _For a quick and dirty solution try [Babel polyfill](https://babeljs.io/docs/usage/polyfill/)_
2. [React](http://facebook.github.io/react/)
3. [ImmutableJS](https://facebook.github.io/immutable-js/) immutable data structures (not depending on it explicitly,
but NuclearJS does use it for store state)
4. The [Flux](http://facebook.github.io/flux/) architecture. Mainly its [NuclearJS](https://optimizely.github.io/nuclear-js/)
implementation.
5. [Jest](https://facebook.github.io/jest/) testing framework.
6. [Webpack](https://webpack.github.io/)
There's no need to dive deep into it, you'll mainly need to know [how to use loaders](http://webpack.github.io/docs/using-loaders.html)
and [polyfills](http://mts.io/2015/04/08/webpack-shims-polyfills/)

## Usage
Get Node.js

Install module's dependencies

    npm install

Run

    npm start

Navigate to _http://localhost:3000/dev.html_ The browser will be automatically updated
 whenever you save some changes.

To get the project ready for production, run

    maven package

Don't forget to add resources necessary for production in _pom.xml_

### Debugging
Add the "debug" key to the local storage to get debug messages
```js
localStorage.debug = "*"
```
### Tests
Run

    npm test

### Pure components
Since we're using immutable data structures, we can reason about whether a component needs to be updated or not
simply by comparing shallowly its previous set of props with current ones. To make use of that, either mix
[PureRenderMixin](https://facebook.github.io/react/docs/pure-render-mixin.html) into your components, or subclass
_components/pure-render-component_

### Mixing non-react modules
As it is known, there are no silver bullets, so sometime you might need to use third-party
modules in your app. When that time comes, use _components/react-ignore_, which allows you
to "hide" a part of your app's subtree from React and do some custom manipulations with it,
like imperatively mutating the DOM. You can use \<ReactIgnore> inside your existing components
as well as subclass it. In case of subclassing put your logic in the _componentDidMount_ method.


### Async modules
It is often when a module must not be loaded unconditionally, but rather it must be loaded async under some logic,
in that case, just use the AMD require:
```js
require(['components/modal'], function(modal){
    //do stuff with modal
});
```

Webpack allows you to load CommonJS modules async, too.

### Organizing files
#### Module oriented
The goal is to group your files according to the component/module they belong to. If a module is trivial it can be just
one single file. If a module has submodules or assets, make it a folder. For this reason, do not specify the file
  extension when importing. To give an example, say you have a _components/my-module.es6_ file, and you require it like this:
```js
var MyModule = require('components/my-module.es6');
```

or
```js
import MyModule from 'components/my-module.es6';
```

Now suppose your module grew big enough it now deserves its own folder, but doing

    mkdir components/my-module && mv components/my-module.es6 components/my-module/index.es6

will make all of your imports break. That could've been avoided if you simply did not specify the file extension
 in the require/import statement.

#### Handling styles
Put your styles inside the component folder. Because of the [css-loader](https://github.com/webpack/css-loader)
and [less-loader](https://github.com/webpack/less-loader) you can just import those styles from JS, i.e.:
```js
require("./style.css")
```

or
```js
import HeaderStyle from "./style.css"
```

Styles will be bundled in index.js/index.min.js, also allowing hot reload during development.

If you have one single style file just name it _style.css_ or _style.less_, if you have complex styles better
put them in a "styles" folder and then require them individually
```js
require("./styles/fonts.css")
require("./styles/header.less")
require("./styles/theme.less")
```
#### Handling assets
If your module needs to read a config JSON, or load translations from a *.po file, or any other type of asset,
 place the asset inside the module's folder, install the necessary WebPack loader, ex.

    npm install json-loader --save-dev

Modify _webpack.dev.config.js_ so that it uses the right loader for the extension. Then just require the asset from
 within your module:
```js
import config from "./config.json"
if(config.userUrl){
    //do stuff
} else {
    /do other stuff
}
```
#### Tests
Put your test cases inside a *\__tests__*(two underscores before the word _test_ and two after) folder within
 your module's folder.

#### Components
Put your components in the _components/_ folder. Nest parent/child components when it makes sense.

#### Flux
##### Actions
Put your actions in _flux/actions_. Don't forget to register the constant in _constants.es6_ as well as implementing
the action creator in _index.es6_
##### Stores
Put your stores in _flux/stores_. Don't forget to import and register them in _flux/index.es6_

##Troubleshooting
If running

    npm start

gives you an error, and/or you had an _optional dependency failed_ error during Maven build, install Python 2 and a C++
compiler on your system then run Maven again. For more information
refer to [node-gyp readme](https://github.com/nodejs/node-gyp)

### Workaround
In case you don't want to/cannot install Python 2 and/or the C++ compiler, run

    webpack --config webpack.dev.config.js --watch

inside the _/ui/_ directory, then open _dev.html_ file.
