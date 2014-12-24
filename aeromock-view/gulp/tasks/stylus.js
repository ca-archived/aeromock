var gulp = require('gulp');
var gulpif = require('gulp-if');
var plumber = require('gulp-plumber');
var stylus = require('gulp-stylus');
var concat = require('gulp-concat');
var autoprefixer = require('gulp-autoprefixer');
var minify = require('gulp-minify-css');
var config = require('../config').stylus;

gulp.task('stylus', function () {
    gulp.src(config.src)
        .pipe(plumber())
        .pipe(stylus())
        .pipe(concat(config.output))
        .pipe(autoprefixer(config.autoprefixer))
        .pipe(gulpif(config.minify, minify()))
        .pipe(gulp.dest(config.dest));
});