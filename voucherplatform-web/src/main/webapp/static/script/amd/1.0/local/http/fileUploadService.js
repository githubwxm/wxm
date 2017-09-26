/**
 * Created by Zale on 6/18/2014.
 */
define(['jquery','ajaxfileupload'],function($){
  return {
<<<<<<< HEAD
    service:'api/client/',
=======
    service:'/voucher/api/client/',
>>>>>>> fix_master
    doUpload : function(id,successMethod,failedMethod,url){
    	 $.ajaxFileUpload({
             url:url,
             secureuri:false,
             fileElementId:id,
             dataType: 'json',
             success: function(data,status){
               successMethod(data, status)
             } ,
             error: function(data, status, e){
               failedMethod(data, status, e)
             }
          });
    },
    upload : function(id,successMethod,failedMethod,cutModel){
	      var url=this.service+'noauth/upload';
	      if(typeof cutModel!='undefined'&&typeof cutModel.x!='undefined'&&typeof cutModel.y!='undefined'&&typeof cutModel.width!='undefined'&&typeof cutModel.height!='undefined'){
	        url+=('?x='+cutModel.x+'&y='+cutModel.y+'&width='+cutModel.width+'&height='+cutModel.height);
	      }
	      this.doUpload(id,successMethod,failedMethod,url);
    },
    uploadFile : function(id,successMethod,failedMethod,paramMap){
        var url=All580.serverName+'/upload';
        if(typeof(paramMap) != 'undefined'){
        	var index = 0;
        	$.each(paramMap, function(key, value){
        		if(index == 0){
        			url+=('?'+key+'='+value);
        		}else{
        			url+=('&'+key+'='+value);
        		}
        		index++;
        	});
        }
        this.doUpload(id,successMethod,failedMethod,url);
    },
	uploadFileByBase64:function(file,successMethod,failedMethod,cutModel){
      if(/image\/\w+/.test(file.type)){
		  var reader = new FileReader();
		  var url=this.service+'noauth/post/file|add|json?fileName='+file.name;
		  if(typeof cutModel!='undefined'&&typeof cutModel.x!='undefined'&&typeof cutModel.y!='undefined'&&typeof cutModel.width!='undefined'&&typeof cutModel.height!='undefined'){
			url+=('&x='+cutModel.x+'&y='+cutModel.y+'&width='+cutModel.width+'&height='+cutModel.height);
		  }
		  reader.readAsDataURL(file);
		  reader.onload=function(f){
			var payLoad = JSON.stringify({file:this.result.replace(/^data:image\/\w+;base64,/, "")});
			$.ajax({
				  url: url,
				  type: "POST",
				  data: payLoad,
				  timeout:300000,
				  contentType : 'application/json',
			  success: function(data,status){
				successMethod(data, status)
			  },
			  error: function(data, status, e){
				failedMethod(data, status, e)
			  }
			});
		  }
	  }
    },
	uploadImageByBase64:function(fileId,image,successMethod,failedMethod,cutModel){
		 if(typeof FileReader == 'undefined'){
			this.upload(fileId,successMethod,failedMethod,cutModel);
			return;
		  }
		  var url=this.service+'noauth/post/file|add|json?fileName='+image.name;
		  if(typeof cutModel!='undefined'&&typeof cutModel.x!='undefined'&&typeof cutModel.y!='undefined'&&typeof cutModel.width!='undefined'&&typeof cutModel.height!='undefined'){
			url+=('&x='+cutModel.x+'&y='+cutModel.y+'&width='+cutModel.width+'&height='+cutModel.height);
		  }
		  var payLoad = JSON.stringify({"file":image.src.replace(/^data:image\/\w+;base64,/, "")});
		  $.ajax({
		    url: url,
		    type: "POST",
		    data: payLoad,
		    contentType : 'application/json',
		    success: function(data,status){
			  successMethod(data, status)
		   },
		    error: function(data, status, e){
			  failedMethod(data, status, e)
		    }
		});
    },
    uploadImageByJson:function(id,successMethod,failedMethod,cutModel){

      if(typeof FileReader == 'undefined'){
        this.upload(id,successMethod,failedMethod,cutModel);
        return;
      }
      var file=$('#'+id)[0].files[0];
      if(!/image\/\w+/.test(file.type)){
        this.upload(id,successMethod,failedMethod,cutModel);
        return;
      }
	  this.uploadFileByBase64(file, successMethod, failedMethod, cutModel);
    },
    maintainAddFile:function(id,successMethod,failedMethod){
    var url=this.service+'core/maintain/fs';

    this.doUpload(id,successMethod,failedMethod,url);
  },
    maintainUptFile:function(id,filename,successMethod,failedMethod){
      var url=this.service+'core/maintain/fs/upt?filename='+filename;

      this.doUpload(id,successMethod,failedMethod,url);
    },
    maintainDelFile:function(filename){
      var url=this.service+'core/del/maintain|fs?filename='+filename;

      return $.ajax({
        url: url,
        type: "POST",
        contentType : 'application/json'
      });
    }
  }
  })