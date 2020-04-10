//控制层
app.controller('goodsController', function ($scope, $controller, goodsService, uploadService, itemCatService, typeTemplateService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function (id) {
        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        );
    }

    //保存
    $scope.save = function () {
        var serviceObject;//服务层对象
        if ($scope.entity.id != null) {//如果有ID
            serviceObject = goodsService.update($scope.entity); //修改
        } else {
            serviceObject = goodsService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    //重新查询
                    $scope.reloadList();//重新加载
                } else {
                    alert(response.message);
                }
            }
        );
    };
    $scope.add = function () {
        $scope.entity.goodsDesc.introduction = editor.html();
        goodsService.add($scope.entity).success(function (response) {
            if (response.success) {
                $scope.entity = {};
                editor.html('');//清空富文本编辑器
            } else {
                alert(response.message)
            }
        })

    };


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    }

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    $scope.uploadFile = function () {
        uploadService.uploadFile().success(function (response) {
            if (response.success) {
                $scope.image_entity.url = response.message
            } else {
                alert(response.message)
            }
        })
    }

    $scope.entity = {goods: {}, goodsDesc: {itemImages: [], specificationItems: []}};//定义页面实体结构

    $scope.add_image_entity = function () {
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }


    $scope.delete_image_entity = function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index, 1);
    }

    $scope.selectItemCat1List = function () {
        itemCatService.findParentId(0).success(function (response) {
            $scope.itemCat1List = response;
        })

    }
    $scope.$watch('entity.goods.category1Id', function (newValue, oldValue) {
        //判断一级分类有选择具体分类值，在去获取二级分类
        if (newValue) {
            //根据选择的值，查询二级分类
            itemCatService.findParentId(newValue).success(
                function (response) {
                    $scope.itemCat2List = response;
                }
            );
        }
    });

    $scope.$watch('entity.goods.category2Id', function (newValue, oldValue) {
        //判断一级分类有选择具体分类值，在去获取二级分类
        if (newValue) {
            //根据选择的值，查询二级分类
            itemCatService.findParentId(newValue).success(
                function (response) {
                    $scope.itemCat3List = response;
                }
            );
        }
    });
    $scope.$watch('entity.goods.category3Id', function (newValue, oldValue) {
        //判断三级分类被选中，在去获取更新模板id
        if (newValue) {
            itemCatService.findOne(newValue).success(
                function (response) {
                    $scope.entity.goods.typeTemplateId = response.typeId; //更新模板ID
                }
            );
        }
    });

    $scope.$watch("entity.goods.typeTemplateId", function (newValue, oldValue) {
        if (newValue) {
            typeTemplateService.findOne($scope.entity.goods.typeTemplateId).success(function (response) {
                $scope.typeTemplate = response;
                $scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);//品牌列表
                $scope.entity.goodsDesc.customAttribute = JSON.parse($scope.typeTemplate.customAttributeItems);//品牌列表
                typeTemplateService.findSpecList(newValue).success(function (response) {
                    $scope.specList = response;
                })
            })
        }

    })

    $scope.searchKey = function (list, key, value) {
        for (var i = 0; i < list.length; i++) {
            if (list[i][key] == value) {
                return list[i];
            }
        }
        return null;
    };


//    封装规格选项相关的数据    封装结果
//   entity.goodDesc.specificationItems=[{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]},{"attributeName":"屏幕尺寸","attributeValue":["6寸","5.5寸"]}]
    //1.完成数据的定义
    //2.开始封装数据 需要传入 事件对象 规格选项的名称 规格选项的值
    $scope.updateSpecAttribute = function ($event, name, value) {
        var obj = $scope.searchKey($scope.entity.goodsDesc.specificationItems, "attributeName", name);
        if (obj != null) {
            //如果选中则添加
            if ($event.target.checked) {
                obj.attributeValue.push(value);
                //否则移除
            } else {
                var index = obj.attributeValue.indexOf(value);
                obj.attributeValue.splice(index, 1);
                if (obj.attributeValue.length == 0) {
                    var index = $scope.entity.goodsDesc.specificationItems.indexOf(obj);
                    $scope.entity.goodsDesc.specificationItems.splice(index, 1);
                }
            }
        } else {
            $scope.entity.goodsDesc.specificationItems.push({"attributeName": name, "attributeValue": [value]})
        }
    }

        //[{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]},{"attributeName":"屏幕尺寸","attributeValue":["6寸","5.5寸"]}]
    //创建SKU列表
    $scope.createItemList = function () {
        //spec 存储sku对应的规格
        $scope.entity.itemList = [{spec: {}, price: 0, num: 99999, status: '0', isDefault: '0'}];//初始
        //定义变量 items指向 用户选中规格集合
        var items = $scope.entity.goodsDesc.specificationItems;
        //遍历用户选中规格集合
        for (var i = 0; i < items.length; i++) {
            //{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]}
            //{"attributeName":"屏幕尺寸","attributeValue":["6寸","5.5寸"]}
         //编写增加sku规格方法addColumn 参数1:sku规格列表  参数2:规格名称  参数3:规格选项  {"attributeName": name, "attributeValue": [value]}
            $scope.entity.itemList = addColumn($scope.entity.itemList, items[i].attributeName, items[i].attributeValue);
        }
    }
    //添加列值
    addColumn = function (list, attributeName, attributeValue) {
        var newList = [];//新的集合
        //遍历sku规格列表
        for (var i = 0; i < list.length; i++) {
            //{spec: {}, price: 0, num: 99999, status: '0', isDefault: '0'}
            //读取每行sku数据，赋值给遍历oldRow
            var oldRow = list[i];
            //遍历规格选项 //2 次
            for (var j = 0; j < attributeValue.length; j++) {
                //深克隆当前行sku数据为 newRow
                var newRow = JSON.parse(JSON.stringify(oldRow));//深克隆
                //在新行扩展列（列名是规格名称），给列赋值（规格选项值）
                newRow.spec[attributeName] = attributeValue[j];
              //保存新sku行到sku新集合
                newList.push(newRow);
            }
        }
        return newList;
    }


});