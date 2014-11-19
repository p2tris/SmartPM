<?php

 require_once('loader.php');
	$gcmRegID    = $_GET["regId"];
	$pushMessage = $_GET["message"];
	
	if (isset($gcmRegID) && isset($pushMessage)) {
		
		
		$registration_ids = array($gcmRegID);
		$message = array("message" => $pushMessage);
		$result = send_push_notification($registration_ids, $message);
	
		echo $result;
	}
?>
