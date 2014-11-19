<?php

$timestamp = time();
$filename = 'gpsrules/GPSrules'.$timestamp.'.xml';


$f = fopen($filename,"a");
fprintf($f, '<?xml version="1.0" encoding="UTF-8"?><location_type><lib name="GPS lib" url="http://smartpm.cloudapp.net/SmartPM_libGPS.apk" />');
fprintf($f, stripslashes($_POST['rules'] ));
fprintf($f, '</location_type>');
fclose($f);

echo 'http://smartpm.cloudapp.net/'.$filename;
?>
