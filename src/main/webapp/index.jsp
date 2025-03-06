<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <title>Title</title>
    </head>

    <body>
        <a href="home">Home</a>
        <a href="time">Time</a>
        <a href="random">Random</a>
        <a href="user">User</a>
        <a href="storage">Storage</a>
        <br/>
        <br/>
        <form action="product" method="post" enctype="multipart/form-data">
            <input name="field1" value="value 1"/>
            <input name="field2" value="value 2"/>
            <input type="file" name="file1"/>
            <button>send</button>
        </form>

<%--        <h1>JSP</h1>--%>

<%--        <h2>Вирази</h2>--%>
<%--            <%= 2 + 3 %>--%>

<%--        <h2>Змінні</h2>--%>
<%--            <%--%>
<%--                int x = 10;--%>
<%--            %>--%>
<%--            <%= x %>--%>

<%--        <h2>Інструкції управління</h2>--%>
<%--            <% if(x % 2 == 0) { %>--%>
<%--        <b>Число <%= x %> парне </b>--%>
<%--            <% } else { %>--%>
<%--        <b>Число <%= x %> не парне </b>--%>
<%--            <% } %>--%>

<%--        <ul>--%>
<%--            <% for(int i = 0; i < 10; i++) {--%>
<%--                %> <li><%= i + 1 %></li> <%--%>
<%--            } %>--%>
<%--        </ul>--%>

    </body>
</html>
