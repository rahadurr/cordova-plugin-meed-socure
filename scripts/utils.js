var fs = require('fs');
var path = require("path");
var parser = require('xml-js');
var plist = require('plist');

var _configXml, _pluginXml, _plist;

var Utils = {};

fs.ensureDirSync = function(dir){
    if(!fs.existsSync(dir)){
        dir.split(path.sep).reduce(function(currentPath, folder){
            currentPath += folder + path.sep;
            if(!fs.existsSync(currentPath)){
                fs.mkdirSync(currentPath);
            }
            return currentPath;
        }, '');
    }
};

Utils.parsePackageJson = function(){
    return JSON.parse(fs.readFileSync(path.resolve('./package.json')));
};

Utils.parseConfigXml = function(){
    if(_configXml) return _configXml;
    _configXml = Utils.parseXmlFileToJson("config.xml");
    return _configXml;
};

Utils.parsePluginXml = function(){
    if(_pluginXml) return _pluginXml;
    _pluginXml = Utils.parseXmlFileToJson(Utils.getPluginXmlPath());
    return _pluginXml;
};


Utils.parseXmlFileToJson = function(filepath, parseOpts){
    parseOpts = parseOpts || {compact: true};
    return JSON.parse(parser.xml2json(fs.readFileSync(path.resolve(filepath), 'utf-8'), parseOpts));
};

Utils.writeJsonToXmlFile = function(jsonObj, filepath, parseOpts){
    parseOpts = parseOpts || {compact: true, spaces: 4};
    var xmlStr = parser.json2xml(JSON.stringify(jsonObj), parseOpts);
    fs.writeFileSync(path.resolve(filepath), xmlStr);
};


Utils.parsePlist = function (filepath) {
    var json = plist.parse(fs.readFileSync(filepath, 'utf8'));
    return json;
}

Utils.writeJsonToPlist = function (jsonObj, filepath) {
    var plistXml = plist.build(jsonObj);
    fs.writeFileSync(path.resolve(filepath), plistXml);
}


/**
 * Used to get the name of the application as defined in the config.xml.
 */
Utils.getAppName = function(){
    return Utils.parseConfigXml().widget.name._text.toString().trim();
};

Utils.getPluginXmlPath = function(){
    return "plugins/"+Utils.getPluginId()+"/plugin.xml";
};

/**
 * The ID of the plugin; this should match the ID in plugin.xml.
 */
Utils.getPluginId = function(){
    return "cordova-plugin-meed-socure";
};

Utils.isObjectEqual = function (object1, object2) {
    const keys1 = Object.keys(object1);
    const keys2 = Object.keys(object2);
  
    if (keys1.length !== keys2.length) {
      return false;
    }
  
    for (let key of keys1) {
      if (object1[key] !== object2[key]) {
        return false;
      }
    }
  
    return true;
  }

Utils.copyKey = function(platform){
    for(var i = 0; i < platform.src.length; i++){
        var file = platform.src[i];
        if(this.fileExists(file)){
            try{
                var contents = fs.readFileSync(path.resolve(file)).toString();

                try{
                    var destinationPath = platform.dest;
                    var folder = destinationPath.substring(0, destinationPath.lastIndexOf('/'));
                    fs.ensureDirSync(folder);
                    fs.writeFileSync(path.resolve(destinationPath), contents);
                }catch(e){
                    // skip
                }
            }catch(err){
                console.log(err);
            }

            break;
        }
    }
};

Utils.fileExists = function(filePath){
    try{
        return fs.statSync(path.resolve(filePath)).isFile();
    }catch(e){
        return false;
    }
};

Utils.directoryExists = function(dirPath){
    try{
        return fs.statSync(path.resolve(dirPath)).isDirectory();
    }catch(e){
        return false;
    }
};

Utils.log = function(msg){
    console.log(Utils.getPluginId()+': '+msg);
};

module.exports = Utils;