'use strict';

const gulp = require('gulp');

// load plugins
const $ = require('gulp-load-plugins')();

// pngquant is irrelevant and does not contain prefix "gulp-"
const pngquant = require('imagemin-pngquant');
const del = require('del');


/**
 * Some constants to save some typing later
 */
var resourcesPath = 'src/main/frontend/',
    webAppRoot = 'src/main/webapp/',
    generatedPath = 'target/generated-resources/frontend/',
    generatedSrcPath = generatedPath + 'src/';

var htmlPath = 'src/main/java',
    generatedHtmlPath = 'target/generated-resources/html/WEB-INF/classes/';

function processImages() {
    return gulp.src(resourcesPath + '/**/*.{png,gif,jpg}')
        .pipe($.changed(generatedSrcPath, {hasChanged: $.changed.compareSha1Digest}))
        .pipe($.imagemin({
            progressive: true,
            svgoPlugins: [
                {removeViewBox: false},
                {cleanupIDs: false}
            ],
            use: [pngquant()]
        }))
        .pipe(gulp.dest(generatedSrcPath)) //just for changed tracking
        .pipe(gulp.dest(resourcesPath)); //override original files by lossless compressed
}

function processStyles() {

    return gulp.src(resourcesPath + '/**/*.css')
        .pipe($.changed(generatedSrcPath))
        .pipe(gulp.dest(generatedSrcPath))
        .pipe($.sourcemaps.init({loadMaps: true}))
        .pipe($.autoprefixer({
            browsers: ['> 5% in RU'],
            cascade: false
        }))
        .pipe($.cleanCss());
}

function processScripts() {
    return gulp.src(resourcesPath + '/**/*.js')
        .pipe($.changed(generatedSrcPath))
        .pipe(gulp.dest(generatedSrcPath))
        .pipe($.sourcemaps.init({loadMaps: true}))
        .pipe($.uglify());
}

function writeSourcemaps() {
    return $.sourcemaps.write('./', {includeContent: false, sourceRoot: '/src'});
}

gulp.task('images-with-rev', function () {
    return processImages()
        .pipe($.rev())
        .pipe(gulp.dest(generatedPath))
        .pipe($.rev.manifest('rev-manifest-images.json', {merge: true}))
        .pipe(gulp.dest(generatedPath));
});

gulp.task('styles-with-rev', ['images-with-rev'], function () {
    var manifest = gulp.src(generatedPath + "/rev-manifest-images.json");
    return processStyles()
        .pipe($.revReplace({manifest: manifest}))
        .pipe($.rev())
        .pipe(writeSourcemaps())
        .pipe(gulp.dest(generatedPath))
        .pipe($.rev.manifest('rev-manifest-styles.json', {merge: true}))
        .pipe(gulp.dest(generatedPath));
});

gulp.task('scripts-with-rev', /*['images-with-rev'], */function () {
    //var manifest = gulp.src(generatedPath + "/rev-manifest-images.json");
    return processScripts()
    //.pipe($.revReplace({manifest: manifest}))
        .pipe($.rev())
        .pipe(writeSourcemaps())
        .pipe(gulp.dest(generatedPath))
        .pipe($.rev.manifest('rev-manifest-scripts.json', {merge: true}))
        .pipe(gulp.dest(generatedPath));
});

gulp.task('marge-rev-manifest', ['scripts-with-rev', 'styles-with-rev'], function () {
    return gulp.src(generatedPath + "/rev-manifest-*.json")
        .pipe($.mergeJson('rev-manifest.json'))
        .pipe(gulp.dest(generatedPath));
});

gulp.task('html-with-rev', ['marge-rev-manifest'], function () {
    var manifest = gulp.src(generatedPath + "rev-manifest.json");
    return gulp.src(htmlPath + '/**/*.html')
        .pipe($.revReplace({
            canonicalUris: true,
            manifest: manifest
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

gulp.task('html-no-rev', function () {
    return gulp.src(htmlPath + '/**/*.html')
        .pipe($.changed(generatedHtmlPath))
        .pipe(gulp.dest(generatedHtmlPath))
        .pipe($.livereload());
});

gulp.task('images-no-rev', function () {
    return processImages()
        .pipe(gulp.dest(generatedPath))
        .pipe($.livereload());
});

gulp.task('styles-no-rev', function () {
    return processStyles()
        .pipe(writeSourcemaps())
        .pipe(gulp.dest(generatedPath))
        .pipe($.filter(['**/*', '!**/*.map']))
        .pipe($.livereload());
});

gulp.task('scripts-no-rev', function () {
    return processScripts()
        .pipe(writeSourcemaps())
        .pipe(gulp.dest(generatedPath))
        .pipe($.filter(['**/*', '!**/*.map']))
        .pipe($.livereload());
});

gulp.task('clean', function () {
    return del([
        webAppRoot + 'redeploy.touch'
    ]);
});

gulp.task('build-development', ['images-no-rev', 'scripts-no-rev', 'styles-no-rev', 'html-no-rev']);

gulp.task('build-deployment', ['html-with-rev']);

gulp.task('build', ['build-deployment']);

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
    gulp.watch(resourcesPath + '**/*.css', ['styles-no-rev']);
    gulp.watch(resourcesPath + '**/*.js', ['scripts-no-rev']);
    gulp.watch(resourcesPath + '**/*.{png,gif,jpg}', ['images-no-rev']);
    gulp.watch(htmlPath + '/**/*.html', ['html-no-rev']);
    gulp.watch(webAppRoot + 'redeploy.touch', function () {
        console.log(webAppRoot + 'redeploy.touch');
        $.livereload.reload();
    });
});