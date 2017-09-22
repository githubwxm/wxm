
define(['amplify'],function() {
var AdressInfo = function(lng,lat,address){
  this.lng = lng;
  this.lat= lat;
  this.address = address;
};
window.apiLoaded = function(){
  baidu_map();
};


  function baidu_map(data) {
    var self = this;
      var opt = self.option;
      var map = new BMap.Map("l-map");
      var myGeo = new BMap.Geocoder();
      var marker1;
      var marker2;
      map.enableScrollWheelZoom();
      if (data) {
        var point = new BMap.Point(data.lng, data.lat);
        marker1 = new BMap.Marker(point);        // 创建标注
        map.addOverlay(marker1);
        var opts = {
          width: 220,                         // 信息窗口宽度 220-730
          height: 60,                         // 信息窗口高度 60-650
          title: ""                           // 信息窗口标题
        }
        var infoWindow = new BMap.InfoWindow("原本位置 " + data.adr + " ,移动红点修改位置!你也可以直接修改上方位置系统自动定位!", opts);  // 创建信息窗口对象
        marker1.openInfoWindow(infoWindow);      // 打开信息窗口
        doit(point);
      } else {
        var point = new BMap.Point(116.331398, 39.897445);
        doit(point);

      }
      map.enableDragging();
      map.enableContinuousZoom();
      map.addControl(new BMap.NavigationControl());
      map.addControl(new BMap.ScaleControl());
      map.addControl(new BMap.OverviewMapControl());
      amplify.subscribe('map.address.change',function(address){
          loadmap(address);
      });
      amplify.subscribe('map.point.change',function(lng,lat){
          loadmapBy(lng,lat);
      });
      amplify.subscribe('map.disable.dragging',function(){
          map.disableDragging();
      });
      amplify.subscribe('map.config.needLocation',function(){
         window.setTimeout(function () {
          auto();
      }, 100);
    });

    function auto() {
        var geolocation = new BMap.Geolocation();
        geolocation.getCurrentPosition(function (r) {
            if (this.getStatus() == BMAP_STATUS_SUCCESS) {
                var point = new BMap.Point(r.point.lng, r.point.lat);
                marker1 = new BMap.Marker(point);        // 创建标注
                map.addOverlay(marker1);
                var opts = {
                    width: 220,     // 信息窗口宽度 220-730
                    height: 60,     // 信息窗口高度 60-650
                    title: ""  // 信息窗口标题
                }

                var infoWindow = new BMap.InfoWindow("定位成功这是你当前的位置!,移动红点标注目标位置，你也可以直接修改上方位置,系统自动定位!", opts);  // 创建信息窗口对象
                marker1.openInfoWindow(infoWindow);      // 打开信息窗口
                doit(point);

            } else {
                //alert('failed' + this.getStatus());
            }
        })
    }
    function doit(point) {
        if (point) {
            //获得经纬度
            map.setCenter(point);
            map.centerAndZoom(point, 15);
            map.panTo(point);

            var cp = map.getCenter();
            marker2 = new BMap.Marker(point,{raiseOnDrag:true});        // 创建标注
            var opts = {
                width: 220,                          // 信息窗口宽度 220-730
                height: 60,                          // 信息窗口高度 60-650
                title: ""                            // 信息窗口标题
            }
            var infoWindow = new BMap.InfoWindow("拖拽地图或红点，在地图上用红点标注您的店铺位置。", opts);  // 创建信息窗口对象
            marker2.openInfoWindow(infoWindow);      // 打开信息窗口

            map.addOverlay(marker2);                     // 将标注添加到地图中

            marker2.enableDragging();
            marker2.addEventListener("dragend", function (e) {
                myGeo.getLocation(new BMap.Point(e.point.lng, e.point.lat), function (result) {
                    if (result) {
                        amplify.publish('map.location',new AdressInfo(e.point.lng, e.point.lat,result.address));
                        marker2.setPosition(new BMap.Point(e.point.lng, e.point.lat));
                        map.panTo(new BMap.Point(e.point.lng, e.point.lat));
                    }
                });
            });
//            map.addEventListener("dragend", function showInfo() {
//                var cp = map.getCenter();
//                myGeo.getLocation(new BMap.Point(cp.lng, cp.lat), function (result) {
//                    if (result) {
//                        amplify.publish('map.location',new AdressInfo(cp.lng, cp.lat,result.address));
//                        marker2.setPosition(new BMap.Point(cp.lng, cp.lat));
//                        map.panTo(new BMap.Point(cp.lng, cp.lat));
//                    }
//                });
//            });
//            map.addEventListener("dragging", function showInfo() {
//                var cp = map.getCenter();
//                marker2.setPosition(new BMap.Point(cp.lng, cp.lat));
//                map.panTo(new BMap.Point(cp.lng, cp.lat));
//                map.centerAndZoom(marker2.getPosition(), map.getZoom());
//            });
        }
    }
    function loadmap(address) {
      var myCity = new BMap.LocalCity();
      // 将结果显示在地图上，并调整地图视野
      myGeo.getPoint(address, function (point) {
        if (point) {
          marker2.setPosition(new BMap.Point(point.lng, point.lat));
          map.panTo(new BMap.Point(marker2.getPosition().lng, marker2.getPosition().lat));
          map.centerAndZoom(marker2.getPosition(), map.getZoom());
          amplify.publish('map.location',new AdressInfo(point.lng, point.lat));
        }
      });
    }
    function loadmapBy(lng,lat) {

        if (lng&&lat) {
          marker2.setPosition(new BMap.Point(lng,lat));
          map.panTo(new BMap.Point(marker2.getPosition().lng, marker2.getPosition().lat));
          map.centerAndZoom(marker2.getPosition(), map.getZoom());
        }

    }

};
  return function() {
    var script = document.createElement("script");
//      http://api.map.baidu.com/api?key=sNHtfFMen9ktOAbQYpC23Gmc&v=1.1
    script.src = "http://api.map.baidu.com/api?v=1.4&key=sNHtfFMen9ktOAbQYpC23Gmc&callback=apiLoaded";
    document.body.appendChild(script);
  }();
});
