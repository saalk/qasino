# [lion quiz]

> An Single-Page application (SPA) project based on Quasar framework and Vuejs

## Links

* [Vuejs Page](https://vuejs.org/)
* [Quasar Framework Page](http://quasar-framework.org/)

## Demo (Android or IOS Style)

![https://](~assets\misc\icon-ios.png)
![https://](~assets\misc\icon-android.png)

## Infos

* Using Quasar default template
* Using [Jsonplaceholder](https://jsonplaceholder.typicode.com/) as backend API
* Using [Flaticon](http://www.flaticon.com) for SVG's
* Using [Adorable Avatars](http://avatars.adorable.io/) for cool avatars

## Dependecies

* [Axios.js](https://github.com/mzabriskie/axios) as HTTP client / Config from [CodeCasts](https://github.com/codecasts/spa-starter-kit/blob/develop/client/src/plugins/http/index.js)
* [Chart.js](http://www.chartjs.org) as Chart render
* [CountUp.js](https://inorganik.github.io/countUp.js/) as number counter animation
* [Gmaps.js](https://hpneo.github.io/gmaps/) as map Api / data
* [Vivus.js](https://maxwellito.github.io/vivus/) as SVG animator
* [Vuelidate.js](https://monterail.github.io/vuelidate/) for form validations
* [Dragula.js](https://github.com/bevacqua/dragula) for drag and drop

## Project Setup

### setup new quasar project

```bash
npm install -g @quasar/cli // for quasar
npm install -g cordova // tool for building mobile apps using HTML, CSS and JS
quasar create <folder_name>
```

### project install

```bash
npm install // install package.json and all dependencies
- dl from nodejs.org // install latest nodes version manager
maintenance:
- npm install -g @quasar/icongenie
- rimraf node_modules package-lock.json yarn.lock // command rm -rf
- npm shrinkwrap
- icongenie generate -i src/statics/start/icon.png
```

### serve with hot reload at localhost:8080

```bash
quasar dev
maintanance:
- quasar info/ quasar upgrade
- remove quasar-cli
- rimraf .quasar
- rimraf .node_modules
- npm i @quasar/app include=dev
- npm i @quasar/extras
```

### Lint the files

```bash
npm run lint
```

### Set the App log everywhere

```bash
// source image should be 1240x1240 and 2436x2436
icongenie generate -i src/statics/start/icon.png
```

### build for production with minification

```bash
quasar build
quasar build -m cordova -T android
```

### Sign the APK for playstore
### https://quasar.dev/quasar-cli/developing-cordova-apps/publishing-to-store

```bash
keytool -genkey -v -keystore my-release-key.keystore -alias alias_name -keyalg RSA -keysize 2048 -validity 20000
cd to src-cordova\platforms\android\app\build\outputs\apk\release\
jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore my-release-key.keystore <path-to-unsigned-apk-file> alias_name
// run zipalign to optimise the APK and generate the final APK file 
// zipalign is part of the Android SDK
zipalign -v 4 <path-to-same-apk-file> HelloWorld.apk
```

### Customize the configuration

See [Configuring quasar.conf.js](https://quasar.dev/quasar-cli/quasar-conf-js).
