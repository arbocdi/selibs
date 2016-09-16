var googletag = googletag || {};
googletag.cmd = googletag.cmd || [];

// Used for answers (runs client side) and used for static itjobs header/footer
var adUnitIdsMap = {
    'cfo': '/8456/IDG.US_E_CFOWorld.com',
    'pcw': '/8456/IDG.US_CSMB_PCWorld.com',
    'cio': '/8456/IDG.US_E_CIO.com',
    'cit': '/8456/IDG.US_E_CITEworld.com',
    'cso': '/8456/IDG.US_E_CSOOnline.com', 
    'grb': '/8456/IDG.US_CSMB_Greenbot.com', 
    'itn': '/8456/IDG.US_E_ITNews.com', 
    'jvw': '/8456/IDG.US_E_JavaWorld.com', 
    'mw' : '/8456/IDG.US_CSMB_Macworld.com',
    'nww': '/8456/IDG.US_E_NetworkWorld.com',
    'th' : '/8456/IDG.US_CSMB_TechHive.com',
    'ans': '/8456/IDG.US_E_IDGAnswers.com',
    'ifw': '/8456/IDG.US_E_Infoworld.com',
    'itw': '/8456/IDG.US_E_ITworld.com',
    'ctw': '/8456/IDG.US_E_Computerworld.com'
};

//IDGAnswers.com needs this to overwrite the ad unit id
function getSrcCookie() {
    var nameEQ = "src=";
    var ca = document.cookie.split(';');
    for(var i=0;i < ca.length;i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1,c.length);
        if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
    }
    return null;
}

(function() {
	var gads = document.createElement("script");
	gads.async = true;
	gads.type = "text/javascript";
	var useSSL = "https:" == document.location.protocol;
	gads.src = (useSSL ? "https:" : "http:") + "//www.googletagservices.com/tag/js/gpt.js";
	var node =document.getElementsByTagName("script")[0];
	node.parentNode.insertBefore(gads, node);
})();

function isElementAboveTheFold(el) {
    var rect = el.getBoundingClientRect();
    return (
        rect.top <= (window.innerHeight || document.documentElement.clientHeight)
    );
}
function isElementInViewport (elementId) {
	var el = document.getElementById(elementId);
    if (typeof jQuery === "function" && el instanceof jQuery) {
        el = el[0];
    }

    var rect = el.getBoundingClientRect();

    return (
        rect.top >= 0 &&
        rect.left >= 0 &&
        rect.bottom <= (window.innerHeight || document.documentElement.clientHeight) && /*or $(window).height() */
        rect.right <= (window.innerWidth || document.documentElement.clientWidth) /*or $(window).width() */
    );
}

var IDG = IDG || {};
IDG.GPT = {
	unitName: "",
	localIsMoble: false,
	isSlideshow: false,
	otherAds:{},
	addOtherAd: function(key, value) {
		IDG.GPT.otherAds[key] = value;
	},
	getOtherAds: function(delimiter) {
		return IDG.GPT.getDelimiterData(IDG.GPT.otherAds, delimiter);
	},
	extIMUCounter: 1,
	addExtIMU: function() {
		IDG.GPT.extIMUCounter = IDG.GPT.extIMUCounter + 1;
		IDG.GPT.addOtherAd('imu' + IDG.GPT.extIMUCounter, true);
	},
	trackOmniture: function() {
		if (typeof(s) != 'undefined') {
			if (typeof(s.prop41) != 'undefined') {
				var currentSlotSet = IDG.GPT.getDisplayedAds(',') + ',' + IDG.GPT.getOtherAds(',');
				if (s.prop41 != currentSlotSet) {
					s.linkTrackEvents = s.events = "event38";
					s.linkTrackVars = "prop41,events";
					s.prop41 = currentSlotSet;
					s.event38 = currentSlotSet;
					s.tl(true, 'o', 'Article DRR IMUs:' + IDG.GPT.getOtherAds(','));
				}
			}
		}
	},
	IMUCounter: 1,
	getIMUSlotName: function() {
		return "imu" + IDG.GPT.IMUCounter;
	},
	getDelimiterData: function(dataSet, delimiter) {
		var delimiterData = "";
		for (var data in dataSet) {
			delimiterData += data + delimiter;
		}
		return delimiterData.slice(0, delimiterData.lastIndexOf(delimiter));
	},
	modules:{},
	addModule: function(key, value) {
		IDG.GPT.modules[key] = value;
	},
	getModules: function(delimiter) {
		return IDG.GPT.getDelimiterData(IDG.GPT.modules, delimiter);
	},
	displayedads:{},
	addDisplayedAd: function(key, value) {
		IDG.GPT.displayedads[key] = value;
	},
	getDisplayedAds: function(delimiter) {
		return IDG.GPT.getDelimiterData(IDG.GPT.displayedads, delimiter);
	},
	lazyloadedads:{},
	addLazyloadedAd: function(key, value) {
		IDG.GPT.lazyloadedads[key] = value;
	},
	getLazyloadedAds: function(delimiter) {
		return IDG.GPT.getDelimiterData(IDG.GPT.lazyloadedads, delimiter);
	},
	slots: {},
	slotsOutOfPage: {},
	slotDefinitions: {},
	refreshAd: function(slotName) {
		if(typeof(slotName) == "undefined") {
			googletag.pubads().refresh();
		}
		else {
			googletag.pubads().refresh([IDG.GPT.slotDefinitions[slotName]]);
		}
	},
	addSlot: function(key, value) {
		IDG.GPT.slots[key] = value;
	},
	addSlotOutOfPage: function(key, value) {
		IDG.GPT.addSlot(key, value);
		IDG.GPT.slotsOutOfPage[key] = value;
	},
	targets: {},
	addTarget: function(key, value) {
		IDG.GPT.targets[key] = value;
	},
	defineGoogleTagSlot: function(slotName, slotAdSize, adSlotRefreshes) {
		if (IDG.GPT.unitName != null && IDG.GPT.unitName != "" && slotName != "") {
			IDG.GPT.addDisplayedAd(slotName, "true");
			googletag.cmd.push(function() {
				IDG.GPT.slotDefinitions[slotName] = googletag.defineSlot(IDG.GPT.unitName, slotAdSize, slotName).addService(googletag.pubads());
				if (adSlotRefreshes) {
					setInterval(function() {
						if (isElementInViewport(slotName)){
							googletag.pubads().refresh([IDG.GPT.slotDefinitions[slotName]])
						}
					}, 15000);
				}
			});
		}
	},
	displayGoogleTagSlot: function(slotName) {
		if (IDG.GPT.unitName != null && IDG.GPT.unitName != "" && slotName != "") {
		googletag.cmd.push(function() {
			googletag.display(slotName);
		});
		}
	},
	defineAllGoogleTagSlots: function() {
		if (IDG.GPT.unitName != null && IDG.GPT.unitName != "") {
			googletag.cmd.push(function() {
				for (var slotName in IDG.GPT.slots) {
					if (IDG.GPT.slots[slotName] != null && IDG.GPT.slots[slotName] != "") {
						if (IDG.GPT.slotsOutOfPage[slotName] !== "undefined" && IDG.GPT.slotsOutOfPage[slotName]) {
							IDG.GPT.slotDefinitions[slotName] = googletag.defineOutOfPageSlot(IDG.GPT.unitName, slotName).addService(googletag.pubads()).setTargeting("pos", slotName);
						}
						else {
							IDG.GPT.slotDefinitions[slotName] = googletag.defineSlot(IDG.GPT.unitName, IDG.GPT.slots[slotName], slotName).addService(googletag.pubads());
						}
					}
				}
			});
		}
	},
	setAllGoogleTagTargetings: function() {
		googletag.cmd.push(function() {
			for (var targetName in IDG.GPT.targets) {
				if (IDG.GPT.targets[targetName] != null && IDG.GPT.targets[targetName] != "") {
					googletag.pubads().setTargeting(targetName, IDG.GPT.targets[targetName]);
				}
			}
		});
	},
	initGoogleTagService: function() {
		googletag.cmd.push(function() {
			//googletag.pubads().enableSingleRequest(); csmb had this ON
			googletag.pubads().enableAsyncRendering();
			googletag.pubads().collapseEmptyDivs(); // csmb did not have this
			//googletag.pubads().disableInitialLoad(); csmb had this ON //defer loading of the ads until after page load
			googletag.enableServices();
		});
	},
	getParameterValue: function(paramString, paramName) {
		var paramValue = "";
		if (paramString && paramString != 'undefined') {
			var searchChars = "[\\?&]"+paramName+"=([^&#]*)";
			var regularExp = new RegExp(searchChars);
			var searchResults = regularExp.exec(paramString);
			if (searchResults != null && searchResults.length >= 1) 
				paramValue = searchResults[1];
		}
		return paramValue;
	},
	getReferrerDomain: function() {
		var referrerDomain = "";
		if (document.referrer && document.referrer != 'undefined') {
			var referrerString = document.referrer;
			referrerDomain = referrerString.match(/^http[s]?:\/\/[a-zA-Z0-9-_\.]+[\:]?[0-9]*?\//);
		}
		return String(referrerDomain);
	},
	getSearchEngineQuery: function() {
		var sQuery = "";
		var referrerDomain = IDG.GPT.getReferrerDomain();
		if (referrerDomain != "") {
			var documentReferrer = document.referrer;
			if (referrerDomain.indexOf("google.com") != -1) {
				documentReferrer = documentReferrer.replace("#q=", "?q=");
				sQuery = IDG.GPT.getParameterValue(documentReferrer, "q");
			}
			else if (referrerDomain.indexOf("bing.com") != -1)
				sQuery = IDG.GPT.getParameterValue(documentReferrer, "q");
			else if (referrerDomain.indexOf("yahoo.com") != -1)
				sQuery = IDG.GPT.getParameterValue(documentReferrer, "p");
			else if (referrerDomain.indexOf("nww.com") != -1) {
				sQuery = IDG.GPT.getParameterValue(documentReferrer, "hpg1");
			}
		}
		sQuery = sQuery.replace("+", " ");
		return sQuery;
	},
	userAgentValue: function() {
		var browser = "";
		if (/(iPad|iPhone)/.test(navigator.userAgent)) {
			browser = RegExp.$1;
		}
		return browser;
	},
	setIDGExtraTargetings: function() {
		try {
			IDG.GPT.addTarget("referrer", IDG.GPT.getReferrerDomain());
			IDG.GPT.addTarget("browser", navigator.userAgent);
			IDG.GPT.addTarget("squery", IDG.GPT.getSearchEngineQuery());
			IDG.GPT.addTarget("nlsource", IDG.GPT.getParameterValue(window.location.href, "source"));

			if (document.cookie.indexOf("nsdr") > -1)
				IDG.GPT.addTarget("insiderauth", "yes");
			else
				IDG.GPT.addTarget("insiderauth", "no");

			IDG.GPT.addTarget("env", IDG.GPT.getParameterValue(window.location.href, "env"));

			if (typeof(isMobile) != "undefined")
				IDG.GPT.localIsMoble = isMobile;
			if (IDG.GPT.localIsMoble) {
				IDG.GPT.addTarget("mobile", "true");
			}

			if (typeof(dogfish_type) != "undefined" && dogfish_type != "(none)") {
				IDG.GPT.addTarget("type", dogfish_type);
			}
		} catch(e) {
			//console.log(e);
		}
	},
	getQsVal: function(name) {
    	if (window.location.search != "") {
    		var qs = window.location.search.substring(1);
    		var pairs = qs.split("&");
    		for (var i=0;i<pairs.length;i++) {
    			var pair = pairs[i].split("=");
    			if (pair[0] == name) {
    				return pair[1];
    				break;
    			}
    		}
    	}
    	return "";
    },
	getAdUnitIdBySrc: function(src) {
		var rtnValue = adUnitIdsMap["ans"];
		if (src) {
			var adUnitIdBySrc = adUnitIdsMap[src];
			if (adUnitIdBySrc) rtnValue = adUnitIdBySrc;	
		}
		return rtnValue;
	},
	isValidSource: function(src) {
		var rtnValue = false;
		if (src) {
			if (adUnitIdsMap[src]) rtnValue = true;	
		}
		return rtnValue;
	}
};

// IDG.GPT.dfpAdManager Keeps track of what ad sizes already present on this page
// seperate counters for Above and Below the Fold
IDG.GPT.dfpAdManager = {
    "constructor" : IDG.GPT.dfpAdManager,
    "init" : function(){
        this.adsizes = {};
        this.adsizes.atf = {};
        this.adsizes.btf = {};
    },
    "push" : function(key, fold){ // add to adsizes, increment if already there
        if(this.has(key, fold))
        {
            this.adsizes[fold + ''][key + ''] = this.adsizes[fold + ''][key + ''] + 1;
        }
        else
        {
            this.adsizes[fold + ''][key + ''] = 1;
        }
    },
    "has" : function(key,fold){
        if(this.adsizes[fold+''].hasOwnProperty(key))
        {
            return true;
        }
        return false;
    },
    "get" : function(key, fold) {
        return this.adsizes[fold][key];
    },
    "all" : function(fold) {
        if(fold == 'atf' || fold == 'btf')
        {
            return this.adsizes[fold];
        }
        return this.adsizes;
    },
    "getCounter" : function(sizes, fold){
        // check how many sizes already there
        var counter = 1;
        for(var i in sizes) {
            // getWidth() and getHeight() are methods on the object return from the googletag.pubads()
            var key = sizes[i].getWidth() + "x" + sizes[i].getHeight();
            // add sizes
            this.push(key, fold);
            // try for a count
            var c = this.get(key, fold);
            // ads can have multiple sizes, 
            // irrespective of their order on the page
            // avoid resetting counter to a lower number
            if(c > counter){
                counter = c;
            }
        }
        return counter;
    }
};

/* Responsive ads  */
(function($){
    $.fn.responsiveAd = function(options) { 
      var opts = $.extend({
          "screenSize": 'all',
          "counter" : 1
      },options);
      
      this.placeAds = function(screenSize) {
        var id = this.attr('id');
        
        var placeAd = true;
   
        var debugmsg = 'placeAd:' + placeAd + ", screenSize:" + screenSize + ", id:" + id;
        if (placeAd == true) {
            var targetElement = document.getElementById(id);
            if (targetElement) {
            	var fold = 'btf'; // defaults to BelowTheFold
                if (opts.forceFold != undefined) { // can force a fold by setting opts.forceFold
                	fold = opts.forceFold;
                }
                else if (true === isElementAboveTheFold(targetElement)){
                    fold = 'atf';
                }

                // Grab reference to defined slot inside func scope sent to googletag.cmd.push
                googletag.cmd.push(function() { 
                	var currentAd = IDG.GPT.slotDefinitions[id]; 
                	if (currentAd != undefined) {
                		var counter = IDG.GPT.dfpAdManager.getCounter(currentAd.getSizes(),fold);
                		//console.log(id + ' ' + fold + counter);
                		if (id != "gpt-skin" || id != "gpt-overlay") {
                			currentAd.setTargeting("pos", fold + counter); 
                		}
                		googletag.display(id);
                	}
                	else {
                		//console.log("currentAd was undefined so no slot Definition for " + id);
                	}
                });
                
            } else {
                console.log('Looking for ' + id + ', but no element found');
            }
        }
      }
      return this.placeAds(opts.screenSize);
    };
})(jQuery);
 
