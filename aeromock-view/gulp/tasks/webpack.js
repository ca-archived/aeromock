var gulp = require('gulp');
var gulpif = require('gulp-if');
var plumber = require('gulp-plumber');
var uglify = require('gulp-uglify');
var webpack = require('gulp-webpack');
var config = require('../config');

gulp.task('webpack', function () {
    gulp.src(config.webpack.entry)
        .pipe(plumber())
        .pipe(webpack(config.webpack))
        .pipe(gulpif(config.js.uglify, uglify()))
        .pipe(gulp.dest(config.js.dest));
});