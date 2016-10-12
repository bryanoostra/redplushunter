<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="stomp.NarratorMessageHandler" %>
<%NarratorMessageHandler messenger = NarratorMessageHandler.getInstance();
//Get the story
String result = messenger.getNarratorStory();
//Format it
result = result.replaceAll("(\r\n|\n\r|\r|\n)", "<br />\n");%>
<%=result %> 