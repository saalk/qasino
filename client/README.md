# ![Qasino Admin](public\images\other\CoolTextQasinocardgames.png)

> An Single-Page application (SPA) project based on Quasar framework and / Vuejs

## Links

* [Vuejs Page](https://vuejs.org/)
* [Quasar Framework Page](http://quasar-framework.org/)

## Demo (Android or IOS Style)

![https://](public\images\other\icon-ios.png)
![https://](public\images\other\icon-android.png)

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

### quasar

```bash
npm install -g @quasar/cli
quasar create <folder_name>
```

### build setup

```bash
npm install // install package.json and all dependencies
npm shrinkwrap
```

### serve with hot reload at localhost:8080

```bash
quasar dev
```

### Lint the files

```bash
quasar lint
```

### build for production with minification

```bash
quasar build

npx cap init

npx cap add android
npx cap add ios
npx cap add electron
```

### Customize the configuration

See [Configuring quasar.conf.js](https://quasar.dev/quasar-cli/quasar-conf-js).
