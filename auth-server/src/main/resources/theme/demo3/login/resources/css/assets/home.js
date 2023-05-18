/******/ (function(modules) { // webpackBootstrap
/******/ 	// install a JSONP callback for chunk loading
/******/ 	function webpackJsonpCallback(data) {
/******/ 		var chunkIds = data[0];
/******/ 		var moreModules = data[1];
/******/ 		var executeModules = data[2];
/******/
/******/ 		// add "moreModules" to the modules object,
/******/ 		// then flag all "chunkIds" as loaded and fire callback
/******/ 		var moduleId, chunkId, i = 0, resolves = [];
/******/ 		for(;i < chunkIds.length; i++) {
/******/ 			chunkId = chunkIds[i];
/******/ 			if(Object.prototype.hasOwnProperty.call(installedChunks, chunkId) && installedChunks[chunkId]) {
/******/ 				resolves.push(installedChunks[chunkId][0]);
/******/ 			}
/******/ 			installedChunks[chunkId] = 0;
/******/ 		}
/******/ 		for(moduleId in moreModules) {
/******/ 			if(Object.prototype.hasOwnProperty.call(moreModules, moduleId)) {
/******/ 				modules[moduleId] = moreModules[moduleId];
/******/ 			}
/******/ 		}
/******/ 		if(parentJsonpFunction) parentJsonpFunction(data);
/******/
/******/ 		while(resolves.length) {
/******/ 			resolves.shift()();
/******/ 		}
/******/
/******/ 		// add entry modules from loaded chunk to deferred list
/******/ 		deferredModules.push.apply(deferredModules, executeModules || []);
/******/
/******/ 		// run deferred modules when all chunks ready
/******/ 		return checkDeferredModules();
/******/ 	};
/******/ 	function checkDeferredModules() {
/******/ 		var result;
/******/ 		for(var i = 0; i < deferredModules.length; i++) {
/******/ 			var deferredModule = deferredModules[i];
/******/ 			var fulfilled = true;
/******/ 			for(var j = 1; j < deferredModule.length; j++) {
/******/ 				var depId = deferredModule[j];
/******/ 				if(installedChunks[depId] !== 0) fulfilled = false;
/******/ 			}
/******/ 			if(fulfilled) {
/******/ 				deferredModules.splice(i--, 1);
/******/ 				result = __webpack_require__(__webpack_require__.s = deferredModule[0]);
/******/ 			}
/******/ 		}
/******/
/******/ 		return result;
/******/ 	}
/******/
/******/ 	// The module cache
/******/ 	var installedModules = {};
/******/
/******/ 	// object to store loaded and loading chunks
/******/ 	// undefined = chunk not loaded, null = chunk preloaded/prefetched
/******/ 	// Promise = chunk loading, 0 = chunk loaded
/******/ 	var installedChunks = {
/******/ 		"home": 0
/******/ 	};
/******/
/******/ 	var deferredModules = [];
/******/
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/
/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId]) {
/******/ 			return installedModules[moduleId].exports;
/******/ 		}
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			i: moduleId,
/******/ 			l: false,
/******/ 			exports: {}
/******/ 		};
/******/
/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);
/******/
/******/ 		// Flag the module as loaded
/******/ 		module.l = true;
/******/
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/
/******/
/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;
/******/
/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;
/******/
/******/ 	// define getter function for harmony exports
/******/ 	__webpack_require__.d = function(exports, name, getter) {
/******/ 		if(!__webpack_require__.o(exports, name)) {
/******/ 			Object.defineProperty(exports, name, { enumerable: true, get: getter });
/******/ 		}
/******/ 	};
/******/
/******/ 	// define __esModule on exports
/******/ 	__webpack_require__.r = function(exports) {
/******/ 		if(typeof Symbol !== 'undefined' && Symbol.toStringTag) {
/******/ 			Object.defineProperty(exports, Symbol.toStringTag, { value: 'Module' });
/******/ 		}
/******/ 		Object.defineProperty(exports, '__esModule', { value: true });
/******/ 	};
/******/
/******/ 	// create a fake namespace object
/******/ 	// mode & 1: value is a module id, require it
/******/ 	// mode & 2: merge all properties of value into the ns
/******/ 	// mode & 4: return value when already ns object
/******/ 	// mode & 8|1: behave like require
/******/ 	__webpack_require__.t = function(value, mode) {
/******/ 		if(mode & 1) value = __webpack_require__(value);
/******/ 		if(mode & 8) return value;
/******/ 		if((mode & 4) && typeof value === 'object' && value && value.__esModule) return value;
/******/ 		var ns = Object.create(null);
/******/ 		__webpack_require__.r(ns);
/******/ 		Object.defineProperty(ns, 'default', { enumerable: true, value: value });
/******/ 		if(mode & 2 && typeof value != 'string') for(var key in value) __webpack_require__.d(ns, key, function(key) { return value[key]; }.bind(null, key));
/******/ 		return ns;
/******/ 	};
/******/
/******/ 	// getDefaultExport function for compatibility with non-harmony modules
/******/ 	__webpack_require__.n = function(module) {
/******/ 		var getter = module && module.__esModule ?
/******/ 			function getDefault() { return module['default']; } :
/******/ 			function getModuleExports() { return module; };
/******/ 		__webpack_require__.d(getter, 'a', getter);
/******/ 		return getter;
/******/ 	};
/******/
/******/ 	// Object.prototype.hasOwnProperty.call
/******/ 	__webpack_require__.o = function(object, property) { return Object.prototype.hasOwnProperty.call(object, property); };
/******/
/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "";
/******/
/******/ 	var jsonpArray = window["webpackJsonp"] = window["webpackJsonp"] || [];
/******/ 	var oldJsonpFunction = jsonpArray.push.bind(jsonpArray);
/******/ 	jsonpArray.push = webpackJsonpCallback;
/******/ 	jsonpArray = jsonpArray.slice();
/******/ 	for(var i = 0; i < jsonpArray.length; i++) webpackJsonpCallback(jsonpArray[i]);
/******/ 	var parentJsonpFunction = oldJsonpFunction;
/******/
/******/
/******/ 	// add entry module to deferred list
/******/ 	deferredModules.push(["./src/js/chunks/index.js","vendors"]);
/******/ 	// run deferred modules when ready
/******/ 	return checkDeferredModules();
/******/ })
/************************************************************************/
/******/ ({

/***/ "./src/js/chunks/index.js":
/*!********************************!*\
  !*** ./src/js/chunks/index.js ***!
  \********************************/
/*! no exports provided */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
eval("__webpack_require__.r(__webpack_exports__);\n/* harmony import */ var jquery__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! jquery */ \"jquery\");\n/* harmony import */ var jquery__WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(jquery__WEBPACK_IMPORTED_MODULE_0__);\n/* harmony import */ var slick_carousel__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! slick-carousel */ \"./node_modules/slick-carousel/slick/slick.js\");\n/* harmony import */ var slick_carousel__WEBPACK_IMPORTED_MODULE_1___default = /*#__PURE__*/__webpack_require__.n(slick_carousel__WEBPACK_IMPORTED_MODULE_1__);\n/* harmony import */ var bootstrap_dist_js_bootstrap__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! bootstrap/dist/js/bootstrap */ \"./node_modules/bootstrap/dist/js/bootstrap.js\");\n/* harmony import */ var bootstrap_dist_js_bootstrap__WEBPACK_IMPORTED_MODULE_2___default = /*#__PURE__*/__webpack_require__.n(bootstrap_dist_js_bootstrap__WEBPACK_IMPORTED_MODULE_2__);\n/* harmony import */ var lightgallery_dist_css_lightgallery_min_css__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! lightgallery/dist/css/lightgallery.min.css */ \"./node_modules/lightgallery/dist/css/lightgallery.min.css\");\n/* harmony import */ var lightgallery_dist_css_lightgallery_min_css__WEBPACK_IMPORTED_MODULE_3___default = /*#__PURE__*/__webpack_require__.n(lightgallery_dist_css_lightgallery_min_css__WEBPACK_IMPORTED_MODULE_3__);\n/* harmony import */ var lightgallery_dist_js_lightgallery_all__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! lightgallery/dist/js/lightgallery-all */ \"./node_modules/lightgallery/dist/js/lightgallery-all.js\");\n/* harmony import */ var lightgallery_dist_js_lightgallery_all__WEBPACK_IMPORTED_MODULE_4___default = /*#__PURE__*/__webpack_require__.n(lightgallery_dist_js_lightgallery_all__WEBPACK_IMPORTED_MODULE_4__);\n/* harmony import */ var animejs_lib_anime_es_js__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! animejs/lib/anime.es.js */ \"./node_modules/animejs/lib/anime.es.js\");\n/* harmony import */ var _sass_main_scss__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ../../sass/main.scss */ \"./src/sass/main.scss\");\n/* harmony import */ var _sass_main_scss__WEBPACK_IMPORTED_MODULE_6___default = /*#__PURE__*/__webpack_require__.n(_sass_main_scss__WEBPACK_IMPORTED_MODULE_6__);\n/* harmony import */ var _components_ticker__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ../components/ticker */ \"./src/js/components/ticker.js\");\n/* harmony import */ var _components_ticker__WEBPACK_IMPORTED_MODULE_7___default = /*#__PURE__*/__webpack_require__.n(_components_ticker__WEBPACK_IMPORTED_MODULE_7__);\n//https://iamakulov.com/notes/optimize-images-webpack/ for image optimization\n\n //bootstrap imports\n\n\n\n // import '../components/ticker'\n//Anime js import\n\n\njquery__WEBPACK_IMPORTED_MODULE_0___default()('[data-toggle=\"tooltip\"]').tooltip(); //custom css\n\n\n // import { CountUp } from 'countup.js';\n// if ($(\".remittance-carousel\")) {\n//   $(\".remittance-carousel\").slick({\n//     infinite: false,\n//     slidesToScroll: 1,\n//     slidesToShow: 8,\n//     arrows: false,\n//     dots: false,\n//     autoplay: true,\n//     responsive: [\n//       {\n//         breakpoint: 993,\n//         settings: {\n//           arrows: false,\n//           slidesToShow: 4,\n//         },\n//       },\n//       {\n//         breakpoint: 768,\n//         settings: {\n//           arrows: false,\n//           slidesToShow: 3,\n//         },\n//       },\n//       {\n//         breakpoint: 576,\n//         settings: {\n//           arrows: false,\n//           slidesToShow: 2,\n//         },\n//       },\n//     ],\n//   });\n// }\n\njquery__WEBPACK_IMPORTED_MODULE_0___default()(\"#burger\").click(function () {\n  var navbar = jquery__WEBPACK_IMPORTED_MODULE_0___default()(\".nav-small\");\n\n  if (navbar.hasClass(\"d-none\")) {\n    navbar.removeClass(\"d-none\");\n  } else {\n    navbar.addClass(\"d-none\");\n  }\n});\nvar header = document.getElementById(\"fixedcontent\");\n\nwindow.onscroll = function () {\n  scrollFunction();\n};\n\nfunction scrollFunction() {\n  if (document.body.scrollTop > 35 || document.documentElement.scrollTop > 35) {\n    document.getElementById(\"scrollBtn\").style.display = \"block\";\n    header.classList.add(\"show\");\n  } else {\n    document.getElementById(\"scrollBtn\").style.display = \"none\";\n    header.classList.remove(\"show\");\n  }\n} // When the user clicks on the button, scroll to the top of the document\n\n\njquery__WEBPACK_IMPORTED_MODULE_0___default()(\"#scrollBtn\").click(function () {\n  document.documentElement.scrollTo({\n    top: 0,\n    behavior: \"smooth\"\n  });\n});\n\nif (jquery__WEBPACK_IMPORTED_MODULE_0___default()(\".atmModal\")) {\n  var atmModals = document.querySelectorAll(\".atmModal\");\n  jquery__WEBPACK_IMPORTED_MODULE_0___default()(atmModals[0]).modal('show');\n  atmModals.forEach(function (atmModal, index) {\n    jquery__WEBPACK_IMPORTED_MODULE_0___default()(atmModal).on('hidden.bs.modal', function () {\n      var openModal = atmModals[index + 1];\n      jquery__WEBPACK_IMPORTED_MODULE_0___default()(openModal).modal('show');\n\n      if (index == atmModals.length - 1) {\n        loadAnimation();\n      }\n    });\n  });\n}\n\nvar servicesSection = document.querySelector(\".services-section\");\nvar productSection = document.querySelector(\".product-section\");\nvar loanSection = document.querySelector(\".loan-section\");\nvar remittanceSection = document.querySelector(\".remittance-section\");\nvar bankingSection = document.querySelector(\".banking-section\");\nvar newsSection = document.querySelector(\".news-section\");\nvar accountSection = document.querySelector(\".accounts\");\nvar footer = document.querySelector(\"footer\");\n\nfunction loadAnimation() {\n  if (servicesSection) {\n    var serviceAnimation = function serviceAnimation() {\n      //Carousel section\n      animejs_lib_anime_es_js__WEBPACK_IMPORTED_MODULE_5__[\"default\"].timeline({\n        easing: \"easeOutExpo\"\n      }).add({\n        targets: \".services-section .card\",\n        opacity: [0, 1],\n        translateY: [50, 0],\n        delay: function delay(el, i) {\n          return 400 * i;\n        }\n      });\n    };\n\n    var serviceObserver = new IntersectionObserver(function (entries) {\n      if (entries[0].intersectionRatio > 0) {\n        serviceAnimation();\n        serviceObserver.unobserve(entries[0].target);\n      }\n    });\n    serviceObserver.observe(servicesSection);\n  }\n\n  if (productSection) {\n    var productAnimation = function productAnimation() {\n      animejs_lib_anime_es_js__WEBPACK_IMPORTED_MODULE_5__[\"default\"].timeline({\n        easing: \"easeOutExpo\"\n      }) //Product Section\n      .add({\n        targets: \".product-section\",\n        opacity: [0, 1],\n        offset: \"-=1000\"\n      }).add({\n        targets: \".product-section .header\",\n        scale: [0.3, 1],\n        autoplay: true,\n        opacity: [0, 1],\n        translateZ: 0\n      }).add({\n        targets: \".product-section .animated-text\",\n        opacity: [0, 1],\n        translateX: [-100, 0],\n        delay: function delay(el, i) {\n          return 400 * i;\n        },\n        offset: \"-=1000\"\n      });\n    };\n\n    var prodObserver = new IntersectionObserver(function (entries) {\n      if (entries[0].intersectionRatio > 0) {\n        productAnimation();\n        prodObserver.unobserve(entries[0].target);\n      }\n    });\n    prodObserver.observe(productSection);\n  }\n}\n\nif (loanSection) {\n  var loanAnimation = function loanAnimation() {\n    //Loan Section\n    animejs_lib_anime_es_js__WEBPACK_IMPORTED_MODULE_5__[\"default\"].timeline({\n      easing: \"easeOutExpo\"\n    }).add({\n      targets: \".capital .card-04\",\n      opacity: [0, 1],\n      translateY: [20, 0],\n      delay: function delay(el, i) {\n        return 200 * i;\n      }\n    }).add({\n      targets: \".loan-section\",\n      opacity: [0, 1]\n    }, \"-=1000\").add({\n      targets: \".loan-section .animated-left\",\n      opacity: [0, 1],\n      translateX: [-100, 0]\n    }).add({\n      targets: \".loan-section .animated-right\",\n      opacity: [0, 1],\n      translateX: [100, 0]\n    }, \"-=1000\").add({\n      targets: \".loan-section .card\",\n      opacity: [0, 1],\n      translateY: [20, 0],\n      delay: function delay(el, i) {\n        return 200 * i;\n      }\n    }).add({\n      targets: \".loan-section .btn-success\",\n      opacity: [0, 1],\n      translateY: [20, 0],\n      offset: \"-=0\"\n    });\n  };\n\n  var loanObserver = new IntersectionObserver(function (entries) {\n    if (entries[0].intersectionRatio > 0) {\n      loanAnimation();\n      loanObserver.unobserve(entries[0].target);\n    }\n  });\n  loanObserver.observe(loanSection);\n}\n\nif (remittanceSection) {\n  var remittanceAnimation = function remittanceAnimation() {\n    //Remittance section\n    animejs_lib_anime_es_js__WEBPACK_IMPORTED_MODULE_5__[\"default\"].timeline({\n      easing: \"easeOutExpo\"\n    }).add({\n      targets: \".remittance-section\",\n      opacity: [0, 1]\n    }).add({\n      targets: \".remittance-title\",\n      opacity: [0, 1],\n      translateX: [100, 0]\n    }).add({\n      targets: \".remittance-carousel\",\n      opacity: [0, 1]\n    });\n  };\n\n  var remittanceObserver = new IntersectionObserver(function (entries) {\n    if (entries[0].intersectionRatio > 0) {\n      remittanceAnimation();\n      remittanceObserver.unobserve(entries[0].target);\n    }\n  });\n  remittanceObserver.observe(remittanceSection);\n}\n\nif (bankingSection) {\n  var bankingAnimation = function bankingAnimation() {\n    //Banking Section\n    animejs_lib_anime_es_js__WEBPACK_IMPORTED_MODULE_5__[\"default\"].timeline({\n      easing: \"easeOutExpo\"\n    }).add({\n      targets: \".banking-section\",\n      opacity: [0, 1]\n    }).add({\n      targets: \".animated-top\",\n      opacity: [0, 1],\n      translateY: [-100, 0]\n    }).add({\n      targets: \".app-store\",\n      opacity: [0, 1],\n      translateX: [100, 0]\n    }, \"-=1000\").add({\n      targets: \".animated-bottom\",\n      opacity: [0, 1],\n      translateY: [100, 0]\n    }, \"-=1000\").add({\n      targets: \"iframe\",\n      opacity: [0, 1],\n      translateX: [-100, 0]\n    }); // Mobile section\n\n    var mobileAnimation = Object(animejs_lib_anime_es_js__WEBPACK_IMPORTED_MODULE_5__[\"default\"])({\n      targets: \".mobile\",\n      translateY: \"10px\",\n      duration: 800,\n      loop: true,\n      direction: \"alternate\",\n      easing: \"linear\"\n    });\n    var mobileShadow = Object(animejs_lib_anime_es_js__WEBPACK_IMPORTED_MODULE_5__[\"default\"])({\n      targets: \".mobile-shadow\",\n      opacity: [0.8, 1],\n      loop: true,\n      easing: \"easeInOutExpo\",\n      duration: 1600\n    });\n  };\n\n  var bankingObserver = new IntersectionObserver(function (entries) {\n    if (entries[0].intersectionRatio > 0) {\n      bankingAnimation();\n      bankingObserver.unobserve(entries[0].target);\n    }\n  });\n  bankingObserver.observe(bankingSection);\n}\n\nif (newsSection) {\n  var newsAnimation = function newsAnimation() {\n    animejs_lib_anime_es_js__WEBPACK_IMPORTED_MODULE_5__[\"default\"].timeline({\n      easing: \"easeOutExpo\"\n    }).add({\n      targets: \".news-list li\",\n      opacity: [0, 1],\n      translateX: [-50, 0],\n      delay: function delay(el, i) {\n        return 400 * i;\n      }\n    });\n  };\n\n  var newsObserver = new IntersectionObserver(function (entries) {\n    if (entries[0].intersectionRatio > 0) {\n      newsAnimation();\n      newsObserver.unobserve(entries[0].target);\n    }\n  });\n  newsObserver.observe(newsSection);\n}\n\nif (accountSection) {\n  var accountAnimation = function accountAnimation() {\n    animejs_lib_anime_es_js__WEBPACK_IMPORTED_MODULE_5__[\"default\"].timeline({\n      easing: \"easeOutExpo\"\n    }).add({\n      targets: \".opening-section\",\n      opacity: [0, 1],\n      translateX: [100, 0]\n    }).add({\n      targets: \".animated-col\",\n      opacity: [0, 1],\n      translateX: [-100, 0],\n      delay: function delay(el, i) {\n        return 200 * i;\n      }\n    }).add({\n      targets: \".plantImg\",\n      opacity: [0, 1],\n      translateY: [100, 0]\n    });\n  };\n\n  var accountObserver = new IntersectionObserver(function (entries) {\n    if (entries[0].intersectionRatio > 0) {\n      accountAnimation();\n      accountObserver.unobserve(entries[0].target);\n    }\n  });\n  accountObserver.observe(accountSection);\n}\n\nif (footer) {\n  var footerAnimation = function footerAnimation() {\n    animejs_lib_anime_es_js__WEBPACK_IMPORTED_MODULE_5__[\"default\"].timeline({\n      easing: \"easeOutExpo\"\n    }).add({\n      targets: \"footer\",\n      opacity: [0, 1]\n    });\n  };\n\n  var footerObserver = new IntersectionObserver(function (entries) {\n    if (entries[0].intersectionRatio > 0) {\n      footerAnimation();\n      footerObserver.unobserve(entries[0].target);\n    }\n  });\n  footerObserver.observe(footer);\n}\n\nif (jquery__WEBPACK_IMPORTED_MODULE_0___default()(\".deposit\")) {\n  var sideMenu = document.querySelectorAll(\".sidemenu-list li a\");\n  sideMenu.forEach(function (menu) {\n    menu.addEventListener(\"click\", function (event) {\n      jquery__WEBPACK_IMPORTED_MODULE_0___default()(\".sidemenu-list li\").removeClass(\"active\");\n      this.parentNode.classList.add(\"active\");\n\n      if (this.hash !== \"\") {\n        event.preventDefault();\n        var hash = this.hash;\n        var scrollPosition = jquery__WEBPACK_IMPORTED_MODULE_0___default()(hash).offset().top - parseInt(\"100px\");\n        jquery__WEBPACK_IMPORTED_MODULE_0___default()(\"html, body\").animate({\n          scrollTop: scrollPosition\n        }, 800, function () {\n          window.location.hash = scrollPosition;\n        });\n      }\n    });\n  });\n}\n\nif (jquery__WEBPACK_IMPORTED_MODULE_0___default()(\".dropdown-mega\")) {\n  var navBarAnimation = function navBarAnimation() {\n    animejs_lib_anime_es_js__WEBPACK_IMPORTED_MODULE_5__[\"default\"].timeline({\n      easing: \"easeOutExpo\"\n    }).add({\n      targets: \".dropdown-mega li\",\n      opacity: [0, 1],\n      translateY: [-20, 0],\n      delay: function delay(el, i) {\n        return 50 * i;\n      }\n    }, \"-=600\");\n  };\n\n  var caller = document.querySelectorAll(\".caller\");\n  caller.forEach(function (call, index) {\n    call.addEventListener(\"mouseover\", function (e) {\n      navBarAnimation();\n    });\n  });\n} // if ($(\".gallery-item\")) {\n//   var openGallery = document.querySelectorAll(\".gallery-item\");\n//   openGallery.forEach(function (gallery) {\n//     gallery.addEventListener(\"click\", function () {\n//       var lightGallery = this.childNodes[5];\n//       console.log(lightGallery.childNodes);\n//       var image = lightGallery.childNodes[1].querySelector(\"img\");\n//       console.log(image);\n//       $(image).trigger(\"click\");\n//     });\n//   });\n//   $(\".lightgallery\").lightGallery({\n//     thumbnail: true,\n//     animateThumb: true,\n//     showThumbByDefault: true,\n//   });\n// }\n// let sections = document.getElementsByClassName(\"snapscroll\");\n// // tracks index of current section\n// let currentSectionIndex = 0;\n// document.addEventListener(\"wheel\", (e) => {\n//   if (e.wheelDeltaY > 0 && currentSectionIndex - 1 >= 0) {\n//     // wheel up\n//     console.log(sections[currentSectionIndex]);\n//     sections[currentSectionIndex].className = \"snapscroll\";\n//     currentSectionIndex--;\n//     sections[currentSectionIndex].className = \"snapscroll active\";\n//   } else if (e.wheelDeltaY < 0 && currentSectionIndex + 1 < sections.length) {\n//     // wheel down\n//     sections[currentSectionIndex].className = \"snapscroll\";\n//     currentSectionIndex++;\n//     sections[currentSectionIndex].className = \"snapscroll active\";\n//   }\n// });\n// if($('.video-section')){\n//   $('.slider-for').slick({\n//     slidesToShow: 1,\n//     slidesToScroll: 1,\n//     arrows: false,\n//     fade: true,\n//     asNavFor: '.slider-nav',\n//     dots:false\n//   });\n//   $('.slider-nav').slick({\n//     slidesToShow: 3,\n//     slidesToScroll: 1,\n//     asNavFor: '.slider-for',\n//     dots: false,\n//     arrows:false,\n//     centerMode: false,\n//     focusOnSelect: true,\n//     vertical:true,\n//   });\n// }\n// const capitalSection = document.querySelector(\".capital\");\n// const capitalObserver = new IntersectionObserver((entries) => {\n//   if (entries[0].intersectionRatio > 0) {\n//     initiateCountUp()\n//     capitalObserver.unobserve(entries[0].target);\n//   }\n// });\n// capitalObserver.observe(capitalSection);\n// function initiateCountUp(){\n//   var counter = document.querySelectorAll(\".countUp\");\n//   if (counter){\n//     counter.forEach(function (count) {\n//     setTimeout(function(){\n//         var countUp = new CountUp(count, 1000 );\n//         countUp.start();\n//       }, 1500)\n//     })\n//   }\n// }\n\n\nif (jquery__WEBPACK_IMPORTED_MODULE_0___default()(\".js-conveyor-example\")) {\n  jquery__WEBPACK_IMPORTED_MODULE_0___default()(function () {\n    jquery__WEBPACK_IMPORTED_MODULE_0___default()('.js-conveyor-example').jConveyorTicker({\n      force_loop: true,\n      anim_duration: 100\n    });\n  });\n}\n\n//# sourceURL=webpack:///./src/js/chunks/index.js?");

/***/ }),

/***/ "./src/js/components/ticker.js":
/*!*************************************!*\
  !*** ./src/js/components/ticker.js ***!
  \*************************************/
/*! no static exports found */
/***/ (function(module, exports) {

eval("/*! jQuery Conveyor Ticker (jConveyorTicker) v1.0.2 - Licensed under the MIT license - Copyright (c) 2017 Luis Luz - UXD Lda <dev@uxd.pt> / Project home: https://github.com/lluz/jquery-conveyor-ticker */\n!function (a, b, c, d) {\n  a.fn.jConveyorTicker = function (b) {\n    if (void 0 === this || 0 === this.length) return console.log(\"jquery.jConveyorTicker() INITIALIZATION ERROR: You need to select one or more elements. See documentation form more information.\"), !1;\n    var c = {\n      anim_duration: 200,\n      reverse_elm: !1,\n      force_loop: !1\n    },\n        d = c.anim_duration,\n        e = c.reverse_elm,\n        f = c.force_loop;\n    b && (void 0 !== b.anim_duration && (d = b.anim_duration), void 0 !== b.reverse_elm && (e = b.reverse_elm), void 0 !== b.force_loop && (f = b.force_loop), a.extend(c, b)), this.each(function () {\n      var b = a(this),\n          c = b.children(\"ul\");\n      c.css({\n        margin: \"0\",\n        padding: \"0\",\n        \"list-style\": \"none\"\n      }).children(\"li\").css({\n        display: \"inline-block\"\n      });\n      var g = c.width(),\n          h = c.parent().width(),\n          i = h / 2 - 20;\n      c.removeAttr(\"style\").children(\"li\").removeAttr(\"style\"), b.addClass(\"jctkr-wrapper\");\n\n      var j = function j() {\n        var f = c.clone().children(\"li\");\n        c.append(f);\n        var g = 0;\n        c.children().each(function () {\n          g += a(this).outerWidth();\n        }), c.width(g);\n\n        var h = function h(a) {\n          var b,\n              e = c.width(),\n              f = c.position().left,\n              g = \"-\",\n              i = \"normal\";\n\n          if (void 0 !== a && \"reverse\" === a) {\n            if (b = e / 2, f > 0) return c.css(\"left\", \"-\" + b + \"px\"), void h(\"reverse\");\n            g = \"+\", i = \"reverse\";\n          } else if (b = e / 2 * -1, f < b) {\n            var j = -1 * (b - f);\n            return c.css(\"left\", j + \"px\"), void h(i);\n          }\n\n          c.animate({\n            left: g + \"=10px\"\n          }, d, \"linear\", function () {\n            h(i);\n          });\n        };\n\n        b.hover(function () {\n          c.stop();\n        }, function () {\n          c.stop(), h(\"normal\");\n        }), e && b.prev(\".jctkr-label\").hover(function () {\n          c.stop(), h(\"reverse\");\n        }, function () {\n          c.stop(), h(\"normal\");\n        }).click(function () {\n          return !1;\n        }), h(\"normal\");\n      };\n\n      if (g >= i) j();else if (f) {\n        var k,\n            l = 0,\n            m = function m() {\n          var a = c.clone().children(\"li\");\n          if (c.append(a), k = c.width(), l = c.parent().width(), !(k < l)) return j(), !1;\n          m();\n        };\n\n        for (m(); k < l;) {\n          if (k >= i) {\n            j();\n            break;\n          }\n\n          m();\n        }\n      }\n      b.addClass(\"jctkr-initialized\");\n    });\n  };\n}(jQuery, window, document);\n\n//# sourceURL=webpack:///./src/js/components/ticker.js?");

/***/ }),

/***/ "./src/sass/main.scss":
/*!****************************!*\
  !*** ./src/sass/main.scss ***!
  \****************************/
/*! no static exports found */
/***/ (function(module, exports) {

eval("// removed by extract-text-webpack-plugin\n\n//# sourceURL=webpack:///./src/sass/main.scss?");

/***/ }),

/***/ "jquery":
/*!*************************!*\
  !*** external "jQuery" ***!
  \*************************/
/*! no static exports found */
/***/ (function(module, exports) {

eval("module.exports = jQuery;\n\n//# sourceURL=webpack:///external_%22jQuery%22?");

/***/ })

/******/ });