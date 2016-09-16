/*global GRAPP */

if (!window.GRAPP) {
    window.GRAPP = function () {
        'use strict';
        throw new TypeError('Illegal exec');
    };
}

window.GRAPP.require =  window.GRAPP.hasOwnProperty('require') ? window.GRAPP.require : (function (win, doc) {
    'use strict';
    var slice = Array.prototype.slice,
        data = [],
        head = doc.head || doc.getElementsByTagName('head')[0],
        loc = location,
        origin = loc.origin || loc.protocol + '//' + loc.host,
        firstTime = true,
        require;

    data.setItem = function (value) {
        var ix,
            src = value.src.replace(origin, '');

        ix = this.push(src);

        ix -= 1;

        value.index = ix;
        this['_' + ix] = value;
        return ix;
    };
    data.getItem = function (src) {
        var ix;

        src = src.replace(origin, '');

        ix = this.indexOf(src);

        if (-1 === ix) {
            return;
        }

        return this['_' + ix];
    };

    function getFileType(src) {
        var ext;

        if (!src || 'string' !== typeof src) {
            return '';
        }

        src = src.replace(/\?.*$/, '');
        ext = /[^.]+$/i.exec(src)[0];
        switch (ext) {
            case 'js':
                return 'javascript';
            default:
                return 'css';
        }
    }

    function applyCallbacks(cb) {
        var fn = cb.shift();

        while (fn) {
            if ('function' === typeof fn) {
                fn();
            }
            fn = cb.shift();
        }
    }

    function loadFile(reg) {
        if (reg.loaded) {
            applyCallbacks(reg.callbacks);
            return;
        }

        if (loadFile[reg.type]) {
            loadFile[reg.type](reg);
        }
    }

    loadFile.javascript = function (reg) {
        var s;

        s = doc.createElement("script");
        s.async = true;

        s[(null === s.onreadystatechange ? 'onreadystatechange' : 'onload')] = function () {
            if (this.readyState && !(/loaded|complete/.test(this.readyState))) {
                return;
            }

            reg.loaded = true;
            applyCallbacks(reg.callbacks);
        };
        s.onerror = function () {
            applyCallbacks(reg.callbacks);
        };

        s.setAttribute("type", "text/javascript");
        s.setAttribute("src", reg.src);

        head.appendChild(s);
    };
    loadFile.css = function (reg) {
        var s = doc.createElement("link");

        s.setAttribute("type", "text/css");
        s.setAttribute("rel", "stylesheet");
        s.setAttribute("href", reg.src);

        if (data.lastCSS) {
            data.lastCSS.parentNode.insertBefore(s, data.lastCSS.nextSibling);
        } else if (data.firstCSS) {
            data.firstCSS = data.firstCSS.parentNode.insertBefore(s, data.firstCSS);
        } else {
            head.appendChild(s);
        }

        data.lastCSS = s;
        reg.loaded = true;

        applyCallbacks(reg.callbacks);
    };

    function iterator(list, callback) {
        var ln = list.length,
            iter = function (ix) {
                var el;
                if (ix === ln) {
                    /*log('[1]');
                     if (callback) {
                     log('[2]');
                     callback();
                     }*/
                    return;
                }

                el = list[ix];

                if ('object' === typeof el) {
                    if (el.defined && win[el.defined]) {
                        return;
                    }
                }

                require((el && (el.src || el)), function () {
                    if (el.callback) {
                        el.callback();
                    }

                    ix += 1;

                    iter(ix);

                    if (ix === ln) {
                        if (callback) {
                            callback.call(callback);
                        }
                    }
                });
            };

        return iter;
    }

    require = function () {
        var args = slice.call(arguments),
            src = args[0],
            callback = args[1],
            reg;

        if (firstTime) {
            require.register(document.scripts || document.querySelectorAll('script'));
            require.register(document.styleSheets || document.querySelectorAll('link'));

            firstTime = false;
        }

        if (!src) {
            return;
        }

        if ('function' !== typeof callback) {
            callback = null;
        }

        if ('string' === typeof src) {
            reg = data.getItem(src);

            if (!reg) {
                reg = {
                    loaded: false,
                    src: src,
                    origin: src.replace(origin, ''),
                    callbacks: [],
                    type: getFileType(src)
                };

                if (callback) {
                    reg.callbacks.push(callback);
                }

                data.setItem(reg);
                loadFile(reg);
                return;
            }

            if (reg.loaded) {
                if (callback) {
                    callback();
                }
                return;
            }

            if (callback) {
                reg.callbacks.push(callback);
            }
            return;
        }
        iterator(src, callback)(0);
    };

    require.register = (function (cycle) {
        function regFile(scripts) {
            var ln = scripts.length,
                i;

            if (!ln) {
                return;
            }

            for (i = 0; i < ln; i += 1) {
                cycle((scripts[i].src || ('stylesheet' === scripts[i].rel && scripts[i].href) || '').replace(/\?.*$/, ''));
            }

            if (/link/i.test(scripts[ln - 1])) {
                data.firstCSS = scripts[0];
                data.lastCSS = scripts[ln - 1];
            } else {
                data.firstJS = scripts[0];
                data.lastJS = scripts[ln - 1];
            }
        }

        //regFile(head.getElementsByTagName('script'));
        //regFile(head.getElementsByTagName('link'));

        return regFile;
    }(function (src) {
        if (!src || 'string' !== typeof src) {
            return;
        }

        data.setItem({
            loaded: true,
            src: src,
            'static': true,
            type: getFileType(src)
        });
    }));

    window.DATAFILE = data;

    return require;
}(window, window.document));

(function WF2Bootstrap(DATA) {
    'use strict';

    var getLoaderIFrame = (function () {

        var iframe = createIFrame(),
            loader = createLoader(),
            sheet = null; // create in iframe at load event

        function createIFrame() {
            var el = document.createElement('iframe');
            el.style.width = DATA.displayType === 'embedded' ? DATA.width + 'px' : '0px';
            el.style.height = DATA.displayType === 'embedded' ? DATA.height + 'px' : '0px';
            el.style.border = 'none';
            el.setAttribute('sandbox', 'allow-same-origin allow-forms allow-scripts');
            el.setAttribute('scrolling', 'no');
            el.setAttribute('allowTransparency', 'true');
            return el;
        }

        function createLoader() {
            var loaderEl = document.createElement('div');
            loaderEl.id = 'grwf2-preloader';
            loaderEl.innerHTML =
                '<div class="grwf2-preloader">' +
                '<div class="grwf2-preloader-element grwf2-preloader-element"></div>' +
                '<div class="grwf2-preloader-element2 grwf2-preloader-element"></div>' +
                '<div class="grwf2-preloader-element3 grwf2-preloader-element"></div>' +
                '<div class="grwf2-preloader-element4 grwf2-preloader-element"></div>' +
                '<div class="grwf2-preloader-element5 grwf2-preloader-element"></div>' +
                '<div class="grwf2-preloader-element6 grwf2-preloader-element"></div>' +
                '<div class="grwf2-preloader-element7 grwf2-preloader-element"></div>' +
                '<div class="grwf2-preloader-element8 grwf2-preloader-element"></div>' +
                '<div class="grwf2-preloader-element9 grwf2-preloader-element"></div>' +
                '<div class="grwf2-preloader-element10 grwf2-preloader-element"></div>' +
                '<div class="grwf2-preloader-element11 grwf2-preloader-element"></div>' +
                '<div class="grwf2-preloader-element12 grwf2-preloader-element"></div>' +
                '</div>';
            return loaderEl;
        }

        function createIFrameStylesheet() {
            var doc = iframe.contentWindow.document,
                style = doc.createElement("style");
            style.appendChild(doc.createTextNode(""));
            doc.head.appendChild(style);
            return style.sheet;
        }

        function addRulesToStylesheet(rules) {
            var selector,
                styles;

            for (selector in rules) {
                if (rules.hasOwnProperty(selector)) {
                    styles = rules[selector];
                    try {
                        if (sheet.insertRule) {
                            sheet.insertRule(selector + " {" + styles + "}", sheet.cssRules.length);
                        } else if (sheet.addRule) {
                            sheet.addRule(selector, styles);
                        }
                    } catch (e) {
                        // Some browsers may throw an error on unknown selector
                    }
                }
            }
        }

        function setLoaderStyles() {

            var backgroundColor = DATA.backgroundCssRules && DATA.backgroundCssRules.backgroundColor || 'rgba(0,0,0,0.07)',
                backgroundImage = DATA.backgroundCssRules && DATA.backgroundCssRules.backgroundImage || 'none',
                backgroundPosition = DATA.backgroundCssRules && DATA.backgroundCssRules.backgroundPosition || '0% 0%',
                backgroundRepeat = DATA.backgroundCssRules && DATA.backgroundCssRules.backgroundRepeat || 'repeat',
                backgroundSize = DATA.backgroundCssRules && DATA.backgroundCssRules.backgroundSize || 'cover',

                rules = {
                    "body": "margin: 0; padding: 0;",
                    "@-webkit-keyframes grwf2fadedelay": "0%, 39%, 100% { opacity: 0; } 40% { opacity: 1; }",
                    "@-moz-keyframes grwf2fadedelay": "0%, 39%, 100% { opacity: 0; } 40% { opacity: 1; }",
                    "@-o-keyframes grwf2fadedelay": "0%, 39%, 100% { opacity: 0; } 40% { opacity: 1; }",
                    "@keyframes grwf2fadedelay": "0%, 39%, 100% { opacity: 0; } 40% { opacity: 1; }",
                    "#grwf2-preloader":
                        'display: block !important;' +
                        'position: relative !important;' +
                        'top: 0 !important;' +
                        'left: 0 !important;' +
                        'width: 100% !important;' +
                        'height: 100% !important;' +
                        'z-index: 9999 !important;' +
                        'background-color: ' + backgroundColor + ' !important;' +
                        'background-image: ' + backgroundImage + ' !important;' +
                        'background-position: ' + backgroundPosition + ' !important;' +
                        'background-repeat: ' + backgroundRepeat + ' !important;' +
                        'background-size: ' + backgroundSize + ' !important;',
                    ".grwf2-preloader":
                        'left: 50% !important;' +
                        'top: 50% !important;' +
                        'width: 40px !important;' +
                        'height: 40px !important;' +
                        'position: relative !important;',
                    ".grwf2-preloader .grwf2-preloader-element":
                        'width: 100% !important;' +
                        'height: 100% !important;' +
                        'margin-left: -20px !important;' +
                        'margin-top: -20px !important;' +
                        'position: absolute !important;',
                    ".grwf2-preloader .grwf2-preloader-element:before":
                        'content: \'\' !important;' +
                        'display: block !important;' +
                        'margin: 0 auto !important;' +
                        'width: 15% !important;' +
                        'height: 15% !important;' +
                        'background-color: #00afec !important;' +
                        'border-radius: 100% !important;' +
                        '-webkit-animation: grwf2fadedelay 1.2s infinite ease-in-out both !important;' +
                        '-moz-animation: grwf2fadedelay 1.2s infinite ease-in-out both !important;' +
                        '-o-animation: grwf2fadedelay 1.2s infinite ease-in-out both !important;' +
                        'animation: grwf2fadedelay 1.2s infinite ease-in-out both !important;',
                    ".grwf2-preloader .grwf2-preloader-element2":
                        '-webkit-transform: rotate(30deg) !important;' +
                        '-moz-transform: rotate(30deg) !important;' +
                        '-ms-transform: rotate(30deg) !important;' +
                        '-o-transform: rotate(30deg) !important;' +
                        'transform: rotate(30deg) !important;',
                    ".grwf2-preloader .grwf2-preloader-element3":
                        '-webkit-transform: rotate(60deg) !important;' +
                        '-moz-transform: rotate(60deg) !important;' +
                        '-ms-transform: rotate(60deg) !important;' +
                        '-o-transform: rotate(60deg) !important;' +
                        'transform: rotate(60deg) !important;',
                    ".grwf2-preloader .grwf2-preloader-element4":
                        '-webkit-transform: rotate(90deg) !important;' +
                        '-moz-transform: rotate(90deg) !important;' +
                        '-ms-transform: rotate(90deg) !important;' +
                        '-o-transform: rotate(90deg) !important;' +
                        'transform: rotate(90deg) !important;',
                    ".grwf2-preloader .grwf2-preloader-element5":
                        '-webkit-transform: rotate(120deg) !important;' +
                        '-moz-transform: rotate(120deg) !important;' +
                        '-ms-transform: rotate(120deg) !important;' +
                        '-o-transform: rotate(120deg) !important;' +
                        'transform: rotate(120deg) !important;',
                    ".grwf2-preloader .grwf2-preloader-element6":
                        '-webkit-transform: rotate(150deg) !important;' +
                        '-moz-transform: rotate(150deg) !important;' +
                        '-ms-transform: rotate(150deg) !important;' +
                        '-o-transform: rotate(150deg) !important;' +
                        'transform: rotate(150deg) !important;',
                    ".grwf2-preloader .grwf2-preloader-element7":
                        '-webkit-transform: rotate(180deg) !important;' +
                        '-moz-transform: rotate(180deg) !important;' +
                        '-ms-transform: rotate(180deg) !important;' +
                        '-o-transform: rotate(180deg) !important;' +
                        'transform: rotate(180deg) !important;',
                    ".grwf2-preloader .grwf2-preloader-element8":
                        '-webkit-transform: rotate(210deg) !important;' +
                        '-moz-transform: rotate(210deg) !important;' +
                        '-ms-transform: rotate(210deg) !important;' +
                        '-o-transform: rotate(210deg) !important;' +
                        'transform: rotate(210deg) !important;',
                    ".grwf2-preloader .grwf2-preloader-element9":
                        '-webkit-transform: rotate(240deg) !important;' +
                        '-moz-transform: rotate(240deg) !important;' +
                        '-ms-transform: rotate(240deg) !important;' +
                        '-o-transform: rotate(240deg) !important;' +
                        'transform: rotate(240deg) !important;',
                    ".grwf2-preloader .grwf2-preloader-element10":
                        '-webkit-transform: rotate(270deg) !important;' +
                        '-moz-transform: rotate(270deg) !important;' +
                        '-ms-transform: rotate(270deg) !important;' +
                        '-o-transform: rotate(270deg) !important;' +
                        'transform: rotate(270deg) !important;',
                    ".grwf2-preloader .grwf2-preloader-element11":
                        '-webkit-transform: rotate(300deg) !important;' +
                        '-moz-transform: rotate(300deg) !important;' +
                        '-ms-transform: rotate(300deg) !important;' +
                        '-o-transform: rotate(300deg) !important;' +
                        'transform: rotate(300deg) !important;',
                    ".grwf2-preloader .grwf2-preloader-element12":
                        '-webkit-transform: rotate(330deg) !important;' +
                        '-moz-transform: rotate(330deg) !important;' +
                        '-ms-transform: rotate(330deg) !important;' +
                        '-o-transform: rotate(330deg) !important;' +
                        'transform: rotate(330deg) !important;',
                    ".grwf2-preloader .grwf2-preloader-element2:before":
                        '-webkit-animation-delay: -1.1s !important;' +
                        '-moz-animation-delay: -1.1s !important;' +
                        '-ms-animation-delay: -1.1s !important;' +
                        '-o-animation-delay: -1.1s !important;' +
                        'animation-delay: -1.1s !important;',
                    ".grwf2-preloader .grwf2-preloader-element3:before":
                        '-webkit-animation-delay: -1s !important;' +
                        '-moz-animation-delay: -1s !important;' +
                        '-ms-animation-delay: -1s !important;' +
                        '-o-animation-delay: -1s !important;' +
                        'animation-delay: -1s !important;',
                    ".grwf2-preloader .grwf2-preloader-element4:before":
                        '-webkit-animation-delay: -0.9s !important;' +
                        '-moz-animation-delay: -0.9s !important;' +
                        '-ms-animation-delay: -0.9s !important;' +
                        '-o-animation-delay: -0.9s !important;' +
                        'animation-delay: -0.9s !important;',
                    ".grwf2-preloader .grwf2-preloader-element5:before":
                        '-webkit-animation-delay: -0.8s !important;' +
                        '-moz-animation-delay: -0.8s !important;' +
                        '-ms-animation-delay: -0.8s !important;' +
                        '-o-animation-delay: -0.8s !important;' +
                        'animation-delay: -0.8s !important;',
                    ".grwf2-preloader .grwf2-preloader-element6:before":
                        '-webkit-animation-delay: -0.7s !important;' +
                        '-moz-animation-delay: -0.7s !important;' +
                        '-ms-animation-delay: -0.7s !important;' +
                        '-o-animation-delay: -0.7s !important;' +
                        'animation-delay: -0.7s !important;',
                    ".grwf2-preloader .grwf2-preloader-element7:before":
                        '-webkit-animation-delay: -0.6s !important;' +
                        '-moz-animation-delay: -0.6s !important;' +
                        '-ms-animation-delay: -0.6s !important;' +
                        '-o-animation-delay: -0.6s !important;' +
                        'animation-delay: -0.6s !important;',
                    ".grwf2-preloader .grwf2-preloader-element8:before":
                        '-webkit-animation-delay: -0.5s !important;' +
                        '-moz-animation-delay: -0.5s !important;' +
                        '-ms-animation-delay: -0.5s !important;' +
                        '-o-animation-delay: -0.5s !important;' +
                        'animation-delay: -0.5s !important;',
                    ".grwf2-preloader .grwf2-preloader-element9:before":
                        '-webkit-animation-delay: -0.4s !important;' +
                        '-moz-animation-delay: -0.4s !important;' +
                        '-ms-animation-delay: -0.4s !important;' +
                        '-o-animation-delay: -0.4s !important;' +
                        'animation-delay: -0.4s !important;',
                    ".grwf2-preloader .grwf2-preloader-element10:before":
                        '-webkit-animation-delay: -0.3s !important;' +
                        '-moz-animation-delay: -0.3s !important;' +
                        '-ms-animation-delay: -0.3s !important;' +
                        '-o-animation-delay: -0.3s !important;' +
                        'animation-delay: -0.3s !important;',
                    ".grwf2-preloader .grwf2-preloader-element11:before":
                        '-webkit-animation-delay: -0.2s !important;' +
                        '-moz-animation-delay: -0.2s !important;' +
                        '-ms-animation-delay: -0.2s !important;' +
                        '-o-animation-delay: -0.2s !important;' +
                        'animation-delay: -0.2s !important;',
                    ".grwf2-preloader .grwf2-preloader-element12:before":
                        '-webkit-animation-delay: -0.1s !important;' +
                        '-moz-animation-delay: -0.1s !important;' +
                        '-ms-animation-delay: -0.1s !important;' +
                        '-o-animation-delay: -0.1s !important;' +
                        'animation-delay: -0.1s !important;'
                };

            addRulesToStylesheet(rules);
        }

        return function () {
            iframe.onload = function () {
                sheet = createIFrameStylesheet();
                setLoaderStyles();
                iframe.contentWindow.document.body.appendChild(loader);
            };

            return iframe;
        };

    })();

    var LOCATION = (function (url) {
            var aEl = document.createElement('a'),
                locationKeys = ['hash', 'host', 'hostname', 'href', 'origin', 'pathname', 'port', 'protocol', 'search'],
                ret = {},
                i,
                currentKey;

            aEl.href = url;

            for (i = 0; i < locationKeys.length; i++) {
                currentKey = locationKeys[i];
                ret[currentKey] = aEl[currentKey];
            }

            ret.search = (function (s) {
                var search = {};

                s.replace(/([^?=&]+)(=([^&]*))?/g, function ($0, $1, $2, $3) {
                    search[$1] = $3;
                });

                return search;
            }(aEl.search));

            return ret;
        }(DATA.url)),
        currentScript = (function () {
            var scripts = document.getElementsByTagName('script'),
                foundScript;
            for (var i = scripts.length - 1; i >= 0; i--) {
                if ((scripts[i].src.indexOf('view_webform_v2.js?') && (scripts[i].src.indexOf('webforms_id=' + DATA.id))) > -1) {
                    foundScript = scripts[i];
                    break;
                }
            }
            return foundScript || scripts[scripts.length - 1];
        }()), // WebForm container will be inserted as sibling of this element
        loaderIFrame; // Needed for loader on embedded WebForms

    if (currentScript) {
        currentScript.setAttribute('data-wf2url', DATA.url);
        loaderIFrame = getLoaderIFrame();
        if ("embedded" === DATA.displayType) {
            currentScript.parentNode.insertBefore(loaderIFrame, currentScript.nextSibling);
        }
    }

    GRAPP.require(LOCATION.protocol + '//' + LOCATION.host + '/javascripts/core/js/pages/panel/webform2/public/gr.js', function () {
        GRAPP.require(LOCATION.protocol + '//' + LOCATION.host + '/javascripts/core/js/pages/panel/webform2/public/gr_wf_v2.js', function () {
            if (!GRAPP || !GRAPP.WF2Register) {
                return;
            }

            GRAPP.WF2Register({
                url: DATA.url,
                displayType: DATA.displayType,
                element: currentScript,
                loaderIFrame: loaderIFrame
            });

            GRAPP.require(LOCATION.protocol + '//' + LOCATION.host + '/stylesheets/core/pages/webFormV2/public/gr_wf_v2.css');
        });
    });

}(JSON.parse('{"id":"3488401","url":"https:\/\/app.getresponse.com\/site2\/codejavanet?u=ml0B&webforms_id=3488401","displayType":"embedded","width":750,"height":132,"backgroundCssRules":{"backgroundColor":"rgb(16, 45, 57)","backgroundImage":"none","backgroundPosition":"0% 0%","backgroundRepeat":"repeat","backgroundSize":"auto auto","gradient":"none","image":"url(\\\"https:\/\/app.getresponse.com\/images\/common\/templates\/webform\/2\/5\/img\/bg.jpg\\\")"}}'))); // encoded string is replaced on the server in ModWebFormV2::ViewWebForm
