// Configuration for your app
// https://quasar.dev/quasar-cli/quasar-conf-js
var path = require('path');

module.exports = function (ctx) {

  return {

    // Webpack aliases
    aliases: {
      quasar: path.resolve(__dirname, '../node_modules/quasar-framework/'),
      src: path.resolve(__dirname, '../src'),
      assets: path.resolve(__dirname, '../src/assets'),
      components: path.resolve(__dirname, '../src/components')
    },

    // Progress Bar Webpack plugin format
    // https://github.com/clessg/progress-bar-webpack-plugin#options
    progressFormat: ' [:bar] ' + ':percent'.bold + ' (:msg)',

    // Default theme to build with ('ios' or 'mat')
    defaultTheme: 'mat',


    // https://quasar.dev/quasar-cli/quasar-conf-js#Property%3A-htmlVariables
    htmlVariables: {
      title: 'Qasino Card games',
      description: 'Card games app'
    },

    // app boot file (/src/boot)
    // - all boot files together are the "main.js"
    // - needed to run code before the App’s Vue root component is instantiated
    // https://quasar.dev/quasar-cli/cli-documentation/boot-files
    boot: [
      'firebase',
      'axios'
      // 'i18n',
    ],

    // https://quasar.dev/quasar-cli/quasar-conf-js#Property%3A-css
    css: [
      'app.sass'
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
      'material-icons' // optional, you are not bound to it
    ],

    // https://quasar.dev/quasar-cli/quasar-conf-js#Property%3A-framework
    framework: {
      iconSet: 'material-icons', // Quasar icon set
      lang: 'en-us', // Quasar language pack

      // Possible values for "all":
      // * 'auto' - Auto-import needed Quasar components & directives
      //            (slightly higher compile time; next to minimum bundle size; most convenient)
      // * false  - Manually specify what to import
      //            (fastest compile time; minimum bundle size; most tedious)
      // * true   - Import everything from Quasar
      //            (not treeshaking Quasar; biggest bundle size; convenient)
      all: true,

      components: ['QRange','QKnob','QFab','QMenu','QItem','QDrawer','QToggle',
      'QTable','QPagination','QTooltip','QField'],
      directives: ['Ripple'],
      // Quasar plugins
      plugins: ['Notify'],
      config: {
        notify: { /* Notify defaults */ }
      }
    },

    // https://quasar.dev/quasar-cli/cli-documentation/supporting-ie
    supportIE: true,

    // Full list of options: https://quasar.dev/quasar-cli/quasar-conf-js#Property%3A-build
    build: {
      scopeHoisting: true,
      vueRouterMode: 'hash', // available values: 'hash', 'history'
      index: path.resolve(__dirname, '../dist/index.html'),
      publicPath: '',
      productionSourceMap: false,
      showProgress: true,
      gzip: false,
      analyze: false,
      // Options below are automatically set depending on the env, set them if you want to override
      // preloadChunks: false,
      // extractCSS: false,

      // https://quasar.dev/quasar-cli/cli-documentation/handling-webpack
      extendWebpack(cfg) {
        cfg.module.rules.push({
          enforce: 'pre',
          test: /\.(js|vue)$/,
          loader: 'eslint-loader',
          exclude: /(node_modules|quasar)/,
          options: {
            formatter: require('eslint').CLIEngine.getFormatter('stylish')
          }
        })
      }
    },

    // Full list of options: https://quasar.dev/quasar-cli/quasar-conf-js#Property%3A-devServer
    devServer: {
      //vueDevtools: true,
      contentBase: path.join(__dirname, 'dist'),
      compress: true,
      https: false,
      host: '0.0.0.0',
      port: 8080,
      publicPath: '/',
      clientLogLevel: 'info', // silent, trace, debug, info, warn , error
      open: 'chrome' // opens browser window automatically, use 'google-chrome;  on mac
    },

    // animations: 'all', // --- includes all animations
    // https://quasar.dev/options/animations
    animations: ['fadeIn', 'fadeOut'],

    // https://quasar.dev/quasar-cli/developing-ssr/configuring-ssr
    // server-rendering Quasar app not a traditional SPA
    // progressive web app = web app with app-like experience to users
    ssr: {
      pwa: false
    },

    // https://quasar.dev/quasar-cli/developing-pwa/configuring-pwa
    pwa: {
      workboxPluginMode: 'GenerateSW', // 'GenerateSW' or 'InjectManifest'
      workboxOptions: {}, // only for GenerateSW
      manifest: {
        // name: 'Quasar App',
        // short_name: 'Quasar App',
        // description: 'A Quasar Framework app',
        display: 'standalone',
        orientation: 'portrait',
        background_color: '#ffffff',
        theme_color: '#027be3',
        icons: [
          {
            'src': 'statics/icons/app_icons/png/128.png',
            'sizes': '128x128',
            'type': 'image/png'
          },
          {
            'src': 'statics/icons/app_icons/png/192.png',
            'sizes': '192x192',
            'type': 'image/png'
          },
          {
            'src': 'statics/icons/app_icons/png/256.png',
            'sizes': '256x256',
            'type': 'image/png'
          },
          {
            'src': 'statics/icons/app_icons/png/384.png',
            'sizes': '384x384',
            'type': 'image/png'
          },
          {
            'src': 'statics/icons/app_icons/png/512.png',
            'sizes': '512x512',
            'type': 'image/png'
          }
        ]
      }
    },

    // Full list of options: https://quasar.dev/quasar-cli/developing-cordova-apps/configuring-cordova
    cordova: {
      // noIosLegacyBuildFlag: true, // uncomment only if you know what you are doing
      id: 'org.cordova.quasar.app'
    },


    // Full list of options: https://quasar.dev/quasar-cli/developing-capacitor-apps/configuring-capacitor
    capacitor: {
      hideSplashscreen: true
    },

    // Full list of options: https://quasar.dev/quasar-cli/developing-electron-apps/configuring-electron
    electron: {
      bundler: 'packager', // 'packager' or 'builder'

      packager: {
        // https://github.com/electron-userland/electron-packager/blob/master/docs/api.md#options

        // OS X / Mac App Store
        // appBundleId: '',
        // appCategoryType: '',
        // osxSign: '',
        // protocol: 'myapp://path',

        // Windows only
        // win32metadata: { ... }
      },

      builder: {
        // https://www.electron.build/configuration/configuration

        appId: 'qasino-app'
      },

      // More info: https://quasar.dev/quasar-cli/developing-electron-apps/node-integration
      nodeIntegration: true,

      extendWebpack(cfg) {
        // do something with Electron main process Webpack cfg
        // chainWebpack also available besides this extendWebpack
      }
    }
  }
}
