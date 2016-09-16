(function (obj, fn, arr) {
    'use strict';

    var hasOwn = obj.hasOwnProperty,
        slice = arr.slice;

    Object.defineProperties(fn, {
        toString: {
            value: function () {
                return ['function ', this.name, '() {\n\t[code]\n}'].join('');
            },
            configurable: true,
            writable: true
        },
        setter: {
            value: function () {
                var oThis = this;

                return function (a, b) {
                    var k,
                        ln;

                    if (!a) {
                        return this;
                    }

                    if ('string' === typeof a) {
                        oThis.call(this, a, b);
                        return this;
                    }

                    if (a.length && a.forEach) {
                        for (k = 0, ln = a.length; k < ln; k += 1) {
                            oThis.call(this, a[k], k);
                        }

                        return this;
                    }

                    for (k in a) {
                        if (a.hasOwnProperty(k)) {
                            oThis.call(this, k, a[k]);
                        }
                    }

                    return this;
                };
            },
            configurable: true,
            writable: true
        },
        inherits: {
            value: function (proto) {
                var ext = slice.call(arguments, 1)[0] || {},
                    func = this,
                    uber = proto,
                    p = this.prototype;

                ext.constructor = {
                    value: func
                };

                if (!proto || Object === proto.constructor) {
                    ext.uber = {
                        value: Object.prototype
                    };

                    Object.defineProperties(this.prototype, ext);

                    return this;
                }

                if ('function' === typeof proto) {
                    proto = proto.prototype;
                } else {
                    uber = proto.constructor;
                }

                ext.uber = {
                    value: uber.prototype
                };

                this.prototype = Object.create(proto, ext);

                Object.getOwnPropertyNames(p).forEach(function (name) {
                    try {
                        func.prototype[name] = p[name];

                    } catch (x) {
                    }
                });

                return this;
            },
            configurable: true,
            writable: true
        }
    });

    Object.defineProperties(fn, {
        assign: {
            value: function (name, value) {
                if (!hasOwn.call(this, name)) {
                    Object.defineProperty(this, name, {
                        value: value
                    });
                }
            }.setter(),
            configurable: true,
            writable: true
        },
        implement: {
            value: function (name, value) {
                if (!hasOwn.call(this.prototype, name)) {
                    Object.defineProperty(this.prototype, name, {
                        value: value
                    });
                }
            }.setter(),
            configurable: true,
            writable: true
        }
    });

    if (!hasOwn.call(Object, 'assign')) {
        Object.defineProperty(Object, 'assign', {
            value: function (target) {
                var to,
                    args;

                try {
                    if (undefined === target || null === target) {
                        throw new TypeError('Cannot convert first argument to object');
                    }

                    to = Object(target);

                    args = slice.call(arguments, 1);

                    args.forEach(function (source) {
                        var keys;
                        if (undefined === source || null === source) {
                            return;
                        }

                        keys = Object.keys(Object(source));

                        keys.forEach(function (key) {
                            var desc = Object.getOwnPropertyDescriptor(source, key);

                            if (undefined !== desc && desc.enumerable) {
                                to[key] = source[key];
                            }
                        });
                    });
                } catch (ex) {
                }

                return to;
            }
        });
    }

    Object.assign(Object, {
        mixin: function (target, source) {
            var property;

            try {
                Object.keys(source).forEach(function (property) {
                    Object.defineProperty(target, property, Object.getOwnPropertyDescriptor(source, property));
                });
            } catch (ex) {
                for (property in source) {
                    if (source.hasOwnProperty(property)) {
                        target[property] = source[property];
                    }
                }
            }

            return target;
        },
        keys: function (o) {
            var ret = [], k;
            for (k in o) {
                if (Object.prototype.hasOwnProperty.call(o, k)) {
                    ret.push(k);
                }
            }
            return ret;
        }
    });

    Function.implement({
        bind: function (context) {
            var oThis = this,
                args = slice.call(arguments, 1);

            return function () {
                return oThis.apply(context, args.concat(slice.call(arguments)));
            };
        }
    });

    Array.assign({
        isArray: function (o) {
            return Object.prototype.toString.call(o) === '[object Array]' || (o instanceof Array);
        }
    });

    Array.implement({
        contains: function contains(item) {
            return -1 < arr.indexOf.call(this, item);
        },
        empty: function empty() {
            this.length = 0;
            return this;
        }
    });

    Number.assign({
        sign: function (value) {
            var number = +value;

            if (0 === number) {
                return number;
            }

            if (isNaN(number)) {
                return number;
            }

            return number < 0 ? -1 : 1;
        },
        isInteger: function isInteger(nVal) {
            return 'number' === typeof nVal && Number.isFinite(nVal) && nVal > -9007199254740992 && nVal < 9007199254740992 && Math.floor(nVal) === nVal;
        },
        parseInt: parseInt,
        parseFloat: parseFloat,
        isNaN: isNaN,
        isFinite: isFinite
    });

    Number.implement({
        limit: function (min, max) {
            return Math.min(max, Math.max(min, this));
        }
    });

    Date.assign({
        now: function () {
            return +new Date();
        }
    });

    (function() {
        var slice = Array.prototype.slice;
     
        function namespace(object, ns) {
            var privates = Object.create(object),
                base = object.valueOf;

            Object.defineProperty(object, 'valueOf', {
                value: function valueOf(value) {
                    return value !== ns || this !== object ? base.apply(this, slice.call(arguments)) : privates;
                },
                configurable: true
            });

            return privates;
        }
     
        function name() {
            var ns = {};

            return function (object) {
                var privates = object.valueOf(ns),
                    is = Object.is || function (a, b) {
                            return a === b;
                        };

                return is(privates, object) ? privates : namespace(object, ns);
            };
        }
     
        function guard(key) {
            if (key !== Object(key)) {
                throw new TypeError('value is not a non-null object');
            }

            return key;
        }
     
        function WeakMap() {
            var privates = name(),
                uid = '__WM__';
     
            return Object.create(Object.defineProperties(WeakMap.prototype, {
                has: {
                    value: function has(object) {
                        return uid in privates(object);
                    },
                    configurable: true,
                    enumerable: false,
                    writable: true
                },
                get: {
                    value: function get(key, fallback) {
                        return privates(guard(key))[uid] || fallback;
                    },
                    configurable: true,
                    enumerable: false,
                    writable: true
                },
                set: {
                    value: function set(key, value) {
                        privates(guard(key))[uid] = value;
                    },
                    configurable: true,
                    enumerable: false,
                    writable: true
                },
                'delete': {
                    value: function set(key) {
                        return delete privates(guard(key))[uid];
                    },
                    configurable: true,
                    enumerable: false,
                    writable: true
                }
            }));
        }
        if (window.WeakMap) {
            return;
        }
        
        Object.defineProperties(window, {
            WeakMap: {
                value: WeakMap
            }
        });
    }());

    (function () {
        var privates = new WeakMap();

        function Map() {
            if (!(this instanceof Map)) {
                return new Map();
            }

            if (!privates.has(this)) {
                privates.set(this, {
                    keys: [],
                    values: []
                });
            }
        }

        Object.defineProperties(Map.prototype, {
            size: {
                get: function () {
                    return privates.get(this).keys.length;
                },
                configurable: true
            },
            clear: {
                value: function () {
                    var priv = privates.get(this);
                    priv.keys.length = 0;
                    priv.values.length = 0;
                },
                configurable: true,
                enumerable: false,
                writable: true
            },
            'delete': {
                value: function (key) {
                    var priv = privates.get(this),
                        index = priv.keys.indexOf(key);

                    if (-1 === index) {
                        return false;
                    }

                    priv.keys.splice(index, 1);
                    priv.values.splice(index, 1);

                    return true;
                },
                configurable: true,
                enumerable: false,
                writable: true
            },
            entries: {
                value: function () {
                },
                configurable: true,
                enumerable: false,
                writable: true
            },
            forEach: {
                value: function (callbackfn, thisArg) {
                    var priv = privates.get(this),
                        result,
                        ln = this.size,
                        i;

                    for (i = 0; i < ln; i += 1) {
                        if (priv.keys[i] && priv.values[i]) {
                            result = callbackfn.call((thisArg || this), priv.values[i], priv.keys[i]);

                            if (false === result) {
                                throw new TypeError();
                            }
                        }
                    }
                },
                configurable: true,
                enumerable: false,
                writable: true
            },
            get: {
                value: function (key) {
                    var priv = privates.get(this),
                        index = priv.keys.indexOf(key);

                    if (-1 !== index) {
                        return priv.values[index];
                    }
                },
                configurable: true,
                enumerable: false,
                writable: true
            },
            has: {
                value: function (key) {
                    var priv = privates.get(this);
                    return (priv && priv.keys && -1 !== priv.keys.indexOf(key));
                },
                configurable: true,
                enumerable: false,
                writable: true
            },
            iterator: {
                value: function () {
                },
                configurable: true,
                enumerable: false,
                writable: true
            },
            keys: {
                value: function () {
                    // mapData.keys;
                },
                configurable: true,
                enumerable: false,
                writable: true
            },
            set: {
                value: function (key, value) {
                    var priv = privates.get(this),
                        index;

                    index = priv.keys.indexOf(key);

                    if (-1 === index) {
                        priv.keys.push(key);
                        priv.values.push(value);
                    } else {
                        priv.values[index] = value;
                    }
                },
                configurable: true,
                enumerable: false,
                writable: true
            }
        });

        function Set() {
            var args,
                priv = [];

            if (!(this instanceof Set)) {
                return new Set();
            }

            privates.set(this, []);

            args = Array.prototype.slice.call(arguments);

            if (args.length) {
                priv.push.apply(priv, args);
            }

            privates.set(this, priv);
        }

        Object.defineProperties(Set.prototype, {
            size: {
                get: function () {
                    return privates.get(this).length;
                }
            },
            add: {
                value: function (value) {
                    var data = privates.get(this),
                        index = data.indexOf(value);

                    if (-1 === index) {
                        data.push(value);
                    } else {
                        data[index] = value;
                    }
                }
            },
            clear: {
                value: function () {
                    privates.get(this).length = 0;
                }
            },
            'delete': {
                value: function (value) {
                    var data = privates.get(this),
                        index = data.indexOf(value);

                    if (-1 === index) {
                        return false;
                    }

                    data.splice(index, 1);

                    return true;
                }
            },
            entries: {
                value: function () {

                }
            },
            forEach: {
                value: function (callbackfn, thisArg) {
                    privates.get(this).forEach(callbackfn, thisArg);
                }
            },
            has: {
                value: function (value) {
                    return -1 !== privates.get(this).indexOf(value);
                }
            },
            iterator: {
                value: function () {
                }
            },
            keys: {
                value: function () {
                }
            },
            values: {
                value: function () {
                }
            }
        });

        if (window.Map) {
            return;
        }

        Object.defineProperties(window, {
            Map: {
                value: Map
            },
            Set: {
                value: Set
            }
        });
    }());
}(Object.prototype, Function.prototype, Array.prototype, String.prototype));

(function () {
    'use strict';
    var div = document.createElement('div'),
        insertAdjacentElement;

    insertAdjacentElement = function insertAdjacentElement(where, content) {
        switch (where) {
        case 'afterBegin':
            this.insertBefore(content, this.firstChild);
            break;
        case 'beforeBegin':
            this.parentNode.insertBefore(content, this);
            break;
        case 'afterEnd':
            this.parentNode.insertBefore(content, this.nextSibling);
            break;
        default:
            this.appendChild(content);
            break;
        }
    };

    try {
        Element.implement({
            insertAdjacentHTML: function insertAdjacentHTML(where, html) {
                var range,
                    frg;

                range = document.createRange();
                frg = range.createContextualFragment(html);

                insertAdjacentElement.call(this, where, frg);
            },
            remove: function remove() {
                this.parentNode.removeChild(this);
            }
        });
        HTMLElement.implement({
            insertAdjacentElement: insertAdjacentElement,
            insertAdjacentText: function insertAdjacentText(where, text) {
                var textNodeToInsert = document.createTextNode(text);

                insertAdjacentElement.call(this, where, textNodeToInsert);
            }
        });
    } catch (ex) {
    }

    if (undefined === div.hidden) {
        try {
            Object.defineProperty(HTMLElement.prototype, 'hidden', {
                set: function (value) {
                    if (/true/i.test(value)) {
                        this.setAttribute('hidden', 'true');
                    } else {
                        this.removeAttribute('hidden');
                    }
                },
                get: function () {
                    return /true/i.test(this.getAttribute('hidden', 2));
                }
            });
        } catch (ex) {}
    }
}());

if (!window.GRAPP) {
    window.GRAPP = function () {
        'use strict';
        throw new TypeError('...');
    };
}

(function () {
    'use strict';
    var GRAPP = window.GRAPP;

    var ARRAY = Array.prototype,
        PRIVATE_VAR = new WeakMap(),
        prepare = function () {
            var priv = PRIVATE_VAR.get(this);

            if (priv && priv.handles) {
                return priv.handles;
            }

            priv = {
                handles: []
            };

            PRIVATE_VAR.set(this, priv);

            return PRIVATE_VAR.get(this).handles;
        };

    function Namespace() {
        throw new TypeError('Illegal constructor');
    }

    function Observer() {
        throw new TypeError('Illegal constructor');
    }

    Namespace.implement({
        setItem: function (name, value) {
            var parent = this,
                parts = name.split('.'),
                last = parts.pop(),
                ln = parts.length,
                i;

            for (i = 0; i < ln; i += 1) {
                parent = parent[parts[i]] = parent[parts[i]] || {};
            }

            if ((typeof parent).test(/string|number|boolean|undefined/)) {
                throw new TypeError('Element is not object');
            }

            parent[last] = value;
        }.setter(),
        getItem: function (name) {
            var parts,
                parent = this,
                ln,
                i;

            if (!name) {
                return parent;
            }

            parts = name.split('.');

            for (i = 0, ln = parts.length; i < ln; i += 1) {
                parent = parent[parts[i]];
                if (!parent) {
                    return;
                }
            }

            return parent;
        },
        removeItem: function (name) {
            var parts = name.split('.'),
                last = parts.pop(),
                parent = Namespace.prototype.getItem.call(this, parts.join('.'));

            return delete parent[last];
        },
        hasItem: function (name) {
            return !!Namespace.prototype.getItem.call(this, name);
        },
        setArrayItem: function (name, value) {
            var parts = name.split('.'),
                parent = this,
                ln = parts.length,
                i;

            for (i = 0; i < ln; i += 1) {
                parent = parent[parts[i]] = parent[parts[i]] || [];
                if (!parent.contains(value)) {
                    parent.push(value);
                }
            }
        }
    });

    Observer.implement({
        on: function (type, handle) {
            var handles,
                handleType = GRAPP.typeOf(handle),
                ix,
                k;

            if (type && handle && 'object' === handleType) {
                for (k in handle) {
                    if (handle.hasOwnProperty(k)) {
                        this.on((type + '.' + k), handle[k]);
                    }
                }
                return;
            }

            if ('function' !== handleType) {
                throw new TypeError('Handle must be a function');
            }

            if (!type) {
                throw new TypeError('Event type can not be empty');
            }

            handles = prepare.call(this);

            ix = handles.indexOf(handle);

            if (-1 === ix) {
                ix = handles.push(handle) - 1;
            }

            if ('string' === GRAPP.typeOf(type)) {
                type = type.split(/\s/);
            }

            type.forEach(function (element) {
                Namespace.prototype.setArrayItem.call(handles, element, ix);
            });

            return this;
        }.setter(),
        off:  function (type, handle) {
            var priv = PRIVATE_VAR.get(this),
                handles = (priv && priv.handles),
                ix;

            if (!handles) {
                return;
            }

            if (!handle) {
                Namespace.prototype.removeItem.call(handles, type);
                return;
            }

            if ('number' === typeof handle) {
                ix = handle;
            } else {
                ix = handles.indexOf(handle);
            }

            if (-1 !== ix) {
                handles[type].splice(handles[type].indexOf(ix), 1);
            }

            if (0 === this[type].length) {
                Namespace.prototype.removeItem.call(handles, type);
            }
        },
        notify: function (type) {
            var args = ARRAY.slice.call(arguments, 1),
                priv = PRIVATE_VAR.get(this),
                handles = (priv && priv.handles),
                results = [],
                list,
                ln,
                i;

            if (!handles) {
                return;
            }

            if (!type) {
                console.log(handles);
                return;
            }

            list = Namespace.prototype.getItem.call(handles, type) || [];

            for (i = 0, ln = list.length; i < ln; i += 1) {
                try {
                    results.push(handles[list[i]].apply((this.context || this), args));
                } catch (ex) {
                    console.error('Observer ' + (this.observername || '[noname]') + ' notify error: ' + ex + ' | type : ' + type);
                    console.error(ex.stack);
                }
            }

            return results.length > 1 ? results : results[0];
        }
    });

    GRAPP.assign({
        UID: function (max) {
            var now = Date.now() + 1,
                ch = now.toString(36),
                ln = ch.length - 1,
                rand,
                ret = [],
                i;

            for (i = 0; i < 36; i += 1) {
                rand = Math.floor(Math.random() * 10);
                if (ln < rand) {
                    rand = ln - 1;
                }

                ret.push(ch.charAt(rand));
            }

            return ret.slice(0, (max || 5)).join('');
        },
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
        DOMReady: (function (win, doc) {
            var readyBound = false,
                isReady = false,
                readyList = [],
                DOMContentLoaded;

            function domReady() {
                var ln,
                    i;

                if (isReady) {
                    return;
                }

                isReady = true;

                if (readyList) {
                    for (i = 0, ln = readyList.length; i < ln; i += 1) {
                        readyList[i].call(window, []);
                    }

                    readyList = [];
                }
            }

            function doScrollCheck() {
                if (isReady) {
                    return;
                }

                try {
                    doc.documentElement.doScroll('left');
                } catch (ex) {
                    win.setTimeout(doScrollCheck, 1);
                    return;
                }

                domReady();
            }

            DOMContentLoaded = doc.addEventListener ? function () {
                doc.removeEventListener('DOMContentLoaded', DOMContentLoaded, false);
                domReady();
            } : function () {
                if ('complete' === doc.readyState) {
                    doc.detachEvent("onreadystatechange", DOMContentLoaded);
                    domReady();
                }
            };

            // does the heavy work of working through the browsers idiosyncracies (let's
            // call them that) to hook onload.
            function bindReady() {
                var toplevel;
                if (readyBound) {
                    return;
                }
                readyBound = true;

                if ('complete' === doc.readyState) {
                    return domReady();
                }

                if (doc.addEventListener) {
                    doc.addEventListener('DOMContentLoaded', DOMContentLoaded, false);
                    win.addEventListener('load', domReady, false);

                } else if (doc.attachEvent) {
                    doc.attachEvent('onreadystatechange', DOMContentLoaded);
                    win.attachEvent('onload', domReady);

                    toplevel = false;

                    try {
                        toplevel = win.frameElement === null;
                    } catch (ex) {
                    }

                    if (doc.documentElement.doScroll && toplevel) {
                        doScrollCheck();
                    }
                }
            }

            return function (fn) {
                bindReady();
                if (isReady) {
                    fn.call(win);
                } else {
                    readyList.push(function () {
                        return fn.call(win);
                    });
                }
            };
        }(window, window.document)),
        Namespace: Namespace,
        Observer: Observer
    });

    GRAPP.dom = (function () {
        var hasClasslist = (function () {
            var div = document.createElement('div');

            return div.hasOwnProperty('classList');
        }());

        function regClass(cls) {
            return new RegExp("(^|\\s+)" + cls + "(\\s+|$)");
        }

        return {
            addClass: (hasClasslist ? function (el, name) {
                el.classList.add(name);
            } : function (el, name) {
                var has = regClass(name).test(el.className);
                if (!has) {
                    el.className = ([el.className, name].join(' ')).trim();
                }
            }),
            removeClass: (hasClasslist ? function (el, name) {
                el.classList.remove(name);
                if ('' === el.className.trim()) {
                    el.removeAttribute('class');
                }

            } : function (el, name) {
                var has;
                if (!el || !el.nodeName) {
                    return;
                }

                has = regClass(name).test(el.className);
                if (has) {
                    el.className = (el.className.replace(new RegExp('(?:^|\\s+)' + name), '')).trim();
                }
                if ('' === (el.className || '').trim()) {
                    el.removeAttribute('class');
                }
            }),
            hasClass: (hasClasslist ? function (el, name) {
                return el.classList.contains(name);
            } : function (el, name) {
                return regClass(name).test(el.className);
            }),
            setCss: function (el, css) {
                if (!el) {
                    return;
                }

                css = css || '';
                if (!css.trim()) {
                    if (el.removeAttribute) {
                        el.removeAttribute('style');
                    }
                    return;
                }

                try {
                    el.style.cssText = css;
                } catch (ex) {
                    if (el.style.setAttribute) {
                        el.style.setAttribute('cssText', css);
                    }
                }
            },
            getCss: function (el) {
                var value;

                if (!el) {
                    return '';
                }

                value = el.style.cssText || (el.style.getAttribute && el.style.getAttribute('cssText', 2)) || '';

                return value;
            }
        };
    }());
}());