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
			<form action="UploadServlet" method="POST">
		
				<!-- put new button: Add Student -->
				<input type="hidden" name="command" value="MAP"/>
				<input type="hidden" name="targetField" value="${TARGET_FIELD}" />
				<input type="submit" value="save" name="save"
					   class="add-student-button"
				/>
				
				<table>
				
					<tr>
						<th>${TARGET_FIELD}</th>
						<th>${INPUT_FIELD}</th>
					</tr>
					
					<c:forEach var="targetValue" items="${TARGET_VALUE_SET}">
																			
						<tr>
							<td> ${targetValue} </td>
	 						<td>  
								<select name="${targetValue}">
									<option disabled selected value> -- select an option -- </option>
	    							<c:forEach var="inputValue" items="${INPUT_VALUE_SET}">
	        							<option value="${inputValue}"><c:out value="${inputValue}" /></option>
	    							</c:forEach>
								</select>
							</td> 
						</tr>
					
					</c:forEach>
					
				</table>
			</form>
		
		</div>
		<c:url var="tempLink" value="UploadServlet">
			<c:param name="command" value="CONTINUE" />
		</c:url>
		<p>
			<a href="${tempLink}">Back to List</a>
		</p>
	</div>
</body>


</html>








