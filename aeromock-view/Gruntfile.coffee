"use strict"
module.exports = (grunt) ->

  require("matchdep").filterDev("grunt-*").forEach grunt.loadNpmTasks

  grunt.initConfig

    # Metadata.
    pkg: grunt.file.readJSON("package.json")

    # Task configuration.
    clean:
      files: ["dist"]

    copy:
      # distへjsをコピー
      js:
        expand: true
        cwd: "src/"
        src: ["**"]
        dest: "dist/public/js"
      # distへhtmlをコピー
      html:
        expand: true
        cwd: "html/"
        src: ["**"]
        dest: "dist/public"
      # faviconへhtmlをコピー
      favicon:
        expand: true
        src: ["favicon.ico"]
        dest: "dist/public"
      # distへcomponentsをコピー
      components:
        expand: true
        src: ["components/**"]
        dest: "dist/public"
      # distへimgをコピー
      img:
        expand: true
        src: ["img/**"]
        dest: "dist/public"
      # src/main/resourcesへコピー
      scala_resource:
        expand: true
        cwd: "dist/"
        src: ["public/**"]
        dest: "../aeromock-server/src/main/resources/"
      # クラスパスへコピー
      scala_target:
        expand: true
        cwd: "dist/"
        src: ["public/**"]
        dest: "../aeromock-server/target/scala-2.11/classes"

     esteWatch:
       options:
         dirs: ["src", "html"]
         livereload:
           enabled: true
           port: 35729
           extensions: ["js", "html"]
       "*": (filepath) ->
         "build"

     prepare:
       src: "src/"
       dest: "dist/"

  grunt.registerTask "default", ["esteWatch"]
  grunt.registerTask "build", ["copy"]
