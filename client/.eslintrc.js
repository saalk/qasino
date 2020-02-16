module.exports = {
  root: true,

  parserOptions: {
    parser: 'babel-eslint',
    sourceType: 'module'
  },

  env: {
    browser: true
  },

  extends: [
    'prettier',
    // https://eslint.vuejs.org/rules/#priority-a-essential-error-prevention
    // consider switching to `plugin:vue/strongly-recommended` or `plugin:vue/recommended` for stricter rules.
    'plugin:vue/essential',
    // https://github.com/quasarframework/eslint-plugin-quasar
    'plugin:quasar/standard'
  ],

  // required to lint *.vue files
  plugins: [
    'quasar'
  ],

  globals: {
    'ga': true, // Google Analytics
    'cordova': true,
    '__statics': true,
    'process': true,
    'Capacitor': true,
    'chrome': true
  },

  // add your custom rules here
  rules: {
    'prefer-promise-reject-errors': 'off',

    // allow debugger during development only
    'no-debugger': process.env.NODE_ENV === 'production' ? 'error' : 'off',
    // The 0'off', 1'warn' or 2'error' tells eslint to turn off the rule
    'quasar/check-valid-props': 1,
    'quasar/no-invalid-qfield-usage': 1
  }
}
