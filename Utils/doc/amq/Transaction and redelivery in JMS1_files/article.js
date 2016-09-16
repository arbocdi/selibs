(function( $ ){
	$.fn.resizeCarousel = function() {
		var carouselCropWidth = $(this).find('.carousel-inside-crop').width(); 
			
    	var itemDiv = $(this).find('.carousel-items > div');
    	itemDiv.width(carouselCropWidth);  
    	
    	return this;
    };
 })( jQuery );

function initZooms() {
	//conditional lightbox for slideshow "see larger image"
	if($thm.deviceWidthAtLeast($thm.deviceBreakpoints.tablet)){
		$('article .zoom').colorbox({
			rel:'article-gallery',
			maxWidth:$thm.deviceWidth,
		});
	}
	else {
		$.colorbox.remove();//remove any stray instances that may be hanging around
		$('article .zoom').attr('target','_blank');
	}
}

$(document).ready(function(){
	
	//zoom image functionality
	$('article .zoom').append('<div class="zoom-icon"></div>');
	$('article .zoom').addClass("article-gallery");
	$('article .zoom').attr("title",function() { 
		if ($(this).parent().find('figcaption').text()) {
			return $(this).parent().find('figcaption').text();
		}
		else 
			return $(this).find("img").attr("alt");
	});
	//bind initZooms() function to resize and trigger resize on ready
	$(window).resize(function() { 
		initZooms();
	}).resize();

	
	//initialize Prettify, which cleans up code samples
	prettyPrint();
	
	//accordion behavior for multiple pros/cons
	//Used on Test Center/reviews
	$(".proscons-wrapper.multi .ss-icon").click(function() {
		var clickedProsCons = $(this).parents(".proscons");
		if (clickedProsCons.hasClass("active")) {
			var toHide = clickedProsCons.find(".proscons-left, .proscons-right");
			toHide.slideUp();
			clickedProsCons.removeClass("active");
			$(this).text("dropdown");
		}
		else {
			var toShow = clickedProsCons.find(".proscons-left, .proscons-right");
			toShow.slideDown();
			clickedProsCons.addClass("active");
			$(this).text("directup");
		}
	});

});


//user taps the promo handles - needs to be $(document).on() because the promo modules load via ajax
$(document).on('click', '.handle', function(e){
	$(this).prev().addClass('open');
    $(this).addClass('open');
});

//collapse and expand tables in CW wide articles - adding this in the global article.js in case of article-sharing

$(document).ready(function(){

	$(".sectionHeader").next().css("display","none");
	$(".sectionHeader").first().next().css("display","block");
	
	$(".sectionHeader").click(function(){
		$(this).next().slideToggle('slow');
	});

  
});