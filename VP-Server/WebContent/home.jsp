<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>

<head>
	<title>Visual Portfolio Benchmarking</title>
	<link href="http://getbootstrap.com/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="http://getbootstrap.com/examples/jumbotron-narrow/jumbotron-narrow.css" rel="stylesheet">
	<link type="text/css" rel="stylesheet" href="css/index.css">
</head>

<body>
    <div class="container">
      <div class="jumbotron">
        <h1>Visual Portfolio Benchmarking</h1>
        <form class="form-signin" enctype="multipart/form-data" method="post">
          <label for="inputName" class="sr-only">Select file: </label>
          <input type="file" accept=".xlsx,.csv" name="file" id="inputName" class="form-control" placeholder="From" required autofocus>
          <input id="btnSignUp" class="btn btn-lg btn-primary btn-block" type="submit" >

        </form>
      </div>




      <footer class="footer">
        <p>&copy; Ernst & Young GmbH 2018</p>
      </footer>

    </div>
</body>


</html>








