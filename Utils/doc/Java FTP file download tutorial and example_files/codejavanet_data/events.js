!function(){"use strict";function a(){throw new TypeError("Illegal constructor")}var b=APP.Namespace.prototype,c=Array.prototype,d={elements:[],handlers:[],map:function(a){var b=this.elements.indexOf(a);return-1===b&&(b=this.elements.push(a)-1,this.handlers[b]={element:a,DOMElement:f.isDOMObject(a),listeners:{},fixed:{}}),this.handlers[b]}},e=Object.create({set:function(a,b){this[a]=b}.setter()}),f={isDOMObject:function(a){return!!a.addEventListener},getGlobalObject:function(a){var b=a.ownerDocument;return b?b.parentWindow||b.defaultView||a:a},prepare:function(a){return function b(c,e,f){var g,h,i;if("string"!=typeof e)APP.typeOf(e,"object")&&Object.keys(e).forEach(function(a){b(c,a,e[a])});else for(e=e.split(/\s/),h=0,i=e.length;i>h;h+=1)g=d.map(c,e[h]),a(c,e[h],f,g)}}},g=/^([^.]*)(?:\.(.+)|)$/;a.assign({add:f.prepare(function(a,d,f,h){var i,j,k,l,m=h.listeners,n=g.exec(d)[1];l=c.indexOf.call(m,f),-1===l&&(l=c.push.call(m,f)-1),b.setArrayItem.call(m,d,l),h.DOMElement&&(j=e[n],j&&(j.onAdd&&j.onAdd.call(a,f),j.condition&&(k=f,f=function(b){j.condition.call(b,a)&&k.call(a,b)}),d=j.base||d),i=function(b){f.call(a,b)},h.fixed[n]=h.fixed[n]||{},h.fixed[n][l]=i,h[n]||a.addEventListener(d,h[n]=function(a){var b,c,d=h.fixed[n],e=Object.keys(d)||[];for(c=0,b=e.length;b>c;c+=1)d[e[c]](a)},!1))}),remove:f.prepare(function(a,d,e,f){var h,i=f.listeners,j=b.getItem.call(f.listeners,d)||[],k=c.indexOf.call(i,e),l=g.exec(d)[1];if(-1!==k)return h=j.indexOf(k),j.splice(h,1),j.length||(a.removeEventListener(d,f[l],!1),b.removeItem.call(i,d),delete f[l]),delete f.fixed[l][k]}),fire:f.prepare(function(a,c,d,e){var f,g,h,i=b.getItem.call(e.listeners,c)||[];if(e.DOMElement)return f=document.createEvent("HTMLEvents"),f.initEvent(c,!0,!0),void a.dispatchEvent(f);for(Array.isArray(d)||(d=[d]),h=0,g=i.length;g>h;h+=1)e.listeners[i[h]].apply(e.context,d)})}).inherits().implement(),APP().plugin("domevents",{on:a.add,off:a.remove,fire:a.fire,custom:e})}(),function(){"use strict";function a(a){var c=this,d=c.relatedTarget;return void 0===d?!0:!1===d?!1:9!==a.nodeType&&d&&d!==a&&"xul"!==d.prefix&&!b(a,d)}var b,c=APP(),d=c.domevents,e=d.custom;b=function(a,c){(b=a.contains?function(a,b){return a!==b&&a.contains(b)}:function(a,b){return!!(16&a.compareDocumentPosition(b))})(a,c)},e.set({mouseOverOut:{onAdd:function(a){d.add(this,{mouseover:a,mouseout:a})},onRemove:function(a){d.remove(this,{mouseover:a,mouseout:a})}},mouseenter:{base:"mouseover",condition:a},mouseleave:{base:"mouseout",condition:a},mousehover:{onAdd:function(a){d.add(this,{mouseenter:a,mouseleave:a})},onRemove:function(a){d.remove(this,{mouseenter:a,mouseleave:a})}},mousewheel:{base:d.isGecko?"DOMMouseScroll":"mousewheel"},clickout:function(){var a,b=function(a,b){for(var c=a.target;c&&c.nodeType;){if(b&&(b.isSameNode&&b.isSameNode(c)||b.isEqualNode&&b.isEqualNode(c)))return!1;c=c.parentNode}return!0};return{onAdd:function(c){var e=this;d.on(e.ownerDocument||document,"click",a=function(a){b(a,e)&&c(a)})},onRemove:function(){d.off(this.ownerDocument||document,"click",a)}}}(),attributechange:function(){function a(a,b){var c=new MutationObserver(function(a){a.forEach(b)});return c.observe(a,f),c}function b(a){var b={prevValue:"oldValue",attrName:"attributeName"};return Object.keys(b).forEach(function(c){a[b[c]]=a[c]}),a}var c,e,f={attributes:!0,attributeOldValue:!0,childList:!1,characterData:!1};switch("function"==typeof global.MutationObserver?c="mutation":document.addEventListener?c="domevents":document.attachEvent&&(c="ieevents"),c){case"domevents":return{onAdd:function(a){var c=this;d.on(c,"DOMAttrModified",e=function(d){a.call(c,b(d))})},onRemove:function(){d.off(this,"DOMAttrModified",e)}};case"mutation":return{onAdd:function(b){e=a(this,b)},onRemove:function(){e.disconnect()}}}}()})}();