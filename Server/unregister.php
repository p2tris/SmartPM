<?php
require_once('loader.php');

// return json response 
$json = array();

$nameUser  = $_POST["name"];

/**
 * Unregistering a user device in database
 */
if (isset($nameUser)) {
    
	$result = mysql_query("DELETE FROM gcm_users WHERE name = '$nameUser' OR gcm_regid = '$nameUser'");

    echo $result;
} else {
    // user details not found
}
?>