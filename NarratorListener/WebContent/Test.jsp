<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="test.SerializationTest" %>
<%@ page import="java.io.File" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Corgis and rats are cute.</title>
</head>
<body>
<%SerializationTest test = new SerializationTest(); 
File tmpdir = (File)application.getAttribute("javax.servlet.context.tempdir");
test.setWriteDir(tmpdir);
test.test();
%>
<%= tmpdir.getAbsolutePath() %>
</body>
</html> 