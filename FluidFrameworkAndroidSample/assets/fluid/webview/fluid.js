var fluidCallbackDict = {};

var fluidDataChangeCallbackDict = {};

var commands = [];

var commandInProgress = false;

function createGuid() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        var r = Math.random()*16|0, v = c === 'x' ? r : (r&0x3|0x8);
        return v.toString(16);
    });
}

function sendCommand(command) {
	commands.push(command);
	if (!commandInProgress) {
		sendCommandHelper();
	}
}

function sendCommandHelper() {
	commandInProgress = true;
	window.location='fluid://' + commands.shift();
}		

function commandFinished() {
	commandInProgress = false;
	if (commands.length > 0) {
		sendCommandHelper();
	}
}

function getData(url, successFunction, failFunction) {
	
	var callback = new Object();
	callback.success = successFunction;
	callback.fail = failFunction;

	var callbackId = createGuid();
	fluidCallbackDict[callbackId] = callback;

	sendCommand('data/' + url + "?callback=" + callbackId);
}

function fluidDataCallback(callbackId, success, data) {
	
	var callback = fluidCallbackDict[callbackId];
	if (callback === undefined)
		return;
	
	delete fluidCallbackDict[callbackId];
		
	if (success) {
		callback.success(data);
	} else {
		callback.fail(data);
	}
}

function addDataChangeListener(key, callbackFunction) {

	var callback = new Object();
	callback.success = callbackFunction;

	var callbackId = createGuid();
	fluidDataChangeCallbackDict[callbackId] = callback;

	sendCommand('addDataChangeListener/' + key + "?callback=" + callbackId);
}

function dataDidChangeFor(callbackId, key, subkeys) {
	subkeys = "";
	var callback = fluidDataChangeCallbackDict[callbackId];
	if (callback === undefined)
		return;
	callback.success(key, subkeys);
}		

function resizeLayout(width, height) {
	$('body').css('width', width + 'px');
	$('body').css('height', height + 'px');	
}

$(function() {
	FastClick.attach(document.body);

	var width = $( document ).width();
	var height = $( document ).height();			

	$('body').css('width', width + 'px');
	$('body').css('height', height + 'px');

	initializeLayout(width, height); // expecting client to implement this	
});
