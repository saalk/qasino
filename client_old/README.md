# Boot Vue Client

## Project setup
```
yarn install
```

### Compiles and hot-reloads for development
```
yarn run serve
```

### Compiles and minifies for production
```
yarn run build
npx cap add android / npx cap add ios // once
npx cap copy
npx cap open android // go to android studio
```
### Android studio
```
yarn run build
npx cap add android / npx cap add ios // once
npx cap copy
npx cap open android // go to android studio
```
### NB Capacitor build + Ionic framework setup
``` 
add ionic script + meta tags to set viewport to public/index.html
add <ion-app></ion-app> tags to app.vue
tell vue that ion is not vue component in src/main.js
```
### NB initialize Capacitor
``` 
npx cap init to get a capacitor.config.json
```
### NB usefull Capacitor plugins
``` 
import { Plugins } from '@capacitor/core';
-> Plugins.Modals.alert({title, msg})
```




