requirejs.config({
    baseUrl: 'http://service.all580.com.cn/voucher/static/script/amd/1.0',
    paths:{
        'vue':'vue/vue.min',
        'vueResource':'vue/vue-resource.min',
        'vueValidator':'vue/vue-validator.min',
        'vuePicker':'vue/vue-datepicker',
        'moment':'moment/moment.min',
        'moment_zh-cn':'moment/lang/zh-cn',
        'fullcalendar':'fullcalendar/fullcalendar',
        'fullcalendar_lang':'fullcalendar/lang-all',
        'jqTreetable':'treeTable/jquery.treetable',
        'jquery':'jquery/jquery-3.1.0.min',
        'jquery-ui':'jquery/jquery-ui.min',
        'jedate':'jedate/jquery.jedate',
        'wangEditor':'dist/js/wangEditor.min',
        'bootstrap':'bootstrap/bootstrap.min',
        'bootstrap-fileupload':'bootstrap/bootstrap-fileupload.min',
        'dialog':'bootstrap/dialog',
        'ajaxfileupload':'bootstrap/ajaxfileupload',
        'wx':'wx/jweixin-1.2.0',
        'am':'amazeui/amazeui.min',
        'select': 'select/vue-select.min',

        'amplify':'jquery/amplify.core',
        'map':'jquery/map',
        'qrcode':'qrcode/qrcode.min',
        'dtree':'treeTable/dhtmlxtree',

        'fnr':'local/all580_util',
        'requestUrl':'local/http/request_method',
        'epRequestUrl':'local/http/ep_request_method',
        'productRequestUrl':'local/http/product_request_method',
        'paramRequestUrl':'local/http/param_request_method',
        'ordRequestUrl':'local/http/ord_request_method',
        'financeRequestUrl':'local/http/finance_request_method',
        'groupRequestUrl':'local/http/group_request_method',
        'hotelRequestUrl':'local/http/hotel_request_method',
        'fileUploadService':'local/http/fileUploadService',
        'reportRequestUrl':'local/http/report_request_method',
    },
    shim:{
        'jquery':{ deps: [], exports: '$' },
        'jedate':{ deps: ['jquery'], exports: '$' },
        'wangEditor':{ deps: ['jquery'], exports: '$' },
        'bootstrap': ['jquery'],
        'amplify': { deps: ['jquery'] , exports: 'amplify' },
        'qrcode' : { deps: ['jquery'] , exports: 'amplify' },
        'jqTreetable' : { deps: ['jquery'] , exports: 'jqTreetable' },
        'ajaxfileupload':['jquery'],
        'wx':{ deps: [], exports: 'wx' },
        'am':{ deps: ['jquery'], exports: '$' },
    }
});
var def_require_jsfile_arr_alias = [
    'vue', 'fnr'
];
var def_require_jsfile_arr = [
    'requestUrl'//, 'jquery','bootstrap',
];
var require_js_file = function (arr,func){
    Array.prototype.push.apply(def_require_jsfile_arr_alias,arr);
    Array.prototype.push.apply(def_require_jsfile_arr_alias,def_require_jsfile_arr);
    requirejs(def_require_jsfile_arr_alias,func);
};
