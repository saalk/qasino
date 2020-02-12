   

# ![Qasino Admin](public\images\other\CoolTextQasinocardgames.png)

> An Single-Page application (SPA) project based on Quasar framework and / Vuejs 

## Links
* [Vuejs Page](https://vuejs.org/)
* [Quasar Framework Page](http://quasar-framework.org/)

## Demo (Android or IOS Style) 
![https://quasar-admin.firebaseapp.com/android/#/](public\images\other\icon-ios.png)
![https://quasar-admin.firebaseapp.com/ios/#/](public\images\other\icon-android.png)

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

## Project setup
```
npm install
```

### Compiles and hot-reloads for development
```
yarn run serve
```

### Compiles and minifies for production //todo
```
yarn run build
npx cap add android / npx cap add ios // once
npx cap copy
npx cap open android // go to android studio
```
### Android studio //todo
```
yarn run build
npx cap add android / npx cap add ios // once
npx cap copy
npx cap open android // go to android studio
```
### NB Capacitor build + Ionic framework setup //todo
``` 
add ionic script + meta tags to set viewport to public/index.html
add <ion-app></ion-app> tags to app.vue
tell vue that ion is not vue component in src/main.js
```
### NB initialize Capacitor //todo
``` 
npx cap init to get a capacitor.config.json
```
### NB usefull Capacitor plugins //todo
``` 
import { Plugins } from '@capacitor/core';
-> Plugins.Modals.alert({title, msg})
```

