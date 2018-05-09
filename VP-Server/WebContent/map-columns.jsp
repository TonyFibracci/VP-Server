<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>

<head>
	<title>Visual Portfolio Benchmarking</title>
	<link href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.1/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://getbootstrap.com/docs/4.1/examples/jumbotron/jumbotron.css" rel="stylesheet">
	<link type="text/css" rel="stylesheet" href="css/style.css">
</head>

<body>

	<div id="wrapper">
		<div id="header">
			<h2>Visual Portfolio Benchmarking</h2>
		</div>
	</div>

	<div id="container">
	
		<div id="content">
			<form action="BenchmarkingServlet" method="GET">
		
				<!-- put new button: Add Student -->
				<input type="hidden" name="command" value="VALIDATE"/>
				<input type="submit" value="Validate" class="add-student-button"/>
				
				<table>
				
					<tr>
						<th>Input field</th>
						<th>Target field</th>
					</tr>
					
					<c:forEach var="tempField" items="${VP_COLUMNS}">
																			
						<tr>
							<td> ${tempField} </td>
	 						<td>  
								<select name="${tempField}">
									<option disabled selected value> -- select an option -- </option>
	    							<c:forEach var="targetField" items="${COLUMN_MAPPING}" varStatus="loop">
	        							<option value="${loop.index}"><c:out value="${targetField.key}" /></option>
	    							</c:forEach>
								</select>
							</td> 
						</tr>
					
					</c:forEach>
					
				</table>
			</form>
		
		</div>
		<p>
			<a href="BenchmarkingServlet">Back to Start</a>
		</p>
	</div>
</body>


</html>








