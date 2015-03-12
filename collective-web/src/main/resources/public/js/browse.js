var app = angular.module('browse',[]);

app.controller('TabController', function(){
	this.current = 1;
	this.getCurrent = function() {
		return this.current;
	}
	this.setCurrent = function(cur) {
		this.current = cur;
	};
	this.isCurrent = function(cur) {
		return this.current === cur;
	};
});