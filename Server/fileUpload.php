<?php
$uploads_dir = 'uploads/';
if(is_uploaded_file($_FILES['userfile']['tmp_name'])) {
	echo  "File ".  $_FILES['userfile']['name']  ." uploaded successfully to $uploads_dir/$dest.\n";
	$dest=  $_FILES['userfile'] ['name'];
	move_uploaded_file ($_FILES['userfile'] ['tmp_name'], "$uploads_dir/$dest");
} else {
	echo "Possible file upload attack: ";
	echo "filename '". $_FILES['userfile']['tmp_name'] . "'.";
	print_r($_FILES);
}
?>