#!/usr/bin/env node

"use strict";

var fs = require("fs");
var path = require("path");

var Utils = require("./utils.js");

var PLUGIN_ID = "cordova-plugin-meed-socure";

var CONFIG_FILES = {
  ANDROID: {
    src: `plugins/${PLUGIN_ID}/sdk/android`,
    dest: "socure/android",
    files: ["styles.xml", "strings.xml", "config.json"],
  },
  IOS: {
    src: `plugins/${PLUGIN_ID}/sdk/ios`,
    dest: "socure/ios",
    files: ["Document.json", "Document.strings", "config.plist"],
  },
};

module.exports = function (context) {
  var platform = context.opts.plugin.platform;

  if (platform === "android") {
    var targetDir = `${context.opts.projectRoot}/${CONFIG_FILES.ANDROID.dest}`;
    if (!Utils.directoryExists(targetDir)) {
      fs.mkdirSync(targetDir, { recursive: true });

      CONFIG_FILES.ANDROID.files.forEach((file) => {
        fs.copyFileSync(
          path.resolve(`${CONFIG_FILES.ANDROID.src}/${file}`),
          path.resolve(`${targetDir}/${file}`)
        );
      });
    }
  }

  if (platform === "ios") {
    var targetDir = `${context.opts.projectRoot}/${CONFIG_FILES.IOS.dest}`;
    if (!Utils.directoryExists(targetDir)) {
      fs.mkdirSync(targetDir, { recursive: true });
      CONFIG_FILES.IOS.files.forEach((file) => {
        fs.copyFileSync(
          path.resolve(`${CONFIG_FILES.IOS.src}/${file}`),
          path.resolve(`${targetDir}/${file}`)
        );
      });
    }
  }
};
