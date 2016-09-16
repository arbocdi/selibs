/*global self, GRAPP */
(function () {
    'use strict';
    var ARRAY = Array.prototype,
        $D = GRAPP.dom,
        CSS_CLASS_PREFIX = 'wf2-',
        CSS_VENDOR_PREFIX = (function (js) {
            var styles,
                pre;

            try {
                styles = global.top.getComputedStyle(document.documentElement, '');
            } catch(ex){
                styles = window.getComputedStyle(document.documentElement, '');
            }
            
            pre = (Array.prototype.slice.call(styles).join('').match(/-(moz|webkit|ms)-/) || ('' === styles.OLink && ['', 'o']))[1];

            return !js ? '-' + pre + '-' : pre[0].toUpperCase() + pre.substr(1);
        }()),
        COOKIE = (function () {
            function Cookie() {
            }
            Cookie.implement({
                write: function (name, value, days) {
                    var expires = '',
                        date;

                    if (days) {
                        date = new Date();
                        date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
                        expires = '; expires=' + date.toGMTString();
                    }

                    document.cookie = name + '=' + value + expires + '; path=/';
                },
                read: function (name) {
                    var regexp = new RegExp("(?:;)?" + name + "=([^;]*);?");
                    if (regexp.test(document.cookie)) {
                        return decodeURIComponent(RegExp.$1);
                    }
                    return null;
                },
                erase: function (name) {
                    this.write(name, '', -1);
                }
            });

            return new Cookie();
        }());

    try {
        window.top.CE = function () {
            COOKIE.erase.bind(COOKIE);
        };
    } catch (e) {}

    function targetIsOutsideOfWindow(e) {
        var from = e.relatedTarget || e.toElement;
        return !from && ((e.clientY <= 0 || e.clientY >= e.screenY) || (e.clientX <= 0 || e.clientX >= e.screenX));
    }

    function FormSubmit() {
        return FormSubmit.create();
    }
    FormSubmit.assign({
        fields: (function () {
            function Mapper(obj) {
                this.ret = [];
                this.obj = obj;

                this.prepare();
            }

            Mapper.prototype = {
                typeOf: function (element, comparison) {
                    var type = Object.prototype.toString.call(element),
                        match = /\[.*\s(.*)\]/ig.exec(type);

                    if (match && match[1]) {
                        type = match[1];
                    }

                    if (comparison) {
                        return comparison === type.toLowerCase();
                    }

                    return type.toLowerCase();
                },
                prepareFromObject: function (data, key) {
                    var oThis = this;

                    (Object.keys(data) || []).forEach(function (k) {
                        if (undefined === key) {
                            oThis.prepare(data[k], k);
                            return;
                        }
                        oThis.prepare(data[k], key + '[' + k + ']');
                    });
                },
                prepareFromArray: function (data, key) {
                    var oThis = this;

                    data.forEach(function (element) {
                        oThis.prepare(element, key);
                    });
                },
                prepare: function (data, key) {
                    data = undefined !== data ? data : this.obj;

                    switch (this.typeOf(data)) {
                    case 'object':
                        this.prepareFromObject(data, key);
                        break;
                    case 'array':
                        this.prepareFromArray(data, key);
                        break;
                    default:
                        this.ret.push('<input type="hidden" name="' + key + '" value="' + data + '">');
                        break;
                    }
                }
            };

            return function (obj) {
                var map;

                if (!obj) {
                    return '';
                }

                map = new Mapper(obj);

                if (!map.ret.length) {
                    return '';
                }

                map.ret.push('<input type="hidden" name="webform[http_referer]" value="' + window.location.href + '">');

                return map.ret.join('');
            };
        }()),
        create: function (data) {
            var formsubmit ;

            if (!data || !data.form) {
                throw new Error('No data');
            }

            formsubmit = Object.create(FormSubmit.prototype);

            Object.assign(formsubmit, data);

            formsubmit.build();

            return formsubmit;
        }
    }).implement({
        build: function () {
            var form = document.createElement('form');

            Object.assign(form, this.form);

            form.innerHTML = FormSubmit.fields(this.values);

            document.body.appendChild(form);

            form.submit();
        }
    });

    function PostMsg(iframe) {
        return PostMsg.create(iframe);
    }
    PostMsg.assign({
        create: function (webform) {
            var uid = webform.uid,
                iframe = webform.iframe,
                backdropElement = webform.backdrop,
                postmsg = Object.create(PostMsg.prototype, {
                    wrapper: {
                        get: function () {
                            var wrap = this.iframe.parentNode;
                            Object.defineProperty(this, 'wrapper', {
                                value: wrap
                            });
                            return wrap;
                        },
                        configurable: true
                    },
                    iframe: {
                        value: iframe
                    },
                    backdropElement: {
                        value: backdropElement
                    },
                    backdrop: {
                        value: 'off',
                        writable: true
                    },
                    cssRegister: {
                        value: {}
                    },
                    linkelement: {
                        get: function () {
                            var element = document.createElement('a');
                            Object.defineProperty(this, 'linkelement', {
                                value: element
                            });
                            return element;
                        },
                        configurable: true
                    },
                    uid: {
                        value: uid
                    },
                    ready: {
                        value: false,
                        writable: true
                    },
                    webform: {
                        value: webform
                    },
                    withBackdrop: {
                        get: function () {
                            return !['scroll-box', 'fixed-bar', 'embeded'].contains(this.displayType);
                        }
                    }
                });

            iframe.contentWindow.postMessage({
                call: 'init',
                http_referer: window.location.href
            }, '*');

            return postmsg;
        },
        getPositionValue: function (/*displayType*/) {
            /*if ('popover' === displayType) {
                return 15 + 'px';
            }*/

            return 0;
        }
    }).inherits(GRAPP.Observer).implement({
        init: function (def) {
            var oThis = this,
                wrapper = this.wrapper,
                loaderIFrame = this.webform.loaderIFrame,
                iframe = this.iframe,
                cssregister = this.cssRegister,
                id = def.id,
                properties = def.properties,
                display = def.display,
                boxWidth,
                transformOrigin,
                winSizeX = self.innerWidth || document.documentElement.clientWidth,
                winSizeY = self.innerHeight || document.documentElement.clientHeight,
                withShadow = (def && def.style && def.style.boxShadow && 'none' !== def.style.boxShadow),
                increase = /(ebedded|fixed-bar)/.test(display.displayType) ? 0 : withShadow ? 100 : 0,
                isHuge = winSizeY < properties.height;

            Object.assign(this, display, def);

            var lock = !def.preview && (!def.published || (-1 < display.displayInterval && !!COOKIE.read('grwf2_' + id)) || this.shouldBeHidden());

            this.backdrop = 'off';
            this.isHuge = isHuge;

            if (lock) {
                this.iframe.parentNode.parentNode.removeChild(this.iframe.parentNode);
                return;
            }

            this.id = id;

            wrapper.id = wrapper.id || 'grwf2_' + id + '_' + GRAPP.UID(5);

            var prop = 0;

            /* scaling webform for mobile */
            
            if ('embedded' === display.displayType) {
                boxWidth = wrapper.parentNode.clientWidth || winSizeX;
                transformOrigin = CSS_VENDOR_PREFIX + 'transform-origin: 0 0;' + 'transform-origin: 0 0;';
            } else {
                boxWidth = winSizeX;
                prop = -10;
            }
            
            if (boxWidth < properties.width) {
                (wrapper.ownerDocument.head || wrapper.ownerDocument.getElementByTagName('head')[0]).insertAdjacentHTML('beforeEnd', '<style>' +
                    '@media only screen and (max-width: ' + properties.width + 'px) {' +
                        '#' + wrapper.id + ' { ' +
                            CSS_VENDOR_PREFIX + 'transform: scale(' + ((boxWidth + prop) / properties.width) + ');' +
                            'transform: scale(' + ((boxWidth + prop) / properties.width) + ');' +
                            transformOrigin +
                        '}' +
                    '}' +
                '</style>');
            }
    
            this.preview = def.preview;

            Object.assign(wrapper.style, {
                width: 0,
                height: 0
            });

            cssregister.top = 'calc(50% - ' + Math.round((properties.height + increase) / 2) + 'px)';
            cssregister.left = 'calc(50% - ' + Math.round((properties.width + increase) / 2) + 'px)';

            if (isHuge) {
                $D.addClass(wrapper, 'wf2-abs');
                Object.assign(iframe, {
                    height: properties.height
                });
            }

            $D.addClass(wrapper, CSS_CLASS_PREFIX + display.displayType);

            if (withShadow) {
                $D.addClass(iframe, 'wf2-shadow');
            }

            if ('embedded' === display.displayType) {
                $D.addClass(document.documentElement, 'grwf2');
                
                if (def.preview) {
                    $D.addClass(document.documentElement, 'preview');
                }

                Object.assign(wrapper.style, {
                    width: properties.width + 'px',
                    height: properties.height + 'px'
                }, cssregister, def.style);

                Object.assign(iframe, {
                    width: '100%',
                    height: isHuge ? properties.height : '100%'
                });

                if (loaderIFrame && loaderIFrame.parentNode) {
                    loaderIFrame.parentNode.removeChild(loaderIFrame);
                }

                return;
            }

            if (this.withBackdrop) {
                wrapper.appendChild(this.backdropElement);
            }

            if (display.displayPosition) {
                $D.addClass(wrapper, CSS_CLASS_PREFIX + display.displayPosition);
            }

            if (display.displayEffect) {
                $D.addClass(wrapper, CSS_CLASS_PREFIX + display.displayEffect);
            }

            if (display.displayBackground) {
                $D.addClass(this.backdropElement, CSS_CLASS_PREFIX + 'bg-' + display.displayBackground);
            }

            if (display.displayPosition) {
                switch (display.displayPosition) {
                case 'top':
                    cssregister.bottom = '';
                    cssregister.top = PostMsg.getPositionValue(display.displayType);
                    break;
                case 'right':
                    cssregister.top = 'calc(50% - ' + Math.round((properties.height + increase) / 2) + 'px)';
                    cssregister.left = '';
                    cssregister.right = PostMsg.getPositionValue(display.displayType);
                    break;
                case 'bottom':
                    cssregister.top = '';
                    cssregister.bottom = PostMsg.getPositionValue(display.displayType);
                    break;
                case 'left':
                    cssregister.top = 'calc(50% - ' + Math.round((properties.height + increase) / 2) + 'px)';
                    cssregister.right = '';
                    cssregister.left = PostMsg.getPositionValue(display.displayType);
                    break;
                case 'left-bottom':
                case 'bottom-left':
                    cssregister.left = PostMsg.getPositionValue(display.displayType);
                    cssregister.right = '';
                    cssregister.bottom = PostMsg.getPositionValue(display.displayType);
                    cssregister.top = '';
                    break;
                case 'right-bottom':
                case 'bottom-right':
                    cssregister.right = PostMsg.getPositionValue(display.displayType);
                    cssregister.left = '';
                    cssregister.bottom = PostMsg.getPositionValue(display.displayType);
                    cssregister.top = '';
                    break;
                 case 'left-top':
                 case 'top-left':
                    cssregister.left = PostMsg.getPositionValue(display.displayType);
                    cssregister.right = '';
                    cssregister.top = PostMsg.getPositionValue(display.displayType);
                    cssregister.bottom = '';
                    break;
                case 'right-top':
                case 'top-right':
                    cssregister.right = PostMsg.getPositionValue(display.displayType);
                    cssregister.left = '';
                    cssregister.top = PostMsg.getPositionValue(display.displayType);
                    cssregister.bottom = '';
                    break;
                }
            }

            if (!display.displayEffect || 'none' === display.displayEffect) {
                Object.assign(wrapper.style, {
                    top: '0px',
                    left: '0px'
                });
            } else if ('just-me' === display.displayEffect) {
                this.backdropElement.style.backgroundColor = def.style.backgroundColor;
            }

            Object.assign(cssregister, {
                width: (properties.width + increase) + 'px',
                height: (properties.height + increase) + 'px'
            });

            switch (display.displayType) {
            case 'fixed-bar':
                cssregister.left = '0px';
                cssregister.width = '100%';
                switch (display.displayPosition) {
                case 'bottom':
                    cssregister.top = '';
                    cssregister.bottom = '0px';
                    break;
                default:
                    cssregister.bottom = '';
                    cssregister.top = '0px';
                }
                break;
            default:
                /*if (!display.appName && /lightbox|popover/.test(display.displayType)) {
                    wrapper.insertAdjacentHTML('afterBegin', '<a href="#close"></a>');
                    wrapper.querySelector('a[href="#close"]').addEventListener('click', function (e) {
                        e.preventDefault();
                        e.stopPropagation();

                        if (def.preview) {
                            return;
                        }
                        
                        PostMsg.prototype.close.call(oThis);
                    }, false);

                    Object.defineProperties(oThis, {
                        closex: {
                            value: (function (element) {
                                return Object.create({
                                    setposition: function (state) {
                                        var top = state ? this.top + 'px' : '',
                                            left = state ? this.left + 'px' : '';

                                        Object.assign(this.element.style, {
                                            top: top,
                                            left: left
                                        });
                                    }
                                }, {
                                    element: {
                                        value: element
                                    },
                                    top: {
                                        get: function () {
                                            var rect = element.getBoundingClientRect();

                                            return rect.top;
                                        }
                                    },
                                    left: {
                                        get: function () {
                                            var rect = element.getBoundingClientRect();

                                            return rect.left;
                                        }
                                    }
                                });
                            }(wrapper.querySelector('a[href="#close"]')))
                        }
                    });
                }*/

                this.backdrop = 'on';
                break;
            }

            Object.assign(wrapper.style, cssregister);

            var timer = null;

            if ('scroll-box' === display.displayType) {
                (function () {
                    var scrollHeight = document.documentElement.scrollHeight,
                        winHeight = self.innerHeight || document.documentElement.clientHeight,
                        r = scrollHeight - winHeight,
                        rr = Math.round(r - Math.abs((r * 9) / 100)),
                        pp = Math.round(Math.abs((r * 5) / 100)),
                        scrollTop = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop;

                    if (r <= 0) {
                        timer = setTimeout(function () {
                            oThis.start(wrapper);
                        }, Number.parseInt(display.displayDelay, 10) * 1000);
                        return;
                    }

                    if (/bottom/.test(display.displayPosition)) {
                        if (r <= scrollTop) {
                            
                            timer = setTimeout(function () {
                                oThis.start(wrapper);
                            }, Number.parseInt(display.displayDelay, 10) * 1000);
                            return;
                        }
                    }
                    if (/^(right|left)$/.test(display.displayPosition)) {
                        if (scrollTop < pp) {
                            timer = setTimeout(function () {
                                oThis.start(wrapper);
                            }, Number.parseInt(display.displayDelay, 10) * 1000);
                            return;
                        }
                    }

                    var starterscroll = function () {
                        var scrollTop = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop;
                        clearTimeout(timer);

                        if (/bottom/.test(display.displayPosition)) {
                            if (scrollTop < rr) {
                                return;
                            }
                        } else {
                            if (/right|left/.test(display.displayPosition)) {
                                if (scrollTop < pp) {
                                    return;
                                }
                            }
                        }

                        timer = setTimeout(function () {
                            oThis.start(wrapper);
                        }, Number.parseInt(display.displayDelay, 10) * 1000);
                        window.removeEventListener('scroll', starterscroll, false);
                    };

                    window.addEventListener('scroll', starterscroll, false);
                }());

                return;
            }

            if ('on_exit' === display.appName) {
                document.addEventListener('mouseout', function (e) {
                    if (targetIsOutsideOfWindow(e)) {
                        setTimeout(function () {
                            oThis.show();
                        } , Number.parseInt(display.displayDelay, 10) * 1000);
                    }
                }, false);
            }

            setTimeout(function () {
                oThis.start(wrapper);
            }, Number.parseInt(display.displayDelay, 10) * 1000);
        },
        start: function (wrapper) {
            var oThis = this;

            wrapper = wrapper || this.wrapper;

            wrapper.addEventListener('transitionend', function () {
                if ('shake-box' === oThis.displayType) {
                    $D.addClass(wrapper, 'wf2-shake-box-animated');
                }

                $D.addClass(wrapper, 'wf2-loaded');
                wrapper.firstElementChild.style.position = 'fixed !important';
            }, false);

            if ('on' === this.webform.showWhenStart && 'on_exit' !== this.appName) {
                this.show();
            }

            this.ready = true;

            this.webform.notify('onready');

            if (-1 === this.displayInterval || this.preview) {
                return;
            }

            COOKIE.write('grwf2_' + oThis.id, 'lock', Number.parseInt(oThis.displayInterval, 10));
        },
        getElementPosition: function (element) {
            var top = 0,
                left = 0;

            while (element.tagName !== "HTML") {
                top += element.offsetTop || 0;
                left += element.offsetLeft || 0;
                if (element.offsetParent) {
                    element = element.offsetParent;
                } else {
                    break;
                }
            }

            top += element.offsetTop || 0;
            left += element.offsetLeft || 0;

            return {
                left: left,
                top: top
            };
        },
        getScrollTop: function () {
            return window.pageYOffset || document.documentElement.scrollTop || 0;
        },
        bodyScroll: {
            preventDefault: function (e) {
                e.preventDefault();
            },
            prevetnDefaultKeys: function (e) {
                if (-1 < [37, 38, 39, 40].indexOf(e.keyCode)) {
                    e.preventDefault();
                }
            },
            hide: function () {
                document.body.style.overflow = 'hidden';
                document.body.setAttribute('scroll', 'no');
                window.addEventListener('DOMMouseScroll', this.preventDefault, false);
                window.onwheel = this.preventDefault; // modern standard
                window.onmousewheel = document.onmousewheel = this.preventDefault; // older browsers, IE
                window.ontouchmove  = this.preventDefault; // mobile
                document.onkeydown  = this.preventDefaultForScrollKeys;
            },
            show: function () {
                document.body.style.overflow = '';
                document.body.removeAttribute('scroll');

                window.removeEventListener('DOMMouseScroll', this.preventDefault, false);
                window.onmousewheel = document.onmousewheel = null; 
                window.onwheel = null; 
                window.ontouchmove = null;  
                document.onkeydown = null;
            }
        },
        selecton: function () {
            var postmsg = this,
                displayType = this.displayType,
                pos = this.getElementPosition(this.iframe.parentNode);


            $D.removeClass(this.wrapper, 'wf2-' + this.displayEffect);
            $D.removeClass(this.wrapper, 'wf2-animated');

            if (!this.preview && 'embedded' === displayType) {

                Object.assign(this.iframe.style, {
                    top: (pos.top - this.getScrollTop()) + 'px',
                    left: pos.left + 'px'
                });
                $D.addClass(this.wrapper, 'wf2-fixed-embedded');
                if (!this.isHuge) {
                    this.bodyScroll.hide();
                }
                this.iframe.setAttribute('scrolling', 'auto');
            } else {
                if (this.closex && 'function' === typeof this.closex.setposition) {
                    this.closex.setposition(true);
                }
                $D.addClass(this.wrapper, 'wf2-fixed');
                $D.addClass(this.iframe, 'wf2-initial');
                this.wrapper.style.opacity = 1;
            }
        },
        selectoff: function () {
            var displayType = this.displayType;

            $D.removeClass(this.iframe.parentNode, 'wf2-fixed');

            if (!this.preview && 'embedded' === displayType) {
                $D.removeClass(this.wrapper, 'wf2-fixed-embedded');
                this.iframe.setAttribute('scrolling', 'no');
                this.bodyScroll.show();
            } else {
                if (this.closex && 'function' === typeof this.closex.setposition) {
                    this.closex.setposition(false);
                }
                $D.removeClass(this.wrapper, 'wf2-fixed');
                $D.removeClass(this.iframe, 'wf2-initial');
            }

           //$D.addClass(this.wrapper, 'wf2-animated');
            //$D.addClass(this.wrapper, 'wf2-' + this.displayEffect);
        },
        reload: function (def) {
            if (def.url) {
                window.top.location = def.url;
            }
        },

        shouldBeHidden : function() {
            var sentFormCookieCreated = !!COOKIE.read('grwf2_sent_' + this.id),
                formClickedAwayCookieCreated = !!COOKIE.read('grwf2_clicked_away_' + this.id),
                isEmbedded = this.displayType === 'embedded',
                isPopOver = this.displayType === 'popover',
                appName = this.appName;

                //Handle situation when form has been already sent
            var hasBeenSent = (sentFormCookieCreated && !isEmbedded),
                //Handle situation when on exit app has been clicked away
                hasBeenClickedAway = (formClickedAwayCookieCreated && isPopOver && appName === 'on_exit');

            return hasBeenSent || hasBeenClickedAway;
        },

        setSentCookie : function() {
            COOKIE.write('grwf2_sent_' + this.id, 'lock', 360);//@todo nie wiem czy czasem tutaj tez nie powinnismy uzywac displayInterval
        },

        setClickedAwayCookie : function() {
            COOKIE.write('grwf2_clicked_away_' + this.id, 'clicked_away', Number.parseInt(this.displayInterval, 10));
        },

        submit: function (def) {
            //Ustaw cookie żeby nie pokazywać już forma
            this.setSentCookie();
            FormSubmit.create(def);
        },
        show: function () {
            if (this.shouldBeHidden()) {
                return;
            }

            var wrapper = this.wrapper;
            wrapper.hidden = false;

            if ('none' === this.displayEffect) {
                $D.addClass(wrapper, 'wf2-loaded');
            }

            $D.addClass(wrapper, 'wf2-animated');

            if (this.withBackdrop) {
                wrapper.ownerDocument.documentElement.setAttribute('data-backdrop', 'on');
                wrapper.insertAdjacentElement('beforeBegin', this.backdropElement);
            }

            this.webform.notify('show');
        },
        close: function () {
            var wrapper = this.wrapper;

            wrapper.ownerDocument.documentElement.removeAttribute('data-backdrop');
            if (this.backdropElement) {
                wrapper.appendChild(this.backdropElement);
            }

            $D.removeClass(wrapper, 'wf2-loaded');
            $D.removeClass(wrapper, 'wf2-animated');

            this.webform.notify('hide');

            wrapper.hidden = true;

            this.setClickedAwayCookie();
        },
        download: function (def) {
            var aEl = this.linkelement;
            aEl.href = def.url;

            if (!/safari/i.test(navigator.userAgent)) {
                aEl.target = '_blank';
            }

            document.body.appendChild(aEl);
            aEl.click();
            aEl.parentNode.removeChild(aEl);
        }
    });

    var PRIV_API_VAR = new WeakMap();

    function WebForm() {
        return WebForm.create();
    }

    WebForm.assign({
        create: function (data) {
            var settings = JSON.parse(data.element.textContent.trim() || '{}'),
                webform = Object.create(WebForm.prototype, {
                    wrapper: {
                        get: function () {
                            return this.iframe.parentNode;
                        }
                    },
                    loaderIFrame: {
                        get: function () {
                            return data.loaderIFrame;
                        }
                    },
                    iframe: {
                        value: (function (element, url, displayType) {
                            var wrapper = document.createElement('div'),
                                iframe = document.createElement('iframe');

                            wrapper.className = 'grwf2-wrapper';

                            Object.assign(iframe, {
                                src: url,
                                width: '100%',
                                height: '100%'
                            });

                            Object.assign(wrapper.style, {
                                width: 0,
                                height: 0
                            });

                            iframe.style.border = 'none';

                            iframe.setAttribute('sandbox', 'allow-same-origin allow-forms allow-scripts');

                            iframe.setAttribute('scrolling', 'no');
                            iframe.setAttribute('allowTransparency', true);

                            wrapper.appendChild(iframe);

                            if ('embedded' === displayType) {
                                element.parentNode.insertBefore(wrapper, element.nextSibling);
                            } else {
                                element.ownerDocument.body.appendChild(wrapper);
                                wrapper.hidden = true;
                            }

                            return iframe;
                        }(data.element, data.url, data.displayType || 'embedded'))
                    },
                    postmsg: {
                        get: function () {
                            var postmsg;

                            if (!this.iframe) {
                                return;
                            }

                            postmsg = PostMsg.create(this);

                            Object.defineProperty(this, 'postmsg', {
                                value: postmsg
                            });

                            return postmsg;
                        },
                        configurable: true
                    },
                    uid: {
                        get: function () {
                            return this.iframe.src;
                        }
                    },
                    showWhenStart: {
                        value: 'on',
                        writable: true
                    }
                }),
                priv = Object.defineProperties({}, {
                    clicktoshowevent: {
                        value: function (e) {
                            e.preventDefault();
                            webform.show();
                        },
                        writable: true
                    },
                    clicktohideevent: {
                        value: function (e) {
                            e.preventDefault();
                            webform.hide();
                        },
                        writable: true
                    },
                    onshowevent: {
                        value: null,
                        writable: true
                    },
                    onhideevent: {
                        value: null,
                        writable: true
                    }
                });

            PRIV_API_VAR.set(webform, priv);

            Object.defineProperties(webform, {
                selector: {
                    set: function (data) {
                        (Object.keys(data) || []).forEach(function (action) {
                            if (webform[action]) {
                                webform[action] = data[action];
                            }
                        });
                    }
                },
                clickToShow: {
                    set: function (selector) {
                        var elements = this.wrapper.ownerDocument.querySelectorAll(selector);

                        elements = Array.prototype.slice.call(elements);

                        elements.forEach(function (element) {
                            element.addEventListener('click', priv.clicktoshowevent, false);
                        });

                        if ('on' !== settings.showWhenStart) {
                            this.showWhenStart = 'off';
                        }
                    },
                    get: function () {
                        return priv.clicktoshowevent;
                    }
                },
                clickToHide: {
                    set: function (selector) {
                        var elements = this.wrapper.ownerDocument.querySelectorAll(selector);

                        elements = Array.prototype.slice.call(elements);

                        elements.forEach(function (element) {
                            element.addEventListener('click', priv.clicktohideevent, false);
                        });
                    },
                    get: function () {
                        return priv.clicktohideevent;
                    }
                }
            });

            Object.defineProperties(webform, {
                backdrop: {
                    value: (function () {
                        var element = document.createElement('div');

                        element.className = 'grwf2_backdrop ';

                        return element;
                    }())
                }
            });

            Object.assign(webform, settings);

            return webform;
        }
    }).inherits(GRAPP.Observer, {
        onshow: {
            get: function () {
                return PRIV_API_VAR.get(this).onshowevent;
            },
            set: function (fn) {
                var priv = PRIV_API_VAR.get(this);

                if ('string' === typeof fn) {
                    fn = window[fn];
                }

                if ('function' !== typeof type) {
                    if (null !== priv.onshowevent) {
                        this.off('show', priv.onshowevent);
                    }
                    priv.onshowevent = null;
                    return;
                }

                priv.onshowevent = fn;

                this.on('show', priv.onshowevent);
            }
        },
        onhide: {
            get: function () {
                return PRIV_API_VAR.get(this).onhideevent;
            },
            set: function (fn) {
                var priv = PRIV_API_VAR.get(this);

                if ('string' === typeof fn) {
                    fn = window[fn];
                }

                if ('function' !== typeof fn) {
                    if (null !== priv.onhideevent) {
                        this.off('show', priv.onhideevent);
                    }
                    priv.onhideevent = null;
                    return;
                }

                priv.onhideevent = fn;
                this.on('hide', priv.onhideevent);
            }
        }
    }).implement({
        exec: function (data) {
            var pm = this.postmsg,
                method;

            if (data && data.call) {
                method = data.call.trim();
            }

            if (!method) {
                return;
            }

            //delete data.call;

            if ('init' === method && !window.LOADED) {
                pm[method](data);
                return;
            }

            if ('function' !== typeof this.postmsg[method]) {
                return;
            }

            this.postmsg[method](data);
        },
        show: function () {
            this.exec({
                call: 'show'
            });
        },
        hide: function () {
            this.exec({
                call: 'close'
            });
        }
    });

    function ApiWebForm() {
        return ApiWebForm.create();
    }
    ApiWebForm.assign({
        create: function (webform) {
            var api = Object.create(ApiWebForm.prototype);

            api.name = webform.name;

            PRIV_API_VAR.set(api, webform);
            return api;
        }
    }).implement({
        on: function () {
            var webform = PRIV_API_VAR.get(this);

            webform.on.apply(webform, Array.prototype.slice.call(arguments));

            return this;
        },
        off: function () {
            var webform = PRIV_API_VAR.get(this);

            webform.off.apply(webform, Array.prototype.slice.call(arguments));

            return this;
        },
        show: function () {
            var webform = PRIV_API_VAR.get(this);

            webform.exec({
                call: 'show'
            });

            return this;
        },
        hide: function () {
            var webform = PRIV_API_VAR.get(this);

            webform.exec({
                call: 'close'
            });
        }
    });

    function WebForms() {
        return WebForms.create();
    }

    WebForms.assign({
        SELECTOR: 'script[src*=view_webform_v2]',
        elements: {},
        create: function () {
            return Object.create(WebForms.prototype, {
                elements: {
                    value: {},
                    enumerable: true
                }
            });
        },
        register: function (data) {

            var webforms = this,
                webform = WebForm.create(data),
                uid = webform.uid,
                name = webform.name || uid,
                api = ApiWebForm.create(webform);

            ARRAY.push.call(this, webform);
            ARRAY.push.call(this.elements, webform);

            if (this.elements[uid]) {
                if (!Array.isArray(this.elements[uid])) {
                    this.elements[uid] = [this.elements[uid]];
                }

                this.elements[uid].push(webform);
            } else {
                this.elements[uid] = webform;
            }

            if (name) {
                Array.prototype.push.call(WebForms.elements, api);
                WebForms.elements[name] = api;
            }

            return {
                get: webforms.get.bind(webforms)
            };
        }
    }).implement({
        get: function (name) {
            if (Number.isInteger(name)) {
                return this[name];
            }

            return GRAPP.Namespace.prototype.getItem.call(WebForms.elements, name);
        }
    });

    var webforms = WebForms.create();

    window.GRAPP.WF2Register = function (data) {
        WebForms.register.call(webforms, data);
    };

    window.GRWF2 = webforms;

    try {
        var debug = document.getElementById('Debug');

        if (debug) {
            debug.parentNode.removeChild(debug);
        }
    } catch (ex) {

    }

    window.addEventListener("message", function (e) {
        var data = e.data,
            uid = data.uid,
            wf = (webforms && webforms.elements && webforms.elements[uid]);

        if (!wf) {
            return;
        }

        delete data.uid;

        try {
            if (!Array.isArray(wf)) {
                wf = [wf];
            }

            wf.forEach(function (w) {
                w.exec(data);
            });
        } catch (ex) {
            //throw ex;
        }
    }, false);
}());