'use strict';

const gulp = require('gulp');

// load plugins
const $ = require('gulp-load-plugins')();

// pngquant is irrelevant and does not contain prefix "gulp-"
const pngquant = require('imagemin-pngquant');

const runSequence = require('run-sequence');

/**
 * Some constants to save some typing later
 */
var resourcesPath = 'src/main/frontend/',
    generatedPath = 'target/generated-resources/frontend/';

var htmlPath = 'src/main/java',
    generatedHtmlPath = 'target/generated-resources/html/';

gulp.task('rev-assets', function () {
    return gulp.src(resourcesPath + '/**/*.{css,js,png,gif,jpg}')
            .pipe($.changed(generatedPath))
            .pipe($.rev())
            .pipe(gulp.dest(generatedPath + 'src/'))
            .pipe($.rev.manifest({merge: true}))
            .pipe(gulp.dest(generatedPath));
});

gulp.task('styles', function () {
    return gulp.src(generatedPath + 'src/**/*.css')
        .pipe($.changed(generatedPath))
        .pipe($.sourcemaps.init({loadMaps: true}))
        .pipe($.revReplace({manifest: gulp.src(generatedPath + "/rev-manifest.json")}))
        .pipe($.autoprefixer({
                browsers: ['> 5% in RU'],
                cascade: false
            }))
        .pipe($.cleanCss())
        .pipe($.sourcemaps.write('./', {includeContent: false, sourceRoot: '/src'}))
        .pipe(gulp.dest(generatedPath));
});

gulp.task('images', function () {
    return gulp.src(generatedPath + 'src/**/*.{png,gif,jpg}')
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
    return gulp.src(generatedPath + 'src/**/*.js')
        .pipe($.changed(generatedPath))
        .pipe($.sourcemaps.init({loadMaps: true}))
        .pipe($.revReplace({manifest: gulp.src(generatedPath + "/rev-manifest.json")}))
        .pipe($.uglify())
        .pipe($.sourcemaps.write('./', {includeContent: false, sourceRoot: '/src'}))
        .pipe(gulp.dest(generatedPath));
});

gulp.task('html', function () {
    return gulp.src(htmlPath + '/**/*.html')
        .pipe($.revReplace({
                canonicalUris: true,
                manifest: gulp.src(generatedPath + "/rev-manifest.json")
            }))
        .pipe($.htmlmin({
                removeComments: true,
                collapseWhitespace: true,
                minifyJS: true,
                minifyCSS: true,
                keepClosingSlash: true
            }))
        .pipe(gulp.dest(generatedHtmlPath));
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
gulp.task('build', function () {
    runSequence('rev-assets', ['styles', 'scripts', 'images', 'html']);
});

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