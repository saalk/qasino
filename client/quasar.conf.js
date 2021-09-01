// Configuration for your app
// https://quasar.dev/quasar-cli/quasar-conf-js

module.exports = function (ctx) {
  console.log(ctx)
  
  return {

    //https://quasar.dev/quasar-cli/supporting-ts
    supportTS: true,

    // https://quasar.dev/quasar-cli/prefetch-feature
    // validate a route and redirect to loon
    // or init a store state:
    // preFetch: true,

    // app boot file (/src/boot)
    // --> boot files are part of "main.js"
    // https://quasar.dev/quasar-cli/cli-documentation/boot-files
    boot: [
      // 'api',
      'filter',
      'axios',
    ],

    // https://quasar.dev/quasar-cli/quasar-conf-js#Property%3A-css
    css: [
      'app.sass',
      'animate.css',
      'vue-range-slider.css',
      'flatpickr.css',
    ],

    // https://github.com/quasarframework/quasar/tree/dev/extras
    extras: [
      // 'ionicons-v4',
      // 'mdi-v4',
      // 'fontawesome-v5',
      // 'eva-icons',
      // 'themify',
      // 'line-awesome',
      // 'roboto-font-latin-ext', // this or either 'roboto-font', NEVER both!
      'roboto-font', // optional, you are not bound to it
      'material-icons', // optional, you are not bound to it
      'line-awesome', // optional, you are not bound to it
    ],

    // https://quasar.dev/quasar-cli/quasar-conf-js#Property%3A-framework
    framework: {
      iconSet: 'material-icons', // Quasar icon set
      lang: 'en-US', // Quasar language pack

      // Possible values for "all":
      // * 'auto' - Auto-import needed Quasar components & directives
      //            (slightly higher compile time; next to minimum bundle size; most convenient)
      // * false  - Manually specify what to import
      //            (fastest compile time; minimum bundle size; most tedious)
      // * true   - Import everything from Quasar
      //            (not treeshaking Quasar; biggest bundle size; convenient)
      all: 'auto',

      components: [],
      directives: [],

      // Quasar plugins
      plugins: [
        // 'Cookies',
      ],
    },

    // https://quasar.dev/quasar-cli/cli-documentation/supporting-ie
    supportIE: false,

    // Full list of options: https://quasar.dev/quasar-cli/quasar-conf-js#Property%3A-build
    build: {
      env: ctx.dev
        ? { // so on dev we'll have
          API: JSON.stringify('https://quizzes.getsandbox.com:443'),
          // API: JSON.stringify('http://localhost:5000/api') 
          // dont use localhost on emulator or phone when testing, use a specific ip and port
          // API: JSON.stringify('http://10.0.2.2:5000/api')
        }
        : { // and on build (production):
          API: JSON.stringify('https://quizzes.getsandbox.com:443'),
        },
      scopeHoisting: true,
      devtool: 'source-map',
      // history does not get you the # in your url but you have to do some extra configuration
      // if you want users to be able to access all sub-urls directly
      // therefore you have to make the catch-all fallback to index.html

      // the configuration is in apache / nginx or node.js
      vueRouterMode: 'hash', // available values: 'hash', 'history'
      showProgress: true,
      gzip: false,
      analyze: false,
      // Options below are automatically set depending on the env, set them if you want to override
      // preloadChunks: false,
      // extractCSS: false,

      // https://quasar.dev/quasar-cli/cli-documentation/handling-webpack
      // extendWebpack(cfg) {
      //   cfg.module.rules.push({
      //     enforce: 'pre',
      //     test: /\.(js|vue)$/,
      //     loader: 'eslint-loader',
      //     exclude: /node_modules/,
      //     options: {
      //       formatter: require('eslint').CLIEngine.getFormatter('stylish'),
      //     },
      //   });
      // },
      // https://github.com/neutrinojs/webpack-chain/tree/v4#getting-started
    },

    // Full list of options: https://quasar.dev/quasar-cli/quasar-conf-js#Property%3A-devServer
    devServer: {
      https: false,
      port: 8080,
      open: true, // opens browser window automatically
      vueDevtools: true,
    },

    // animations: 'all', // --- includes all animations
    // https://quasar.dev/options/animations
    animations: 'all',

    // https://quasar.dev/quasar-cli/developing-ssr/configuring-ssr
    ssr: {
      pwa: false,
    },

    // https://quasar.dev/quasar-cli/developing-pwa/configuring-pwa
    pwa: {
      workboxPluginMode: 'GenerateSW', // 'GenerateSW' or 'InjectManifest'
      workboxOptions: {}, // only for GenerateSW
      manifest: {
        name: 'Lion quiz',
        short_name: 'Lion quiz',
        description: 'A Quasar Framework app',
        display: 'standalone',
        orientation: 'portrait',
        background_color: '#ffffff',
        theme_color: '#027be3',
        icons: [
          {
            src: 'statics/icons/icon-128x128.png',
            sizes: '128x128',
            type: 'image/png',
          },
          {
            src: 'statics/icons/icon-192x192.png',
            sizes: '192x192',
            type: 'image/png',
          },
          {
            src: 'statics/icons/icon-256x256.png',
            sizes: '256x256',
            type: 'image/png',
          },
          {
            src: 'statics/icons/icon-384x384.png',
            sizes: '384x384',
            type: 'image/png',
          },
          {
            src: 'statics/icons/icon-512x512.png',
            sizes: '512x512',
            type: 'image/png',
          },
        ],
      },
    },

    // Full list of options: https://quasar.dev/quasar-cli/developing-cordova-apps/configuring-cordova
    cordova: {
      // noIosLegacyBuildFlag: true, // uncomment only if you know what you are doing
      id: 'cloud.casino.cards.quiz',
      name: 'Lion Quiz',
      version: '1.1.0',
      androidVersionCode: '000010002',
      description: 'Make your own quiz',
    },

    // Full list of options: https://quasar.dev/quasar-cli/developing-capacitor-apps/configuring-capacitor
    capacitor: {
      hideSplashscreen: true,
    },

    // Full list of options: https://quasar.dev/quasar-cli/developing-electron-apps/configuring-electron
    // electron: {
    //   bundler: 'packager', // 'packager' or 'builder'

    //   packager: {
    //     // https://github.com/electron-userland/electron-packager/blob/master/docs/api.md#options

    //     // OS X / Mac App Store
    //     // appBundleId: '',
    //     // appCategoryType: '',
    //     // osxSign: '',
    //     // protocol: 'myapp://path',

    //     // Windows only
    //     // win32metadata: { ... }
    //   },

    //   builder: {
    //     // https://www.electron.build/configuration/configuration

    //     appId: 'Lion Quiz',
    //   },

    //   // More info: https://quasar.dev/quasar-cli/developing-electron-apps/node-integration
    //   nodeIntegration: true,

    //   extendWebpack(cfg) {
    //     // do something with Electron main process Webpack cfg
    //     // chainWebpack also available besides this extendWebpack
    //   },
    // },
  };
};
