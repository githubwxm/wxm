define(['jquery'],function($){
  var Dialog = function(){
    this.confirm = function(title,content,callback){
      var me = this;
      if(typeof $("#dialog_confirm").html() == 'undefined'){
        var str = '<div id="dialog_confirm" aria-hidden="true" aria-labelledby="myModalLabel" role="dialog" tabindex="-1" id="myModal2" class="modal fade">'
          +'<div class="modal-dialog">'
          +'<div class="modal-content">'
          +'<div class="modal-header">'
          +'<button aria-hidden="true" data-dismiss="modal" class="close" type="button">×</button>'
          +'<h4 class="modal-title" id="dialog_confirm_title">'+title+'</h4>'
          +'</div>'
          +'<div class="modal-body" id="dialog_confirm_content">'+content+'</div>'
          +'<div class="modal-footer">'
          +'<button type="button" class="btn btn-warning" id="btn_sure">确认</button>'
          +'<button type="button" class="btn btn-default" id="btn_close" data-dismiss="modal">关闭</button>'
          +'</div>'
          +'</div>'
          +'</div>'
          +'</div>';
        $('body').append(str);
      }else{
    	  $("#dialog_confirm_title").html(title);
    	  $("#dialog_confirm_content").html(content);
      }
      
      $("#btn_sure").unbind('click'); 
      $("#btn_close").unbind('click'); 
      $("#btn_sure").bind("click",function(){
		  me.close();
		  callback(true);
      });
      $("#btn_close").bind("click",function(){
        me.close();
        callback(false);
      });
      this.close = function(){
        $("#dialog_confirm").modal('hide');
      };
      $("#dialog_confirm").modal('show');
    };
    this.alert= function(title,content,callback){
    	var me = this;
    	if(typeof $("#dialog_alert").html() == 'undefined'){
    		var str = '<div id="dialog_alert" aria-hidden="true" aria-labelledby="myModalLabel" role="dialog" tabindex="-1" id="myModal3" class="modal fade">'
                +'<div class="modal-dialog">'
                +'<div class="modal-content">'
                +'<div class="modal-header">'
                +'<button aria-hidden="true" data-dismiss="modal" class="close" type="button" id="btn_alert_close">×</button>'
                +'<h4 class="modal-title" id="dialog_alert_title">'+title+'</h4>'
                +'</div>'
                +'<div class="modal-body" id="dialog_alert_content">'+content+'</div>'
                +'<div class="modal-footer">'
                +'<button type="button" class="btn btn-danger" id="btn_alert_sure">确定</button>'
                +'</div>'
                +'</div>'
                +'</div>'
                +'</div>';
        	$('body').append(str);
    	}else{
    		$("#dialog_alert_title").html(title);
      	    $("#dialog_alert_content").html(content);
    	}
    	$("#btn_alert_sure").bind("click",function(){
      	  $("#btn_alert_sure").unbind('click'); 
  		  me.close();
        });
    	$("#btn_alert_close").bind("click",function(){
        	  $("#btn_alert_close").unbind('click'); 
    		  me.close();
          });
    	this.close = function(){
    		 $("#dialog_alert").modal('hide');
    		if(callback){
    			callback();
    		}
    	};
    	$("#dialog_alert").modal('show');
    };
  };
  $.dialog =new Dialog();
  $.Dialog =new Dialog();
});