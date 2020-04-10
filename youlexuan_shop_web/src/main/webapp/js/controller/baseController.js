app.controller('baseController',function ($scope) {
    //定义分页配置
    $scope.paginationConf={
        currentPage: 1, //当前页码
        totalItems: 10,//总记录数
        itemsPerPage: 10,//每页显示记录数
        perPageOptions:[10,15,20,30,40,50],//选择每页显示记录数下拉菜单选项
        onChange: function () {
            //从后台读取分页数据
            $scope.reloadList();
        }
    };

    //把分页控件，发生变化后调用findPage方法进行封装
    $scope.reloadList=function(){
        $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
    }
    //定义一个集合存储选中要删除的品牌id
    $scope.selectIds=[];

    //点击复选框，进行要删除id数据更新 $event 可以存储复选框的选中、取消选中状态
    $scope.updateSelection=function ($event,id) {
        //判断复选框的状态
        if($event.target.checked){
            //把当前勾选中的id存储到要删除的品牌id数组
            $scope.selectIds.push(id);
        }else {
            //当取消勾选，要把当前id从要删除品牌的id数组移除
            //获取当前id元素在数组的角标位置
            var index=	$scope.selectIds.indexOf(id);
            $scope.selectIds.splice(index,1);
        }
    };

    //遍历json数组，根据传递的属性名称提取属性值，拼接成一个字符串
    $scope.jsonToString=function (jsonStr, key) {
        //1、把json字符串解析成json对象
      var jsonObj=JSON.parse(jsonStr);
      var value="";
      //2、遍历json数组
        for(var i=0;i<jsonObj.length;i++){
            if(i>0){
                value+=",";
            }
            value+=jsonObj[i][key];
        }
        return value;
    }
})
