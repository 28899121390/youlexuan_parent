app.controller('searchController', function ($scope, searchService) {
    //搜索
    $scope.search = function () {
        searchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap = response;//搜索返回的结果
            }
        );
    };
    $scope.searchMap = {'keywords': '', 'category': '', 'brand': '', 'spec': {}};//搜索对象


    $scope.addSearchItem = function (key, value) {
        if (key == "brand" || key == "category") {
            $scope.searchMap[key] = value;
        } else {
            $scope.searchMap.spec[key] = value;
        }
        $scope.search();
    };

    $scope.deleteKey = function (key) {
        if (key == "brand" || key == "category") {
            $scope.searchMap[key] = '';
        } else {
            delete $scope.searchMap.spec[key]
        }
        $scope.search();
    }

});
