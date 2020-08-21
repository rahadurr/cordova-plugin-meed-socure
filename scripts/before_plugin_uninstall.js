#!/usr/bin/env node

"use strict";

var fs = require("fs");
var path = require("path");
var Utils = require("./utils");

var appName = Utils.getAppName();

var PATHS = {
  ANDROID: {
    CONFIG: "plugins/cordova-plugin-meed-socure/sdk/android",
    MAIN: "platforms/android/app/src/main",
    RESOURCES: "platforms/android/app/src/main/res",
  },
  IOS: {

    RESOURCES: "platforms/ios/" + appName + "/Resources",
    PLIST: "platforms/ios/" + appName + "/" + appName + "-Info.plist",
  },
};

var CONFIG_FILES = {
  ANDROID: {
    files: ["styles.xml", "strings.xml", "config.json"],
  },
  IOS: {
    files: ["Document.json", "Document.strings", "config.plist"],
  },
};

var clearAndroidConfig = function (context) {
  if (Utils.fileExists(`${PATHS.ANDROID.RESOURCES}/values/strings.xml`)) {
    var socureStringsJson = Utils.parseXmlFileToJson(
        `${PATHS.ANDROID.CONFIG}/strings.xml`
      );
      var appStringsJson = Utils.parseXmlFileToJson(
        `${PATHS.ANDROID.RESOURCES}/values/strings.xml`
      );
    
      socureStringsJson.resources.string.forEach((socureString) => {
        appStringsJson.resources.string.map((appStr) => {
          if (appStr._attributes.name === socureString._attributes.name || appStr._attributes.name === 'socurePublicKey') {
            var index = appStringsJson.resources.string.indexOf(appStr);
            appStringsJson.resources.string.splice(index, 1);
          }
        });
      });

    Utils.writeJsonToXmlFile(
      appStringsJson,
      `${PATHS.ANDROID.RESOURCES}/values/strings.xml`
    );
  }

  if (Utils.fileExists(`${PATHS.ANDROID.RESOURCES}/values/styles.xml`)) {
    var socureStylesJson = Utils.parseXmlFileToJson(
        `${PATHS.ANDROID.CONFIG}/styles.xml`
    );
    var appStylesJson = Utils.parseXmlFileToJson(
        `${PATHS.ANDROID.RESOURCES}/values/styles.xml`
    );

    socureStylesJson.resources.style.forEach((socureStyle) => {
    appStylesJson.resources.style.map((appStyle) => {
        if (appStyle._attributes.name === socureStyle._attributes.name || appStyle._attributes.name === 'socurePublicKey') {
          var index = appStylesJson.resources.style.indexOf(appStyle);
          appStylesJson.resources.style.splice(index, 1);
        }
    });
    });

    Utils.writeJsonToXmlFile(
        appStylesJson,
      `${PATHS.ANDROID.RESOURCES}/values/styles.xml`
    );
  }

  // Remove config.json file to assets folder
  if(Utils.fileExists(`${PATHS.ANDROID.MAIN}/assets/config.json`)) {
    fs.unlinkSync(`${PATHS.ANDROID.MAIN}/assets/config.json`);
  }

};


var clearIOSConfig = function() {
  if(Utils.fileExists(PATHS.IOS.PLIST)) {
    var plist = Utils.parsePlist(PATHS.IOS.PLIST);
    if(plist.hasOwnProperty('socurePublicKey')) {
      delete plist['socurePublicKey'];
      Utils.writeJsonToPlist(plist, PATHS.IOS.PLIST);
    }
  }

  CONFIG_FILES.IOS.files.forEach((file) => {
    if(Utils.fileExists(`${PATHS.IOS.RESOURCES}/${file}`)) {
      fs.unlinkSync(`${PATHS.IOS.RESOURCES}/${file}`);
    }
  });

}

module.exports = function (context) {
  var platform = context.opts.plugin.platform;

  if (platform === "android") clearAndroidConfig(context);
  if (platform === "ios")  clearIOSConfig(context);
};