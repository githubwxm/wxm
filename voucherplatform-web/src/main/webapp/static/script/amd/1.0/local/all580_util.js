define(['vue','vueResource'],function(Vue,vueResource){
    Vue.prototype.menu_replace = function (str){
        return str.replace(/\(.*\)$/,'');
    };
    Vue.prototype.hasrole = function (func){
        var asset = window.top.rootVue.serverData.asset;
        if(asset == undefined || asset[func] == undefined){
            return false;
        }
        return true;
    };
    var fnr = {request:{}};
    fnr.Cookie = {
        get:function(name){
            var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");
            if(arr=document.cookie.match(reg))return unescape(arr[2]);
            return null;
        },set:function(name,value){
            var Days = 30;
            var exp = new Date();
            exp.setTime(exp.getTime() + Days*24*60*60*1000);
            document.cookie = name + "="+ escape (value) + ";expires=" + exp.toGMTString();
        }
    };
    fnr.BMap = {
        changeByAddress:function(addr){
            var map = new BMap.Map("l-map");
            map.enableScrollWheelZoom();
            var myGeo = new BMap.Geocoder();
            myGeo.getPoint(addr, function(point){
                if (point) {
                    map.centerAndZoom(point,16);
                    var marker = new BMap.Marker(point);
                    map.addOverlay(marker);
                    marker.enableDragging();
                    window.rootVue.subFormData.props.map = point.lng+','+ point.lat;
                    marker.addEventListener("dragend", function (e) {
                        window.rootVue.subFormData.props.map = e.point.lng+','+ e.point.lat;
                        console.log("当前位置：" + e.point.lng + ", " + e.point.lat);
                    });
                    map.addEventListener("click", function(e){
                        window.rootVue.subFormData.props.map = e.point.lng+','+ e.point.lat;
                        var now_point =  new BMap.Point(e.point.lng, e.point.lat );
                        marker.setPosition(now_point);//设置覆盖物位置
                    });
                }else{
                    alert("您选择地址没有解析到结果!");
                }
            });
        },

        changeAddress:function(addr){
            var map = new BMap.Map("l-map");
            map.enableScrollWheelZoom();
            var myGeo = new BMap.Geocoder();
            myGeo.getPoint(addr, function(point){
                if (point) {
                    map.centerAndZoom(point,16);
                    var marker = new BMap.Marker(point);
                    map.addOverlay(marker);
                    marker.enableDragging();
                    window.rootVue.subFormData.map = point.lng+','+ point.lat;
                    marker.addEventListener("dragend", function (e) {
                        window.rootVue.subFormData.map = e.point.lng+','+ e.point.lat;
                        console.log("当前位置：" + e.point.lng + ", " + e.point.lat);
                    });
                    map.addEventListener("click", function(e){
                        window.rootVue.subFormData.map = e.point.lng+','+ e.point.lat;
                        var now_point =  new BMap.Point(e.point.lng, e.point.lat );
                        marker.setPosition(now_point);//设置覆盖物位置
                    });
                }else{
                    alert("您选择地址没有解析到结果!");
                }
            });
        },
        changeByPoint:function(x,y){
            var map = new BMap.Map("l-map");
            map.enableScrollWheelZoom();
            var point = new BMap.Point(x,y);
            var marker = new BMap.Marker(point);
            marker.enableDragging();
            map.addOverlay(marker);
            map.centerAndZoom(point, 16);
            marker.addEventListener("dragend", function (e) {
                window.rootVue.subFormData.props.map = e.point.lng+','+ e.point.lat;
                console.log("当前位置：" + e.point.lng + ", " + e.point.lat);
            });
            map.addEventListener("click", function(e){
                window.rootVue.subFormData.props.map = e.point.lng+','+ e.point.lat;
                var now_point =  new BMap.Point(e.point.lng, e.point.lat );
                marker.setPosition(now_point);//设置覆盖物位置
            });
        },
        changeByPointIsHotel:function(x,y){
            var map = new BMap.Map("l-map");
            map.enableScrollWheelZoom();
            var point = new BMap.Point(x,y);
            var marker = new BMap.Marker(point);
            marker.enableDragging();
            map.addOverlay(marker);
            map.centerAndZoom(point, 16);
            marker.addEventListener("dragend", function (e) {
                //window.rootVue.subFormData.props.map = e.point.lng+','+ e.point.lat;
                console.log("当前位置：" + e.point.lng + ", " + e.point.lat);
            });
            /*map.addEventListener("click", function(e){
                //window.rootVue.subFormData.props.map = e.point.lng+','+ e.point.lat;
                var now_point =  new BMap.Point(e.point.lng, e.point.lat );
                marker.setPosition(now_point);//设置覆盖物位置
            });*/
        }
    };
    fnr.visitorTmpl= global_config.resBaseUrl + '/download/游客导入模板示例.xlsx'; // 团队成员模板地址
    fnr.visitorItineraryTmpl= global_config.resBaseUrl + '/download/出游人导入模板示例.xlsx';
    fnr.adjustIframeHeight = function(){ return; };
    fnr.getQueryString = function (name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
        var r = window.location.search.substr(1).match(reg);
        if (r != null) return decodeURIComponent(r[2]);
        return undefined;
    };
    fnr.isIdentityCardNumber = function (code) {
        var city = {
            11: "北京",
            12: "天津",
            13: "河北",
            14: "山西",
            15: "内蒙古",
            21: "辽宁",
            22: "吉林",
            23: "黑龙江 ",
            31: "上海",
            32: "江苏",
            33: "浙江",
            34: "安徽",
            35: "福建",
            36: "江西",
            37: "山东",
            41: "河南",
            42: "湖北 ",
            43: "湖南",
            44: "广东",
            45: "广西",
            46: "海南",
            50: "重庆",
            51: "四川",
            52: "贵州",
            53: "云南",
            54: "西藏 ",
            61: "陕西",
            62: "甘肃",
            63: "青海",
            64: "宁夏",
            65: "新疆",
            71: "台湾",
            81: "香港",
            82: "澳门",
            91: "国外 "
        };
        var tip = "";
        var pass = true;

        if (!code || !/^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$|^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}([0-9]|X|x)$/i.test(code)) {    //  18位验证
            tip = "身份证号格式错误";
            pass = false;
        } else if (!city[code.substr(0, 2)]) {
            tip = "地址编码错误";
            pass = false;
        } else {
            //18位身份证需要验证最后一位校验位
            if (code.length == 18) {
                code = code.split('');
                //∑(ai×Wi)(mod 11)
                //加权因子
                var factor = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2];
                //校验位
                var parity = [1, 0, 'X', 9, 8, 7, 6, 5, 4, 3, 2,'x'];
                var sum = 0;
                var ai = 0;
                var wi = 0;
                for (var i = 0; i < 17; i++) {
                    ai = code[i];
                    wi = factor[i];
                    sum += ai * wi;
                }
                var last = parity[sum % 11];
                if (parity[sum % 11] != code[17]) {
                    tip = "校验位错误";
                    pass = false;
                }
            }
        }
        // if(!pass) alert(tip);
        return pass;
    },
    fnr.format = function(time, format) {
        if(time == undefined){
            return '';
        }
        var finalTime;
        if(typeof(time) == 'string'){
            var timeIOS = time.replace(/\-/g, "/");
            finalTime = new Date(timeIOS);
        }else {
            finalTime = time;
        }

        var t = new Date(finalTime);
        var tf = function (i) { return (i < 10 ? '0' : '') + i };
        return format.replace(/yyyy|MM|dd|HH|mm|ss/g, function(a) {
            switch (a) {
                case 'yyyy':
                    return tf(t.getFullYear());
                    break;
                case 'MM':
                    return tf(t.getMonth() + 1);
                    break;
                case 'mm':
                    return tf(t.getMinutes());
                    break;
                case 'dd':
                    return tf(t.getDate());
                    break;
                case 'HH':
                    return tf(t.getHours());
                    break;
                case 'ss':
                    return tf(t.getSeconds());
                    break;
            }
        });
    };
    /*根据日期获取星期几*/
    fnr.getWeekDay = function(temp){
        var weekArray = new Array("星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六");
        var weekDay = weekArray[new Date(temp).getDay()];
        return weekDay;
    };
    // 单位转换成元
    fnr.formatNumber = function(numbers) {
        return Number(numbers/100).toFixed(2);
    };

    // 判断数组是否包含某元素
    fnr.contains = function(arr, obj) {
        var i = arr.length;
        while (i--) {
            if (arr[i] === obj) {
                return true;
            }
        }
        return false;
    };

    fnr.each = function(object,func){
        for(var k in object){
            func(k,object[k]);
        }
    };
    fnr.clearVueList = function(list){
        while(list.length > 0){
            list.$remove(list[0]);
        }
    };
    fnr.mergeJson = function(from,to){
        if(from == undefined)return to;
        this.each(from,function(k,v){
            if(v == undefined)return;
            to[k] = v;
        });
        return to;
    };
    fnr.ajaxJson=function(url,params,options){
        var loading = fnr.openLoading();
        var def_options = {
            method: 'POST',
            alertMessage: 1 //0-不显示消息 1-不显示succ消息 2-显示所有消息
        };
        fnr.mergeJson(options,def_options);
        var reqParam;
        if(def_options.method == 'POST'){
            reqParam = params;
        }else{
            reqParam = { headers: {'Content-Type': 'application/x-www-form-urlencoded'}, params: params };
        }

        Vue.use(vueResource);var deferred;
        if(def_options.method == 'POST'){
            deferred = new Vue().$http.post(url,reqParam);
        }else{
            deferred = new Vue().$http.get(url,reqParam);
        }
        deferred.then(function(result){
            fnr.closeLoading(loading);
            var resp = result.json();
            if(resp.code == 99){
<<<<<<< HEAD
                window.top.location = '/error/403.html';
=======
                window.top.location = '/voucher/error/403.html';
>>>>>>> fix_master
                return;
            }
            if(resp.code == 98){
                fnr.showErrMessage('没有权限');
                return;
            }
            if (resp.code == 200){
                if(def_options.alertMessage == 2){
                    if(resp.message != null ){
                        fnr.showMessage(resp.message);
                    }
                }
            }else{
                if( def_options.alertMessage != 0 ){
                    if(resp.code == 111){
                        if(resp.message != null ){
                            fnr.alertErr(resp.message);
                        }else{
                            fnr.alertErr('操作失败');
                        }
                    }else{
                        if(resp.message != null ){
                            fnr.showErrMessage(resp.message);
                        }else{
                            fnr.showErrMessage('操作失败');
                        }
                    }
                }
            }
        },function(error){
            fnr.closeLoading(loading);
            console.log(error);
            if(def_options.alertMessage != 0){
                fnr.showMessage('网络错误');
            }
        }).catch(function(exception){
            fnr.closeLoading(loading);
            console.log(exception);
            if(def_options.alertMessage != 0){
                fnr.showMessage('数据格式错误');
            }
        });
        return deferred;

    };
    fnr.getElFromJson = function(json,el){
        if(el==undefined || el == '')return json;
        var arr = el.split('.');
        var result = json;
        fnr.each(arr,function(k,v){
            result = result[v];
        });
        return result;
    };

    var LoadingComponent = Vue.extend({
        template:
        '<div style="width:100%; height:100%; position:fixed; left:0px;top:0px;z-index: 99999998">' +
        '<div style="position:absolute; left:50%; top:50%; margin-left:-50px; margin-top:-50px; border:1px solid #e6e6e6; border-radius:5px;">' +
<<<<<<< HEAD
        '<img src="../static/images/2222023P4-12.gif" />' +
=======
        '<img src="/voucher/static/images/2222023P4-12.gif" />' +
>>>>>>> fix_master
        '</div>' +
        '</div>',
        methods:{
            close:function(){
                this.$destroy(true);
            }
        }
    });
    fnr.openLoading = function(){
        var loading = new LoadingComponent();
        loading.$mount().$appendTo('body');
        return loading;
    };
    fnr.closeLoading = function(loading){
        loading.close();
    };

    fnr.openFrame = function(params){
        window.top.rootVue.openFrame(params);
    };
    fnr.dialogArr = {};
    fnr.iDialog = function(params){
        var defParams = {
            id:'dialog_'+new Date().getTime(),
            title:'',
            parent:'top',
            model:true,
            width:'800',
            minWidth:'300',
            height:'600',
            callbackSucc:function(result){},
            callbackCancel:function(result){}
        };
        fnr.mergeJson(params,defParams);
        if(defParams.parent == 'top'){
            if(window.top!=window){
                window.top.rootVue.fnr.iDialog(defParams);return;
            }
        }else{
            if(window.top != window.parent){
                window.parent.rootVue.fnr.iDialog(defParams);return;
            }
        }

        var maxWidth = window.innerWidth;
        if(maxWidth < defParams.width){
            defParams.width = maxWidth;
        }

        var maxHeight = window.innerHeight - 200;
        if(maxHeight < defParams.height){
            defParams.height = maxHeight;
        }

        var template = '';
        if(defParams.text == undefined){
            template += '<div class="Eject"><div class="event"><div class="event-box" style="max-width:'+defParams.width+'px; min-width:'+defParams.minWidth+'px;height:'+defParams.height+'px;">' +
            '<div class="title title-padding"><em></em><h3>'+defParams.title+'</h3></div><div class="iconfont icon-guanbi" onclick="window.rootVue.fnr.iDialogCancel(\''+defParams.id+'\',\'\',\'curr\');"></div>';

            if(defParams.url.indexOf('?') == -1){
                defParams.url += '?';
            }else{
                defParams.url += '&';
            }
            defParams.url += 'componentid__='+defParams.id + '&parent__='+defParams.parent;
            template += '<div style="width: 100%;height: 100%"><iframe style="width:100%;height:100%;border:none;" src="'+defParams.url+'"></iframe></div>'
            template += '</div></div></div>';
        }else{
            defParams.minWidth = 0;
            template += '<div class="Eject"><div class="event"><div class="event-box" style="max-width:'+defParams.width+'px; min-width:'+defParams.minWidth+'px;height:'+defParams.height+'px;">' +
                '<div class="event-top"';
            if(defParams.type == 'err')template += 'style="background: #ff6633;"';
            template += '><em>'+defParams.title+'</em><i class="iconfont icon-guanbi" onclick="window.rootVue.fnr.iDialogCancel(\''+defParams.id+'\',\'\',\'curr\');"></i></div>';
            template += '<div>'+defParams.text+'</div>';
            template += '</div></div></div>';
        }

        var DialogComponent = Vue.extend({
            template: template,
            methods:{
                succ:function(result){
                    defParams.callbackSucc(result);
                    this.$destroy(true);
                },
                cancel:function(result){
                    defParams.callbackCancel(result);
                    this.$destroy(true);
                }
            }
        });
        var dialog =  new DialogComponent();
        fnr.dialogArr[defParams.id] = dialog;
        dialog.$mount().$appendTo('body');
        return dialog;
    };
    fnr.iDialogSucc = function(id,result,parent){
        if(parent != 'curr'){
            window.parent.rootVue.fnr.iDialogSucc(id,result,'curr');return;
        }
        var dialog = fnr.dialogArr[id];
        dialog.succ(result);
        delete fnr.dialogArr[id];
    };
    fnr.iDialogCancel = function(id,result,parent){
        if(parent != 'curr'){
            window.parent.rootVue.fnr.iDialogCancel(id,result,'curr');return;
        }
        var dialog = fnr.dialogArr[id];
        dialog.cancel(result);
        delete fnr.dialogArr[id];
    };
    fnr.alert =function(message,params,width,height){
        if(width == undefined){
            width = '400';
        }
        if(height == undefined){
            height = '200'
        }
        var defParams = { title:'成功提示',id:'dialog_'+new Date().getTime(),width:width, height:height };
        fnr.mergeJson(params,defParams);
        defParams.text = '<div class="event-txt"><h3>'+message+'</h3></div><div class="Button-box">' +
            '<button type="button" class="btn btn-orange" onclick="window.rootVue.fnr.iDialogCancel(\''+defParams.id+'\',\'\',\'curr\');">确认</button></div>';
        fnr.iDialog(defParams);
    };
    fnr.alertErr =function(message,params,width,height){
        if(width == undefined){
            width = '400';
        }
        if(height == undefined){
            height = '200'
        }
        var defParams = { title:'错误提示',id:'dialog_'+new Date().getTime(),width:width, height:height,type:'err' };
        fnr.mergeJson(params,defParams);
        fnr.alert(message,defParams,width,height);
    };
    fnr.confirm = function(message,params){
        var defParams = { title:'系统提示',id:'dialog_'+new Date().getTime(), width:'400', height:'200'};
        fnr.mergeJson(params,defParams);
        defParams.text = '<div class="event-txt"><h3>'+message+'</h3></div>' +
            '<div class="Button-box"><button type="button" class="btn btn-orange" onclick="window.rootVue.fnr.iDialogSucc(\''+defParams.id+'\',\'\',\'curr\');">确认</button>' +
            '<button type="button" class="btn btn-default" onclick="window.rootVue.fnr.iDialogCancel(\''+defParams.id+'\',\'\',\'curr\');">关闭</button></div>';
        fnr.iDialog(defParams);
    };

    fnr.confirm1 = function(title,message,params){
        var defParams = { title:title,id:'dialog_'+new Date().getTime(), width:'500', height:'350'};
        fnr.mergeJson(params,defParams);
        defParams.text = '<div class="event-txt"><h3>'+message+'</h3></div>' +
            '<div class="Button-box"><button type="button" class="btn btn-orange" onclick="window.rootVue.fnr.iDialogSucc(\''+defParams.id+'\',\'\',\'curr\');">确认</button>' +
            '<button type="button" class="btn btn-default" onclick="window.rootVue.fnr.iDialogCancel(\''+defParams.id+'\',\'\',\'curr\');">关闭</button></div>';
        fnr.iDialog(defParams);
    };

    fnr.getAddDate = function (dateTemp, days) {
        var dateTemp = dateTemp.split("-");
        var nDate = new Date(dateTemp[1] + '-' + dateTemp[2] + '-' + dateTemp[0]); //转换为MM-DD-YYYY格式
        var millSeconds = Math.abs(nDate) + (days * 24 * 60 * 60 * 1000);
        var rDate = new Date(millSeconds);
        var year = rDate.getFullYear();
        var month = rDate.getMonth() + 1;
        if (month < 10) month = "0" + month;
        var date = rDate.getDate();
        if (date < 10) date = "0" + date;
        return (year + "-" + month + "-" + date);
    };

    fnr.showErrMessage = function(text,title){
        if(text == undefined)text = '操作失败';
        if(title == undefined)title = '错误提示';
        if(window.top.rootVue == undefined || window.top.rootVue.openEventMessage == undefined){
            //alert(text);
        }else window.top.rootVue.openEventMessage({title:title,text:text,type:'err'});
    };
    fnr.showMessage=function(text,title){
        if(text == undefined)return;
        if(title == undefined)title = '系统提示';
        if(window.top.rootVue == undefined || window.top.rootVue.openEventMessage == undefined){
            //alert(text);
        }else window.top.rootVue.openEventMessage({title:title,text:text});
    };

    fnr.dataPageTable = function(componentId,methods){
        var isDynamic = true;
        var template;
        if(componentId == undefined){
            componentId = 'data-page-table';
            template = '<div class="table-responsive">' +
                '<component :is="loadStatus"></component>' +
                '<div v-show="loadStatus == \'dt-load-succ\'">' +
                '<table class="table table-bordered table-box">' +
                '<thead><tr class="tr-bt-sc"><th v-for="head in setting.header" style="text-align:center">{{ head }}</th></tr></thead>' +
                '<tbody ctag="content"></tbody>' +
                '</table>' +
                '<pager :page="page"></pager>' +
                '</div>' +
                '</div>';
            isDynamic = false;
        }else{
            template = '#'+componentId;
        }

        var def_methods = {
            packData:function(data){
                return data;
            },
            change:function(status){
                this.loadStatus = status;
            },
            query:function(){
                var self = this;
                fnr.each(this.params,function(k,v){
                    self.queryParams[k] = v;
                });
                this.reload(1);
            },
            reload:function(currPage){
                if(currPage == undefined)currPage = this.page.curr;
                if(currPage < 1)currPage = 1;
                var params = this.queryParams;
                params.record_start = (currPage-1)*this.page.record_count;
                params.record_count = this.page.record_count;

                var self = this;
                self.change('dt-loading');

                if(typeof this.remote.link == 'function'){
                    deferred = this.remote.link(params,this.remote.options);
                }else{
                    deferred = fnr.ajaxJson(this.remote.link,params,this.remote.options);
                }
                deferred.then(function(result){
                    resp = result.json();

                    var totalCount = parseInt(fnr.getElFromJson(resp,self.result.pageRoot));
                    var totalPage = Math.ceil(totalCount/self.page.record_count);
                    if(totalPage < 1)totalPage = 1;
                    if( totalPage < currPage){
                        self.reload(totalPage);
                        return;
                    }

                    if(totalCount > 0){
                        self.change('dt-load-succ');
                    }
                    else{
                        self.change('dt-load-empty');
                    }

                    self.page.curr = currPage;
                    totalPage = totalCount>0?totalPage:0;
                    self.page.totalPage = totalPage;
                    self.page.totalCount = totalCount;
                    var startRecord = (currPage-1)*self.page.record_count+1;
                    if(startRecord>totalCount)startRecord = totalCount;
                    self.page.startRecord = startRecord;
                    var endRecord = currPage * self.page.record_count;
                    if(endRecord>totalCount)endRecord = totalCount;
                    self.page.endRecord = endRecord;

                    var shownum = 2;
                    var startPage = 1;var endPage=1;var pageno = self.page.curr;
                    if(pageno > shownum && pageno+shownum <= totalPage){
                        startPage = pageno - shownum;
                        endPage = pageno + shownum;
                    }else if(pageno <= shownum){
                        startPage = 1;
                        endPage = startPage + shownum*2;
                    }else{
                        endPage = totalPage;
                        startPage = totalPage - shownum*2;
                    }
                    //if(startPage < 0)startPage = 0;
                    if(endPage > totalPage)endPage = totalPage;


                    fnr.clearVueList(self.page.list);
                    for(var i=startPage; i<=endPage; i++){
                        if(i<=0)continue;
                        self.page.list.$set(self.page.list.length,i);
                    }

                    if(isDynamic){
                        fnr.clearVueList(self.list);
                        var data;
                        try{
                            data = self.packData(fnr.getElFromJson(resp,self.result.dataRoot),resp);
                        }catch(e){
                            data = fnr.getElFromJson(resp,self.result.dataRoot);
                        }
                        fnr.each(data,function(k,line){
                            self.list.$set(self.list.length,line);
                        });
                    }else{
                        var str = '';
                        var data = self.packData(fnr.getElFromJson(resp,self.result.dataRoot));
                        fnr.each(data,function(k,line){
                            str += '<tr>';
                            fnr.each(self.setting.fields,function(key,item){
                                str += '<td style="text-align:center">';
                                if(typeof item.field == 'function'){
                                    str += item.field(line);
                                }else{
                                    str += line[item.field];
                                }
                                str += '</td>';
                            });
                            str += '</tr>';
                        });
                        self.contentEl.innerHTML = str;
                    }
                },function(error){
                    self.change('dt-load-fail');
                }).catch(function(exception){
                    self.change('dt-load-fail');
                });
            }
        };
        fnr.mergeJson(methods,def_methods);
        Vue.component(componentId,{
            template: template,
            props: ['setting','params'],
            data:function () {
                return {
                    remote:{link:'',options: {method:'POST',alertMessage:0},isLoadOnPageInit:true},
                    queryParams:{},
                    result:{dataRoot:'data.list',pageRoot:'data.totalCount'},
                    list:[],contentEl:Element,
                    page:{ startRecord:0,endRecord:0,totalCount:0,list:[],curr:1,totalPage:0,record_count:20 },
                    loadStatus:'dt-load-succ'
                };
            },
            ready: function (){
                if(!isDynamic)this.contentEl = this.$el.querySelector('[ctag=content]');
                fnr.mergeJson(this.setting.remote,this.remote);
                fnr.mergeJson(this.setting.result,this.result);

                var record_count = fnr.Cookie.get('record_count');
                if(record_count != null){ this.page.record_count = record_count; }
                fnr.mergeJson(this.setting.page,this.page);

                if(this.remote.isLoadOnPageInit)this.query();
            },
            methods:def_methods
        });
    };
    fnr.dataTable = function(componentId,methods){
        var isDynamic = true;
        var template;
        if(componentId == undefined){
            componentId = 'data-table';
            template = '<div class="table-responsive">' +
                '<component :is="loadStatus"></component>' +
                '<div v-show="loadStatus == \'dt-load-succ\'">' +
                '<table class="table table-bordered table-box">' +
                '<thead><tr class="tr-bt-sc"><th v-for="head in setting.header">{{ head }}</th></tr></thead>' +
                '<tbody ctag="content"></tbody>' +
                '</table>' +
                '<table-footer v-show="page.isShowFooter" :page="page"></table-footer>' +
                '</div>' +
                '</div>';
            isDynamic = false;
        }else{
            template = '#'+componentId;
        }
        var def_methods = {
            packData:function(data){
                return data;
            },
            change:function(status){
                this.loadStatus = status;
            },
            query:function(){
                var params = this.queryParams;
                params.record_start = 0;
                params.record_count = 500;

                var self = this;
                self.change('dt-loading');
                if(typeof this.remote.link == 'function'){
                    deferred = this.remote.link(params,this.remote.options);
                }else{
                    deferred = fnr.ajaxJson(this.remote.link,params,this.remote.options);
                }
                deferred.then(function(result){
                    resp = result.json();

                    var list = self.packData(fnr.getElFromJson(resp,self.result.dataRoot));
                    if(list == undefined || list.length <= 0){
                        self.change('dt-load-empty');
                    }else{
                        self.change('dt-load-succ');
                    }

                    if(isDynamic){
                        fnr.clearVueList(self.list);
                        fnr.each(list,function(k,line){
                            self.list.$set(self.list.length,line);
                        });
                    }else{
                        var str = '';
                        fnr.each(list,function(k,line){
                            str += '<tr>';
                            fnr.each(self.setting.fields,function(key,item){
                                str += '<td>';
                                if(typeof item.field == 'function'){
                                    str += item.field(line);
                                }else{
                                    str += line[item.field];
                                }
                                str += '</td>';
                            });
                            str += '</tr>';
                        });
                        self.contentEl.innerHTML = str;
                    }
                },function(error){
                    self.change('dt-load-fail');
                }).catch(function(exception){
                    self.change('dt-load-fail');
                });
            },
            reload:function(){
                var params = this.queryParams;
                params.record_start = 0;
                params.record_count = 500;

                var self = this;
                self.change('dt-loading');
                if(typeof this.remote.link == 'function'){
                    deferred = this.remote.link(params);
                }else{
                    deferred = fnr.ajaxJson(this.remote.link,params,this.remote.options);
                }
                deferred.then(function(result){
                    resp = result.json();

                    var list = self.packData(fnr.getElFromJson(resp,self.result.dataRoot));
                    if(list == undefined || list.length <= 0){
                        self.change('dt-load-empty');
                    }else{
                        self.change('dt-load-succ');
                    }

                    if(isDynamic){
                        fnr.clearVueList(self.list);
                        fnr.each(list,function(k,line){
                            self.list.$set(self.list.length,line);
                        });
                    }else{
                        var str = '';
                        fnr.each(list,function(k,line){
                            str += '<tr>';
                            fnr.each(self.setting.fields,function(key,item){
                                str += '<td>';
                                if(typeof item.field == 'function'){
                                    str += item.field(line);
                                }else{
                                    str += line[item.field];
                                }
                                str += '</td>';
                            });
                            str += '</tr>';
                        });
                        self.contentEl.innerHTML = str;
                    }
                },function(error){
                    self.change('dt-load-fail');
                }).catch(function(exception){
                    self.change('dt-load-fail');
                });
            }
        };
        fnr.mergeJson(methods,def_methods)
        Vue.component(componentId,{
            template: template,
            props: ['setting','params'],
            data:function () {
                return {
                    remote:{link:'',options: {method:'POST',alertMessage:0},isLoadOnPageInit:true},
                    queryParams:{},
                    result:{dataRoot:'data.list'},
                    list:[],contentEl:Element,
                    loadStatus:'dt-load-succ'
                };
            },
            ready: function (){
                if(!isDynamic)this.contentEl = this.$el.querySelector('[ctag=content]');
                fnr.mergeJson(this.setting.remote,this.remote);
                fnr.mergeJson(this.setting.result,this.result);
                this.queryParams = this.params;
                if(this.remote.isLoadOnPageInit)this.query();
            },
            methods:def_methods
        });
    };
    Vue.component('dt-loading',{
        template:'<div class="content-no"><em class="iconfont icon-baocuo1"></em><i>数据加载中...</i></div>'
    });
    Vue.component('dt-load-succ',{
        template: '<div></div>'
    });
    Vue.component('dt-load-fail',{
        template:'<div class="content-no"><em class="iconfont icon-baocuo1"></em><i>数据加载失败</i></div>'
    });
    Vue.component('dt-load-empty',{
        template:'<div class="content-no"><em class="iconfont icon-baocuo1"></em><i>没有查询到相关数据</i></div>'
    });
    Vue.component('pager',{
        template:
            '<div class="row-paging">' +
            '<div class="paging-left">显示第 {{ page.startRecord }} 至 {{ page.endRecord }} 项记录，共 {{ page.totalCount }} 项</div>' +
            '<div class="row-right">' +
            '<div class="paging-right clearfix">' +
            '<ul class="pagination pagination-sm">' +
            '<li class="{{ page.curr <= 1?\'disabled\':\'\' }}"><a href="#" @click="goPage(1)">&laquo;第一页</a></li>' +
            '<li v-for="pageno in page.list" class="{{ pageno==page.curr?\'active\':\'\' }}"><a href="#" @click="goPage(pageno)">{{ pageno }}</a></li>' +
            '<li class="{{ page.curr >= page.totalPage?\'disabled\':\'\' }}"><a href="#" @click="goPage(page.totalPage)">最后一页&raquo;</a></li>' +
            '</ul>' +
            '</div>' +

            '<div class="paging-zhong clearfix"><em>每页显示</em>' +
            '<div class="col-sm-2 col-lg-4">' +
            '<select class="form-control" v-model="page.record_count" @change="changeCount"><option>10</option><option>20</option><option>50</option><option>100</option><option>200</option></select>' +
            '</div>' +
            '<i>条</i>' +
            '</div>' +

            '</div>' +
            '</div>',
        props:['page'],
        methods:{
            goPage : function(pageno){
                if(pageno < 1)return;
                if(pageno > this.page.totalPage)return;
                if(this.page.curr == pageno)return;
                this.$parent.reload(pageno);
            },
            changeCount:function(){
                fnr.Cookie.set('record_count',this.page.record_count);
                this.$parent.reload(1);
            }
        }
    });
    Vue.component('table-footer',{
        template: '<div class="row-fluid">' +
        '<div class="span6">' +
        '<div class="dataTables_info" style="float:left;">显示第 {{ page.startRecord }} 至 {{ page.endRecord }} 项记录，共 {{ page.totalCount }} 项</div>' +
        '</div>' +
        '</div>',
        props:['page']
    });

    Vue.component('remote-sel',{
        template:'<select class="form-control" id="{{ setting.tags.id }}" name="{{ setting.tags.name }}" v-model="value"><option v-for="option in setting.options" v-bind:value="option.key">{{ option.value }}</option></select>',
        props: ['setting','value'],
        data:function () {
            return {
                options:{},fields:{root:'data.list',key:'id',value:'name'},remote:{link:'',params:{},options: {method:'POST'},resp:[]}
            };
        },
        ready: function (){
            fnr.mergeJson(this.setting.fields,this.fields);
            fnr.mergeJson(this.setting.remote,this.remote);
            fnr.mergeJson(this.setting.options,this.options);
            this.resetData();
        },
        methods: {
            resetData:function(){
                var self = this;
                fnr.clearVueList(self.setting.options);
                fnr.each(self.options,function(k,v){
                    self.setting.options.$set(self.setting.options.length,v);
                });

                if(this.remote.link == undefined || this.remote.link == ''){
                    this.init();return;
                }

                var deferred;
                if(typeof this.remote.link == 'function'){
                    deferred = this.remote.link(this.remote.params);
                }else{
                    deferred = fnr.ajaxJson(this.remote.link,this.remote.params,this.remote.options);
                }
                deferred.then(function(result){
                    var resp = result.json();
                    self.resp = resp;
                    fnr.each(fnr.getElFromJson(resp,self.fields.root),function(k,v){
                        var item = {key:v[self.fields.key],value:v[self.fields.value]};
                        self.setting.options.$set(self.setting.options.length,item);
                    });
                    self.init();
                });
            },
            init: function(){
                var self = this;
                var flag = false;
                fnr.each(this.setting.options,function(k,v){
                    if(v.key == self.value)flag = true;
                });
                if(flag == false)this.value = '';
            },
            reset:function(){
                fnr.mergeJson(this.setting.remote,this.remote);
                this.resetData();
            }
        }
    });
    Vue.component('remote-sel-zj',{
        template:'<select style="width: 100px;height: 34px;" class="form-control form-input-2" id="{{ setting.tags.id }}" name="{{ setting.tags.name }}" v-model="value"><option v-for="option in setting.options" v-bind:value="option.key">{{ option.value }}</option></select>',
        props: ['setting','value'],
        data:function () {
            return {
                options:{},fields:{root:'data.list',key:'id',value:'name'},remote:{link:'',params:{},options: {method:'POST'}}
            };
        },
        ready: function (){
            fnr.mergeJson(this.setting.fields,this.fields);
            fnr.mergeJson(this.setting.remote,this.remote);
            fnr.mergeJson(this.setting.options,this.options);
            this.resetData();
        },
        methods: {
            resetData:function(){
                var self = this;
                fnr.clearVueList(self.setting.options);
                fnr.each(self.options,function(k,v){
                    self.setting.options.$set(self.setting.options.length,v);
                });

                if(this.remote.link == undefined || this.remote.link == ''){
                    this.init();return;
                }

                var deferred;
                if(typeof this.remote.link == 'function'){
                    deferred = this.remote.link(this.remote.params);
                }else{
                    deferred = fnr.ajaxJson(this.remote.link,this.remote.params,this.remote.options);
                }
                deferred.then(function(result){
                    resp = result.json();
                    fnr.each(fnr.getElFromJson(resp,self.fields.root),function(k,v){
                        var item = {key:v[self.fields.key],value:v[self.fields.value]};
                        self.setting.options.$set(self.setting.options.length,item);
                    });
                    self.init();
                });
            },
            init: function(){
                var self = this;
                var flag = false;
                fnr.each(this.setting.options,function(k,v){
                    if(v.key == self.value)flag = true;
                });
                if(flag == false)this.value = '';
            },
            reset:function(){
                fnr.mergeJson(this.setting.remote,this.remote);
                this.resetData();
            }
        }
    });
    Vue.component('remote-sel-disabled',{
        template:'<select class="form-control" id="{{ setting.tags.id }}" name="{{ setting.tags.name }}" v-model="value" disabled><option v-for="option in setting.options" v-bind:value="option.key">{{ option.value }}</option></select>',
        props: ['setting','value'],
        data:function () {
            return {
                options:{},fields:{root:'data.list',key:'id',value:'name'},remote:{link:'',params:{},options: {method:'POST'}}
            };
        },
        ready: function (){
            fnr.mergeJson(this.setting.fields,this.fields);
            fnr.mergeJson(this.setting.remote,this.remote);
            fnr.mergeJson(this.setting.options,this.options);
            this.resetData();
        },
        methods: {
            resetData:function(){
                var self = this;
                fnr.clearVueList(self.setting.options);
                fnr.each(self.options,function(k,v){
                    self.setting.options.$set(self.setting.options.length,v);
                });

                if(this.remote.link == undefined || this.remote.link == ''){
                    this.init();return;
                }

                var deferred;
                if(typeof this.remote.link == 'function'){
                    deferred = this.remote.link(this.remote.params);
                }else{
                    deferred = fnr.ajaxJson(this.remote.link,this.remote.params,this.remote.options);
                }
                deferred.then(function(result){
                    resp = result.json();
                    fnr.each(fnr.getElFromJson(resp,self.fields.root),function(k,v){
                        var item = {key:v[self.fields.key],value:v[self.fields.value]};
                        self.setting.options.$set(self.setting.options.length,item);
                    });
                    self.init();
                });
            },
            init: function(){
                var self = this;
                var flag = false;
                fnr.each(this.setting.options,function(k,v){
                    if(v.key == self.value)flag = true;
                });
                if(flag == false)this.value = '';
            },
            reset:function(){
                fnr.mergeJson(this.setting.remote,this.remote);
                this.resetData();
            }
        }
    });
    Vue.component('input-sel',{
        template: '<div style="position: relative;float:left;width: 100%;">' +
                '<input class="form-control" ctag="input" @focus="inputFocus" @blur="inputBlur" @input="filterOptions"/>' +
<<<<<<< HEAD
                '<span style="position:absolute;right: 0;top:0;height:100%;width:18px;background:#f0f0f0;" @click="showOptions"><img src="../static/style/img/sjx.png" style="margin-top:14px;margin-left:4px;"></span>' +
=======
                '<span style="position:absolute;right: 0;top:0;height:100%;width:18px;background:#f0f0f0;" @click="showOptions"><img src="/voucher/static/style/img/sjx.png" style="margin-top:14px;margin-left:4px;"></span>' +
>>>>>>> fix_master
                '<select class="form-control" style="position: absolute;z-index: 100"' +
                'id="{{ setting.tags.id }}" name="{{ setting.tags.name }}"' +
                'ctag="select" v-model="value" v-show="show" v-bind:size="size" @focus="selectFocus" @blur="selectBlur" @change="inputText">' +
                '<option v-for="option in list" v-show="option._show_" v-bind:value="option.key">{{ option.value }}</option>' +
                '</select>' +
                '</div>',
        props: ['setting','value'],
        data:function () {
            return {
                size:0,show:false,inputIsFocus:false,selectIsFocus:false,inputEl:Element,selectEl:Element,
                options:[],list:[],fields:{root:'data.list',key:'id',value:'name'},remote:{link:'',params:{},options: {method:'POST'}}
            };
        },
        ready: function (){
            fnr.mergeJson(this.setting.fields,this.fields);
            fnr.mergeJson(this.setting.remote,this.remote);
            fnr.mergeJson(this.setting.options,this.options);
            this.resetData();
        },
        methods: {
            resetData:function(){
                var self = this;
                fnr.clearVueList(self.setting.options);
                fnr.each(self.options,function(k,v){
                    self.setting.options.$set(self.setting.options.length,v);
                });

                if(this.remote.link == undefined || this.remote.link == ''){
                    this.init();return;
                }

                var deferred;
                if(typeof this.remote.link == 'function'){
                    deferred = this.remote.link(this.remote.params);
                }else{
                    deferred = fnr.ajaxJson(this.remote.link,this.remote.params,this.remote.options);
                }

                deferred.then(function(result){
                    resp = result.json();
                    fnr.each(fnr.getElFromJson(resp,self.fields.root),function(k,v){
                        var item = {key:v[self.fields.key],value:v[self.fields.value]};
                        self.setting.options.$set(self.setting.options.length,item);
                    });
                    self.init();
                });
            },
            init: function(){
                var self = this;
                fnr.clearVueList(self.list);
                for(var i=0; i<this.setting.options.length; i++){
                    var item = {key:this.setting.options[i].key,value:this.setting.options[i].value,_show_:true};
                    this.list.$set(i,item);
                }
                this.inputEl = this.$el.querySelector('[ctag=input]');
                this.selectEl = this.$el.querySelector('[ctag=select]');
                if(this.list.length <= 1){
                    this.size = 2;
                }else{
                    this.size = this.list.length < 10 ? this.list.length : 10;
                }
                var flag = false;
                fnr.each(this.list,function(k,v){
                    if(v.key == self.value)flag = true;
                });
                if(flag == false)this.value = '';
                setTimeout(function(){ self.inputText(); },100);
            },
            reset:function(){
                fnr.mergeJson(this.setting.remote,this.remote);
                this.resetData();
            },
            inputFocus: function(){ this.inputIsFocus = true;this.selectIsFocus = false; },
            inputBlur: function(){
                var self = this;
                this.inputIsFocus = false;
                setTimeout(function(){ self.hideOptions(); },100);
            },
            selectFocus: function(){ this.selectIsFocus = true;this.inputIsFocus = false; },
            selectBlur: function(){ this.selectIsFocus = false;this.hideOptions(); },
            inputText: function(){
                var idx = this.selectEl.selectedIndex;
                if(idx < 0)idx = 0;
                this.inputEl.value = this.selectEl.options[idx].text;
                this.selectBlur();
            },
            filterOptions: function(){
                var query_str = this.inputEl.value.trim();
                if(query_str == ''){ this.showOptions();return; }
                for(var i=0; i<this.list.length; i++){
                    var item = this.list[i];
                    item._show_ = item.value.indexOf(query_str) == -1?false:true;
                    this.list.$set(i,item);
                }
                this.show = true;
                this.inputEl.focus();
            },
            showOptions: function(){
                for(var i=0; i<this.list.length; i++){
                    var item = this.list[i];
                    item._show_ = true;
                    this.list.$set(i,item);
                }
                this.show = true;
                this.inputEl.focus();
            },
            hideOptions: function(){
                if(this.inputIsFocus == false && this.selectIsFocus == false){
                    var value = this.inputEl.value.trim();
                    var idx = this.selectEl.selectedIndex;
                    if(this.selectEl.options[idx].text != value){
                        var flag = false;
                        for(var i=0; i<this.list.length; i++){
                            if(this.list[i].value == value){
                                this.value = this.list[i].key;flag=true;break;
                            }
                        }
                        if(flag == false)this.value = '';
                    }
                    this.show = false;
                }
            }
        }
    });
    Vue.component('upload-pic',{
        template:
            '<div>'+
            '<div class="uploaded_container" v-for="(idx,item) in list"><div class="uploaded">' +
            '<img v-bind:src="item.isNew == true?item.link:\''+global_config.resBaseUrl+'\'+item.link" @click="add(idx)"/>' +
<<<<<<< HEAD
            '<p @click.stop="remove(idx)"><img src="../static/style/img/gb.png" /></p>' +
=======
            '<p @click.stop="remove(idx)"><img src="/voucher/static/style/img/gb.png" /></p>' +
>>>>>>> fix_master
            '</div></div>' +
            '<span v-show="list.length < setting.amount" @click="add(\'\')"><b>格式：jpg,png不大于{{size}}M</b></span><input type="file" style="display: none;" @change="fileSelected"/>' +
            '</div>',
        props:['setting','list'],
        data:function(){
            return { inputFile:Element,idx:'',size:2 };
        },
        ready:function(){
            this.inputFile = this.$el.querySelector('[type=file]');
            if(this.setting.size == undefined){
                this.setting.size = this.size;
            }else{
                this.size = this.setting.size;
            }
        },
        methods:{
            add:function(idx){
                this.idx = idx;
                this.inputFile.click();
            },
            fileSelected:function(){
                var self = this;
                var fr = new FileReader();
                var file = this.inputFile.files[0];
                //console.log(file);
                if(!/image\/\w+/.test(file.type)){
                    fnr.alert('图片哦，亲');
                    return;
                }
                if(file.size > self.setting.size*1024*1024){
                    fnr.alert('图片不能超过'+self.setting.size+'M哦，亲');
                    return;
                }
                if(self.setting.width && self.setting.height){
                    var img = new Image();
                    img.src = window.URL.createObjectURL(file);;
                    img.onload = function () {
                         if(this.width > self.setting.width || this.height > self.setting.height){
                             //fnr.alert('图片尺寸必须是'+self.setting.width+'*'+self.setting.height+'px哦，亲');
                             return false;
                         }
                         return true;
                    };
                    if(!img.onload()){
                        fnr.alert('图片尺寸不要超过'+self.setting.width+'*'+self.setting.height+'px哦，亲');
                        return;
                    }

                }
                fr.onload = function(e){
                    var src = e.target.result;
                    if(self.idx === ''){
                        self.list.$set(self.list.length,{link:src,isNew:true,name:file.name});
                    }else{
                        self.list.$set(self.idx,{link:src,isNew:true,name:file.name});
                    }
                };
                fr.readAsDataURL(file);
                this.inputFile.value = '';
            },
            remove:function(idx){
                this.list.$remove(this.list[idx]);
            }
        }
    });
    var HeaderTitleComponent = Vue.extend({template: '<title>{{ page_title }}</title>',data: function () { return { page_title: global_config.page.title }; }});
    var init = function(){
        new HeaderTitleComponent().$mount('header-title');
    };
    init();

    return fnr;
});