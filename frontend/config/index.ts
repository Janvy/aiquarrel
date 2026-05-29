import { defineConfig, type UserConfigExport } from '@tarojs/cli';
import path from 'path';

const API_ENV = process.env.TARO_APP_API_ENV || 'prod';
const isDev = API_ENV === 'dev';

export default defineConfig<'webpack5'>(async (merge) => {
  const baseConfig: UserConfigExport<'webpack5'> = {
    projectName: 'ai-quarrel',
    date: '2026-5-28',
    designWidth: 750,
    deviceRatio: {
      640: 2.34 / 2,
      750: 1,
      375: 2,
      828: 1.81 / 2,
    },
    sourceRoot: 'src',
    outputRoot: 'dist',
    plugins: [
      '@tarojs/plugin-platform-weapp',
      '@tarojs/plugin-framework-react',
    ],
    defineConstants: {
      __API_BASE_URL__: JSON.stringify(
        isDev
          ? 'http://localhost:9080/api/v1'
          : 'https://api.aiquarrel.com/api/v1'
      ),
    },
    copy: {
      patterns: [],
      options: {},
    },
    framework: 'react',
    compiler: 'webpack5',
    cache: {
      enable: false,
    },
    mini: {
      webpackChain(chain) {
        chain.resolve.alias.set('@', path.resolve(__dirname, '..', 'src'));
      },
      postcss: {
        pxtransform: {
          enable: true,
          config: {},
        },
        cssModules: {
          enable: false,
          config: {
            namingPattern: 'module',
            generateScopedName: '[name]__[local]___[hash:base64:5]',
          },
        },
      },
    },
    h5: {
      publicPath: '/',
      staticDirectory: 'static',
      output: {
        filename: 'js/[name].[hash:8].js',
        chunkFilename: 'js/[name].[chunkhash:8].js',
      },
      miniCssExtractPluginOption: {
        ignoreOrder: true,
        filename: 'css/[name].[hash].css',
        chunkFilename: 'css/[name].[chunkhash].css',
      },
      postcss: {
        autoprefixer: {
          enable: true,
          config: {},
        },
        cssModules: {
          enable: false,
          config: {
            namingPattern: 'module',
            generateScopedName: '[name]__[local]___[hash:base64:5]',
          },
        },
      },
      webpackChain(chain) {
        chain.resolve.alias.set('@', path.resolve(__dirname, '..', 'src'));
      },
    },
    rn: {
      appName: 'taroDemo',
      postcss: {
        cssModules: {
          enable: false,
        },
      },
    },
  };

  return baseConfig;
});
