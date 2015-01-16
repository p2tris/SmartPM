var map;
var myCenter=new google.maps.LatLng(41.89092091793598,12.503621578216553);
var markers = {};
var polys = {};

function initialize()
{
var mapProp = {
  center:myCenter,
  zoom:14,
  mapTypeId:google.maps.MapTypeId.ROADMAP
  };

  map = new google.maps.Map(document.getElementById("googleMap"),mapProp);

  google.maps.event.addListener(map, 'click', function(event) {
    placeMarker(event.latLng);
  });
}

var points = 0;
var fstLat = 0;
var fstLon = 0;
var sndLat = 0;
var sndLon = 0;

function placeMarker(location) {
	
	points += 1;
	if(points % 2 == 0) {
		if(fstLat > location.lat() && fstLon < location.lng()){
			sndLat = location.lat();
			sndLon = location.lng();
			
			if (!gridCheckBoxStatus()) {
				
				var marker = new google.maps.Marker({
					position: location,
					map: map,
				});
				
				markers[sndLat + '_' + sndLon + '_0'] = marker; // cache marker in markers object
				
				var squareCoords = [
					new google.maps.LatLng(fstLat, fstLon),
					new google.maps.LatLng(sndLat, fstLon),
					new google.maps.LatLng(sndLat, sndLon),
					new google.maps.LatLng(fstLat, sndLon)
				];
				
				// Construct the polygon.
				squareElem = new google.maps.Polygon({
					paths: squareCoords,
					strokeColor: '#FF0000',
					strokeOpacity: 0.8,
					strokeWeight: 3,
					fillColor: '#FF0000',
					fillOpacity: 0.35
				});
				
				polys[fstLat + '_' + fstLon + '_' + sndLat + '_' + sndLon] = squareElem;

				squareElem.setMap(map);
				
				addRow(fstLat, fstLon, sndLat, sndLon, "Location name", '0');
			} else {
				var colnr = prompt("Enter number of columns", 1);
				var rownr = prompt("Enter number of rows", 1);
				var name = prompt("Enter default name value", "Loc");
				var rowstep = (fstLat - sndLat) / rownr;
				var colstep = (sndLon - fstLon) / colnr;
				
				var marker = markers[fstLat + '_' + fstLon + '_0']; // find marker
			    removeMarker(marker, fstLat + '_' + fstLon + '_0'); // remove it
			    
				for (var i=0; i<rownr; i++){
					for (var j=0; j<colnr; j++){
						var loclattop = fstLat - (rowstep * i);
						var loclontop = fstLon + (colstep * j);
						var loclatbot = fstLat - (rowstep * (i+1));
						var loclonbot = fstLon + (colstep * (j+1));
						
						var marker = new google.maps.Marker({
							position: new google.maps.LatLng(loclattop,loclontop),
							map: map,
						});
						
						// i+1 and j+1 because somehow if 0, then first row marks will not get deleted
						markers[loclattop + '_' + loclontop + '_' + (i+1).toString() + (j+1).toString()] = marker; // cache marker in markers object
						
						var marker2 = new google.maps.Marker({
							position: new google.maps.LatLng(loclatbot,loclonbot),
							map: map,
						});
						
						markers[loclatbot + '_' + loclonbot + '_' + (i+1).toString() + (j+1).toString()] = marker2; // cache marker in markers object
						
						var squareCoords = [
							new google.maps.LatLng(loclattop, loclontop),
							new google.maps.LatLng(loclatbot, loclontop),
							new google.maps.LatLng(loclatbot, loclonbot),
							new google.maps.LatLng(loclattop, loclonbot)
						];
						
						// Construct the polygon.
						squareElem = new google.maps.Polygon({
							paths: squareCoords,
							strokeColor: '#FF0000',
							strokeOpacity: 0.8,
							strokeWeight: 3,
							fillColor: '#FF0000',
							fillOpacity: 0.35
						});
						
						polys[loclattop + '_' + loclontop + '_' + loclatbot + '_' + loclonbot] = squareElem;

						squareElem.setMap(map);
						
						addRow(loclattop, loclontop, loclatbot, loclonbot, name+i.toString()+j.toString(), (i+1).toString() + (j+1).toString());
					}
				}
			}
			
		} else {
			alert("Second point must be bottomright point of the box!");
			points -= 1;
		}
		
	} else {
		
		var marker = new google.maps.Marker({
			position: location,
			map: map,
		});
		fstLat = location.lat();
		fstLon = location.lng();
		markers[fstLat + '_' + fstLon + '_0'] = marker; // cache marker in markers object
	}
}
function addRow(topLat, topLon, botLat, botLon, name, ids) {
          
    var table = document.getElementById("LocList");
 
    var rowCount = table.rows.length;
    var row = table.insertRow(rowCount);
 
	row.insertCell(0).innerHTML= '<input type="button" value = "Delete" onClick="Javacsript:deleteRow(this,' + topLat + ',' + topLon + ',' + botLat + ',' + botLon + ',' + ids + ')" class="btn btn-danger btn-block" />';
    row.insertCell(1).innerHTML= topLat;
    row.insertCell(2).innerHTML= topLon;
	row.insertCell(3).innerHTML= botLat;
	row.insertCell(4).innerHTML= botLon;
	row.insertCell(5).innerHTML= "<div contenteditable>"+ name +"</div>";
 
}

function deleteRow(obj, lat1, lon1, lat2, lon2, ids) {
      
    var index = obj.parentNode.parentNode.rowIndex;
    var table = document.getElementById("LocList");
    table.deleteRow(index);
    
    var poly = polys[lat1 + '_' + lon1 + '_' + lat2 + '_' + lon2];
    removePoly(poly, lat2 + '_' + lon2 + '_' + lat2 + '_' + lon2); // remove it
    
	var marker1 = markers[lat1 + '_' + lon1 + '_' + ids]; // find marker
    removeMarker(marker1, lat1 + '_' + lon1 + '_' + ids); // remove it
	
	var marker2 = markers[lat2 + '_' + lon2 + '_' + ids]; // find marker
    removeMarker(marker2, lat2 + '_' + lon2 + '_' + ids); // remove it
}

function removePoly(poly, polyId){
	poly.setMap(null);
	delete polys[polyId];
}

function removeMarker(marker, markerId) {
    marker.setMap(null); // set markers setMap to null to remove it from map
    delete markers[markerId]; // delete marker instance from markers object
}

function printXml() {
	var table = document.getElementById("LocList");
	var xml = "";
	var arr = new Array();
	for (var i = 0, row; row = table.rows[i]; i++) {
		if(row.cells[1].textContent != "Top Latitude"){
			xml += "<data_value ";
			for (var j = 0, col; col = row.cells[j]; j++) {
				switch(j){
					case 1:
						xml += 'topLat="' + col.textContent + '" ';
						break;
					case 2:
						xml += 'topLon="' + col.textContent + '" ';
						break;
					case 3:
						xml += 'botLat="' + col.textContent + '" ';
						break;
					case 4:
						xml += 'botLon="' + col.textContent + '" ';
						break;
					case 5:
						xml += 'value="' + col.textContent + '" ';
						xml += '/>';
						var found = arr.some(function (el) {
						    return el === col.textContent;
						  });
						  if (!found) { 
							  arr.push(col.textContent);
						  } else {
							  alert("All the names must be unique!");
							  return;
						  }
						break;
					}
				}
		   }
		}  
	
	$.ajax({
        url: 'http://www.dis.uniroma1.it/~smartpm/webtool/GPSrulesPost.php',
        type: 'POST',
        data: "rules=" + encodeURI(xml),
        success: function (data) {
        	document.getElementById("resultXMLURL").innerHTML = data;
			window.prompt("Copy to clipboard: Ctrl+C, Enter", data);
                }
        });
		
	/*var xmlhttp = new XMLHttpRequest();

	xmlhttp.onreadystatechange = function() {
		if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
			var serverResponse = xmlhttp.responseText;
			document.getElementById("resultXMLURL").innerHTML = serverResponse;
			window.prompt("Copy to clipboard: Ctrl+C, Enter", serverResponse);
		}
	}

	xmlhttp.open("GET", "http://halapuu.host56.com/pn/GPSrulesPost.php?rules=" + encodeURI(xml), true);
	xmlhttp.send();
	*/	
}

function gridCheckBoxStatus()
{
  return document.getElementById("gridCheckBox").checked;
}

google.maps.event.addDomListener(window, 'load', initialize);