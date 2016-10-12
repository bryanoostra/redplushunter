<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
    %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Narrator Stomp Listener</title>
<script language="Javascript">
function xmlhttpPost(strURL) {
var xmlHttpReq = false;
var self = this;
// Mozilla/Safari
if (window.XMLHttpRequest) {
self.xmlHttpReq = new XMLHttpRequest();
}
// IE
else if (window.ActiveXObject) {
self.xmlHttpReq = new ActiveXObject("Microsoft.XMLHTTP");
}
self.xmlHttpReq.open('POST', strURL, true);
self.xmlHttpReq.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
self.xmlHttpReq.onreadystatechange = function() {
if (self.xmlHttpReq.readyState == 4) {
updatepage(self.xmlHttpReq.responseText);
}
};
self.xmlHttpReq.send("");
}

function getquerystring() {
var form = document.forms['f1'];
var word = form.word.value;
qstr = 'w=' + escape(word); // NOTE: no '?' before querystring
return qstr;
}

function updatepage(str){
document.getElementById("result").innerHTML = str;
}
</script>
<% String result = new stomp.StompConnector(application).test();%>
</head>
<body>
<h1>Stomp Connector!</h1>
<%=result  %>

<h1>Live messages: </h1>
<form name="f1">
<input value="Refresh" type="button" onclick='JavaScript:xmlhttpPost("LiveUpdate.jsp")' />
</form>
<p><div id="result"></div></p>

</body>
</html>