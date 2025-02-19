<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Hello World - JSP App</title>
    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/css/style.css"
    />
  </head>
  <body>
    <div class="container">
      <header>
        <h1>Hello World!</h1>
      </header>
      <main>
        <section class="content">
          <p>
            JSP (JavaServer Pages) and Servlets are Java technologies for web
            development. Servlets are Java classes that process HTTP requests,
            while JSP is a technology that allows creating dynamic web pages by
            combining HTML with Java code!
          </p>
          <p>
            Together, they form a robust foundation for Java web applications,
            where Servlets handle business logic (Controller) and JSPs manage
            presentation (View), following the MVC (Model-View-Controller)
            pattern.
          </p>
        </section>
      </main>
      <footer>
        <p>&copy; 2025 JSP Application</p>
      </footer>
    </div>
  </body>
</html>
