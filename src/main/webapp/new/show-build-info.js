(function() {

    var method = 'GET';
    var url = '/api/info/short';
    var async = true;

    var request = new XMLHttpRequest();
    request.open(method, url, async);
    request.onreadystatechange = function () {
        if (r.readyState != 4 || r.status != 200) return;
        displayInfo(request.responseText);
    };
    request.send();

    function displayInfo(text) {
        document.title = '[itcatan] ' + text;
    }

})();