<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <title>Title</title>
    </head>

    <body>
        <h1>JSP</h1>

        <h2>Вирази</h2>
            <%= 2 + 3 %>

        <h2>Змінні</h2>
            <%
                int x = 10;
            %>
            <%= x %>

        <h2>Інструкції управління</h2>
            <% if(x % 2 == 0) { %>
        <b>Число <%= x %> парне </b>
            <% } else { %>
        <b>Число <%= x %> не парне </b>
            <% } %>

        <ul>
            <% for(int i = 0; i < 10; i++) {
                %> <li><%= i + 1 %></li> <%
            } %>
        </ul>

    </body>
</html>
