document.addEventListener('plusready', function() {
	//插件别名
	var _LCUPDATER = 'lcUpdater';
	var B = window.plus.bridge;
	//声明函数原型
	var lcUpdater = {
		//解压
		upZipFile: function(newDirPath, successCallback, failCallback) {
			var success = typeof successCallback !== 'function' ? null : function(args) {
				successCallback(args);
			};
			var fail = typeof failCallback !== 'function' ? null : function(args) {
				failCallback(args);
			};
			callbackID = B.callbackId(success, fail);
			return B.exec(_LCUPDATER, "upZipFile", [callbackID, newDirPath]);
		}

	};
	window.plus.lcUpdater = lcUpdater;
	console.log("window.plus.lcUpdater=" + JSON.stringify(window.plus.lcUpdater));
}, true);