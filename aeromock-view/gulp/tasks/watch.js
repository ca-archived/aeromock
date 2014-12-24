var gulp = require('gulp');
var config = require('../config');

gulp.task('watch', function () {
    // js
    gulp.watch(config.js.src, ['webpack']);
    // stylus
    gulp.watch(config.stylus.src, ['stylus']);
    // www
    gulp.watch(config.copy.src, ['copy']);
});