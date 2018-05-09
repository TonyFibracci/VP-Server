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
		
			<!-- put new button: Add Student -->
			
			<input type="button" value="Export" 
				   onclick="window.location.href='add-student-form.jsp'; return false;"
				   class="add-student-button"
			/>
			
			<table>
			
				<tr>
					<th>Target</th>
					<th>Input</th>
					<th>Status</th>
					<th>Action</th>
				</tr>
				
				<c:forEach var="entry" items="${TARGET_INPUT_MAP}">
					
					<!-- set up a link for each student -->
					<c:url var="tempLink" value="UploadServlet">
						<c:param name="command" value="LOAD" />
						<c:param name="targetField" value="${entry.key}" />
						<c:param name="inputField" value="${entry.value}" />
					</c:url>
																		
					<tr>
						<td> ${entry.key} </td>
						<td> ${entry.value} </td>
						<td> ${inputIndexMapping[entry.value]} </td>
						<td> 
							<c:if test="${fieldValueMapping[entry.key]!=null}">
								<a href="${tempLink}">Edit value mapping</a> 
							</c:if>
						</td>
					</tr>
				
				</c:forEach>
				
			</table>
		
		</div>
	
	</div>
</body>


</html>








