<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="stomp.NarratorMessageHandler" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%NarratorMessageHandler messenger = NarratorMessageHandler.getInstance();
//Get messages
String result = messenger.getAllMessages();
//Escape HTML
result = StringEscapeUtils.escapeHtml(result);
//Format result
result = result.replaceAll("(\r\n|\n\r|\r|\n)", "<br />\n");
%> 

<%=result%>