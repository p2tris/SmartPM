<?php

$timestamp = time();
$filename = 'noiserules/Noizerules'.$timestamp.'.xml';


$f = fopen($filename,"a");
fprintf($f, '<?xml version="1.0" encoding="UTF-8"?><noize_type><lib name="Noize lib" url="http://www.dis.uniroma1.it/~smartpm/webtool/SmartPM_libNoize.apk" />');
fprintf($f, stripslashes($_POST['rules'] ));
fprintf($f, '</noize_type>');
fclose($f);

echo 'http://www.dis.uniroma1.it/~smartpm/webtool/'.$filename;
?>
