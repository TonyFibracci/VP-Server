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
			<form action="UploadServlet" method="GET">
		
				<!-- put new button: Add Student -->
				<input type="hidden" name="command" value="CONTINUE"/>
				<input type="submit" value="Continue" class="add-student-button"/>
				
				<table>
				
					<tr>
						<th>Input field</th>
						<th>Target field</th>
					</tr>
					
					<c:forEach var="tempField" items="${TARGET_FIELDS}">
						
						<!-- set up a link for each student -->
						<c:url var="tempLink" value="UploadServlet">
							<c:param name="command" value="LOAD" />
							<c:param name="targetField" value="${tempField}" />
							<c:param name="inputField" value="${tempField}" />
						</c:url>
	
	<!-- 					 set up a link to delete a student -->
	<%-- 					<c:url var="deleteLink" value="StudentControllerServlet"> --%>
	<%-- 						<c:param name="command" value="DELETE" /> --%>
	<%-- 						<c:param name="studentId" value="${tempStudent.id}" /> --%>
	<%-- 					</c:url> --%>
																			
						<tr>
							<td> ${tempField} </td>
	 						<td>  
								<select name="${tempField}">
									<option disabled selected value> -- select an option -- </option>
	    							<c:forEach var="targetField" items="${INPUT_FIELDS}">
	        							<option value="${targetField}"><c:out value="${targetField}" /></option>
	    							</c:forEach>
								</select>
							</td> 
						</tr>
					
					</c:forEach>
					
				</table>
			</form>
		
		</div>
		<p>
			<a href="UploadServlet">Back to Start</a>
		</p>
	</div>
</body>


</html>








