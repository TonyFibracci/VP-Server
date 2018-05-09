<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>

<head>
	<title>Visual Portfolio Benchmarking</title>
	<link href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.1/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://getbootstrap.com/docs/4.1/examples/jumbotron/jumbotron.css" rel="stylesheet">
	<link type="text/css" rel="stylesheet" href="css/index.css">
</head>

<body>
    <div class="container">
      <div class="jumbotron">
        <h1>Visual Portfolio Benchmarking</h1>
        <form class="form-signin" method="GET">
          <label for="inputName" class="sr-only">Select sheet: </label>
    	  <select name="sheet">
			<option disabled selected value> -- select an option -- </option>
			<c:forEach var="selectedSheet" items="${SHEETS}" varStatus="loop">
   				<option value="${loop.index}"><c:out value="${selectedSheet}" /></option>
			</c:forEach>
		  </select>
		  <label for="inputName" class="sr-only">Header index:</label>
		  <input type="text" id="from" class="form-control" name="headerRowIndex">
          <input id="btnSignUp" class="btn" type="submit" value="->">
		  <input type="hidden" name="command" value="MAP"/>
        </form>
      </div>




      <footer class="footer">
        <p>&copy; Ernst & Young GmbH 2018</p>
      </footer>

    </div>
</body>


</html>








