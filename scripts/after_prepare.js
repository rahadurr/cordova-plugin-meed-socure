#!/usr/bin/env node

"use strict";

var fs = require("fs");
var path = require("path");
var Utils = require("./utils");

var appName = Utils.getAppName();
var PLUGIN_VARIABLES = {};

var PLUGIN_ID = "cordova-plugin-meed-socure";

var PATHS = {
  ANDROID: {
    ROOT: "platforms/android",
    MAIN: "platforms/android/app/src/main",
    RESOURCES: "platforms/android/app/src/main/res",
  },
  IOS: {
    ROOT: "platforms/ios",
    MAIN: "platforms/ios/" + appName,
    RESOURCES: "platforms/ios/" + appName + "/Resources",
    PLIST: "platforms/ios/" + appName + "/" + appName + "-Info.plist"
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

var parsePluginVariables = function () {
  var plugin = Utils.parsePluginXml();

  var prefs = [];
  if (plugin.plugin.preference) {
    prefs = prefs.concat(plugin.plugin.preference);
  }
  plugin.plugin.platform.forEach(function (platform) {
    if (platform.preference) {
      prefs = prefs.concat(platform.preference);
    }
  });

  // Parse config.xml
  var config = Utils.parseConfigXml();
  (config.widget.plugin ? [].concat(config.widget.plugin) : []).forEach(
    function (plugin) {
      (plugin.variable ? [].concat(plugin.variable) : []).forEach(function (
        variable
      ) {
        if (
          (plugin._attributes.name === PLUGIN_ID ||
            plugin._attributes.id === PLUGIN_ID) &&
          variable._attributes.name &&
          variable._attributes.value
        ) {
          PLUGIN_VARIABLES[variable._attributes.name] =
            variable._attributes.value;
        }
      });
    }
  );

  // Parse package.json
  var packageJSON = Utils.parsePackageJson();
  if (packageJSON.cordova && packageJSON.cordova.plugins) {
    for (const pluginId in packageJSON.cordova.plugins) {
      if (pluginId === PLUGIN_ID) {
        for (const varName in packageJSON.cordova.plugins[pluginId]) {
          var varValue = packageJSON.cordova.plugins[pluginId][varName];
          PLUGIN_VARIABLES[varName] = varValue;
        }
      }
    }
  }
};

var updateSocureAndroidSdkKey = function (context) {
  if (PLUGIN_VARIABLES.SOCURE_SDK_KEY) {
    var stringsJson = Utils.parseXmlFileToJson(`${PATHS.ANDROID.RESOURCES}/values/strings.xml`);

    // Modify existing strings.xml
    stringsJson.resources.string.map((stringAttr) => {
      if (stringAttr._attributes.name === "socurePublicKey") {
        stringAttr._text = PLUGIN_VARIABLES.SOCURE_SDK_KEY;
      }
    });

    // Write change to the strings.xml
    Utils.writeJsonToXmlFile(stringsJson, `${PATHS.ANDROID.RESOURCES}/values/strings.xml`);
  }
};


var updateAndroidResource = function(context) {
  var rootConfigDir = `${context.opts.projectRoot}/socure/android`;
  
  if(Utils.fileExists(`${PATHS.ANDROID.RESOURCES}/values/strings.xml`)) {
    var socureStringsJson = Utils.parseXmlFileToJson(`${rootConfigDir}/strings.xml`);
    var appStringsJson = Utils.parseXmlFileToJson(`${PATHS.ANDROID.RESOURCES}/values/strings.xml`);

    appStringsJson.resources._attributes = socureStringsJson.resources._attributes;

    socureStringsJson.resources.string.forEach((socureString) => {
      var matched = false;
      appStringsJson.resources.string.map((appStr) => {
        if(appStr._attributes.name === socureString._attributes.name) {
          appStr._text = socureString._text;
          matched = true;
        }
      });
      if(!matched) appStringsJson.resources.string.push(socureString);
    });
  
    Utils.writeJsonToXmlFile(appStringsJson, `${PATHS.ANDROID.RESOURCES}/values/strings.xml`);

  } else {
    fs.copyFileSync(`${rootConfigDir}/strings.xml`, `${PATHS.ANDROID.RESOURCES}/values/strings.xml`)
  }

  if(Utils.fileExists(`${PATHS.ANDROID.RESOURCES}/values/styles.xml`)) {
    var socureStylesJson = Utils.parseXmlFileToJson( `${rootConfigDir}/styles.xml`);
    var appStylesJson = Utils.parseXmlFileToJson( `${PATHS.ANDROID.RESOURCES}/values/styles.xml`);

    if(appStylesJson.resources.style) {
      socureStylesJson.resources.style.forEach((socureStyle) => {
        var matched = false;
        appStylesJson.resources.style.map((appStyle) => {
          if(appStyle._attributes.name === socureStyle._attributes.name) {
            appStyle.item = socureStyle.item;
            matched = true;
          }
        });
        if(!matched) appStylesJson.resources.string.push(socureStyle);
      });
    } else {
      appStylesJson.resources.style = socureStylesJson.resources.style;
    }
  
    Utils.writeJsonToXmlFile(appStylesJson, `${PATHS.ANDROID.RESOURCES}/values/styles.xml`);

  } else {
    fs.copyFileSync(`${rootConfigDir}/styles.xml`, `${PATHS.ANDROID.RESOURCES}/values/styles.xml`)
  }

  // Copy config.json file to assets folder
  if(Utils.fileExists(`${rootConfigDir}/config.json`)) {
    fs.copyFileSync(`${rootConfigDir}/config.json`, `${PATHS.ANDROID.MAIN}/assets/config.json`);
  }

}


var updateSocureIOSSdkKey = function (context) {
  if (PLUGIN_VARIABLES.SOCURE_SDK_KEY) {
    var plist = Utils.parsePlist(PATHS.IOS.PLIST);
    plist.socurePublicKey =  PLUGIN_VARIABLES.SOCURE_SDK_KEY;
    Utils.writeJsonToPlist(plist, PATHS.IOS.PLIST);
  }
};

var copySocureIOSConfigFiles = function (context) {
  var rootConfigDir = `${context.opts.projectRoot}/socure/ios`;

  CONFIG_FILES.IOS.files.forEach((file) => {
    fs.copyFileSync(
      path.resolve(`${rootConfigDir}/${file}`),
      path.resolve(`${PATHS.IOS.RESOURCES}/${file}`)
    );
  });
};



module.exports = function (context) {
  var platforms = context.opts.platforms;

  parsePluginVariables();

  if (platforms.includes("android")) {
    updateSocureAndroidSdkKey(context);
    updateAndroidResource(context);
  }

  if (platforms.includes("ios")) {
    updateSocureIOSSdkKey(context);
    copySocureIOSConfigFiles(context);
  }
};