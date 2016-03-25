'use strict';

const gulp = require('gulp');

// load plugins
const $ = require('gulp-load-plugins')();

// pngquant is irrelevant and does not contain prefix "gulp-"
const pngquant = require('imagemin-pngquant');

/**
 * Some constants to save some typing later
 */
var resourcesPath = 'src/main/frontend/',
    generatedPath = 'target/generated-resources/frontend/';

gulp.task('styles', function () {
    return gulp.src(resourcesPath + '/**/*.css')
        .pipe($.changed(generatedPath))
        .pipe(gulp.dest(generatedPath + 'src/'))
        .pipe($.sourcemaps.init({loadMaps: true}))
        .pipe($.cleanCss())
        .pipe($.sourcemaps.write('./', {includeContent: false, sourceRoot: '/src'}))
        .pipe(gulp.dest(generatedPath));
});

gulp.task('images', function () {
    return gulp.src(resourcesPath + '/**/*.png')
        .pipe($.changed(generatedPath))
        .pipe($.imagemin({
                    progressive: true,
                    svgoPlugins: [
                        {removeViewBox: false},
                        {cleanupIDs: false}
                    ],
                    use: [pngquant()]
                }))
        .pipe(gulp.dest(generatedPath));
});

gulp.task('scripts', function () {
    return gulp.src(resourcesPath + '/**/*.js')
        .pipe($.changed(generatedPath))
        .pipe(gulp.dest(generatedPath + 'src/'))
        .pipe($.sourcemaps.init({loadMaps: true}))
        .pipe($.uglify())
        .pipe($.sourcemaps.write('./', {includeContent: false, sourceRoot: '/src'}))
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
    $.livereload.listen();
    gulp.watch(resourcesPath + '**/*.css', function (event) {
        gulp.start('styles').on('task_stop', function () {
            $.livereload.changed(event.path)
        })
    });
    gulp.watch(resourcesPath + '**/*.js', function (event) {
        gulp.start('scripts').on('task_stop', function () {
            $.livereload.changed(event.path)
        })
    });
    gulp.watch(resourcesPath + '**/*.png', function (event) {
        gulp.start('images').on('task_stop', function () {
            $.livereload.changed(event.path)
        })
    });
});