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
    'airbnb-base',
    //'prettier',
    // https://eslint.vuejs.org/rules/#priority-a-essential-error-prevention
    // consider switching to `plugin:vue/strongly-recommended` or `plugin:vue/recommended` for stricter rules.
    'plugin:vue/recommended',
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

  // https://quasar.dev/quasar-cli/cli-documentation/linter
  'rules': {
    "linebreak-style": 0,
    'brace-style': [2, 'stroustrup', { 'allowSingleLine': true }],

    'vue/max-attributes-per-line': 0,
    'vue/valid-v-for': 0,

    // allow async-await
    'generator-star-spacing': 'off',

    // allow paren-less arrow functions
    'arrow-parens': 0,
    'one-var': 0,

    'import/first': 0,
    // 'import/named': 2,
    // 'import/namespace': 2,
    // 'import/default': 2,
    // 'import/export': 2,
    'import/extensions': 0,
    'import/no-unresolved': 0,
    'import/no-extraneous-dependencies': 0,

    // The 0'off', 1'warn' or 2'error' tells eslint to turn off the rule
    'no-debugger': process.env.NODE_ENV === 'production' ? 2 : 0,

    // https://github.com/quasarframework/eslint-plugin-quasar
    'quasar/check-valid-props': 1,
    'quasar/no-invalid-qfield-usage': 0,

    "no-param-reassign": 0,
    "trailing-comma": 0
  }
}
