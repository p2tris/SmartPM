var createSlider = function ($slider, values) {
	$slider.slider({
		min: 0,
		max: 194,
		step: 2,
		values: values,
		slide: function(event, ui) {
			// delete the table
			document.getElementById("volList").innerHTML = '';
			sliderValues = [0];
			for (var i = 0; i < ui.values.length; ++i) {
//				$("input.sliderValue[data-index=" + i + "]").val(ui.values[i]);
				// create new table
				sliderValues[sliderValues.length] = ui.values[i];
			}
			sliderValues[sliderValues.length] = 194;
			sliderValues.sort(function(a, b){return a-b});
			for (var i = 1; i < sliderValues.length; i++) {
				addRow(sliderValues[i-1], sliderValues[i], "value"+i);
			}
		}
	});        
};

$(document).ready(function() {
	correctValue = false;
	while (correctValue == false) {
		var nrValues = parseInt(prompt("Enter number of sliders", 3));
		if (nrValues < 1 || nrValues > 12 || isNaN(nrValues)) {
			alert("Numebr of sliders must be between 1 and 12!");
		} else {
			correctValue = true;
		}
	}
	
	var values = [],
		$slider = $("#slider");
			
	var s = '';
	for (var int = 0; int < nrValues; int++) {
		values[values.length] = 10*(int+1);
		s += ('<input type="hidden" class="sliderValue" data-index="'+ int +'" value="'+ 10*(int+1) +'" />');
	}
	document.getElementById("sliderElements").innerHTML = s;
	
	createSlider($slider, values);
	
//	$("input.sliderValue").change(function() {
//		var $this = $(this);
//		$("#slider").slider("values", $this.data("index"), $this.val());
//	});
});

function addRow(low, high, name) {
    
    var table = document.getElementById("volList");
 
    var rowCount = table.rows.length;
    var row = table.insertRow(rowCount);
 
	row.insertCell(0).innerHTML= low;
    row.insertCell(1).innerHTML= high;
    row.insertCell(2).innerHTML= "<div contenteditable>"+ name +"</div>";
 
}

function printXml() {
	var table = document.getElementById("volList");
	var xml = "";
	var arr = new Array();
	for (var i = 0, row; row = table.rows[i]; i++) {
		if(row.cells[0].textContent != "Low"){
			xml += "<data_value ";
			for (var j = 0, col; col = row.cells[j]; j++) {
				switch(j){
					case 0:
						xml += 'low="' + col.textContent + '" ';
						break;
					case 1:
						xml += 'high="' + col.textContent + '" ';
						break;
					case 2:
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
						  if(col.textContent[0].toUpperCase() == col.textContent[0])
						  {
						     alert('First character can not be upper case.');  
						     return;
						  }
						  if(col.textContent.indexOf(' ') >= 0){
							  alert('No white spaces allowed in values.');
							  return;
						  }
						break;
					}
				}
		   }
		}  
	
	$.ajax({
        url: 'http://smartpm.cloudapp.net/NoizerulesPost.php',
        type: 'POST',
        data: "rules=" + encodeURI(xml),
        success: function (data) {
        	document.getElementById("resultXMLURL").innerHTML = data;
			window.prompt("Copy to clipboard: Ctrl+C, Enter", data);
                }
        });
		
}