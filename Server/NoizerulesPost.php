<?php

$timestamp = time();
$filename = 'Noizerules'.$timestamp.'.xml';


$f = fopen($filename,"a");
fprintf($f, '<?xml version="1.0" encoding="UTF-8"?><noize><lib name="Noize lib" url="http://halapuu.host56.com/pn/SmartPM_libNoize.apk" />');
fprintf($f, stripslashes($_POST['rules'] ));
fprintf($f, '</noize>');
fclose($f);

echo 'http://halapuu.host56.com/pn/'.$filename;
?>
