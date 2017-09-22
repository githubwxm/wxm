var rootVue;
require_js_file([], function (Vue, fnr) {
    Vue.component('menu', {
        template: '#menu',
        props: ['menu', 'menu_status'],
        data: function () {
            return {open: false};
        },
        methods: {
            togglepic: function () {
                return this.open ? '../static/style/img/sjx.png' : '../static/style/img/sjt_12.png';
            },
            toggle: function () {
                var val = !this.open;
                this.$parent.closeAllMenu();
                this.open = val;
            },
            menuClick: function (child) {
                var params = {title: child.name, ico: this.menu.icon, isHome: false, link: child.url};
                var id = child.target;
                if (id == undefined || id == '') {
                    id = this.menu.target
                }
                params.id = id;
                this.$parent.openFrame(params);
            }
        }
    });

    Vue.component('iframe-tab', {
        template: '#iframe-tab',
        props: ['iframe'],
        methods: {
            active: function () {
                if (this.iframe.active)return;
                this.$parent.activeFrame(this.iframe);
            },
            close: function () {
                if (this.iframe.isHome)return;
                this.$parent.closeFrame(this.iframe);
            }
        }
    });
    Vue.component('iframe-content', {
        template: '#iframe-content',
        props: ['iframe'],
        ready: function () {
            this.iframe.iframeEl = this.$el.querySelector('iframe');
        }
    });

    rootVue = new Vue({
        el: '[vtag=root]',
        data: {
            fnr: fnr,
            page_config: global_config.page,
            serverData: Object,
            menuData: Object,
            iframes: [],
            activeIframeId: '',

            menu_status: 'expand',
            has_sys_self_setting_role: false
        },
        ready: function () {
            var self = this;
            var deferred = fnr.ajaxJson("../api/user/getLoginUser", {}, {method: "GET"})
            deferred.then(function (result) {
                var resp = result.json();
                if (resp.code == 98) {
                    alert(resp.message);
                    window.location = '../';
                }
                if (resp.code != 200 || resp.data == null)return;
                self.serverData = resp.data;

                self.menuData = menu;
            });

            var initFrames = [
                {id: 'homeFrame', title: '我的主页', ico: 'fa fa-home', isHome: true}
            ];
            fnr.each(initFrames, function (k, v) {
                self.openFrame(v);
            });
        },
        methods: {
            upMyInfo: function () {
                fnr.openFrame({link: '../user/updatePwd.html', title: '修改登录密码'});
            },

            closeAllMenu: function () {
                fnr.each(this.$refs.menuContainer, function (k, v) {
                    v.open = false;
                });
            },
            changeMenuStatus: function () {
                if (this.menu_status == 'expand') {
                    this.menu_status = 'collapse';
                } else {
                    this.menu_status = 'expand'
                }
            },

            openFrame: function (params) {
                var def_params = {
                    id: 'frame' + new Date().getTime(),
                    active: true,
                    height: '500px',
                    iframeEl: Element
                };
                fnr.mergeJson(params, def_params);

                this.unactiveAllFrame();
                this.activeIframeId = def_params.id;
                var idx = this.iframes.length;
                for (var i = 0; i < this.iframes.length; i++) {
                    if (this.iframes[i].id == def_params.id) {
                        idx = i;
                        break;
                    }
                }
                this.iframes.$set(idx, def_params);
            },
            activeFrame: function (item) {
                this.unactiveAllFrame();
                item.active = true;
                this.activeIframeId = item.id;
            },
            unactiveAllFrame: function () {
                fnr.each(this.iframes, function (k, v) {
                    v.active = false;
                });
            },
            closeFrame: function (item) {
                if (item.active == true) {
                    var index = this.iframes.indexOf(item);
                    this.iframes[index - 1].active = true;
                    this.activeIframeId = this.iframes[index - 1].id;
                }
                this.iframes.$remove(item);
            },
            getActiveFrame: function () {
                for (var i = 0; i < this.iframes.length; i++) {
                    var item = this.iframes[i];
                    if (item.id == this.activeIframeId) {
                        return item;
                    }
                }
                return false;
            },

            openEventMessage: function (params) {
                var self = this;
                var template = '';
                if (params.type == 'err') {
                    template =
                        '<div class="event-box" style="margin-bottom:10px;max-width:300px; min-width:250px; height:80px; background:#f0765e; overflow:hidden;">' +
                        '<div class="iconfont icon-guanbi" style="color:#fff;"></div>' +
                        '<div class="event-txt">' +
                        '<div class="event-show-1"><em class="iconfont icon-baocuo"></em></div>' +
                        '<div class="event-show-2"><i>' + params.title + '</i><p>' + params.text + '</p></div>' +
                        '</div>' +
                        '</div>';
                } else {
                    template =
                        '<div class="event-box" style="margin-bottom:10px;max-width:300px; min-width:250px; height:80px; background:#22BEC3; overflow:hidden;">' +
                        '<div class="iconfont icon-guanbi" style="color:#fff;" @click="close"></div>' +
                        '<div class="event-txt">' +
                        '<div class="event-show-1"><em class="iconfont icon-tongguo"></em></div>' +
                        '<div class="event-show-2"><i>' + params.title + '</i><p>' + params.text + '</p></div>' +
                        '</div>' +
                        '</div>';
                }

                //var id = 'event_message_'+new Date().getTime();
                var EventMessageComponent = Vue.extend({
                    template: template,
                    methods: {
                        close: function () {
                            this.$destroy(true);
                        }
                    }
                });
                var eventMessage = new EventMessageComponent();
                eventMessage.$mount().$appendTo('.event-right');

                setTimeout(function () {
                    eventMessage.close();
                }, 3000);
            },

            adjustActiveframeHeight: function () {
                try {
                    var item = this.getActiveFrame();
                    if (item.isHome)return;
                    var height = item.iframeEl.contentWindow.document.body.offsetHeight;
                    //if(height < 500)height=500;
                    //var old = item.iframeEl.offsetHeight;
                    //if(height == old)return;
                    item.height = height + 'px';
                } catch (e) {
                    console.log(e);
                }
            }
        }
    });

    setInterval(function () {
        rootVue.adjustActiveframeHeight();
    }, 1000);
});