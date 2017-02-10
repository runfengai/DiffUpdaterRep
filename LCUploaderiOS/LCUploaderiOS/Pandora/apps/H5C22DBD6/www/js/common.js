//取消浏览器的所有事件，使得active的样式在手机上正常生效
document.addEventListener( "touchstart", function(e) {
    return false;
}, false );
// 屏蔽选择函数
function shield() {
	return false;
}
document.oncontextmenu = shield;
// H5 plus事件处理
document.addEventListener( "plusready", function() {
	gInit();
	document.body.onselectstart = shield;
	// Android处理返回键
	plus.ui.getSelfWindow().addEventListener( "back", function(){
		back();
	}, false );
	// iOS平台使用滚动的div
	if ( "iOS" == plus.os.name ) {
		var t = document.getElementById("dcontent");
		if ( t ) {
			t.className = "sdcontent";
		}
		t = document.getElementById("content");
		if ( t ) {
			t.className = "scontent";
		}
	}
}, false );
// 兼容非H5 plus终端
if ( navigator.userAgent.indexOf("Html5Plus") < 0 ) {
	document.addEventListener( "DOMContentLoaded", function () {
		gInit();
		document.body.onselectstart = shield;
	}, false );
}
// 处理返回事件
function back() {
	if ( window.plus ) {
		plus.ui.getSelfWindow().close();
	} else if ( history.length > 1 ) {
		history.back();
	} else {
		window.close();
	}
}
// 处理点击事件
function clicked( id ) {
	if ( window.plus ) {
		var pre = "";//"http://192.168.1.178:8080/h5/";
		plus.ui.createWindow( pre+id, {name:id,scalable:false} ).show( "slide-in-right", 300 );
	} else {
		window.open( id );
	}
}
// 通用元素对象
var _dout_,_dcontent_;
function gInit() {
	_dout_ = document.getElementById("output");
	_dcontent_ = document.getElementById("dcontent");
}
// 清空输出内容
function outClean() {
	_dout_.innerHTML = "";
}
// 输出内容
function outSet( s ) {
	_dout_.innerHTML = s+"<br/>";
	_dout_.scrollTop = 0;
}
// 输出行内容
function outLine( s ) {
	_dout_.innerHTML += s+"<br/>";
}

// 格式化时长字符串，格式为"HH:MM:SS"
function timeToStr( ts ) {
	if ( isNaN(ts) ) {
		return "--:--:--";
	}
	var h = parseInt(ts/3600);
	var m = parseInt((ts%3600)/60);
	var s = parseInt(ts%60);
	if ( h < 10 ) {
		h = "0"+h;
	}
	if ( m < 10 ) {
		m = "0"+m;
	}
	if ( s < 10 ) {
		s = "0"+s;
	}
	return (h+":"+m+":"+s);
}
// 格式化日期时间字符串，格式为"YY-MM-DD HH:MM:SS"
function dateToStr( datetime ) {
	var year = datetime.getFullYear(),
	month = datetime.getMonth()+1,
	date = datetime.getDate(),
	hour = datetime.getHours(),
	minutes = datetime.getMinutes(),
	second = datetime.getSeconds();
	if ( month < 10 ) {
		month = "0" + month;
	}
	if ( date < 10 ) {
		date = "0" + date;
	}
	if ( hour < 10 ) {
		hour = "0" + hour;
	}
	if ( minutes < 10 ) {
		minutes = "0" + minutes;
	}
	if ( second < 10 ) {
		second = "0" + second;
	}
	return (year+"-"+month+"-"+date+" "+hour+":"+minutes+":"+second);
}
