<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
</head>
<body>
<form action="http://192.168.1.107:8080/YFfg/zuhe/checktoken.do" method="post">
	<table>
		<tr>
			<td>userid:</td>
			<td><input type="text" name="userid"></td>
		</tr>
		<tr>
			<td>token:</td>
			<td><input type="text" name="token"></td>
		</tr>

		<input type="submit" value="提交">
	</table>
</form>
</body>
</html>