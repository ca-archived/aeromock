'use strict';

var glob            = require('glob');
var path            = require('path');

var gulp            = require('gulp');
var $               = require('gulp-load-plugins')();
var runSequence     = require('run-sequence');

var rimraf          = require('rimraf');
var browserSync     = require('browser-sync');
var reload          = browserSync.reload;
var pagespeed       = require('psi');
var stylishReporter = require('jshint-stylish');

var tmpDir = '_tmp';
var distDir = '_dist';
var vulcanizedDir = '_vulcanized';
var vulcanizedDirBase = 'raw';
var vulcanizedDirInline = 'inline';
var vulcanizedDirCsp = 'csp';

var sizeOf = function(stream, title){
  return stream
    .pipe($.size({title: title}));
};

// --------------------------------------------------------
// Lint JavaScript
// --------------------------------------------------------

(function(scope){

  gulp.task('jshint', function () {
    return jshint();
  });

  gulp.task('jshint:breaking', function () {
    return jshint()
      .pipe($.jshint.reporter('fail'));
  });

  var jshint = function(){
    return gulp.src('app/scripts/**/*.js')
      .pipe($.jshint())
      .pipe($.jshint.reporter(stylishReporter))
  }.bind(scope);

})(this);

// --------------------------------------------------------
// Build JavaScript
// --------------------------------------------------------

(function(scope){

  gulp.task('scripts:dev', ['jshint:breaking'], function() {
    return sizeOf(scriptsBrowserify('dev'), 'scripts:dev');
  });

  gulp.task('p-scripts:dev:reload', ['jshint'], function() {
    return sizeOf(
      scriptsBrowserify('dev')
        .pipe(reload({stream: true, once: true}))
      , 'scripts:dev');
  });

  gulp.task('p-scripts:pre:package', ['jshint:breaking'], function() {
    return sizeOf(scriptsBrowserify('prod'), 'scripts:pre:package');
  });

  gulp.task('scripts:package', ['jshint:breaking'], function() {
    return scope.htmlPackage(['p-scripts:package:now']);
  })

  gulp.task('p-scripts:package:now', function(){
    var jsBundles = [distDir + '/scripts/aeromock-report.min.js'];
    glob(distDir + '/scripts/**/*-b.js', function(er, filesArray){
      for(var i=0;i<filesArray.length;i++){
        jsBundles.push(filesArray[i]);
      }
    })

    for(var i=0;i<jsBundles.length;i++){
      // FIXME
      // inelegant way of returning something
      // for run-sequence - we return the last one
      // as we want to iterate over each found path
      // to retrieve filename and pass it to closure compiler
      if(i<jsBundles-1){
        compileJS(jsBundles[i]);
      }else{
        return compileJS(jsBundles[i]);
      }
    }
  });

  var scriptsBrowserify = function(env){
    var jsBundles = ['app/scripts/aeromock-report.js'];
    glob('app/scripts/**/*-b.js', function(er, filesArray){
      for(var i=0;i<filesArray.length;i++){
        jsBundles.push(filesArray[i]);
      }
    })
    // Single point of entry (make sure not to src ALL your files, browserify will figure it out)
    return gulp.src(jsBundles)
      .pipe($.browserify({
        insertGlobals: env === 'prod' ? false : true,
        debug: false
      }))

      // Preprocess
      .pipe($.preprocess({context: {ENV: env}}))

      // Output it to tmp folder
      .pipe(gulp.dest(tmpDir + '/scripts'))
  }.bind(scope);

  var compileJS = function(filepath){
    var currentFilename = path.basename(filepath, path.extname(filepath));
    return gulp.src(filepath)
      .pipe($.stripDebug())
      .pipe($.closureCompiler({
        compilerPath    : 'lib/.bower_components/closure-compiler/compiler.jar',
        fileName        : currentFilename + '.js',
        compilerFlags   : {
          warning_level : 'QUIET'
          //, language_in      : 'ECMASCRIPT5_STRICT'
        }
      }))
      .pipe(gulp.dest(distDir + '/scripts'))
      .pipe($.zopfli())
      .pipe(gulp.dest(distDir + '/scripts'))
  }.bind(scope);

})(this);

// --------------------------------------------------------
// Build Images
// --------------------------------------------------------

(function(scope){

  gulp.task('images:dev', function () {
    return sizeOf(imagesSymlink()
      , 'images:dev');
  });

  gulp.task('p-images:dev:reload', function () {
    return sizeOf(imagesSymlink()
      .pipe(reload({stream: true, once: true}))
      , 'images:dev');
  });

  gulp.task('images:package', function () {
    return sizeOf(imagesPackage()
      , 'images:package');
  });

  var imagesSymlink = function () {
    return gulp.src('app/images/**/*')
        .pipe($.symlink(tmpDir + '/images'));
  }.bind(scope);;

  var imagesPackage = function () {
    return gulp.src('app/images/**/*')
        .pipe($.cache($.imagemin({
            progressive: true,
            interlaced: true
        })))
        .pipe(gulp.dest(distDir + '/images'));
  }.bind(scope);

})(this);

// --------------------------------------------------------
// Build Public folder
// --------------------------------------------------------

(function(scope){

  gulp.task('public:dev', function () {
    return sizeOf(publicSymlink()
      , 'public:dev');
  });

  gulp.task('p-public:dev:reload', function () {
    return sizeOf(publicSymlink()
      .pipe(reload({stream: true, once: true}))
      , 'public:dev');
  });

  gulp.task('public:package', function () {
    return sizeOf(publicPackage()
      , 'public:package');
  });

  var publicSymlink = function () {
    return gulp.src('app/public/**/*')
        .pipe($.symlink(tmpDir + '/public'));
  }.bind(scope);;

  var publicPackage = function () {
    return gulp.src('app/public/**/*')
        .pipe(gulp.dest(distDir + '/public'));
  }.bind(scope);

})(this);

// --------------------------------------------------------
// Build Css
// --------------------------------------------------------

(function(scope){

  gulp.task('styles:dev', function () {
    return sizeOf(stylesBuild()
      , 'styles:dev');
  });

  gulp.task('p-styles:dev:reload', function () {
    return sizeOf(stylesBuild()
      .pipe($.filter('**/*.css')) // Filtering stream to only css files
      .pipe(reload({stream: true, once: true}))
      , 'styles:dev');
  });

  gulp.task('styles:package', function(){
    return scope.htmlPackage(['p-styles:package:now']);
  })

  gulp.task('p-styles:package:now', function(){
    var htmlFiles = [];
    glob(tmpDir + '/**/*.html', function(er, filesArray){
      var file = "";
      for(var i=0;i<filesArray.length;i++){
        file = filesArray[i];
        if(!file.match(new RegExp('^' + distDir + '/lib')))
          htmlFiles.push(filesArray[i]);
      }
    })
    // uncss and minify used css in distDir
    return gulp.src(distDir + '/styles/**/*.css', {base: distDir + '/'})
      .pipe($.uncss({ html: htmlFiles }))
      .pipe($.csso())
      .pipe(gulp.dest(distDir))
  });

  // Compile all other css && sass Files
  var stylesBuild = function(){
    return gulp.src(['app/styles/**/*.{css,scss,sass}'])
        .pipe($.if(function(file){
            var ext = path.extname(file.path);
            return ext !== '.css';
          }, $.rubySass({
              style: 'expanded',
              precision: 10,
              loadPath: ['app/styles']
        })))
        .pipe($.autoprefixer('last 1 version'))
        .pipe(gulp.dest(tmpDir + '/styles'))
  }.bind(scope);

})(this);

// --------------------------------------------------------
// Send external libs to lib
// --------------------------------------------------------

(function(scope){

  gulp.task('lib:dev', function () {
    return copyBowerComponentsToLib('dev')
  });

  gulp.task('p-lib:dev:reload', function () {
    return copyBowerComponentsToLib('dev')
      .pipe(reload({stream: true, once: true}));
  });

  gulp.task('lib:package', function () {
    return copyBowerComponentsToLib('prod')
  });

  var copyBowerComponentsToLib = function(env){
    var src = ['lib/.bower_components/**/*.{css,js,html,swf}*'];
    var dest = env === 'dev' ? tmpDir : distDir;
    return gulp.src(src)
      .pipe(gulp.dest(dest + '/lib'));
  }.bind(scope);

})(this);

// --------------------------------------------------------
// Send html to tmpDir
// --------------------------------------------------------

(function(scope){

  gulp.task('html:dev', function(){
    return htmlDev();
  });

  gulp.task('p-html:dev:reload', ['p-images:dev:reload', 'p-public:dev:reload'], function(){
    return htmlDev()
      .pipe(reload({stream: true, once: true}));
  });

  var htmlDev = function(){
    return gulp.src('app/**/*.html')
      .pipe(gulp.dest(tmpDir));
  };

})(this);

// --------------------------------------------------------
// Package html and used css / js
// --------------------------------------------------------

(function(scope){

  scope.htmlPackage = function(){

    // *inelegant*
    // We want to use useref parse html and
    // build only assets (css, js) needed in prod
    // into distDir
    // Useref will basically build/copy assets based
    // on prebuilt assets in tmpDir
    // What if we need to prebuild assets differently
    // for prod and dev (ex with browserify being passed
    // different params in dev and prod).
    // What we do here is prebuilding assets with a prod
    // flag instead of dev
    // Then we execute useref
    // And when we are done we reprebuild everything
    // for dev so that there is no surprise to find prod
    // assets in tmpDir when in development
    // NB: Once we have executed useref, we have the correct
    // in distDir and we are not dependent of files in tmpDir for
    // further build steps
    runSequence(
      'p-assets:prepare:package',
      ['p-html:package:now','lib:package'],
      arguments[0] || 'p-dummy-do:nothing',
      arguments[1] || 'p-dummy-do:nothing',
      arguments[2] || 'p-dummy-do:nothing',
      arguments[3] || 'p-dummy-do:nothing',
      function(){
        return gulp.start('p-assets:prepare:dev');
      }
    );
  };

  gulp.task('p-html:package:now', function(){
    return gulp.src(tmpDir + '/**/*.html')
      .pipe($.useref.assets({searchPath: tmpDir}))
      .pipe($.useref.restore())
      .pipe($.useref())
      // Minify HTML
      .pipe($.if(function(file){
        var ext = path.extname(file.path);
        return ext !== '.js' && ext !== '.css';
      }, $.minifyHtml()))
      // output
      .pipe(gulp.dest(distDir))
      .pipe($.size({title: 'html'}));
  });

  gulp.task('p-assets:prepare:dev', [ 'html:dev', 'styles:dev', 'images:dev', 'public:dev', 'lib:dev',
    'scripts:dev' ],
    function(){ return true; }
  );

  gulp.task('p-assets:prepare:package', [ 'html:dev', 'styles:dev', 'images:dev', 'public:dev', 'lib:dev',
    'p-scripts:pre:package' ], function(){
    return gulp.start('p-scripts:pre:package');
  });

  gulp.task('p-dummy-do:nothing');

})(this);

// --------------------------------------------------------
// Clean Output Directory
// --------------------------------------------------------

(function(scope){

  gulp.task('clean:vulcanizedDir', function (cb) {
      rimraf(vulcanizedDir, cb);
  });
  gulp.task('clean:distDir', function (cb) {
      rimraf(distDir, cb);
  });
  gulp.task('clean:tmpDir', function (cb) {
      rimraf(tmpDir, cb);
  });
  gulp.task('clean', ['clean:tmpDir', 'clean:distDir','clean:vulcanizedDir']);

})(this);

// --------------------------------------------------------
// Create server, Watch Files For Changes & Reload
// --------------------------------------------------------

(function(scope){

  gulp.task('serve', ['p-assets:prepare:dev'], function () {
      browserSync.init(null, {
          server: {
              baseDir: [tmpDir]
          },
          notify: false
      });

      gulp.watch(['app/**/*.html']                            , ['p-html:dev:reload']);
      gulp.watch(['app/styles/**/*.{css,scss,sass}']          , ['p-styles:dev:reload']);
      gulp.watch(['app/scripts/**/*.js']                      , ['p-scripts:dev:reload']);
      gulp.watch(['app/images/**/*.*']                        , ['p-images:dev:reload']);
      gulp.watch(['app/public/**/*.*']                        , ['p-public:dev:reload']);
      gulp.watch(['lib/.bower_components/**/*.{css,js,html}'] , ['p-lib:dev:reload']);
  });

})(this);

// --------------------------------------------------------
// Build Prod files & Vulcanized Polymer
// --------------------------------------------------------

(function(scope){

  gulp.task('default', ['clean'], function () {
    runSequence(
      ['vulcanize','p-copy-bower-to-dist']
    );
  });

  gulp.task('p-copy-bower-to-dist', function () {
    return gulp.src('bower.json')
      .pipe(gulp.dest(distDir + '/'));
  });

  var assetsPackageTasks = [
    'p-styles:package:now',
    'p-scripts:package:now',
    'images:package',
    'public:package',
    'lib:package'
  ];

  var destDir = distDir + '/' + vulcanizedDir + '/';

  gulp.task('vulcanize', ['clean:vulcanizedDir'], function () {
    return scope.htmlPackage(
      assetsPackageTasks,
      'vulcanize:inline:now',
      'vulcanize:base:now',
      'vulcanize:csp:now'
    );
  });

  // vulcanize in inline mode
  gulp.task('vulcanize:inline',function(){
    return scope.htmlPackage(
      assetsPackageTasks,
      'vulcanize:inline:now'
    );
  });

  gulp.task('vulcanize:inline:now',function(){
    return gulp.src(distDir + '/aeromock-report.html', {base: distDir + '/'})
      .pipe($.vulcanize({
        dest: destDir + vulcanizedDirInline + '/',
        inline: true
      }));
  });

  // vulcanize in csp mode
  gulp.task('vulcanize:csp',function(){
    return scope.htmlPackage(
      assetsPackageTasks,
      'vulcanize:csp:now'
    );
  });

  gulp.task('vulcanize:csp:now', function(){
    return vulcanizeWithExternalScriptsAndStyles({
      dest: destDir + vulcanizedDirCsp + '/',
      csp: true
    });
  });

  // vulcanize in standard mode
  gulp.task('vulcanize:base',function(){
    return scope.htmlPackage(
      assetsPackageTasks,
      'vulcanize:base:now'
    );
  });

  gulp.task('vulcanize:base:now', function(){
    return vulcanizeWithExternalScriptsAndStyles({
      dest: destDir + vulcanizedDirBase + '/'
    });
  });

  var vulcanizeWithExternalScriptsAndStyles = function(vulcanizeOptions){
    return gulp.src(tmpDir + '/aeromock-report.html')
      // add needed css and js
      .pipe($.useref.assets({searchPath: tmpDir}))
      .pipe($.tap(function(file,t){
        var pathS = file.path.split(new RegExp('/' + tmpDir + '/'));
        var base = pathS[0] + '/' + distDir + '/';
        var path = base + pathS[1];
        return gulp.src(path, {base: base})
          .pipe(gulp.dest(vulcanizeOptions.dest));
      }))
      .pipe($.useref.restore())
      .pipe($.tap(function(file,t){
        return gulp.src(distDir + '/aeromock-report.html', {base: distDir + '/'})
          .pipe(gulp.dest(vulcanizeOptions.dest))
          .pipe($.vulcanize(vulcanizeOptions));
      }));
  };

})(this);

// --------------------------------------------------------
// PageSpeed
// --------------------------------------------------------

(function(scope){

  gulp.task('pagespeed', pagespeed.bind(null, {
      // key: 'YOUR_API_KEY' // http://goo.gl/RkN0vE
      url: 'https://please.update.me',
      strategy: 'mobile'
  }));

})(this);
