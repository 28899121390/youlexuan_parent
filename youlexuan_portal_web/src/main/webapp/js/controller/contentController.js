//控制层
app.controller('contentController', function ($scope, $controller, contentService) {

    $controller('baseController', {$scope: $scope});//继承

    $scope.findByCateGoryId = function () {
        contentService.findByCateGoryId(1).success(function (response) {
            $scope.list = response;
        })
    }

});	