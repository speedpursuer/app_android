cordova.define("cordova-plugin-hybrid.HybridBridge", function(require, exports, module) {
var exec = require('cordova/exec'),
    cordova = require('cordova');

function HybridBridge() {

}

HybridBridge.prototype.showList = function(urls, showTip, successCallback, errorCallback) {
    exec(successCallback, errorCallback, "HybridBridge", "showList", [urls, showTip]);
};

HybridBridge.prototype.showAlert = function(title, desc, clean, successCallback, errorCallback) {
    exec(successCallback, errorCallback, "HybridBridge", "showAlert", [title, desc, clean]);
};

HybridBridge.prototype.checkPush = function(successCallback, errorCallback) {
    exec(successCallback, errorCallback, "HybridBridge", "checkPush", []);
};

HybridBridge.prototype.getDBString = function(successCallback, errorCallback) {
    exec(successCallback, errorCallback, "HybridBridge", "getDBString", []);
};

HybridBridge.prototype.moveToBack = function(successCallback, errorCallback) {
    exec(successCallback, errorCallback, "HybridBridge", "moveToBack", []);
};

HybridBridge.prototype.showFavorite = function(successCallback, errorCallback) {
    exec(successCallback, errorCallback, "HybridBridge", "showFavorite", []);
};

module.exports = new HybridBridge();


});
