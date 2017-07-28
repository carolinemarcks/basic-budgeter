module.exports = {
  entry: [
    './src/main/webapp/index.js'
  ],
  output: {
    path: __dirname + "/target/scala-2.11/classes/WEB-INF/app",
    publicPath: '/', //TODO do I need this line?
    filename: 'bundle.js'
  },
  module: {
    loaders: [{
      exclude: /node_modules/,
      loader: 'babel',
      query: {
        presets: ['react', 'es2015', 'stage-1']
      }
    }]
  },
  resolve: {
    extensions: ['', '.js', '.jsx']
  }
};
