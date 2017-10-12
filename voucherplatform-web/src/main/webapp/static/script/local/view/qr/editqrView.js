var rootVue;
require_js_file(['vueValidator', 'vuePicker'], function (Vue, fnr, validator) {
    Vue.use(validator);

    rootVue = new Vue({
        el: '[vtag=root]',
        data: {
            lenSetting: {
                options: [
                    {key: '16', value: '16位'},
                    {key: '17', value: '17位'},
                    {key: '18', value: '18位'},
                    {key: '19', value: '19位'},
                    {key: '20', value: '20位'},
                ],
                fields: {root: 'data', key: 'id', value: 'name'}
            },
            sizeSetting: {
                options: [
                    {key: '100', value: '100px'},
                    {key: '200', value: '200px'},
                    {key: '300', value: '300px'},
                    {key: '400', value: '400px'},
                    {key: '500', value: '500px'},
                ],
                fields: {root: 'data', key: 'id', value: 'name'}
            },

            errorRateSetting: {
                options: [
                    {key: 'L', value: 'L[7%]'},
                    {key: 'M', value: 'M[15%]'},
                    {key: 'Q', value: 'Q[25%]'},
                    {key: 'H', value: 'H[30%]'},
                ],
                fields: {root: 'data', key: 'id', value: 'name'}
            },
            subFormData: {
                name: '',
                len: 16,
                prefix: '',
                postfix: '',
                errorRate: 'M',
                size: 200,
                foreColor: '#000',
            },
            id: fnr.getQueryString("id"),
            componentid__: fnr.getQueryString("componentid__"),
            readonly: false
        },
        ready: function () {
            if (this.id != undefined) {
                this.loadData();
            }
        },
        methods: {
            loadData: function () {
                var self = this;
                var param = {};
                param.id = self.id;
                var defrend = fnr.ajaxJson("/voucher/api/qr/get", param, {method: "GET"});
                defrend.then(function (result) {
                    var resp = result.json();
                    fnr.mergeJson(resp.data, self.subFormData);
                    self.readonly = resp.data.defaultOption == '1';
                });
            },
            save: function () {
                var self = this;
                this.$validate();
                if (this.$checkSubForm.valid) {
                    var defrend = fnr.ajaxJson("/voucher/api/qr/update", self.subFormData, {});
                    defrend.then(function (result) {
                        var resp = result.json();
                        if (resp.code == 200) {
                            fnr.alert('操作成功');
                            fnr.iDialogSucc(self.componentid__);
                        } else {
                            fnr.alert(resp.message);
                        }
                    });
                }
            }
        }
    });
});
