;(function(p){
	var b = p.bridge;
	p.test = {
		"getString":function(){
			return p.bridge.execSync( "Test", "getString" );
		},
		"show":function( tip, okCB ) {
			var okID = p.bridge.callbackId( okCB );
			p.bridge.exec( "Test", "show", [tip,okID] );
		}
	};
})(window.plus);