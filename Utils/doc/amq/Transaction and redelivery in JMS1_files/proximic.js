IDG = window.IDG || {};
IDG.Proximic = IDG.Proximic || {};
IDG.Proximic = function() {
	
	var dfp_called = false;
	var timer;
	var time_out = 200; // Will call dfp if timeout in millisec exceeded
	timer = window.setTimeout(
		function(){
			if (dfp_called == false) {
				dfp_called = true;
		        /*Timeout occurred, call DFP without data*/
		        //console.log("Call to proximic timed out.");
		        IDG.Proximic.addDataValuesToDfpTag();
		    }
		}, time_out);
	
	gotSegmentData = function(dataViaJsonp) {
		if (dfp_called == false) {
			dfp_called = true;
		    /*
		      Data has now been received and is now in dataViaJsonp
		      Sample dataViaJsonp (note: response format can be easily changed):
		      {"data":["300003","210000","240000","240001","240005","240006","3fy87w"]}		
		    */
	        window.clearTimeout(timer); /* kill the timeout timer */
		    // use the information within the 'data' key, if key isn't there - don't use the data
		    if ("data" in dataViaJsonp) {
		      /*call DFP with the data*/
		    	IDG.Proximic.addDataValuesToDfpTag(dataViaJsonp.data);
		     } else {
		       /*
		           Do not send error codes or unknown values to DFP
		           call DFP without using the data
		           console.log("error");
		       */
		        IDG.Proximic.addDataValuesToDfpTag();
		     }
		}
	}	

	return {
		addDataValuesToDfpTag: function(dataForDFP) {
			// set key:values to the DFP tag and, if this is the final step, call DFPs
			//These values need to be passed in custom criteria as below
			//.setTargeting("proximic", dataForDFP)
			//console.log(dataForDFP);
			if (typeof(dataForDFP) !== "undefined") {
				//call DFP with .setTargeting("proximic", dataForDFP)
				IDG.GPT.addTarget("proximic", dataForDFP);
			 } else {
				 //call DFP without .setTargeting("proximic", dataForDFP)
			 }
		}
	};
}();