<?php

/*
* Database Constants
* Make sure you are putting the values according to your database here
*/
define('DB_HOST','localhost');
define('DB_USERNAME','root');
define('DB_PASSWORD','');
define('DB_NAME', 'tye');

//Connecting to the database
$conn = new mysqli(DB_HOST, DB_USERNAME, DB_PASSWORD, DB_NAME);

//checking the successful connection
if($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

//making an array to store the response
$response = array();

//if there is a post request move ahead
if($_SERVER['REQUEST_METHOD']=='POST'){

	//getting the name from request
	$designacao = $_POST['designacao'];
  $saldo = $_POST['saldo'];
  $userCode = $_POST['userCode'];

	//creating a statement to insert to database
	$stmt = $conn->prepare("UPDATE conta SET saldo=? WHERE designacao=? AND userCode=?");

	//binding the parameter to statement
	$stmt->bind_param("sss", $saldo, $designacao, $userCode);

	//if data inserts successfully
	if($stmt->execute()){
		//making success response
		$response['error'] = false;
		$response['message'] = 'Saldo atualizado com sucesso';
	}else{
		//if not making failure response
		$response['error'] = true;
		$response['message'] = 'Please try later';
	}

}else{
	$response['error'] = true;
	$response['message'] = "Invalid request";
}

//displaying the data in json format
echo json_encode($response);
