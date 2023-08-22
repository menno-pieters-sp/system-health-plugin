
var hostsUrl = SailPoint.CONTEXT_PATH + '/adminConsole/index.jsf#/environmentMonitoring/hosts';
var jQueryClone = jQuery;
var statusClass = 'healthUNKNOWN';
var CsrfToken = Ext.util.Cookies.get('CSRF-TOKEN');

jQuery(document).ready(function(){

	jQuery("ul.navbar-right li:first")
		.before(
				'<li class="dropdown">' +
				'		<a href="' + hostsUrl + '" tabindex="0" role="menuitem" data-snippet-debug="off">' +
				'			<i id="systemHealthStatusIcon" role="presenation" class="fa fa-heartbeat fa-lg ' + statusClass + '"></i>' +
				'		</a>' +
				'</li>'
		);
	
    jQueryClone.ajax({
        method: "GET",
        beforeSend: function (request) {
            request.setRequestHeader("X-XSRF-TOKEN", CsrfToken);
        },
        url: SailPoint.CONTEXT_PATH + "/plugin/rest/systemhealthplugin/getStatus"
    })
    .done(function (msg) {
        healthstatus = msg._status;
        statusClass = 'health' + healthstatus;
	    document.getElementById("systemHealthStatusIcon").className = 'fa fa-heartbeat fa-lg ' + statusClass;
    });

	setInterval(function(){
	    jQueryClone.ajax({
	        method: "GET",
	        beforeSend: function (request) {
	            request.setRequestHeader("X-XSRF-TOKEN", CsrfToken);
	        },
	        url: SailPoint.CONTEXT_PATH + "/plugin/rest/systemhealthplugin/getStatus"
	    })
	    .done(function (msg) {
	        healthstatus = msg._status;
	        statusClass = 'health' + healthstatus;
		    document.getElementById("systemHealthStatusIcon").className = 'fa fa-heartbeat fa-lg ' + statusClass;
	    });
	
	
    }, 15000);

	
});
