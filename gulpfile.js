'use strict';

var gulp = require('gulp');

// load plugins
var $ = require('gulp-load-plugins')();

/**
 * Some constants to save some typing later
 */
var resourcesPath = 'src/main/frontend/',
    generatedPath = 'target/generated-resources/frontend/';

gulp.task('styles', function () {
    return gulp.src(resourcesPath + '/**/*.css')
        .pipe($.sourcemaps.init({loadMaps: true}))
        .pipe($.cleanCss())
        .pipe($.sourcemaps.write('./'))
        .pipe(gulp.dest(generatedPath));
});

gulp.task('images', function () {
    return gulp.src(resourcesPath + '/**/*.png')
        .pipe(gulp.dest(generatedPath));
});

gulp.task('scripts', function () {
    return gulp.src(resourcesPath + '/**/*.js')
        .pipe($.sourcemaps.init({loadMaps: true}))
        .pipe($.uglify())
        .pipe($.sourcemaps.write('./'))
        .pipe(gulp.dest(generatedPath));
});

gulp.task('clean', function () {
    //return gulp.src(
    //    [
    //        webappPath + '**/*.css'
    //    ],
    //    { read: false }
    //).pipe($.rimraf());
});

/**
 * The build task just executes 'styles' and 'scripts' tasks
 */
gulp.task('build', ['styles', 'scripts', 'images']);

/**
 * Executing just 'gulp' will execute 'clean' and start 'build' tasks
 */
gulp.task('default', ['clean'], function () {
    gulp.start('build');
});

/**
 * Watch for modifications in Less and JavaScript resources and
 * recompile/jshint them
 * Usage: $ gulp watch
 */
gulp.task('watch', function () {
    gulp.watch(resourcesPath + '**/*.css', ['styles']);
    gulp.watch(resourcesPath + '**/*.js', ['scripts']);
});