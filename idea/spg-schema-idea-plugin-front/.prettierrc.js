const fabric = require('@umijs/fabric');

/**
 * Prettier 配置
 * https://www.prettier.cn/docs/options.html
 */

module.exports = {
    ...fabric.prettier,
    "printWidth": 160,
    "tabWidth": 4,
    "useTabs": false,
    "semi": false,
    "singleQuote": false,
    "quoteProps": "as-needed",
    "jsxSingleQuote": false,
    "trailingComma": "none",
    "bracketSpacing": false,
    "bracketSameLine": false,
    "arrowParens": "always",
    "requirePragma": false,
    "singleAttributePerLine": false,

};
