'use strict';

var gulp = require('gulp');
var merge = require('merge-stream');
var browserify = require('browserify');
var babelify = require('babelify');
var watchify = require('watchify');
var source = require('vinyl-source-stream');

// load plugins
var $ = require('gulp-load-plugins')();

// pngquant is irrelevant and does not contain prefix "gulp-"
var pngquant = require('imagemin-pngquant');
var del = require('del');


/**
 * Some constants to save some typing later
 */
var resourcesPath = 'src/main/frontend/',
    webAppRoot = 'src/main/webapp/',
    generatedPath = 'target/generated-resources/frontend/',
    generatedSrcPath = generatedPath + 'src/';

var htmlPath = 'src/main/java',
    generatedHtmlPath = 'target/generated-resources/html/WEB-INF/classes/';


/**
 * Wrap gulp streams into fail-safe function for better error reporting
 * Usage:
 * gulp.task('less', wrapPipe(function(success, error) {
 *   return gulp.src('less/*.less')
 *      .pipe(less().on('error', error))
 *      .pipe(gulp.dest('app/css'));
 * }));
 */
function wrapPipe(taskFn) {
    return function (done) {
        var onSuccess = function () {
            done();
        };
        var onError = function (err) {
            done(err);
        };
        var outStream = taskFn(onSuccess, onError);
        if (outStream && typeof outStream.on === 'function') {
            outStream.on('end', onSuccess);
        }
    }
}

function processImages(success, error) {
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

function processStyles(success, error) {
    var cssFiles = gulp.src(resourcesPath + '/**/*.css')
        .pipe($.changed(generatedSrcPath))
        .pipe(gulp.dest(generatedSrcPath))
        .pipe($.sourcemaps.init({loadMaps: true}))
        .pipe($.autoprefixer({
            browsers: ['> 5% in RU'],
            cascade: false
        }))
        .pipe($.cleanCss().on('error', error));

    var stylusFiles = gulp.src(resourcesPath + '/**/*.styl')
        .pipe($.changed(generatedSrcPath))
        .pipe(gulp.dest(generatedSrcPath))
        .pipe($.stylus({compress: true}))
        .pipe($.autoprefixer({
            browsers: ['> 5% in RU'],
            cascade: false
        }))
        .pipe($.cleanCss().on('error', error));
        
    return merge(cssFiles, stylusFiles);
}

var browserifyBundle = browserify(resourcesPath + 'js/radio/index.js')
    .transform(babelify, {presets: ['es2015', 'stage-0', 'react']});

function browserifyScripts(success, error) {
    return browserifyBundle.bundle().on('error', error)
        // .pipe($.babel({presets: ['es2015', 'stage-0', 'react']}).on('error', error))
        .pipe(source('js/radio.js'))
        .pipe($.buffer());
}

function processScripts(success, error) {
    return merge(
        gulp.src(resourcesPath + '/**/*.js')
            .pipe($.filter(['**/*', '!**/radio/**/*.js']))
            .pipe($.changed(generatedSrcPath)),
        browserifyScripts(success, error))
        .pipe(gulp.dest(generatedSrcPath))
        .pipe($.sourcemaps.init({loadMaps: true}))
        .pipe($.uglify().on('error', error));
}

function writeSourcemaps() {
    return $.sourcemaps.write('./', {includeContent: false, sourceRoot: '/src'});
}

gulp.task('images-with-rev', wrapPipe(function (success, error) {
    return processImages(success, error)
        .pipe($.rev())
        .pipe(gulp.dest(generatedPath))
        .pipe($.rev.manifest('rev-manifest-images.json', {merge: true}))
        .pipe(gulp.dest(generatedPath));
}));

gulp.task('styles-with-rev', ['images-with-rev'], wrapPipe(function (success, error) {
    var manifest = gulp.src(generatedPath + "/rev-manifest-images.json");
    return processStyles(success, error)
        .pipe($.revReplace({manifest: manifest}))
        .pipe($.rev())
        .pipe(writeSourcemaps())
        .pipe(gulp.dest(generatedPath))
        .pipe($.rev.manifest('rev-manifest-styles.json', {merge: true}))
        .pipe(gulp.dest(generatedPath));
}));

gulp.task('scripts-with-rev', /*['images-with-rev'], */wrapPipe(function (success, error) {
    //var manifest = gulp.src(generatedPath + "/rev-manifest-images.json");
    return processScripts(success, error)
    //.pipe($.revReplace({manifest: manifest}))
        .pipe($.rev())
        .pipe(writeSourcemaps())
        .pipe(gulp.dest(generatedPath))
        .pipe($.rev.manifest('rev-manifest-scripts.json', {merge: true}))
        .pipe(gulp.dest(generatedPath));
}));

gulp.task('marge-rev-manifest', ['scripts-with-rev', 'styles-with-rev'], wrapPipe(function (success, error) {
    return gulp.src(generatedPath + "/rev-manifest-*.json")
        .pipe($.mergeJson('rev-manifest.json').on('error', error))
        .pipe(gulp.dest(generatedPath));
}));

gulp.task('html-with-rev', ['marge-rev-manifest'], wrapPipe(function (success, error) {
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
}));

gulp.task('html-no-rev', wrapPipe(function (success, error) {
    return gulp.src(htmlPath + '/**/*.html')
        .pipe($.changed(generatedHtmlPath))
        .pipe(gulp.dest(generatedHtmlPath))
        .pipe($.livereload());
}));

gulp.task('images-no-rev', wrapPipe(function (success, error) {
    return processImages(success, error)
        .pipe(gulp.dest(generatedPath))
        .pipe($.livereload());
}));

gulp.task('styles-no-rev', wrapPipe(function (success, error) {
    return processStyles(success, error)
        .pipe(writeSourcemaps())
        .pipe(gulp.dest(generatedPath))
        .pipe($.filter(['**/*', '!**/*.map']))
        .pipe($.livereload());
}));

gulp.task('scripts-no-rev', wrapPipe(function (success, error) {
    return processScripts(success, error)
        .pipe(writeSourcemaps())
        .pipe(gulp.dest(generatedPath))
        .pipe($.filter(['**/*', '!**/*.map']))
        .pipe($.livereload());
}));

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
    browserifyBundle = watchify(browserifyBundle);
    gulp.watch(resourcesPath + '**/*.css', ['styles-no-rev']);
    gulp.watch(resourcesPath + '**/*.js', ['scripts-no-rev']);
    gulp.watch(resourcesPath + '**/*.{png,gif,jpg}', ['images-no-rev']);
    gulp.watch(htmlPath + '/**/*.html', ['html-no-rev']);
    gulp.watch(webAppRoot + 'redeploy.touch', function () {
        $.livereload.reload();
    });
});
